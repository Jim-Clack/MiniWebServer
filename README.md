## Basic web server - lightweight and easy to use.
  Handles HTML, PNG, JPEG, JS, CSS, etc.\
  Supports both HTTP and HTTPS. (TLS/SSL)\
  Has a local and a web console for managing the server.\
  Can be embedded or standalone.\
  Requires no extra code libraries except slf4j.\
  Unit tests for classes that provide underlying functionality.\
  Decently readable with a good maintainability index.
## Put files from webroot into /users/<yourname>/webroot, then try...
  localhost:12345/index.html\
  localhost:12345/webconsole\
*Note: Requires slf4j 2 (i.e. slf4j-api:2.0.3 and slf4j-simple:2.0.3)*
## Does NOT support...
  sessions based on a cookie and a sessionId (yet)\
  basic auth, URL-based credentials (yet)\
  web services, JSON, SOAP, etc. (yet)\
  brokering requests for an application server\
  websockets\
  multi-part messages\
  HTTP other than 1.1, overlapping requests\
  plugins\
  JEE, servlets, JSP\
  zip/jar/was/aar deployment\
  load balancing\
  alternate connections (non-HTTP)\
  could also use more thorough exception/error handling
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
You will have to catch IOException and InterruptedException, although all\
recoverable exceptions will be handled by the server without throwing.\
I'm 75 and  just wrote this, so I hope it's not too old-fashioned.
