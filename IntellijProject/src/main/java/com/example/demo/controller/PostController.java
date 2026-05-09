package com.example.demo.controller;

import com.example.demo.dto.PostCreateRequest;
import com.example.demo.dto.PostUpdateDto;
import com.example.demo.dto.PostViewResponse;
import com.example.demo.exception.PostNotFoundException;
import com.example.demo.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostMapper postMapper;

    @GetMapping("/max-page")
    public ResponseEntity<?> getMaxPageCount(
            @RequestParam(value="maxPostCount") String stringMaxPostCount
    ) {
        Integer maxPostCount = Integer.parseInt(stringMaxPostCount);
        if(maxPostCount == null || maxPostCount <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Long maxPageCount = postMapper.getMaxPageCount(maxPostCount);
        return ResponseEntity.ok(Map.of("maxPageCount", maxPageCount));
    }

    @GetMapping("/{post_pk}")
    public ResponseEntity<?> getPostByPk(
            @PathVariable("post_pk") Long postPk
    ) {
        System.out.println("/post/"+ postPk);

        PostViewResponse response = postMapper.getPostByPk(postPk);

        if(response == null) {
            throw new PostNotFoundException();
        }

        return ResponseEntity.ok(response);

    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllPosts(
            @RequestParam(name="page", defaultValue="1") String stringPage,
            @RequestParam(name="maxPostCount", defaultValue="10") String stringMaxPostCount
    ) {
        Integer page = Integer.parseInt(stringPage);
        Integer maxPostCount = Integer.parseInt(stringMaxPostCount);
        Integer offset = (page-1) * maxPostCount;
        return ResponseEntity.ok(postMapper.getPostAll(offset, maxPostCount));
    }

    @PostMapping("")
    public ResponseEntity<?> createPost(
            @RequestBody PostCreateRequest request,
            @RequestAttribute("memberPk") Long memberPk
    ) {
        request.setMemberPk(memberPk);
        this.postMapper.createPost(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "게시글이 생성되었습니다",
                        "postPk", request.getPostPk()));
    }

    @DeleteMapping("/{post_pk}")
    public ResponseEntity<?> deletePost(
            @PathVariable("post_pk") Long postPk
    ) {
        Integer count = postMapper.checkPostByPk(postPk);

        if(count <= 0) {
            throw new PostNotFoundException();
        }

        return ResponseEntity.ok(this.postMapper.deletePostByPk(postPk));
    }

    @PatchMapping("/{post_pk}")
    public ResponseEntity<?> updatePost(
            @PathVariable("post_pk") Long postPk,
            @RequestBody PostUpdateDto request
            ) {
        if(postMapper.checkPostByPk(postPk) <= 0) {
            throw new PostNotFoundException();
        }
        request.setPostPk(postPk);
        return ResponseEntity.ok(this.postMapper.updatePostTitleAndPostContentByPk(request));
    }

}
