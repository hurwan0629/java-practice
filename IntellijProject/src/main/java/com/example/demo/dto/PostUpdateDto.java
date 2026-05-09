package com.example.demo.dto;

public class PostUpdateDto {
    private Long postPk;
    private String postTitle;
    private String postContent;

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
        return "PostUpdateDto{" +
                "postPk=" + postPk +
                ", postTitle='" + postTitle + '\'' +
                ", postContent='" + postContent + '\'' +
                '}';
    }
}
