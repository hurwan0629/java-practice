package com.example.demo.mapper;

import com.example.demo.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {

    PostViewResponse getPostByPk(Long postPk);

    Long createPost(PostCreateRequest request);

    Long getMaxPageCount(Integer maxPostCount);

    List<PostBoardResponse> getPostAll(
            @Param("offset") Integer offset,
            @Param("limit") Integer limit);

    Integer checkPostByPk(@Param("postPk") Long postPk);

    PostDeleteResponse deletePostByPk(@Param("postPk") Long postPk);

    PostUpdateDto updatePostTitleAndPostContentByPk(
            PostUpdateDto postUpdateDto);
}
