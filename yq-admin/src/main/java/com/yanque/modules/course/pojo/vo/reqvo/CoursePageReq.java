package com.yanque.modules.course.pojo.vo.reqvo;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** 课程关键词分页查询参数。 */
@Data
public class CoursePageReq {
    /** 当前页码，从 1 开始。 */
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNum = 1;

    /** 每页课程数量，最大 100。 */
    @Min(value = 1, message = "每页数量不能小于1")
    @Max(value = 100, message = "每页数量不能超过100")
    private Integer pageSize = 10;

    /** 按课程名称模糊匹配的关键字。 */
    @Size(max = 128, message = "关键词长度不能超过128个字符")
    private String keyword;
}
