package com.jc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HttpResponseBase {

    @SuppressWarnings("all")
    protected Path getFilePath(String url, boolean mustExist, Configuration configuration, String... defaultTo) {
        Path pathToFile = Paths.get(configuration.getRootPath(), url);
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

    protected byte[] readFile(Path pathToFile) {
        File fileToReturn = new File(pathToFile.toString());
        try (FileInputStream inStream = new FileInputStream(fileToReturn)) {
            return inStream.readAllBytes();
        } catch (IOException e) {
            Logger.ERROR("Problem reading file " + fileToReturn.getAbsolutePath() + " " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    protected void assembleHeaders(
            StringBuilder headerBuffer, String line1, int contentLength, int maxSeconds) {
        DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss zzz");
        String now = dateFormat.format(new Date());
        headerBuffer.append(line1);
        headerBuffer.append("content-type: text/html; charset=UTF-8\n");
        headerBuffer.append("date: " + now + "\n");
        headerBuffer.append("cache-control: public, max-age=" + maxSeconds + "\n");
        headerBuffer.append("server: mini\n");
        headerBuffer.append("content-length: " + contentLength + "\n");
        headerBuffer.append("x-xss-protection: 0\n");
        headerBuffer.append("x-frame-options: SAMEORIGIN\n");
        headerBuffer.append("\n");
    }

}
