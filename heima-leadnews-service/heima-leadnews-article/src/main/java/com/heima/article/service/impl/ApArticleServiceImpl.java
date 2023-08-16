package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
import com.heima.common.cache.CacheService;
import com.heima.common.constants.ApArticleConstants;
import com.heima.common.constants.BehaviorConstants;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.dtos.ArticleInfoDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.article.vo.HotArticleVo;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.pojos.ApUser;
import com.heima.utils.common.UserIdThreadLocalUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description TODO
 * @Author bo.li
 * @Date 2023/4/18 9:50
 * @Version 1.0
 */
@Service
public class ApArticleServiceImpl implements ApArticleService {

    @Autowired
    private ApArticleMapper articleMapper;

    @Autowired
    private ApArticleConfigMapper configMapper;
    @Autowired
    private ApArticleContentMapper contentMapper;
    @Autowired
    private CacheService cacheService;


    public final static int DEFAULT_SIZE = 10;

    @Override
    public ResponseResult loadArticle(ArticleHomeDto dto,Integer type) {
        Integer size = dto.getSize();
        if(size == null || size <= 0 || size > 10){
            size = DEFAULT_SIZE;
        }
        dto.setSize(size);
        if(dto.getMaxBehotTime() == null){
            dto.setMaxBehotTime(new Date());
        }
        if(dto.getMinBehotTime() != null){
            dto.setMinBehotTime(new Date());
        }

        if(StringUtils.isEmpty(dto.getTag())){
            dto.setTag("__all__");
        }

        List<ApArticle> articles = articleMapper.loadArticleList(dto,type);
        return ResponseResult.okResult(articles);
    }

    @Override
    @Transactional
    public ResponseResult save(ArticleDto articleDto) {
        //基本校验
        ApArticle article = new ApArticle();
        BeanUtils.copyProperties(articleDto,article);
        article.setPublishTime(new Date());
        if(article.getId() == null){
            articleMapper.insert(article);
            //保存配置表
            ApArticleConfig config = new ApArticleConfig(article.getId());
            configMapper.insert(config);


            ApArticleContent content = new ApArticleContent();
            content.setArticleId(article.getId());
            content.setContent(articleDto.getContent());
            contentMapper.insert(content);
        }else {
            articleMapper.updateById(article);

            ApArticleContent articleContent = contentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, articleDto.getId()));
            articleContent.setContent(articleDto.getContent());
            contentMapper.updateById(articleContent);
        }
        return ResponseResult.okResult(article.getId());
    }

    @Override
    public ResponseResult loadArticleBehavior(ArticleInfoDto dto) {

        //0.检查参数
        if (dto.getArticleId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //{ "isfollow": true, "islike": true,"isunlike": false,"iscollection": true }
        boolean isfollow = false, islike = false, isunlike = false, iscollection = false;

        Integer userId = UserIdThreadLocalUtil.getUserId();
        if(userId != null){
            //喜欢行为
            Object likeBehavior = cacheService.hGet(BehaviorConstants.LIKE_BEHAVIOR + dto.getArticleId().toString(), userId.toString());
            if(likeBehavior != null){
                islike = true;
            }
            //不喜欢的行为
            Object unLikeBehavior = cacheService.hGet(BehaviorConstants.UN_LIKE_BEHAVIOR + dto.getArticleId().toString(), userId.toString());
            if(unLikeBehavior != null){
                isunlike = true;
            }
            //是否收藏
            Object collctionBehavior = cacheService.hGet(BehaviorConstants.COLLECTION_BEHAVIOR+dto.getArticleId().toString(),userId.toString());
            if(collctionBehavior != null){
                iscollection = true;
            }
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("isfollow", isfollow);
        resultMap.put("islike", islike);
        resultMap.put("isunlike", isunlike);
        resultMap.put("iscollection", iscollection);

        return ResponseResult.okResult(resultMap);
    }


    /**
     * 加载文章列表
     * @param dto
     * @param type      1 加载更多   2 加载最新
     * @param firstPage true 是首页  false 不是首页
     * @return
     */
    @Override
    public ResponseResult loadArticleListV2(ArticleHomeDto dto, Integer type, boolean firstPage) {
        if(firstPage){
            //从缓存中获取数据
            String articleListStr = cacheService.get(ApArticleConstants.HOT_ARTICLE_FIRST_PAGE + dto.getTag());
            if(org.apache.commons.lang3.StringUtils.isNotBlank(articleListStr)){
                List<HotArticleVo> hotArticleVoList = JSON.parseArray(articleListStr, HotArticleVo.class);
                return ResponseResult.okResult(hotArticleVoList);
            }
        }
        return loadArticle(dto,type);
    }
}