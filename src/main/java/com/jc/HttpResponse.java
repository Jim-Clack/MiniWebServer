package com.jc;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HttpResponse implements IHttpResponse {

    private final HttpRequest request;
    private final Configuration configuration;
    private final StringBuilder headerBuffer;
    private byte[] responseBuffer = null;
    private ResponseCode responseCode;

    public HttpResponse(HttpRequest request, Configuration configuration) {
        this.request = request;
        this.configuration = configuration;
        this.headerBuffer = new StringBuilder();
    }

    public ResponseCode generateContent(Socket socket) {
        responseCode = ResponseCode.RC_OK;
        Path pathToFile = getFilePath(true, "index.html", "index.htm", "default.htm");
        Logger.INFO("Sending " + pathToFile + " to " + socket);
        if(pathToFile == null) {
            responseCode = ResponseCode.RC_NOT_FOUND;
        } else {
            loadFile(pathToFile);
        }
        return responseCode;
    }

    public byte[] getContent() {
        return responseBuffer;
    }

    private void loadFile(Path pathToFile) {
        File fileToReturn = new File(pathToFile.toString());
        try (FileInputStream inStream = new FileInputStream(fileToReturn)) {
            byte[] content = inStream.readAllBytes();
            createHeaders(pathToFile.toString(), content.length);
            headerBuffer.append("\n");
            // now switch to binary I/O...
            responseBuffer = java.util.Arrays.copyOf(
                    headerBuffer.toString().getBytes(StandardCharsets.UTF_8),
                    headerBuffer.length() + content.length);
            System.arraycopy(content, 0, responseBuffer, headerBuffer.length(), content.length);
        } catch (IOException e) {
            Logger.ERROR("Problem reading file " + fileToReturn.getAbsolutePath() + " " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("all")
    private Path getFilePath(boolean mustExist, String... defaultTo) {
        Path pathToFile = Paths.get(configuration.getRootPath(), request.getUrl());
        for(String defaultPath : defaultTo) {
            if (mustExist && (!pathToFile.toFile().exists() || pathToFile.toFile().isDirectory())) {
                pathToFile = Paths.get(configuration.getRootPath(), defaultPath);
            }
        }
        if(mustExist && !pathToFile.toFile().exists()) {
            return null;
        }
        return pathToFile;
    }

    private void createHeaders(String pathToFile, int contentLength) {
        String line1 = request.getVersion() + " " + responseCode.getNumValue() + " " + responseCode.getTextValue() + "\n";
        DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss zzz");
        // Thu, 17 Apr 2025 23:25:14 GMT
        String now = dateFormat.format(new Date());
        headerBuffer.append(line1);
        headerBuffer.append("location: http://localhost\n"); // TODO
        headerBuffer.append("content-type: text/html; charset=UTF-8\n");
        headerBuffer.append("date: " + now + "\n");
        headerBuffer.append("expires: " + now + "\n");
        headerBuffer.append("cache-control: public, max-age=5000\n");
        headerBuffer.append("server: mini\n");
        headerBuffer.append("content-length: " + contentLength + "\n");
        headerBuffer.append("x-xss-protection: 0\n");
        headerBuffer.append("x-frame-options: SAMEORIGIN\n");
    }

}
