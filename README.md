## Basic web server
 - Lightweight and easy to use.
 - Handles static pages: HTML, PNG, JPEG, JS, CSS, etc.
 - Supports both HTTP and HTTPS. (TLS/SSL)
 - Has a local and a web console for managing the server.
 - Can be embedded or standalone.
 - Requires no extra code libraries except slf4j.
 - Unit tests for classes that provide underlying functionality.
 - Decently readable with a good maintainability index.
## Quick-Start
 - Put files from webroot into /users/<yourname>/webroot
 - Run the App (no special settings)
 - Bring up a web browser and go to...
   - localhost:12345/index.html
   - localhost:12345/webconsole
 - *Note: Requires slf4j 2 (i.e. slf4j-api:2.0.3 and slf4j-simple:2.0.3)
## Does NOT support...
 - basic auth, URL-based credentials (yet)
 - web services, JSON, SOAP, etc. (yet)
 - brokering requests for an application server
 - ftp, webdav, websockets, or other protocols
 - multi-part messages
 - HTTP other than 1.1, overlapping requests
 - plugins/extensions
 - JEE, servlets, JSP
 - zip/jar/war/aar/ear deployment
 - load balancing
 - alternate connections (non-HTTP)
 - could also use more thorough exception/error handling
## You can pass in configuration settings or put them into the java properties
```
   Setting          arg[n] Java property              Default
   IP port to listen   0  MiniWebServer.portNumber    12345
   SSL IP listen port  1  MiniWebServer.sslPortNumber 0 (disabled)
   Website root path   2  MiniWebServer.rootPath      /Users/[user]/webroot
```
In order to support SSL/HTTPS, you have to set certain Java properties, as\
listed in Preferences.java.
## You may link as an embedded web server or run it as a standalone web server.
```
   new Server(args).start();
```
## Developer notes, guide to the sources:
- Connection = Client-Server match-up based on IP-Address-and-Port
- Session = Client-Server match-up based on sessionId(mws) cookie
- The main loop is in ConnectionHandler, handleRequest()
- You may want to catch IOException and InterruptedException
  - (...but recoverable exceptions will be handled by the server)
- (I'm 75 and just wrote this, so I hope it's not too old-fashioned.)
- If you want to contact me, I'm at jim.clack@ablestrategies.com
## TODO...
- Need to use IP Address to track session if there is no sessionID
