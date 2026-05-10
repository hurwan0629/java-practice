package com.example.demo.dto;

// 현재에는 `/member/me`에 대한 요청에 응답하기 위해 만들어진 dto
public class MemberInfoResponse {
    private Long memberPk;
    private String memberName;
    private String memberId;
    private String memberEmail;

    public Long getMemberPk() {
        return memberPk;
    }

    public void setMemberPk(Long memberPk) {
        this.memberPk = memberPk;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberEmail() {
        return memberEmail;
    }

    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
    }

    @Override
    public String toString() {
        return "MemberInfoResponse{" +
                "memberPk=" + memberPk +
                ", memberName='" + memberName + '\'' +
                ", memberId='" + memberId + '\'' +
                ", memberEmail='" + memberEmail + '\'' +
                '}';
    }
}
