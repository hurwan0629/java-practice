package com.example.demo.exception.legacy;

class LoginFailedException extends RuntimeException {
    public LoginFailedException(String message) {
        super(message);
    }
}
