### Basic Web Server ###
 - Lightweight and easy to use.
 - Handles static pages: HTML, PNG, JPEG, JS, CSS, etc.
 - Supports both HTTP and HTTPS. (TLS/SSL)
 - Has a local and a web console for managing the server.
 - Can be embedded or standalone.
 - Has straightforward provision for plugins.
 - Requires no extra code libraries except slf4j.
 - Unit tests for classes that provide underlying functionality.
 - Decently readable with a good maintainability index.

### Quick-Start ###
 - Put files from webroot into your <home>/webroot
 - Run the App (no special settings)
 - Bring up a web browser and go to...
   - localhost:12345/index.html
   - localhost:12345/webconsole
 - Note: Requires slf4j 2 (i.e. slf4j-api:2.0.3 and slf4j-simple:2.0.3)

### Does NOT Support ###
 - multi-tenant hosting and managed subdomains (yet)
 - basic auth, URL-based credentials (yet)
 - web services, JSON, SOAP, etc. (yet)
 - ftp, webdav, websockets, or other protocols
 - multi-part forms
 - HTTP other than 1.1, i.e. overlapping requests on a socket
 - JEE, servlets, JSP
 - zip/jar/war/aar/ear deployment
 - load balancing
 - alternate connections (non-HTTP)
 - could also use more thorough exception/error handling

### Args and Preferences ###
You can pass in configuration settings or put them into the java properties
```
   Setting              arg[n]  Java property               Default
   IP port to listen         0  MiniWebServer.portNumber    12345
   SSL IP listen port        1  MiniWebServer.sslPortNumber 0 (disabled) 
   Website root path         2  MiniWebServer.rootPath      ~/webroot
   Plugins (comma-delimited)    MiniWebServer.plugins       (none)
```
In order to support SSL/HTTPS, you have to set certain Java properties, as\
listed in Preferences.java.

### Server App or Server Library ###
You may link as an embedded web server or run it as a standalone web server.
```
   new Server(args).start();
```

### To Create a Plugin ###
- Extend HttpResponsePlugin with your own, implementing getContent()
- Extend PluginBase with your own, implementing handleRequest()
- The above classes or a JAR than contains them must be in the classpath
- setProperty("MiniWebServer.plugins", "comma,delimited,plugin,classes");

### Developer Notes, Source Code ###
- Connection = Client-Server match-up based on IP-Address-and-Port
- Session = Client-Server match-up based on sessionid-mws cookie
- The main loop is in ConnectionHandler: handleRequest()
- (I'm 76 and just wrote this, so I hope it's not too old-fashioned.)
- If you want to contact me, I'm at jim.clack@ablestrategies.com
