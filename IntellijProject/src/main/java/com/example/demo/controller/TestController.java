package com.example.demo.controller;

import com.example.demo.mapper.TestMapper;
import com.example.demo.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class TestController {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private TestMapper testMapper;

    public static AtomicInteger a= new AtomicInteger(0);

    @GetMapping("/test")
    public ResponseEntity test() {
        int current = a.getAndIncrement();
        System.out.println(current);

        // try{
        //     Thread.sleep(1000);
        // } catch (InterruptedException e) {
        //     throw new RuntimeException(e);
        // }
        boolean dbConnected = false;
        try {
            dbConnected = this.testMapper.checkConnection();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return ResponseEntity.ok(Map.of("message", "hello",
                "dbConnection", dbConnected));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> body) {
//        System.out.println(body);
//        String username = (String) body.get("username");
//        System.out.println(username);
//        return ResponseEntity.ok(Map.of("accessToken", JwtService.createToken(username)));
        return null;
    }

    @GetMapping("/check")
    public ResponseEntity<?> check(
            @RequestHeader("Authorization") String authorization
    ) {
        System.out.println(authorization);
        String token = authorization.replace("Bearer ", "");
        String username = jwtService.getMemberPk(token);

        return ResponseEntity.ok(Map.of("username", username));
    }
}
