package com.yanque.modules.course.pojo.entity;

import lombok.Data;

import java.util.Date;

/** 课程主表实体，对应 sys_course。 */
@Data
public class SysCourseEntity {
    /** 课程唯一标识。 */
    private Long id;
    /** 课程名称。 */
    private String courseName;
    /** 课程总天数，由课程新增或修改请求维护。 */
    private Integer courseDays;
    /** 上课方式：ONLINE 线上，OFFLINE 线下。 */
    private String teachingMode;
    /** 课程资料访问或存储路径。 */
    private String materialPath;
    /** 课程创建时间。 */
    private Date createdAt;
    /** 课程最后更新时间。 */
    private Date updatedAt;
}
