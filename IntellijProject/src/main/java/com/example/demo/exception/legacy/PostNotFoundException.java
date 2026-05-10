package com.example.demo.exception.legacy;

class PostNotFoundException extends RuntimeException {
    public PostNotFoundException() {
        super("게시글을 찾을 수 없습니다");
    }
}
