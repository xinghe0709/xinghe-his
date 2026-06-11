package org.xinghe.xinghehis.service.dto;

public class LoginResponse {
    private String token;
    private String realName;
    private String role;

    public LoginResponse(String token, String realName, String role) {
        this.token = token;
        this.realName = realName;
        this.role = role;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
