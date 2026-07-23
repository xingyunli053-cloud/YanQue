package com.yanque.modules.course.mapper;

import com.yanque.modules.course.pojo.entity.SysCourseDetailEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/** sys_course_detail 表的数据访问接口。 */
@Mapper
public interface SysCourseDetailMapper {
    SysCourseDetailEntity selectById(Long id);

    List<SysCourseDetailEntity> selectByCourseId(Long courseId);

    int insert(SysCourseDetailEntity entity);

    int updateById(SysCourseDetailEntity entity);

    /** 仅统计线下课程详情中非空的天数。 */
    Integer sumDayNumbersByCourseId(Long courseId);

    /** 判断课程是否仍存在阶段详情，用于保护课程主表删除。 */
    int countByCourseId(Long courseId);

    /** 删除指定课程下全部阶段详情。 */
    int deleteByCourseId(Long courseId);

    /** 删除单条阶段详情。 */
    int deleteById(Long id);

    /** 覆盖式 Excel 导入时批量写入已校验的详情。 */
    int batchInsert(List<SysCourseDetailEntity> entities);
}
