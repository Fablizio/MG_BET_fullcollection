package it.smibet.exception;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.nio.charset.Charset;


public class HttpStatusCodeException extends org.springframework.web.client.HttpStatusCodeException {
    public HttpStatusCodeException(HttpStatus statusCode) {
        super(statusCode);
    }

    public HttpStatusCodeException(HttpStatus statusCode, String statusText) {
        super(statusCode, statusText);
    }

    public HttpStatusCodeException(HttpStatus statusCode, String statusText, byte[] responseBody, Charset responseCharset) {
        super(statusCode, statusText, responseBody, responseCharset);
    }

    public HttpStatusCodeException(HttpStatus statusCode, String statusText, HttpHeaders responseHeaders, byte[] responseBody, Charset responseCharset) {
        super(statusCode, statusText, responseHeaders, responseBody, responseCharset);
    }
}
