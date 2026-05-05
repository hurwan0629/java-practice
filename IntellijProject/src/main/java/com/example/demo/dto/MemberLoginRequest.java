package com.example.demo.dto;

public class MemberLoginRequest {
    private String memberId;
    private String memberPassword;

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberPassword() {
        return memberPassword;
    }

    public void setMemberPassword(String memberPassword) {
        this.memberPassword = memberPassword;
    }

    @Override
    public String toString() {
        return "MemberLoginRequest{" +
                "memberId='" + memberId + '\'' +
                ", memberPassword='" + memberPassword + '\'' +
                '}';
    }
}
