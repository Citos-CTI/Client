package com.citos.client.panels.gui.plugins.LdapPlugin;

import com.citos.client.panels.gui.plugins.PluginDataField;

import javax.naming.Context;
import java.util.ArrayList;
import java.util.Hashtable;

public class LdapLoaderStorage {
    private String ldapAddress;       //LDAP Server Address
    private int ldapServerPort;      //LDAP Server Port
    private String ldapSearchBase;   //LDAP Suchbasis
    private String ldapType;         //LDAP Basis
    private int ldapSearchAmount;       //Amount of Entrys that will be loaded
    private ArrayList<PluginDataField> ldapFields = new ArrayList<>();  // LDAP Felder mit Namen

    private String ldapUrl;
    private String base;
    private Hashtable env;

    private int telephone = -1;
    private int mobile = -1;

    public LdapLoaderStorage() {
        initLdap();
    }

    public LdapLoaderStorage(LdapLoaderStorage old) {
        this.ldapAddress = old.getLdapAddress();
        this.ldapServerPort = old.getLdapServerPort();
        this.ldapSearchBase = old.getLdapSearchBase();
        this.ldapType = old.getLdapType();
        this.telephone = old.getTelephone();
        this.mobile = old.getMobile();
        ldapFields = new ArrayList<>();
        for(PluginDataField oldFields : old.getLdapFields()) {
            ldapFields.add(new PluginDataField(oldFields.getFieldname(),oldFields.getFieldvalue(), oldFields.isTelephone(),oldFields.isMobile()));
        }
        initLdap();
    }

    public void initLdap() {
        env = new Hashtable();

        String sp = "com.sun.jndi.ldap.LdapCtxFactory";
        env.put(Context.INITIAL_CONTEXT_FACTORY, sp);

        //quick fix TODO: Remove
        //ldapUrl = "ldap://" + ldapAddress + ":" + ldapServerPort + "/" + ldapSearchBase;
        ldapUrl = "ldap://" + ldapAddress + ":" + ldapServerPort + "/" + ldapSearchBase;

        env.put(Context.PROVIDER_URL, ldapUrl);

        base = ldapType;
    }

    public String getLdapAddress() {
        return ldapAddress;
    }

    public int getLdapServerPort() {
        return ldapServerPort;
    }

    public String getLdapSearchBase() {
        return ldapSearchBase;
    }

    public String getLdapType() {
        return ldapType;
    }

    public int getLdapSearchAmount() {
        return ldapSearchAmount;
    }

    public ArrayList<PluginDataField> getLdapFields() {
        int i = 0;
        for(PluginDataField field: ldapFields) {
            field.setMobile(false);
            field.setTelephone(false);
            if(telephone == i) {
                field.setTelephone(true);
            } else if(mobile == i) {
                field.setMobile(true);
            }
            ++i;
        }
        return ldapFields;
    }

    public int removeFromLdapFields(String text, String text1) {
        int i =0;
        for (PluginDataField entry : ldapFields) {
            if (entry.getFieldname().equals(text) && entry.getFieldvalue().equals(text1)) {
                if(entry.isTelephone()) {
                    unsetTelephone();
                } else if(entry.isMobile()) {
                    unsetMobile();
                }
                ldapFields.remove(entry);
                return i;
            }
            ++i;
        }
        return 0;
    }

    public void setLdapFields(ArrayList<PluginDataField> ldapFields) {
        this.ldapFields = ldapFields;
    }


    public void alterLdapFields(String text, String text1, String textNex, String text1New) {
        ldapFields.forEach((field)->{
            if(field.getFieldname().equals(text) && field.getFieldvalue().equals(text1)) {
                field.setFieldname(textNex);
                field.setFieldvalue(text1New);
            }
        });
    }

    public boolean addToLdapFields(String text, String text1) {
        for (PluginDataField entry : ldapFields) {
            if ((entry.getFieldname().equals(text) || entry.getFieldvalue().equals(text1)) ||
                    (text.length()==0 || text1.length()==0)) {
                //One of the entries is already available
                return false;
            }
        }
        //String[] g = {text, text1};
        PluginDataField g = new PluginDataField(text, text1);
        //implement here status
        ldapFields.add(g);

        return true;
    }

    public String getLdapUrl() {
        return ldapUrl;
    }

    public Hashtable getEnv() {
        return env;
    }

    public int getMobile() {
        return mobile;
    }

    public void setMobile(int mobile) {
        this.mobile = mobile;
    }

    public void unsetMobile() {this.mobile = -1;}

    public int getTelephone() {
        return telephone;
    }

    public void setTelephone(int telephone) {
        this.telephone = telephone;
    }

    public void unsetTelephone() {this.telephone =-1;}

    public void setLdapAddress(String ldapAddress) {
        this.ldapAddress = ldapAddress;
    }

    public void setLdapServerPort(int ldapServerPort) {
        this.ldapServerPort = ldapServerPort;
    }

    public void setLdapSearchBase(String ldapSearchBase) {
        this.ldapSearchBase = ldapSearchBase;
    }

    public void setLdapBase(String ldapBase) {
        this.ldapType = ldapBase;
    }
}
