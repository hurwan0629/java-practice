package com.example.demo.dto;

public class PostUpdateRequest {
    private Long postPk;
    private String postTitle;
    private String postContent;
    private Long memberPk;

    public Long getMemberPk() {
        return memberPk;
    }

    public void setMemberPk(Long memberPk) {
        this.memberPk = memberPk;
    }

    public Long getPostPk() {
        return postPk;
    }

    public void setPostPk(Long postPk) {
        this.postPk = postPk;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    @Override
    public String toString() {
        return "PostUpdateRequest{" +
                "postPk=" + postPk +
                ", postTitle='" + postTitle + '\'' +
                ", postContent='" + postContent + '\'' +
                ", memberPk=" + memberPk +
                '}';
    }
}
