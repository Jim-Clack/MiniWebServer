package com.jc;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpResponse {

    private final HttpRequest request;
    private final IConfiguration configuration;
    private ResponseCode responseCode;

    public HttpResponse(HttpRequest request, IConfiguration configuration) {
        this.request = request;
        this.configuration = configuration;
    }

    public ResponseCode respond(Socket socket) {
        responseCode = ResponseCode.RC_OK;
        Path pathToFile = getFilePath(true, "index.html", "default.htm");
        if(pathToFile == null) {
            responseCode = ResponseCode.RC_NOT_FOUND;
        } else {
            File fileToReturn = new File(pathToFile.toString());

        }
        // TODO
        return responseCode;
    }

    public String getContent() {

        // TODO
        return "";
    }

    public Path getFilePath(boolean mustExist, String defaultTo1, String defaultTo2) {
        Path pathToFile = Paths.get(configuration.getRootPath(), request.getUrl());
        if(mustExist && !pathToFile.toFile().exists() && defaultTo1 != null) {
            pathToFile = Paths.get(configuration.getRootPath(), defaultTo1);
        }
        if(mustExist && !pathToFile.toFile().exists() && defaultTo2 != null) {
            pathToFile = Paths.get(configuration.getRootPath(), defaultTo2);
        }
        if(mustExist && !pathToFile.toFile().exists()) {
            return null;
        }
        return pathToFile;
    }

}
