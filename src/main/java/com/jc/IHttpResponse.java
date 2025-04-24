package com.jc;

import java.net.Socket;

public interface IHttpResponse {
    ResponseCode generateContent(Socket socket);
    byte[] getContent();
}
