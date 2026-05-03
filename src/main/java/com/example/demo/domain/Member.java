package com.example.demo.domain;

public class Member {
    private Long memberPk;
    private String memberId;
    private String memberName;
    private String memberEmail;
    private String memberPasswordHash;

    public Long getMemberPk() {
        return memberPk;
    }

    public void setMemberPk(Long memberPk) {
        this.memberPk = memberPk;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberEmail() {
        return memberEmail;
    }

    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
    }

    public String getMemberPasswordHash() {
        return memberPasswordHash;
    }

    public void setMemberPasswordHash(String memberPasswordHash) {
        this.memberPasswordHash = memberPasswordHash;
    }


    @Override
    public String toString() {
        return "Member{" +
                "memberPk=" + memberPk +
                ", memberId='" + memberId + '\'' +
                ", memberName='" + memberName + '\'' +
                ", memberEmail='" + memberEmail + '\'' +
                ", memberPasswordHash='" + memberPasswordHash + '\'' +
                '}';
    }
}
