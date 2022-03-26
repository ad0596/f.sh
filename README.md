# f.sh
A P2P Cli tool based in REST services to share files between 2 users

## FshServer
A simple Spring-Boot REST api that stands as an intermediate node which has only 1 job i.e. get the connection requests and share each other's connection info between 2 clients (SENDER & RECEIVER).

## FshClient
This application is the one that represents a user as a SENDER or RECEIVER.  
This application serves as a REST api (Spring-Boot) as well as a Shell (Spring-Shell) application.  
<i>
> Change or remove logging level config in application.properties file for REST api to enable logging.
</i>

### Startup
Use your favourite method to run the application (preferred to run in terminal/cmd for best results).
Application runs with a startup prompt as following:

<img src="https://user-images.githubusercontent.com/36183057/122452413-dccf9d00-cfc6-11eb-86a6-5b2a09bfbc50.png" width="500" height="200" />

Use 'help' command to see all the commands.

<img src="https://user-images.githubusercontent.com/36183057/122454316-e22de700-cfc8-11eb-8445-ddf9883c8de9.png" width="500" height="470" />

There are 3 command groups:  
**Commands**: Initially available commands  
**SENDER**: Commands that will be available for user type SENDER  
**REVEICER**: Commands that will be available for user type SENDER  
<i>
> Commands that have * mark in front are not available at the moment.  
Command availability changes after setting user profile as SENDER or RECEIVER and on connection status update.
</i>

### Connection
Step1: `set-profile` command to set user profile as SENDER or RECEIVER.  
Step2: `my-addr` command will provide application port, local ip and public ip.  
Step3: `sconnect` or `rconnect` for connection request.  
<i>
> `--addr` : `<your-ip>:<app-port>`
`--id` : your id (any unique string, preferred phone num)  
`--sid` &nbsp; : SENDER's Id  
`--rid` &nbsp; : RECEIVER's Id  
</i>
  
<img src="https://user-images.githubusercontent.com/36183057/122460689-1062f500-cfd0-11eb-98c6-c212ec03efd8.png"/>

## How it works?
At SENDER's end, the file is split into partitions and sends them to RECEIVER one after the other serially.  
At RECEIVER's end, the incoming partitions are appended to the file, thus, eventually making it complete file that is sent by SENDER.
<i>
> Partition size varies (increases) for 1GB+ file, thus, limiting the number of requests to share file.
</i>

**ENJOY SHARING !!!**
