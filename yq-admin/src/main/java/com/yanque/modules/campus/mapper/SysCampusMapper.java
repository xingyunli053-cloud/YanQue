package com.yanque.modules.campus.mapper;

import com.yanque.modules.campus.pojo.entity.SysCampusEntity;
import com.yanque.modules.campus.pojo.vo.reqvo.CampusPageReq;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/** sys_campus 表的数据访问接口。 */
@Mapper
public interface SysCampusMapper {
    SysCampusEntity selectById(Long id);

    /** 根据校区地点查询，用于保证一个地点只对应一个校区。 */
    SysCampusEntity selectByCampusLocation(String campusLocation);

    List<SysCampusEntity> selectList(CampusPageReq req);

    int insert(SysCampusEntity entity);

    int updateById(SysCampusEntity entity);

    int deleteById(Long id);

    /** 根据约定的 sys_class.campus_id 字段统计校区下的班级数。 */
    int countClassByCampusId(Long campusId);
}
