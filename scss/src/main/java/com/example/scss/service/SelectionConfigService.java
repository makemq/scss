package com.example.scss.service;

import com.example.scss.entity.SelectionConfig;

public interface SelectionConfigService {
    //查询选课配置
    SelectionConfig getActiveConfig();
    //修改选课配置
    boolean updateConfig(SelectionConfig config);
}
