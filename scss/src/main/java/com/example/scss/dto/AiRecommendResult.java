package com.example.scss.dto;

import lombok.Data;
import java.util.List;

@Data
public class AiRecommendResult {
    private String reason;
    private List<Long> courseIds;
}
