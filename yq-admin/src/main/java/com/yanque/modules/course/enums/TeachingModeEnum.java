package com.yanque.modules.course.enums;

/** 课程上课方式，与 sys_course.teaching_mode 保持一致。 */
public enum TeachingModeEnum {
    /** 线上课程：详情只维护阶段名称。 */
    ONLINE,
    /** 线下课程：详情必须维护天数与上课内容。 */
    OFFLINE
}
