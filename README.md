# multisite-wedding
Multiple Celebration Wedidng Invitation Site

Are you in the unenviable position of requiring more than one wedding ceremony or celebration?
Do you want a want to invite some people to only one celebration, but others to both?
Do you want the invite to be personal, but not have to send out a physical invite?
Then this is for you!

This is a Play! application, written in Java, that stores invites in a backend database.  Everyone
gets their own invitation, and is able to RSVP to whichever ceremony or celebration you have invited
them to.

Create you own website in the 'invite.scala.html' page.  You can then managed your invitations by
heading to the admin page (<https://yourhost.com/admin>).

You will need to setup the database, email and dropbox backups, admin login passwords, and crypto
keys in the wedding.conf and application.conf files. 

# Basic Setup
There are two basic configuration files to configure before running this project.  The first is the wedding.conf file.  

### wedding.conf
Auth Config
* username and password - straight forward.
* realm - The realm you're hosting the site on.  e.g. fredandjillsfantasticwedding.com.

dbx
* token - To setup dropbox backup, you'll need to configure a dropbox token.  Call the application what you want.  I strongly reccommend you only give the application permission to a single application folder.
* rsvpFile - The default name of the backup file.  Will be backed up on every rsvp.

mail
* email - This is the email of the account used to send the email.  This has only been tested with gmail accounts.  Create a new account, with a new password.  You will need to setup the account to allow less secure apps to connect: https://support.google.com/accounts/answer/6010255?hl=en
* to - A list of emails to email on receipt of a new rsvp.  Semi-colon separated.

### application.conf
* db.default.password - Put in a secure password for your database.
* play.crypto.secret - Make sure it's populated.  See <http://www.playframework.com/documentation/latest/> ApplicationSecret for more details.

# Startup
To start the application, run `./test.sh` to run it locally.  You will have no invites initially.  Head on to <https://localhost:9443/admin> to add an invite.  You can then visit it by heading to <http://localhost:9000/invite/irn>, with the irn replaced with the Invitee Reference Number that you're not invitee has been given.

To deploy to a remote server, run the command `./activator universal:packageZipTarball`.  This will package the project up into a tarball for you in the target folder.  You can then use the `start.sh` script to start the site once you've deployed and unpackaged the tarball.

Good luck.
