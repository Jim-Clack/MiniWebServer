package com.jc;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class HttpResponse {

    private final HttpRequest request;
    private final IConfiguration configuration;
    private final StringBuilder buffer;
    private ResponseCode responseCode;

    public HttpResponse(HttpRequest request, IConfiguration configuration) {
        this.request = request;
        this.configuration = configuration;
        this.buffer = new StringBuilder();
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
                String content = new String(inStream.readAllBytes(), StandardCharsets.UTF_8);
                createHeaders(pathToFile.toString(), content.length());
                buffer.append("\n");
                buffer.append(content);
            } catch (IOException e) {
                Logger.INFO("Problem reading file " + fileToReturn.getAbsolutePath() + " " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
        return responseCode;
    }

    public StringBuilder getContent() {
        return buffer;
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
        buffer.append(line1);
        buffer.append("location: http://localhost\n");              // TODO
        buffer.append("content-type: text/html; charset=UTF-8\n");
        buffer.append("date: Thu, 17 Apr 2025 23:25:14 GMT\n");     // TODO
        buffer.append("expires: Sat, 17 May 2025 23:25:14 GMT\n");  // TODO
        buffer.append("cache-control: public, max-age=2592000\n");
        buffer.append("server: mini\n");
        buffer.append("content-length: " + contentLength + "\n");
        buffer.append("x-xss-protection: 0\n");
        buffer.append("x-frame-options: SAMEORIGIN\n");
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