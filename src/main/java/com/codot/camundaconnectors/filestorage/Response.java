package com.codot.camundaconnectors.filestorage;

public class Response {

    private String statusCode = "";
    private String statusMsg = "";
    private String response = "";


    public String getStatusCode() {
        return statusCode;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public String getResponse() {
        return response;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
