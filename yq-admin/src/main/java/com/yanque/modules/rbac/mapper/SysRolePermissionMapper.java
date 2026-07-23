package com.yanque.modules.rbac.mapper;

import com.yanque.modules.rbac.pojo.entity.SysRolePermissionEntity;
import com.yanque.modules.rbac.pojo.vo.reqvo.RolePermissionPageReq;
import com.yanque.modules.rbac.pojo.vo.resvo.RolePermissionRes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysRolePermissionMapper {
    SysRolePermissionEntity selectById(Long id);
    RolePermissionRes selectResById(Long id);
    List<RolePermissionRes> selectList(RolePermissionPageReq req);
    List<Long> selectPermissionIdsByRoleId(Long roleId);
    List<Long> selectUserIdsByPermissionId(Long permissionId);
    int countUnique(@Param("roleId") Long roleId,
                    @Param("permissionId") Long permissionId,
                    @Param("excludeId") Long excludeId);
    int insert(SysRolePermissionEntity entity);
    int batchInsert(@Param("roleId") Long roleId, @Param("permissionIds") List<Long> permissionIds);
    int updateById(SysRolePermissionEntity entity);
    int deleteById(Long id);
    int deleteByRoleId(Long roleId);
    int deleteByPermissionId(Long permissionId);
}
