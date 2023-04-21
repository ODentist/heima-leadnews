package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.constants.WeMediaConstants;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import com.heima.utils.common.UserIdThreadLocalUtil;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmNewsMaterialMapper;
import com.heima.wemedia.service.WmAutoScanService;
import com.heima.wemedia.service.WmNewsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description TODO
 * @Author bo.li
 * @Date 2023/4/20 10:47
 * @Version 1.0
 */
@Service
@Slf4j
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper,WmNews> implements WmNewsService {

    @Autowired
    private WmNewsMaterialMapper wmNewsMaterialMapper;
    @Autowired
    private WmMaterialMapper wmMaterialMapper;
    @Autowired
    private WmAutoScanService wmAutoScanService;

    @Override
    public ResponseResult queryNewsList(WmNewsPageReqDto reqDto) {
        //检查参数
        reqDto.checkParam();
        //构造分页对象
        IPage<WmNews> page = new Page<>(reqDto.getPage(), reqDto.getSize());
        //构造查询条件
        LambdaQueryWrapper<WmNews> queryWrapper = new LambdaQueryWrapper<>();
        //状态
        queryWrapper.eq(reqDto.getStatus() !=null,WmNews::getStatus,reqDto.getStatus());
        //开始结束时间
        if(reqDto.getBeginPubDate() != null && reqDto.getEndPubDate() != null){
            queryWrapper.between(WmNews::getCreatedTime,reqDto.getBeginPubDate(),reqDto.getEndPubDate());
        }
        //频道
        queryWrapper.eq(reqDto.getChannelId() !=null,WmNews::getChannelId,reqDto.getChannelId());
        //关键字
        queryWrapper.like(!StringUtils.isEmpty(reqDto.getKeyword()),WmNews::getTitle,reqDto.getKeyword());
        //userId
        queryWrapper.eq(WmNews::getUserId, UserIdThreadLocalUtil.getUserId());
        //排序
        queryWrapper.orderByDesc(WmNews::getCreatedTime);

        this.page(page,queryWrapper);

        ResponseResult result = new PageResponseResult(reqDto.getPage(), reqDto.getSize(), (int) page.getTotal());
        result.setData(page.getRecords());

        return result;
    }

    @Override
    public ResponseResult submit(WmNewsDto newsDto) {
        log.info("收到请求：{}",newsDto);
        //参数校验
        if(StringUtils.isEmpty(newsDto.getTitle()) || StringUtils.isEmpty(newsDto.getContent())){
            log.info("参数为空");
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        //保存文章
        WmNews wmNews = saveOrUpdate(newsDto);
        //如果是草稿
        if(newsDto.getStatus() == WmNews.Status.NORMAL.getCode()){
            log.info("保存为草稿，不再执行");
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }
        //提取文章内容中的图片
        List<String> images = getContentUrl(newsDto.getContent());
        //关联关系
        //关联正文图片和素材的关系
        saveRelationContent(images,wmNews.getId(),WeMediaConstants.REFERENCE_CONTENT);
        //关联封面图片和素材的关系
        saveRelationCover(newsDto,images,wmNews);

        //审核
        try {
            wmAutoScanService.autoScan(wmNews);
        }catch (Exception e){
            log.error("审核异常",e);
        }
        log.info("我要证明我先执行");

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 保存封面和素材的关系
     *
     * @param newsDto
     * @param images
     * @param wmNews
     */
    private void saveRelationCover(WmNewsDto newsDto, List<String> images, WmNews wmNews) {
        List<String> coverImages = newsDto.getImages();
        //封面图片为自动的时候，需要从正文图片中提取
        if(newsDto.getType() == WeMediaConstants.TYPE_AUTO){
            int size = images.size();
            if(size >= 3){
                wmNews.setType(WeMediaConstants.TYPE_MORE);
                coverImages = images.stream().limit(3).collect(Collectors.toList());
            }else if(size >= 1 && size < 3){
                wmNews.setType(WeMediaConstants.TYPE_SIGNAL);
                coverImages = images.stream().limit(1).collect(Collectors.toList());
            }else{
                wmNews.setType(WeMediaConstants.TYPE_NO);
            }
            if(!CollectionUtils.isEmpty(coverImages)){
                wmNews.setImages(StringUtils.collectionToCommaDelimitedString(coverImages));
            }
            updateById(wmNews);
        }

        if(!CollectionUtils.isEmpty(coverImages)) {
            saveRelationContent(coverImages, wmNews.getId(), WeMediaConstants.REFERENCE_COVER);
        }
    }


    private void saveRelationContent(List<String> images,Integer newsId,Integer type) {
        if(CollectionUtils.isEmpty(images)){
            return;
        }
        //通过URL批量去查询id
        LambdaQueryWrapper<WmMaterial> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(WmMaterial::getUrl,images);
        List<WmMaterial> wmMaterials = wmMaterialMapper.selectList(queryWrapper);

        List<Integer> ids = wmMaterials.stream().map(WmMaterial::getId).collect(Collectors.toList());
        wmNewsMaterialMapper.saveRelations(ids,newsId,type);
    }

    /**
     *
     * 提取正文中的图片
     * @param content
     * @return
     */
    private List<String> getContentUrl(String content) {
        List<String> images = new ArrayList<>();
        //[{"type":"text","value":"xxx"},{"type":"image","value":"http://xxx"}]
        List<Map> maps = JSONArray.parseArray(content, Map.class);
        for (Map map : maps) {
            String type = map.get("type") + "";
            if("image".equals(type)){
                images.add(map.get("value") + "");
            }
        }
        return images;
    }

    /**
     * 保存或者修改文章
     *
     * @param newsDto
     */
    @Transactional
    public WmNews saveOrUpdate(WmNewsDto newsDto) {
        WmNews wmNews = new WmNews();
        BeanUtils.copyProperties(newsDto,wmNews);

        //图片处理
        if(!CollectionUtils.isEmpty(newsDto.getImages())){
            //将集合转换为以逗号分隔的字符串
            String images = StringUtils.collectionToCommaDelimitedString(newsDto.getImages());
            wmNews.setImages(images);
        }
        //处理类型为自动的情况
        if(newsDto.getType() == WeMediaConstants.TYPE_AUTO){
            wmNews.setType(null);
        }
        //补充参数
        wmNews.setUserId(UserIdThreadLocalUtil.getUserId());
        wmNews.setCreatedTime(new Date());
        wmNews.setSubmitedTime(new Date());

        if(newsDto.getId() == null){
            save(wmNews);
        }else{
            //删除关联关系
            wmNewsMaterialMapper.delete(Wrappers.<WmNewsMaterial>lambdaQuery().eq(WmNewsMaterial::getNewsId,newsDto.getId()));
            //修改
            updateById(wmNews);
        }
        return wmNews;
    }
}