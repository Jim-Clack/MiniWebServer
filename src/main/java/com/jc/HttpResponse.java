package com.jc;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpResponse {

    private final HttpRequest request;
    private final IConfiguration configuration;
    private final StringBuilder headerBuffer;
    private byte[] responseBuffer = null;
    private ResponseCode responseCode;

    public HttpResponse(HttpRequest request, IConfiguration configuration) {
        this.request = request;
        this.configuration = configuration;
        this.headerBuffer = new StringBuilder();
    }

    public ResponseCode respond(Socket socket) {
        responseCode = ResponseCode.RC_OK;
        Path pathToFile = getFilePath(true, "index.html", "index.htm", "default.htm");
        Logger.INFO("Sending " + pathToFile + " to " + socket);
        if(pathToFile == null) {
            responseCode = ResponseCode.RC_NOT_FOUND;
        } else {
            File fileToReturn = new File(pathToFile.toString());
            try (FileInputStream inStream = new FileInputStream(fileToReturn)) {
                byte[] content = inStream.readAllBytes();
                createHeaders(pathToFile.toString(), content.length);
                headerBuffer.append("\n");
                responseBuffer = java.util.Arrays.copyOf(
                        headerBuffer.toString().getBytes(StandardCharsets.UTF_8),
                        headerBuffer.length() + content.length);
                System.arraycopy(content, 0, responseBuffer, headerBuffer.length(), content.length);
            } catch (IOException e) {
                Logger.INFO("Problem reading file " + fileToReturn.getAbsolutePath() + " " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
        return responseCode;
    }

    public byte[] getContent() {
        return responseBuffer;
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
        headerBuffer.append(line1);
        headerBuffer.append("location: http://localhost\n");              // TODO
        headerBuffer.append("content-type: text/html; charset=UTF-8\n");
        headerBuffer.append("date: Thu, 17 Apr 2025 23:25:14 GMT\n");     // TODO
        headerBuffer.append("expires: Sat, 17 May 2025 23:25:14 GMT\n");  // TODO
        headerBuffer.append("cache-control: public, max-age=2592000\n");
        headerBuffer.append("server: mini\n");
        headerBuffer.append("content-length: " + contentLength + "\n");
        headerBuffer.append("x-xss-protection: 0\n");
        headerBuffer.append("x-frame-options: SAMEORIGIN\n");
    }

}

/*

        buffer.append("HTTP/1.1 200 OK\n");
        buffer.append("location: http://localhost\n");
        buffer.append("content-type: text/html; charset=UTF-8\n");
        buffer.append("date: Thu, 17 Apr 2025 23:25:14 GMT\n");
        buffer.append("expires: Sat, 17 May 2025 23:25:14 GMT\n");
        buffer.append("cache-control: public, max-age=2592000\n");
        buffer.append("server: gws\n");
        buffer.append("content-length: 219\n");
        buffer.append("x-xss-protection: 0\n");
        buffer.append("x-frame-options: SAMEORIGIN\n");

 */