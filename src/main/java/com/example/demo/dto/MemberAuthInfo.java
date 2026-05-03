package com.example.demo.dto;

public class MemberAuthInfo {
    private Long memberPk;
    private String memberName;
    private String memberPasswordHash;

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberPasswordHash() {
        return memberPasswordHash;
    }

    public void setMemberPasswordHash(String memberPasswordHash) {
        this.memberPasswordHash = memberPasswordHash;
    }

    public Long getMemberPk() {
        return memberPk;
    }

    public void setMemberPk(Long memberPk) {
        this.memberPk = memberPk;
    }

    @Override
    public String toString() {
        return "MemberAuthInfo{" +
                "memberPk=" + memberPk +
                ", memberName='" + memberName + '\'' +
                ", memberPasswordHash='" + memberPasswordHash + '\'' +
                '}';
    }
}
