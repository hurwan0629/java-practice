package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping("/max-page")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getMaxPageCount(
            @RequestParam(value="maxPostCount", defaultValue="10") Integer maxPostCount
    ) {

        return ResponseEntity.ok(ApiResponse.success(Map.of("maxPageCount", this.postService.getMaxPageCount(maxPostCount))));
    }

    @GetMapping("/{post_pk}")
    public ResponseEntity<ApiResponse<PostViewResponse>> getPostByPk(
            @PathVariable("post_pk") Long postPk
    ) {
        System.out.println("/post/"+ postPk);

        return ResponseEntity.ok(ApiResponse.success(this.postService.getPost(postPk)));

    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<PostBoardResponse>>> getAllPosts(
            @RequestParam(name="page", defaultValue="1") int page,
            @RequestParam(name="maxPostCount", defaultValue="10") int maxPostCount
    ) {
        return ResponseEntity.ok(ApiResponse.success(this.postService.getPosts(page, maxPostCount)));
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createPost(
            @RequestBody PostCreateRequest request,
            @RequestAttribute("memberPk") Long memberPk
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(Map.of(
                        "message", "게시글이 생성되었습니다",
                        "postPk", this.postService.createPost(request, memberPk))));
    }

    @DeleteMapping("/{post_pk}")
    public ResponseEntity<ApiResponse<PostDeleteResponse>> deletePost(
            @PathVariable("post_pk") Long postPk,
            @RequestAttribute("memberPk") Long memberPk
    ) {

        return ResponseEntity.ok(ApiResponse.success(this.postService.setPostDeletedTrueByUserDeleteRequest(postPk, memberPk)));
    }

    @PatchMapping("/{post_pk}")
    public ResponseEntity<ApiResponse<PostUpdateRequest>> updatePost(
            @PathVariable("post_pk") Long postPk,
            @RequestBody PostUpdateRequest request,
            @RequestAttribute("memberPk") Long memberPk
            ) {
        return ResponseEntity.ok(ApiResponse.success(this.postService.updatePost(postPk, request, memberPk)));
    }


}
