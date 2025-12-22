package com.ablestrategies.web.resp;

import com.ablestrategies.web.conn.ContentMimeType;
import com.ablestrategies.web.ServerManager;
import com.ablestrategies.web.rqst.HttpRequestPojo;

import java.net.Socket;

/**
 * Just like an HttpResponse but for a web console - remote server management.
 * ---------------------------------------------------------------------------
 * Menu:
 *    Address:Port   Show IP Address and port of this server
 *    Connections    Show all Connections
 *    Threads        Show all Threads
 *    Kill Idle 60   Kill connections that have been inactive for 60 seconds or more
 */
public class HttpResponseWebConsole extends HttpResponse {

    /** The top-level object that knows about all connections. */
    private final ServerManager manager;

    /** Buffer for assembling the HTML body. */
    private final StringBuilder bodyBuffer;

    /**
     * Ctor.
     * @param request The HttpRequestXxx that requested this response.
     * @param manager The top-level object that knows about all connections.
     */
    public HttpResponseWebConsole(HttpRequestPojo request, ServerManager manager) {
        super(request);
        this.description = "WebConsole";
        this.headerBuffer = new StringBuilder();
        this.bodyBuffer = new StringBuilder();
        this.manager = manager;
    }

    /**
     * Create the HTML response.
     * @param socket Connection - needed for discovering the IP address and port.
     * @return The appropriate ResponseCode.
     */
    @Override
    public ResponseCode generateContent(Socket socket) {
        generateHtmlAtTop();
        generateHtmlMessage();
        generateHtmlAtBottom();
        generateHeaders(); // generate headers AFTER the body because of Content-Length
        return ResponseCode.RC_OK;
    }

    /**
     * Retrieve the HTTP response.
     * @return The entire response including line1, headers, and body.
     */
    @Override
    public byte[] getContent() {
        headerBuffer.append(bodyBuffer);
        return headerBuffer.toString().getBytes();
    }

    /**
     * Must be called AFTER bodyBuffer is fully generated.
     * @apiNote Output is appended to headerBuffer.
     */
    private void generateHeaders() {
        assembleHeaders(request, ResponseCode.RC_OK, bodyBuffer.length(), ContentMimeType.MIME_HTML, 1);
    }

    /**
     * Generate the title and menu.
     * @apiNote Output is appended to bodyBuffer.
     */
    private void generateHtmlAtTop() {
        bodyBuffer.append("<html>\n<head>\n</head>\n<body>\n");
        bodyBuffer.append("<style>\n");
        bodyBuffer.append("p {padding-top: 0; padding-bottom: 0; margin-top: 0; margin-bottom: 0;}\n");
        bodyBuffer.append(".m {font-family: 'Courier New'; font-weight: bold; font-size: small; width: 1000%;}\n");
        bodyBuffer.append("</style>\n");
        bodyBuffer.append("<p style='line-height: 60%;'>\n");
        bodyBuffer.append("<h1>Web Console for Mini Web Server</h1><p/>\n");
        bodyBuffer.append("<h3>Click a selection...</h3><p/>\n");
        bodyBuffer.append("<form name='myForm' action='/webconsole' method='get'>\n");
        bodyBuffer.append("<button type='submit' name='selection' value='C'>&nbsp;Connections&nbsp;</button>&nbsp;\n");
        bodyBuffer.append("<button type='submit' name='selection' value='S'>&nbsp;Sessions&nbsp;</button>&nbsp;\n");
        bodyBuffer.append("<button type='submit' name='selection' value='T'>&nbsp;Threads&nbsp;</button>&nbsp;\n");
        bodyBuffer.append("<button type='submit' name='selection' value='P'>&nbsp;Properties&nbsp;</button>&nbsp;\n");
        bodyBuffer.append("<button type='submit' name='selection' value='L'>&nbsp;Log Level&nbsp;</button>&nbsp;\n");
        bodyBuffer.append("<button type='submit' name='selection' value='K'>&nbsp;Kill Idle 60&nbsp;</button>&nbsp;\n");
        bodyBuffer.append("</form><p/>\n");
    }

    /**
     * Generate the response to the previous menu selection.
     * @apiNote Output is appended to bodyBuffer.
     */
    private void generateHtmlMessage() {
        bodyBuffer.append("<div class='m'>\n");
        char selection = request.getQueryValue("selection", "C").charAt(0);
        switch (selection) {
            case 'C':
                toBodyAsHtml(manager.getConsole().listAllConnections());
                break;
            case 'S':
                toBodyAsHtml(manager.getConsole().listAllSessions());
                break;
            case 'T':
                toBodyAsHtml(manager.getConsole().listAllThreads());
                break;
            case 'P':
                toBodyAsHtml(manager.getConsole().listProperties());
                break;
            case 'L':
                toBodyAsHtml(manager.getConsole().toggleLogLevel());
                break;
            case 'K':
                toBodyAsHtml(manager.getConsole().killIdleClients());
                break;
            default:
                toBodyAsHtml("Invalid command " + selection);
                break;
        }
        bodyBuffer.append("</div>\n");
    }

    /**
     * Generate the closing tags of the HTML.
     * @apiNote Output is appended to bodyBuffer.
     */
    private void generateHtmlAtBottom() {
        bodyBuffer.append("</body>\n</html>\n");
    }

    /**
     * Handle HTML 2-space indents, and convert line endings.
     * @param text HTML to be cleaned up.
     * @apiNote Output is appended to bodyBuffer.
     */
    private void toBodyAsHtml(String text) {
        bodyBuffer.append(text.replace(" ", "&nbsp;").replaceAll("\n", "<p/>\n"));
    }

}
