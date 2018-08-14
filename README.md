# Citos CTI
A pretty and simple CTI client for (soon) multiple telephony systems.

What is this project about?

Product of this project will be a telephony client which will be compatible for several telephony servers.
## GUI
### Frontpage
![alt text](https://github.com/Citos-CTI/Manager-Client/blob/master/images/states.PNG)
- direct access to favorite numbers 
- see the availability of a user in the intranet directly 
- quickdial via the searchbox
- enable do not disturb and redirect 
- sort user as you want per drag and drop or sort them by call frequency 
### Addresses
![alt text](https://github.com/Citos-CTI/Manager-Client/blob/master/images/addresses.PNG)
![alt text](https://github.com/Citos-CTI/Manager-Client/blob/master/images/addresses_unfolded.PNG)
![alt text](https://github.com/Citos-CTI/Manager-Client/blob/master/images/ldap%20Plugin.PNG)
![alt text](https://github.com/Citos-CTI/Manager-Client/blob/master/images/addresses_extensive_search.PNG)
- use plugins to access your contactbooks (multiple plugins at one time possible)
- specify the fields you want to see in the settings
- multi-field search 
### Call History
![alt text](https://github.com/Citos-CTI/Manager-Client/blob/master/images/history_scroll_function.PNG)
![alt text](https://github.com/Citos-CTI/Manager-Client/blob/master/images/history_search.PNG)
- history with the most important facts
- search your personal call history
## Plugin System
### Address Plugins
One of the main features for a calling assistant or computer telephony integration is the addressbook. Therefore I've focused on bringing multiple addressbook sources to the manager. Right now there is a plugin in development for LDAP, MySql and local text files. If there is demand I'll later develop other plugins.
- extendable plugin system 
- add and remove plugins on runtime 
- api to develop own plugins (thinking about also providing a sdk later)
### Telephony Server Plugins 
The manager will also work with telephony servers other than asterisk. Therefore the server side is built modular. Read more in the server section.
