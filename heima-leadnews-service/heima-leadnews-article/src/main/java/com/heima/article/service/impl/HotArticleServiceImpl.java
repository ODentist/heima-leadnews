package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.apis.wemedia.IWemediaClient;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.HotArticleService;
import com.heima.common.cache.CacheService;
import com.heima.common.constants.ApArticleConstants;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.article.vo.HotArticleVo;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmChannel;
import io.swagger.models.auth.In;
import org.checkerframework.checker.units.qual.A;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: ODENTIST
 * @Date: 2023/06/20/17:20
 * @Description:
 */
@Service
public class HotArticleServiceImpl implements HotArticleService {
    @Autowired
    ApArticleMapper apArticleMapper;
    @Autowired
    CacheService cacheService;
    @Autowired
    IWemediaClient wemediaClient;
    @Override
    public void computeHotArticle() {
        //查前五天的文章数据
        Date dateParam = DateTime.now().minusDays(5).toDate();
        List<ApArticle> articleList =
                apArticleMapper.findArticleBy5days(dateParam);
        //计算文章分值
        List<HotArticleVo> hotArticleVos=computeHotArticleList(articleList);

        //为每个评到缓存30条分值较高的文章
        cacheTagToRedis(hotArticleVos);

    }

    /**
     * @Description: 把标签数据存入到redis
     * @Author: ODENTIST
     * @Date: 2023/6/20
     * @param hotArticleVos
     */
    private void cacheTagToRedis(List<HotArticleVo> hotArticleVos) {
        ResponseResult channels = wemediaClient.getChannels();
        String channelString = JSON.toJSONString(channels.getData());
        //转list
        List<WmChannel> wmChannels = JSON.parseArray(channelString, WmChannel.class);
        //检索出每个频道的文昭
        if (wmChannels!=null&&wmChannels.size()>0){
            for (WmChannel wmChannel : wmChannels) {
                List<HotArticleVo> hotChannelArticle = hotArticleVos.stream().filter(x -> x.getChannelId().equals(wmChannel.getId())).collect(Collectors.toList());
                sortToCache(hotArticleVos, hotChannelArticle, ApArticleConstants.HOT_ARTICLE_FIRST_PAGE + wmChannel.getId());

            }
        }
        //设置推荐数据
        //给文章进行排序，取30条较高的文章存入redis key：id  value：30条高分文章
        sortToCache(hotArticleVos, hotArticleVos, ApArticleConstants.HOT_ARTICLE_FIRST_PAGE+ApArticleConstants.DEFAULT_TAG);
    }

    private void sortToCache(List<HotArticleVo> hotArticleVos, List<HotArticleVo> hotChannelArticle, String key) {
        hotChannelArticle = hotChannelArticle.stream().sorted(Comparator.comparing(HotArticleVo::getScore).reversed()).collect(Collectors.toList());
        if (hotChannelArticle.size() > 30) {
            hotChannelArticle.subList(0, 30);
        }
        cacheService.set(key    , JSON.toJSONString(hotArticleVos));
    }

    private List<HotArticleVo> computeHotArticleList(List<ApArticle> articleList) {
        ArrayList<HotArticleVo> hotArticleVos = new ArrayList<>();
        if (articleList !=null && articleList.size()>0){
            for (ApArticle apArticle : articleList) {
                HotArticleVo hotArticleVo = new HotArticleVo();
                BeanUtils.copyProperties(apArticle,hotArticleVo);
                Integer score=compputeScore(apArticle);
                hotArticleVo.setScore(score);
                hotArticleVos.add(hotArticleVo);
            }
        }
        return hotArticleVos;
    }

    /**
     * @Description: 具体分值计算
     * @Author: ODENTIST
     * @Date: 2023/6/20
     * @param apArticle
     * @return
     */
    private Integer compputeScore(ApArticle apArticle){
        Integer score=0;
        if (apArticle.getLikes()!=null){
            score+=apArticle.getLikes()*3;
        }
        if (apArticle.getViews()!=null){
            score+=apArticle.getLikes()*1   ;
        }
        if (apArticle.getComment()!=null){
            score+=apArticle.getLikes()*5;
        }
        if (apArticle.getCollection()!=null){
            score+=apArticle.getLikes()*8;
        }
        return score;
    }
}
