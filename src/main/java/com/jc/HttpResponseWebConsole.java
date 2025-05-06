package com.jc;

import java.net.Socket;

/**
 * Just like an HttpResponse but for a web console - remote server management.
 * ---------------------------------------------------------------------------
 * Menu:
 *    Address:Port   Show IP Address and port of this server
 *    Sessions       Show all sessions
 *    Kill Idle 60   Kill sessions that have been inactive for 60 seconds or more
 */
public class HttpResponseWebConsole extends HttpResponseBase {

    /** The HttpRequestXxx that requested this response. */
    private final HttpRequestPojo request;

    /** The top-level object that knows about all sessions. */
    private final ServerManager manager;

    /** Buffer for assembling the HTTP header. */
    private final StringBuilder headerBuffer;

    /** Buffer for assembling the HTML body. */
    private final StringBuilder bodyBuffer;

    /**
     * Ctor.
     * @param request The HttpRequestXxx that requested this response.
     * @param manager The top-level object that knows about all sessions.
     */
    public HttpResponseWebConsole(HttpRequestPojo request, ServerManager manager) {
        this.request = request;
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
        generateHtmlMessage(socket);
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
        String line1 = request.getVersion() + " 200 OK\n";
        assembleHeaders(headerBuffer, line1, bodyBuffer.length(), ContentMimeType.MIME_HTML, 1);
    }

    /**
     * Generate the title and menu.
     * @apiNote Output is appended to bodyBuffer.
     */
    private void generateHtmlAtTop() {
        bodyBuffer.append("<html>\n<head>\n</head>\n<body>\n");
        bodyBuffer.append("<style>\n");
        bodyBuffer.append("p {\n");
        bodyBuffer.append(" line-height: 50%;\n");
        bodyBuffer.append(" }\n");
        bodyBuffer.append(".m {\n");
        bodyBuffer.append(" font-family: 'Courier New';\n");
        bodyBuffer.append(" font-weight: bold;\n");
        bodyBuffer.append(" font-size: small;\n");
        bodyBuffer.append(" }\n");
        bodyBuffer.append("</style>\n");
        bodyBuffer.append("<p style='line-height: 60%;'>\n");
        bodyBuffer.append("<h1>Web Console for Mini Web Server</h1><p/>\n");
        bodyBuffer.append("<h3>Click a selection...</h3><p/>\n");
        bodyBuffer.append("<form name='myForm' action='/webconsole' method='get'>\n");
        bodyBuffer.append("<button type='submit' name='selection' value='A'>&nbsp;Address:Port&nbsp;</button>&nbsp;\n");
        bodyBuffer.append("<button type='submit' name='selection' value='S'>&nbsp;Sessions&nbsp;</button>&nbsp;\n");
        bodyBuffer.append("<button type='submit' name='selection' value='T'>&nbsp;Threads&nbsp;</button>&nbsp;\n");
        bodyBuffer.append("<button type='submit' name='selection' value='K'>&nbsp;Kill Idle 60&nbsp;</button>&nbsp;\n");
        bodyBuffer.append("</form><p/>\n");
    }

    /**
     * Generate the response to the previous menu selection.
     * @param socket Connection - needed for discovering the IP address and port.
     * @apiNote Output is appended to bodyBuffer.
     */
    private void generateHtmlMessage(Socket socket) {
        bodyBuffer.append("<div class='m'>\n");
        char selection = request.getQueryValue("selection", "S").charAt(0);
        switch (selection) {
            case 'K':
                toBodyAsHtml("Number of sessions killed: " + manager.killIdleSessions(60) + "\n");
                break;
            case 'A': case 'P':
                toBodyAsHtml("Server address and port: " + socket.getRemoteSocketAddress().toString() + "\n");
                break;
            case 'S':
                toBodyAsHtml(manager.listAllSessions() + "\n");
                break;
            case 'T':
                toBodyAsHtml(manager.listAllThreads() + "\n");
                break;
            default:
                toBodyAsHtml("Invalid command " + selection + "\n");
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
