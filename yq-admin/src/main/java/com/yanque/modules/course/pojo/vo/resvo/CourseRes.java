package com.yanque.modules.course.pojo.vo.resvo;

import lombok.Data;

import java.util.Date;

/** 返回给前端的课程主表信息。 */
@Data
public class CourseRes {
    /** 课程唯一标识。 */
    private Long id;
    /** 课程名称。 */
    private String courseName;
    /** 课程总天数。 */
    private Integer courseDays;
    /** 上课方式：ONLINE 线上，OFFLINE 线下。 */
    private String teachingMode;
    /** 课程资料路径。 */
    private String materialPath;
    /** 创建时间。 */
    private Date createdAt;
    /** 最后更新时间。 */
    private Date updatedAt;
}
