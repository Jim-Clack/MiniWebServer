package com.jc;

import java.net.Socket;

/**
 * Just like an HttpResponse but for a web console - remote server management.
 */
public class WebConsoleResponse extends HttpResponseBase implements IHttpResponse {

    private final HttpRequest request;
    private final StringBuilder headerBuffer;
    private final StringBuilder bodyBuffer;
    private final ServerManager manager;

    public WebConsoleResponse(HttpRequest request, ServerManager manager) {
        this.request = request;
        this.headerBuffer = new StringBuilder();
        this.bodyBuffer = new StringBuilder();
        this.manager = manager;
    }

    public ResponseCode generateContent(Socket socket) {
        generateHtmlAtTop();
        generateHtmlMessage(socket);
        generateHtmlAtBottom();
        generateHeaders();
        return ResponseCode.RC_OK;
    }

    public byte[] getContent() {
        headerBuffer.append(bodyBuffer.toString());
        return headerBuffer.toString().getBytes();
    }

    /**
     * Must be called AFTER bodyBuffer is fully generated.
     */
    private void generateHeaders() {
        String line1 = request.getVersion() + " 200 OK\n";
        assembleHeaders(headerBuffer, line1, bodyBuffer.length(), 1);
    }

    private void generateHtmlAtTop() {
        bodyBuffer.append("<html>\n");
        bodyBuffer.append("<head>\n");
        bodyBuffer.append("</head>\n");
        bodyBuffer.append("<body>\n");
        bodyBuffer.append("<h1>Web Console for Mini Web Server</h1><p/>\n");
        bodyBuffer.append("<h3>Click a selection...</h3><p/>\n");
        bodyBuffer.append("<form name='myForm' action='/webconsole' method='get'>\n");
        bodyBuffer.append("<button type='submit' name='selection' value='A'>&nbsp;Address:Port&nbsp;</button>&nbsp;\n");
        bodyBuffer.append("<button type='submit' name='selection' value='S'>&nbsp;Sessions&nbsp;</button>&nbsp;\n");
        bodyBuffer.append("<button type='submit' name='selection' value='K'>&nbsp;Kill Idle 60&nbsp;</button>&nbsp;\n");
        bodyBuffer.append("</form><br/>\n");
    }

    private void generateHtmlMessage(Socket socket) {
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
            default:
                toBodyAsHtml("Invalid command " + selection + "\n");
                break;
        }
    }

    private void generateHtmlAtBottom() {
        bodyBuffer.append("</body>\n");
        bodyBuffer.append("</html>\n");
    }

    private void toBodyAsHtml(String text) {
        bodyBuffer.append(text.replace("\n  ", "\n&nbsp;&nbsp;").replaceAll("\n", "<p/>\n"));
    }

}
