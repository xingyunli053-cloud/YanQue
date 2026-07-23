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
    /** 查询用户拥有的启用角色编码，用于识别 SUPER_ADMIN。 */
    List<String> selectActiveRoleCodesByUserId(@Param("userId") Long userId);
    List<Long> selectExistingIds(@Param("ids") List<Long> ids);
    List<SysRoleEntity> selectList(RolePageReq req);
    int insert(SysRoleEntity entity);
    int updateById(SysRoleEntity entity);
    int deleteById(Long id);
}
