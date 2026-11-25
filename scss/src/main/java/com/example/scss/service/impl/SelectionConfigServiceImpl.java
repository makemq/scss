package com.example.scss.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.scss.entity.SelectionConfig;
import com.example.scss.mapper.SelectionConfigMapper;
import com.example.scss.service.SelectionConfigService;
import org.springframework.stereotype.Service;

@Service
public class SelectionConfigServiceImpl implements SelectionConfigService {
    private final SelectionConfigMapper selectionConfigMapper;
    public SelectionConfigServiceImpl(SelectionConfigMapper selectionConfigMapper) {
        this.selectionConfigMapper = selectionConfigMapper;
    }
    //查询选课配置
    @Override
    public SelectionConfig getActiveConfig() {
        LambdaQueryWrapper<SelectionConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SelectionConfig::getIsActive,1).orderByDesc(SelectionConfig::getId).last("LIMIT 1");
        return selectionConfigMapper.selectOne(wrapper);
    }
    //修改选课配置
    @Override
    public boolean updateConfig(SelectionConfig config) {
        //检查配置对象
        if(config == null|| config.getId() == null){
            return false;
        }
        if(config.getSemester() == null || config.getSemester().trim().isEmpty()){
            return false;
        }
        if(config.getStartTime() == null || config.getEndTime() == null){
            return false;
        }
        if(!config.getEndTime().isAfter(config.getStartTime())){
            return false;
        }
        return selectionConfigMapper.updateById(config)>0;
    }
}
