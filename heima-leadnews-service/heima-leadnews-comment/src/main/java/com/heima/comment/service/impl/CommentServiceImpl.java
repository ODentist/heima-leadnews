package com.heima.comment.service.impl;

import com.heima.apis.user.IUserClient;
import com.heima.comment.pojos.ApComment;
import com.heima.comment.pojos.ApCommentLike;
import com.heima.comment.pojos.CommentVo;
import com.heima.comment.service.CommentService;
import com.heima.model.comment.dtos.CommentDto;
import com.heima.model.comment.dtos.CommentLikeDto;
import com.heima.model.comment.dtos.CommentSaveDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.pojos.ApUser;
import com.heima.utils.common.UserIdThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    @Autowired
    private IUserClient userClient;

    @Autowired
    private MongoTemplate mongoTemplate;


    public final static  int DEFAULT_SIZE = 10;

    @Override
    public ResponseResult saveComment(CommentSaveDto dto) {
        //1.检查参数
        if (StringUtils.isBlank(dto.getContent()) || dto.getArticleId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        if (dto.getContent().length() > 140) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "评论内容不能超过140字");
        }

        //2.判断是否登录
        Integer userId = UserIdThreadLocalUtil.getUserId();
        if (userId == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        //3.保存评论
        ApUser dbUser = userClient.findUserById(userId);
        if (dbUser == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "当前登录信息有误");
        }
        ApComment apComment = new ApComment();
        apComment.setAuthorId(userId);
        apComment.setContent(dto.getContent());
        apComment.setCreatedTime(new Date());
        apComment.setEntryId(dto.getArticleId());
        apComment.setImage(dbUser.getImage());
        apComment.setAuthorName(dbUser.getName());
        apComment.setLikes(0);
        apComment.setReply(0);
        apComment.setType((short) 0);
        apComment.setFlag((short) 0);
        mongoTemplate.save(apComment);

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }


    @Override
    public ResponseResult like(CommentLikeDto dto) {
        //1.检查参数
        if (dto == null || dto.getCommentId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //2.判断是否登录
        Integer userId = UserIdThreadLocalUtil.getUserId();
        if (userId == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        ApComment apComment = mongoTemplate.findById(dto.getCommentId(), ApComment.class);

        //3.点赞
        if (dto.getOperation() == 0) {
            //更新评论点赞数量
            apComment.setLikes(apComment.getLikes() + 1);
            mongoTemplate.save(apComment);

            //保存评论点赞数据
            ApCommentLike apCommentLike = new ApCommentLike();
            apCommentLike.setCommentId(apComment.getId());
            apCommentLike.setAuthorId(userId);
            mongoTemplate.save(apCommentLike);
        } else {
            //更新评论点赞数量
            int tmp = apComment.getLikes() - 1;
            apComment.setLikes(tmp < 1 ? 0 : tmp);
            mongoTemplate.save(apComment);

            //删除评论点赞
            Query query = Query.query(Criteria.where("commentId").is(apComment.getId()).and("authorId").is(userId));
            mongoTemplate.remove(query, ApCommentLike.class);
        }

        //4.取消点赞
        Map<String, Object> result = new HashMap<>();
        result.put("likes", apComment.getLikes());
        return ResponseResult.okResult(result);
    }

    @Override
    public ResponseResult findByArticleId(CommentDto dto) {
        //1.检查参数
        if (dto == null || dto.getArticleId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //2.加载数据
        Query query = Query.query(Criteria.where("entryId").is(dto.getArticleId()).and("createdTime").lt(dto.getMinDate()));
        query.with(Sort.by(Sort.Direction.DESC, "createdTime")).limit(DEFAULT_SIZE);
        List<ApComment> commentList = mongoTemplate.find(query, ApComment.class);

        //3.数据封装返回
        //3.1 用户未登录
        Integer userId = UserIdThreadLocalUtil.getUserId();
        if (userId == null) {
            return ResponseResult.okResult(commentList);
        }

        //3.2 用户已登录

        //需要查询当前评论中哪些数据被点赞了
        List<String> idList = commentList.stream().map(x -> x.getId()).collect(Collectors.toList());
        Query query1 = Query.query(Criteria.where("commentId").in(idList).and("authorId").is(userId));
        List<ApCommentLike> apCommentLikes = mongoTemplate.find(query1, ApCommentLike.class);
        if (CollectionUtils.isEmpty(apCommentLikes)) {
            return ResponseResult.okResult(commentList);
        }

        List<CommentVo> resultList = new ArrayList<>();
        commentList.forEach(x -> {
            CommentVo vo = new CommentVo();
            BeanUtils.copyProperties(x, vo);
            for (ApCommentLike apCommentLike : apCommentLikes) {
                if (x.getId().equals(apCommentLike.getCommentId())) {
                    vo.setOperation((short) 0);
                    break;
                }
            }
            resultList.add(vo);
        });

        return ResponseResult.okResult(resultList);
    }
}
