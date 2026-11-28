package com.example.scss.ai;

import com.example.scss.annotation.RequireRole;
import com.example.scss.config.Result;
import com.example.scss.dto.AiChatRequest;
import com.example.scss.dto.AiChatResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai/chat")
public class AiChatController {
    private final AiChatService aiChatService;
    public AiChatController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }
    @RequireRole("STUDENT")
    @PostMapping
    public Result<AiChatResponse> chat(
            @RequestBody AiChatRequest request,
            @RequestAttribute("studentId") Long studentId
    ) {
        if (request.getMessage() == null || request.getMessage().isBlank()) {
            return Result.error("问题不能为空");
        }
        if (request.getMessage().length() > 1000) {
            return Result.error("问题不能超过1000个字符");
        }
        AiChatResponse response = aiChatService.chat(studentId, request.getMessage());
        return Result.success(response);
    }
}
