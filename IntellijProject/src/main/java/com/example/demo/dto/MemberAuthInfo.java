package com.example.demo.dto;

// 로그인 시 id/pw 확인을 위해 가져오는 정보 (맞으면 그대로 pk와 name를 반환하는 방식)
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
