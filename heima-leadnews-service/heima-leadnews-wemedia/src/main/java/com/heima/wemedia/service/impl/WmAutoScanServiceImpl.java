package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.apis.article.IArticleClient;
import com.heima.common.aliyun.GreenImageScan;
import com.heima.common.aliyun.GreenTextScan;
import com.heima.common.tess4j.Tess4jClient;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmSensitive;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.common.SensitiveWordUtil;
import com.heima.wemedia.mapper.WmChannelMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmSensitiveMapper;
import com.heima.wemedia.mapper.WmUserMapper;
import com.heima.wemedia.service.WmAutoScanService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @Description TODO
 * @Author bo.li
 * @Date 2023/4/21 10:33
 * @Version 1.0
 */
@Service
@Slf4j
public class WmAutoScanServiceImpl implements WmAutoScanService {

    @Autowired
    private GreenTextScan greenTextScan;
    @Autowired
    private GreenImageScan greenImageScan;

    @Autowired
    private WmNewsMapper wmNewsMapper;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private IArticleClient articleClient;
    @Autowired
    private WmUserMapper wmUserMapper;
    @Autowired
    private WmChannelMapper wmChannelMapper;
    @Autowired
    private WmSensitiveMapper wmSensitiveMapper;
    @Autowired
    private Tess4jClient tess4jClient;

    @Override
    @Async
    @GlobalTransactional
    public void autoScan(WmNews wmNews) {
        log.info("开始进行内容安全检测");
        if(wmNews.getStatus() != WmNews.Status.SUBMIT.getCode()){
            log.info("文章状态不是审核，不走后续流程");
            return;
        }
        //提取文本和图片
        Map<String, Object> contentAndImage = getContentAndImage(wmNews);
        //自定义敏感词审核
        boolean flag = scanSensitive(wmNews,contentAndImage.get("content") + "");
        if(!flag){
            log.info("文章 {} 存在敏感词，不走后续流程",wmNews.getId());
            return;
        }
        //审核文本
        flag = textScan(contentAndImage.get("content") + "", wmNews);
        if(!flag){
            log.info("文章 {}文本审核未通过，不走后续流程",wmNews.getId());
            return;
        }
        //审核图片
        flag = imageScan((List<String>) contentAndImage.get("images"), wmNews);
        if(!flag){
            log.info("文章 {}图片审核未通过，不走后续流程",wmNews.getId());
            return;
        }
        //保存到APP端
        ResponseResult result = saveToApp(wmNews);
        log.info("调用文章微服务返回：{}", JSON.toJSONString(result));
        if(result.getCode() != AppHttpCodeEnum.SUCCESS.getCode()){
            log.info("同步失败");
            return;
        }
        //更新数据
        Long articleId = (Long)result.getData();
        wmNews.setArticleId(articleId);
        wmNews.setStatus(WmNews.Status.PUBLISHED.getCode());
        wmNewsMapper.updateById(wmNews);
        //发消息
    }

    /**
     *
     * 自定义敏感词审核
     * @param wmNews
     */
    private boolean scanSensitive(WmNews wmNews,String content) {
        boolean flag = true;
        //从数据库查询敏感词
        List<WmSensitive> wmSensitives = wmSensitiveMapper.selectList(Wrappers.emptyWrapper());
        if(CollectionUtils.isEmpty(wmSensitives)){
            return flag;
        }
        List<String> sensitveList = wmSensitives.stream().map(WmSensitive::getSensitives).collect(Collectors.toList());
        SensitiveWordUtil.initMap(sensitveList);
        Map<String, Integer> result = SensitiveWordUtil.matchWords(content);
        if(!CollectionUtils.isEmpty(result)){
            log.info("敏感词审核未通过：{}",result);
            wmNews.setStatus(WmNews.Status.FAIL.getCode());
            wmNews.setReason("文章中存在违规内容：" + result);
            wmNewsMapper.updateById(wmNews);
            flag = false;
        }
        return flag;
    }

    private ResponseResult saveToApp(WmNews wmNews) {
        ArticleDto articleDto = new ArticleDto();
        BeanUtils.copyProperties(wmNews,articleDto);
        articleDto.setLayout(wmNews.getType());
        articleDto.setAuthorId(wmNews.getUserId());
        //设置用户
        WmUser user = wmUserMapper.selectById(wmNews.getUserId());
        articleDto.setAuthorName(user == null ? ""  : user.getNickname());

        WmChannel channel = wmChannelMapper.selectById(wmNews.getChannelId());
        articleDto.setChannelName(channel == null ? "" : channel.getName());

        if(wmNews.getArticleId() != null){
            articleDto.setId(wmNews.getArticleId());
        }

        ResponseResult result = articleClient.save(articleDto);

        return result;
    }

    /**
     *
     * 图片审核
     * @param images
     * @param wmNews
     */
    private boolean imageScan(List<String> images, WmNews wmNews) {
        boolean flag = true;
        if(CollectionUtils.isEmpty(images)){
            log.info("没有图片");
            return flag;
        }
        try{
            List<byte[]> imageList = new ArrayList<>();
            for (String image : images) {
                byte[] bytes = fileStorageService.downLoadFile(image);
                //识别图片中文字，进行敏感词审核
                String picText = tess4jClient.doOCR(ImageIO.read(new ByteArrayInputStream(bytes)));
                boolean sensitiveFlag = scanSensitive(wmNews, picText);
                if(!sensitiveFlag){
                    return false;
                }
                imageList.add(bytes);
            }
            Map map = greenImageScan.imageScan(imageList);
            return dealResult(map,wmNews);
        }catch (Exception e){
            log.error("图片审核异常",e);
            flag = false;
        }
        return flag;
    }

    private  boolean dealResult(Map map,WmNews wmNews){
        boolean flag = true;
        log.info("调用阿里云审核返回:{}",map);
        if(CollectionUtils.isEmpty(map)){
            return false;
        }
        String suggestion = map.get("suggestion") + "";
        String label = map.get("label") + "";
        //未通过
        if("block".equals(suggestion)){
            flag = false;
            wmNews.setStatus(WmNews.Status.FAIL.getCode());
            wmNews.setReason("文章中存在违规内容：" + label);
            wmNewsMapper.updateById(wmNews);
        }else if("review".equals(suggestion)){
            flag = false;
            wmNews.setStatus(WmNews.Status.FAIL.getCode());
            wmNews.setReason("文章中存在不确定内容：" + label);
            wmNewsMapper.updateById(wmNews);
        }
        return flag;
    }

    /**
     * 文本审核
     *
     * @param content
     */
    private boolean textScan(String content,WmNews wmNews) {
        boolean flag = true;
        try {
            Map map = greenTextScan.greeTextScan(content);
            return dealResult(map,wmNews);
        }catch (Exception e){
            flag = false;
            log.error("阿里云文本审核异常",e);
        }
        return flag;
    }

    /**
     *
     * 获取图片和文本
     * @param wmNews
     */
    private Map<String,Object> getContentAndImage(WmNews wmNews) {

        Map<String,Object> resultMap = new HashMap<>();

        StringBuilder stringBuilder = new StringBuilder();
        String content = wmNews.getContent();
        //图片
        List<String> images = new ArrayList<>();

        List<Map> contentList = JSONArray.parseArray(content, Map.class);
        for (Map map : contentList) {
            String type = map.get("type") + "";
            String value = map.get("value") + "";
            if("text".equals(type)){
                stringBuilder.append(value);
            }else if("image".equals(type)){
                images.add(value);
            }
        }
        //处理封面图片 1,2,3
        String coverImages = wmNews.getImages();
        if(!StringUtils.isEmpty(coverImages)) {
            String[] imgs = StringUtils.commaDelimitedListToStringArray(coverImages);
            images.addAll(Arrays.asList(imgs));
        }
        images.stream().distinct().collect(Collectors.toList());

        //处理标题
        stringBuilder.append("-").append(wmNews.getTitle());

        resultMap.put("content",stringBuilder.toString());
        resultMap.put("images",images);

        return resultMap;
    }
}