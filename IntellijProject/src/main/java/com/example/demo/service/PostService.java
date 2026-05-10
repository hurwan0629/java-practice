package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.exception.BadRequestParamException;
import com.example.demo.exception.PostNotFoundException;
import com.example.demo.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostMapper postMapper;

    // PostController에서 조금 복잡하거나 중복되는 로직들 다 빼기

    // max-page는 계산이 좀 있기때문에 빼보기.
    // maxPostCount (Integer로 받아서 Long를 반환함)
    public Long calcMaxPageCountByMaxPostCount(Integer maxPostCount) {
        if(maxPostCount <= 0) {
            throw new BadRequestParamException("maxPostCount는 1 이상이여야합니다.");
        }
        return this.postMapper.getMaxPageCount(maxPostCount);
    }

    // getPostByPostPk
    public PostViewResponse getPostService(Long postPk) {
        PostViewResponse result = this.postMapper.getPostByPk(postPk);
        if(result == null) {
            throw new PostNotFoundException();
        }
        return result;
    }

    // getAllPost
    public List<PostBoardResponse> getAllPost(int page, int maxPostCount) {
        if(page <=0 || maxPostCount <=0 ){
            throw new BadRequestParamException("page와 maxPostCount는 1 이상이여야합니다.");
        }
        int offset = (page-1) * maxPostCount;
        return this.postMapper.getPostAll(offset, maxPostCount);
    }

    // createPost
    // 이건 쓰기 전에 유효성 검사 한번 해야해서 분리 필요가 더 있다고 판단.
    public Long createPost(
            PostCreateRequest request,
            Long memberPk) {
        if(request.getPostTitle() == null
                || request.getPostTitle().length() <= 0
                || request.getPostTitle().contains("(욕설 검사할 예정)")) {
            throw new BadRequestParamException("제목을 다시 확인해주세요. 쓰지 않거나 욕설이 포함되면 안됩니다.");
        }
        if(request.getPostContent() == null
                || request.getPostContent().length() <= 0
                || request.getPostContent().contains("(욕설 검사할 예정)")) {
            throw new BadRequestParamException("내용을 다시 확인해주세요. 쓰지 않거나 욕설이 포함되면 안됩니다.");
        }
        request.setMemberPk(memberPk);

        return this.postMapper.createPost(request);
    }

    public PostDeleteResponse setPostDeletedTrueByUserDeleteReqeust(Long postPk) {
        Integer count = this.postMapper.checkPostByPk(postPk);

        if(count <= 0) {
            throw new PostNotFoundException();
        }

        return this.postMapper.deletePostByPk(postPk);
    }

    // updatePost
    public PostUpdateDto updatePostTitleAndPostContentByPk(
            Long postPk,
            PostUpdateDto request) {
        if(postMapper.checkPostByPk(postPk) <= 0) {
            throw new PostNotFoundException();
        }
        request.setPostPk(postPk);
        return this.postMapper.updatePostTitleAndPostContentByPk(request);
    }
}
