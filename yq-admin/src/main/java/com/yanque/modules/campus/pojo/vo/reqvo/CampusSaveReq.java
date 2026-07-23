package com.yanque.modules.campus.pojo.vo.reqvo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** 新增、修改校区共用的请求体。 */
@Data
public class CampusSaveReq {
    /** 校区详细地点。 */
    @NotBlank(message = "校区地点不能为空")
    @Size(max = 255, message = "校区地点长度不能超过255个字符")
    private String campusLocation;

    /** 该校区负责人姓名。 */
    @NotBlank(message = "负责人不能为空")
    @Size(max = 64, message = "负责人长度不能超过64个字符")
    private String managerName;

    /** 负责人 11 位中国大陆手机号。 */
    @NotBlank(message = "负责人电话不能为空")
    @Size(max = 32, message = "负责人电话长度不能超过32个字符")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "负责人电话必须是11位中国大陆手机号")
    private String managerPhone;
}
