package com.example.scss.dto;

import lombok.Data;

@Data
public class StudyRequirementResponse {
    //四类课程是否满足学分要求
    private Boolean socialSatisfied;
    private Boolean naturalSatisfied;
    private Boolean artSatisfied;
    private Boolean ethnicSatisfied;
    //线下课程是否满足数量要求
    private Boolean offlineSatisfied;
    //尚未满足的课程类别
    private Integer unmetCategoryCount;
}
