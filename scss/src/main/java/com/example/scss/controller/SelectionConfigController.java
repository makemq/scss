package com.example.scss.controller;
import com.example.scss.annotation.RequireRole;
import com.example.scss.config.Result;
import com.example.scss.entity.SelectionConfig;
import com.example.scss.service.SelectionConfigService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/selection-config")
public class SelectionConfigController {
    private final SelectionConfigService selectionConfigService;
    public SelectionConfigController(SelectionConfigService selectionConfigService) {
        this.selectionConfigService = selectionConfigService;
    }
    //查询选课配置
    @GetMapping("/active")
    public Result<SelectionConfig> getActiveConfig(){
        SelectionConfig config = selectionConfigService.getActiveConfig();
        if(config == null){
            return Result.error("暂无启用的选课配置");
        }
        return  Result.success(config);
    }
    @RequireRole("ADMIN")
    @PutMapping("/update")
    public Result<String> updateConfig(@RequestBody SelectionConfig config){
        boolean success = selectionConfigService.updateConfig(config);
        if(!success){
            return Result.error("选课配置修改失败，请检查参数");
        }
        return Result.success("选课配置修改成功");
    }
}
