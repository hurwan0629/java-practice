package com.example.demo.dto;

public class MemberLoginResponse {
    private Long memberPk;
    private String memberName;
    private String jwtToken;

    @Override
    public String toString() {
        return "MemberLoginServiceDto{" +
                "memberPk='" + memberPk + '\'' +
                ", memberName='" + memberName + '\'' +
                ", jwtTokenPresent='" + (jwtToken != null) + '\'' +
                '}';
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public Long getMemberPk() {
        return memberPk;
    }

    public void setMemberPk(Long memberPk) {
        this.memberPk = memberPk;
    }
}
