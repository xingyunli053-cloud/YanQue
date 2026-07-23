package com.yanque.modules.course.pojo.vo.reqvo;

import com.yanque.commons.validation.EnumValue;
import com.yanque.modules.course.enums.TeachingModeEnum;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** 新增、修改课程主表的请求体。 */
@Data
public class CourseSaveReq {
    /** 课程名称。 */
    @NotBlank(message = "课程名称不能为空")
    @Size(max = 128, message = "课程名称不能超过128个字符")
    private String courseName;

    /** 课程总天数，由管理员手动维护。 */
    @NotNull(message = "课程天数不能为空")
    @Min(value = 0, message = "课程天数不能小于0")
    private Integer courseDays;

    /** 上课方式，只能为 ONLINE 或 OFFLINE。 */
    @NotNull(message = "上课方式不能为空")
    @EnumValue(enumClass = TeachingModeEnum.class, message = "上课方式只能是ONLINE或OFFLINE")
    private String teachingMode;

    /** 课程资料访问或存储路径。 */
    @NotBlank(message = "资料路径不能为空")
    @Size(max = 500, message = "资料路径不能超过500个字符")
    private String materialPath;
}
