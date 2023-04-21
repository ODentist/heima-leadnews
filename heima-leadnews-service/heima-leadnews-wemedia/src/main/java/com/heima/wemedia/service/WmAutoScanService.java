package com.heima.wemedia.service;

import com.heima.model.wemedia.pojos.WmNews;

public interface WmAutoScanService {

    /**
     *
     * 内容安全扫描
     *
     * @param wmNews
     */
    void autoScan(WmNews wmNews);
}
