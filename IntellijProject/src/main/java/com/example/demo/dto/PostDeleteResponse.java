package com.example.demo.dto;

public class PostDeleteResponse {
    private Long postPk;
    private Boolean postDeleted;

    @Override
    public String toString() {
        return "PostDeleteResponse{" +
                "postPk=" + postPk +
                ", postDeleted=" + postDeleted +
                '}';
    }

    public Long getPostPk() {
        return postPk;
    }

    public void setPostPk(Long postPk) {
        this.postPk = postPk;
    }

    public Boolean getPostDeleted() {
        return postDeleted;
    }

    public void setPostDeleted(Boolean postDeleted) {
        this.postDeleted = postDeleted;
    }
}
