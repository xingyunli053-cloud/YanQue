package com.yanque.modules.rbac.mapper;

import com.yanque.modules.rbac.pojo.entity.SysRoleEntity;
import com.yanque.modules.rbac.pojo.vo.reqvo.RolePageReq;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysRoleMapper {
    SysRoleEntity selectById(Long id);
    SysRoleEntity selectByRoleCode(String roleCode);
    List<Long> selectExistingIds(@Param("ids") List<Long> ids);
    List<SysRoleEntity> selectList(RolePageReq req);
    int insert(SysRoleEntity entity);
    int updateById(SysRoleEntity entity);
    int deleteById(Long id);
}
