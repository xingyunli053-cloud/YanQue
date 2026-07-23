package com.yanque.modules.course.pojo.vo.reqvo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** 新增、修改课程阶段详情的请求体；必填规则由所属课程类型在服务层决定。 */
@Data
public class CourseDetailSaveReq {
    /** 课程阶段名称，线上、线下课程均必填。 */
    @Size(max = 128, message = "阶段名称不能超过128个字符")
    private String stageName;

    /** 线下课程第几天；线上课程将由服务层置空。 */
    @Min(value = 1, message = "第几天必须大于0")
    private Integer dayNumber;

    /** 线下课程当天上课内容；线上课程将由服务层置空。 */
    @Size(max = 1000, message = "上课内容不能超过1000个字符")
    private String classContent;
}
