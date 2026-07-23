package com.yanque.modules.course.pojo.entity;

import lombok.Data;

import java.util.Date;

/** 课程阶段详情实体，对应 sys_course_detail。 */
@Data
public class SysCourseDetailEntity {
    /** 课程详情唯一标识。 */
    private Long id;
    /** 所属课程 ID。 */
    private Long courseId;
    /** 课程阶段名称；线上、线下课程均必填。 */
    private String stageName;
    /** 线下课程的第几天；线上课程为空。 */
    private Integer dayNumber;
    /** 线下课程当天的上课内容；线上课程为空。 */
    private String classContent;
    /** 详情创建时间。 */
    private Date createdAt;
    /** 详情最后更新时间。 */
    private Date updatedAt;
}
