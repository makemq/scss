package com.example.scss.ai;

import com.example.scss.dto.AiChatResponse;

public interface AiChatService {
    AiChatResponse chat(Long stduentId,String message);
}
