/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.johannes.lsctic.amiapi.netty;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.johannes.lsctic.messagestage.ErrorMessage;
import com.johannes.lsctic.messagestage.SuccessMessage;
import com.johannes.lsctic.panels.gui.fields.HistoryField;
import com.johannes.lsctic.panels.gui.fields.callrecordevents.AskForCdrCountEvent;
import com.johannes.lsctic.panels.gui.fields.callrecordevents.RemoveCdrAndUpdateEvent;
import com.johannes.lsctic.panels.gui.fields.otherevents.CloseApplicationSafelyEvent;
import com.johannes.lsctic.panels.gui.fields.otherevents.StartConnectionEvent;
import com.johannes.lsctic.panels.gui.fields.serverconnectionhandlerevents.*;
import com.johannes.lsctic.panels.gui.plugins.pluginevents.CheckLicenseForPluginEvent;
import com.johannes.lsctic.panels.gui.settings.PasswordChangeRequestEvent;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import javafx.application.Platform;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author johannesengler
 */
public class ServerConnectionHandler {
    private final EventBus bus;
    private String ownExtension;
    private boolean firstStart = true;
    private boolean silentReconnect;
    private String address;
    private ExecutorService executor;
    private int port;
    private String id;
    private String actHash;
    private Channel ch;
    private boolean loggedIn;
    // without this var the user would get the loop with retries even if has never connected to the server properly
    private boolean serverDataChanged;

    public ServerConnectionHandler(EventBus bus) {
        this.bus = bus;
        this.loggedIn = false;
        this.serverDataChanged = true;
        bus.register(this);
    }
    @Subscribe
    public void closeConnection(CloseApplicationSafelyEvent event) {
        Platform.runLater(()->{
            this.write("loff" + "\r\n");
            this.ch.disconnect();
            this.ch.close();
        });
    }

    @Subscribe
    public void setOwnExtension(ReceivedOwnExtensionEvent event) {
        this.ownExtension = event.getOwnExtension();
        aboCdrExtension(new AboCdrExtensionEvent(ownExtension));
        Logger.getLogger(getClass().getName()).info(ownExtension);
    }

    @Subscribe
    public void sendBack(SendBackEvent event) {
        this.write(event.getMessage() + "\r\n");
    }

    @Subscribe
    public  void passwordChange(PasswordChangeRequestEvent event) {
        Logger.getLogger(getClass().getName()).info(event.getUser() + "   "+ event.getOldPw());
        this.write("chpw"+event.getUser()+";"+event.getOldPw()+";"+event.getNewPw()+"\r\n");
    }

    @Subscribe
    public void aboStatusExtension(AboStatusExtensionEvent event) {
        this.write("000" + event.getPhonenumber() + "\r\n");
    }

    @Subscribe
    public void deAboStatusExtension(DeAboStatusExtensionEvent event) {
        this.write("001" + event.getPhonenumber() + "\r\n");
    }

    @Subscribe
    public void askForCdrCount(AskForCdrCountEvent event) {
        this.write("007 \r\n");
    }

    @Subscribe
    public void removeCdrAndUpdate(RemoveCdrAndUpdateEvent event) {
        HistoryField f = event.getHistoryField();
        String source = ownExtension;
        String destination = f.getWho();
        if(!f.isOutgoing()) {
            source = f.getWho();
            destination = ownExtension;
        }
        this.write("006"+String.valueOf(f.getTimeStamp())+";"+source+";"+destination+"\r\n");
    }

    @Subscribe
    public void orderCdrsHistory(OrderCDRsEvent event) {
        this.write("005"+event.getStart()+";"+event.getAmount()+"\r\n");
    }

    @Subscribe
    public void checkLicenseForPlugin(CheckLicenseForPluginEvent event) {
        this.write("011"+event.getPluginName()+";"+event.getPluginLicense()+"\r\n");
    }

    @Subscribe
    public void aboCdrExtension(AboCdrExtensionEvent event) {
        this.write("004" + event.getPhoneNumber() + "\r\n");
    }

    @Subscribe
    public void call(CallEvent event) {
        Logger.getLogger(getClass().getName()).info("Versucht: " + event.getPhoneNumber() + " anzurufen");
        this.write("003" + ownExtension+":"+event.getPhoneNumber() + "\r\n");
    }

    @Subscribe
    public void startConnection(StartConnectionEvent event) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(()->{
        if (ch != null && ch.isOpen()) {
            this.ch.disconnect();
            this.ch.close();
        }
        this.address = event.getAddress();
        this.port = event.getPort();
        this.id = event.getId();
        this.silentReconnect = event.isSilentRetry();
        try {
            final SslContext sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();

            EventLoopGroup group = new NioEventLoopGroup();

            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new SecureChatClientInitializer(sslCtx, this.bus, this.address, this.port));

            // Start the connection attempt.
            ch = b.connect(address, port).sync().channel();

        } catch (InterruptedException | IOException | IllegalArgumentException ex) {
            Logger.getLogger(ServerConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
            if(!event.isSilentRetry()) {
                Platform.runLater(()->new ErrorMessage("Could not establish connection to server. Please edit connection to server under options."));
                firstStart = false;
            }
        }
        if(ch!=null && ch.isOpen()) {
            if (event.isHash()) {
                Logger.getLogger(getClass().getName()).info(event.getPw());
                ch.writeAndFlush(event.getId() + ";" + event.getPw() + "\r\n");
                this.actHash = event.getPw();
            } else {
                ch.writeAndFlush("ndb" + event.getId() + ";" + event.getPw() + "\r\n");
            }
        }
        });
    }

    @Subscribe
    public void userLoggedInStatusChanged(UserLoginStatusEvent event) {
        this.loggedIn = event.isLoggedIn();
        if (!event.isLoggedIn()) {
            Platform.runLater(() -> new ErrorMessage("Could not log in on server. Please edit username or password for server."));
            this.ch.disconnect();
            this.ch.close();
        } else if(event.isLoggedIn()) {
            // Check cyclically if the user is still connected -> if not reconnect attempt until hes again connected
            executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                final String address = this.address;
                final String id = this.id;
                final String actHash = this.actHash;
                while(ch.isOpen()) {
                    try {
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException e) {
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
                        Thread.currentThread().interrupt();
                    }
                }
                // If connection becomes closed: inform!
                Platform.runLater(() -> bus.post(new ConnectionToServerLostEvent()));

                if(address.equals(this.address)&&id.equals(this.id)&&actHash.equals(this.actHash)) {
                    int i = 1;
                    while (!ch.isOpen()) {
                        bus.post(new StartConnectionEvent(this.address, this.port, this.id, this.actHash, true, true));
                        try {
                            TimeUnit.SECONDS.sleep(5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (ch.isOpen()) {
                            break;
                        }
                        Logger.getLogger(getClass().getName()).info("Login attempt: " + String.valueOf(i));
                        ++i;
                    }
                }

            });
            this.actHash = event.getHashedPw();
        }
        // It would be annoying if every time you start the program a message pops up that informs about the connection
        if (!firstStart && event.isLoggedIn() && !this.silentReconnect) {
            Platform.runLater(() -> new SuccessMessage("Established connection to server: " + address + "\n Logged in successfully"));
        }
        firstStart = false;


    }


    public void write(String message) {
        if (ch != null && ch.isOpen() && this.loggedIn) {
            ch.writeAndFlush(message);
        }
    }

}
