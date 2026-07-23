package com.yanque.modules.campus.pojo.vo.resvo;

import lombok.Data;

import java.util.Date;

/** 返回给前端的校区信息。 */
@Data
public class CampusRes {
    /** 校区唯一标识。 */
    private Long id;
    /** 校区详细地点。 */
    private String campusLocation;
    /** 校区负责人姓名。 */
    private String managerName;
    /** 校区负责人手机号。 */
    private String managerPhone;
    /** 校区创建时间。 */
    private Date createdAt;
    /** 校区最后更新时间。 */
    private Date updatedAt;
}
