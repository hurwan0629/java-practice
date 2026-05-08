package com.example.demo.mapper;

import com.example.demo.dto.PostBoardResponse;
import com.example.demo.dto.PostCreateRequest;
import com.example.demo.dto.PostViewResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {

    PostViewResponse getPostByPk(Long postPk);

    void createPost(PostCreateRequest request);

    Long getMaxPageCount(Integer maxPostCount);

    List<PostBoardResponse> getPostAll(
            @Param("offset") Integer offset,
            @Param("limit") Integer limit);
}
