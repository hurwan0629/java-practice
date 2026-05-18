package com.test.security.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String role;

    @Builder
    public Member(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public void changeUsername(String username) {
        this.username = username;
    }
    public void changeRole(String role) {
        if(role.equals("Basic")) {
            this.role = role;
        }
        else if(role.equals("Advanced")) {
            this.role = role;
        }
        else if(role.equals("Pro")) {
            this.role = role;
        }
        else if(role.equals("Ultimate")) {
            this.role = role;
        }
    }

}
