package com.yanque.modules.course.pojo.vo.resvo;

import lombok.Data;

import java.util.Date;

/** 返回给前端的课程阶段详情。 */
@Data
public class CourseDetailRes {
    /** 课程详情唯一标识。 */
    private Long id;
    /** 所属课程 ID。 */
    private Long courseId;
    /** 阶段名称，线上、线下课程均有值。 */
    private String stageName;
    /** 线下课程的第几天；线上课程返回 null。 */
    private Integer dayNumber;
    /** 线下课程当天上课内容；线上课程返回 null。 */
    private String classContent;
    /** 详情创建时间。 */
    private Date createdAt;
    /** 详情最后更新时间。 */
    private Date updatedAt;
}
