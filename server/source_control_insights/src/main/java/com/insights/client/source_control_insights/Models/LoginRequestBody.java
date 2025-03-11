package com.insights.client.source_control_insights.Models;


public class LoginRequestBody {
    public String authCode;

    public LoginRequestBody(String authCode) {
        this.authCode = authCode;
    }

    public LoginRequestBody() {
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }
}
