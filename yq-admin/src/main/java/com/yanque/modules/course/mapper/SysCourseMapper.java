package com.yanque.modules.course.mapper;

import com.yanque.modules.course.pojo.entity.SysCourseEntity;
import com.yanque.modules.course.pojo.vo.reqvo.CoursePageReq;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/** sys_course 表的数据访问接口。 */
@Mapper
public interface SysCourseMapper {
    SysCourseEntity selectById(Long id);

    List<SysCourseEntity> selectList(CoursePageReq req);

    int insert(SysCourseEntity entity);

    int updateById(SysCourseEntity entity);

    /** 同步线下课程的详情天数总和。 */
    int updateCourseDays(Long id, Integer courseDays);

    /** 物理删除课程主表记录。 */
    int deleteById(Long id);
}
