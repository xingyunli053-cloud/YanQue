package com.yanque.modules.campus.pojo.vo.reqvo;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** 校区分页查询参数。 */
@Data
public class CampusPageReq {
    /** 当前页码，从 1 开始。 */
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNum = 1;

    /** 每页返回的校区数量，最大 100。 */
    @Min(value = 1, message = "每页数量不能小于1")
    @Max(value = 100, message = "每页数量不能超过100")
    private Integer pageSize = 10;

    /** 同时匹配校区地点、负责人和负责人电话。 */
    @Size(max = 255, message = "关键词长度不能超过255个字符")
    private String keyword;
}
