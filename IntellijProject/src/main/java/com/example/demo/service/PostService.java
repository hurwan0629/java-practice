package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PostService {

    @Autowired
    private PostMapper postMapper;

    // PostController에서 조금 복잡하거나 중복되는 로직들 다 빼기

    // max-page는 계산이 좀 있기때문에 빼보기.
    // maxPostCount (Integer로 받아서 Long를 반환함)
    public Long getMaxPageCount(Integer maxPostCount) {
        if(maxPostCount <= 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST_PARAM, Map.of(
                "resource", "maxPostCount",
                "action", "select",
                "reason", "해당 파라미터는 1 이상이여야 합니다."
            ));
        }
        return this.postMapper.getMaxPageCount(maxPostCount);
    }

    // getPostByPostPk
    public PostViewResponse getPost(Long postPk) {
        PostViewResponse result = this.postMapper.getPostByPk(postPk);
        if(result == null) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND, Map.of(
                    "resource", "post",
                    "action", "select",
                    "reason", "해당 포스트가 존재하지 않습니다."
            ));
        }
        return result;
    }

    // getPosts
    public List<PostBoardResponse> getPosts(int page, int maxPostCount) {
        if(page <=0 || maxPostCount <=0 ){
            throw new BusinessException(ErrorCode.BAD_REQUEST_PARAM, Map.of(
                    "resource", "[page, maxPostCount]",
                    "action", "update",
                    "reason", "두 인자는 모두 1 이상이여야 합니다."
            ));
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
            throw new BusinessException(ErrorCode.BAD_REQUEST_PARAM, Map.of(
                    "resource", "postTitle",
                    "action", "create",
                    "reason", "제목이 없거나 부적절합니다."
            ));
        }
        if(request.getPostContent() == null
                || request.getPostContent().length() <= 0
                || request.getPostContent().contains("(욕설 검사할 예정)")) {
            throw new BusinessException(ErrorCode.BAD_REQUEST_PARAM, Map.of(
                    "resource", "postContent",
                    "action", "create",
                    "reason", "내용이 없거나 부적절합니다."
            ));
        }
        request.setMemberPk(memberPk);

        return this.postMapper.createPost(request);
    }

    public PostDeleteResponse setPostDeletedTrueByUserDeleteRequest(Long postPk, Long memberPk) {
        Integer count = this.postMapper.checkPostByPk(postPk);

        if(count <= 0) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }

        if(!checkMemberPkOwnsPost(postPk, memberPk)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_REQUEST, Map.of(
                    "resource", "post",
                    "action", "update",
                    "reason", "글의 소유자가 아닙니다."
            ));
        }

        return this.postMapper.deletePostByPk(postPk);
    }

    // updatePost
    public PostUpdateRequest updatePost(
            Long postPk,
            PostUpdateRequest request,
            Long memberPk
    ) {
        // 만약 본인이 아닌 사용자가 수정하려한다면 반환
        if(!this.checkMemberPkOwnsPost(request.getPostPk(), memberPk)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_REQUEST, Map.of(
                    "resource", "post",
                    "action", "delete",
                    "reason", "글의 소유자가 아닙니다."
            ));
        }

        if(postMapper.checkPostByPk(postPk) <= 0) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND, Map.of(
                    "resource", "post",
                    "action", "select",
                    "reason", "글이 존재하지 않거나 삭제되었습니다."
            ));
        }
        request.setPostPk(postPk);
        return this.postMapper.updatePostTitleAndPostContentByPk(request);
    }

    public boolean checkMemberPkOwnsPost(Long postPk, Long memberPk) {
        return this.postMapper.checkPostByPostPkAndMemberPk(postPk, memberPk) >= 1;
    }
}
