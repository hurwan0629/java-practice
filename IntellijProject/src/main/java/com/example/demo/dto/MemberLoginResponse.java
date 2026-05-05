package com.example.demo.dto;

public class MemberLoginResponse {
    private String accessToken;
    private String memberName;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    @Override
    public String toString() {
        return "MemberLoginResponse{" +
                "accessToken='" + accessToken + '\'' +
                ", memberName='" + memberName + '\'' +
                '}';
    }
}
