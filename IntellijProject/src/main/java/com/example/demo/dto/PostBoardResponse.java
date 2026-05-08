package com.example.demo.dto;

import java.time.LocalDateTime;

public class PostBoardResponse {
    private Long postPk;
    private String postTitle;
    private String writerId;
    private LocalDateTime postCreatedAt;
    private Long postViewCount;

    @Override
    public String toString() {
        return "PostBoardResponse{" +
                "postPk=" + postPk +
                ", postTitle='" + postTitle + '\'' +
                ", writerId='" + writerId + '\'' +
                ", postCreatedAt=" + postCreatedAt +
                ", postViewCount=" + postViewCount +
                '}';
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

    public String getWriterId() {
        return writerId;
    }

    public void setWriterId(String writerId) {
        this.writerId = writerId;
    }

    public LocalDateTime getPostCreatedAt() {
        return postCreatedAt;
    }

    public void setPostCreatedAt(LocalDateTime postCreatedAt) {
        this.postCreatedAt = postCreatedAt;
    }

    public Long getPostViewCount() {
        return postViewCount;
    }

    public void setPostViewCount(Long postViewCount) {
        this.postViewCount = postViewCount;
    }
}
