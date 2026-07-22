package com.yanque.modules.rbac.mapper;

import com.yanque.modules.rbac.pojo.entity.SysPermissionEntity;
import com.yanque.modules.rbac.pojo.vo.reqvo.PermissionPageReq;
import com.yanque.modules.rbac.pojo.vo.reqvo.PermissionTreeReq;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysPermissionMapper {
    SysPermissionEntity selectById(Long id);
    SysPermissionEntity selectByPermissionCode(String permissionCode);
    List<SysPermissionEntity> selectList(PermissionPageReq req);
    List<SysPermissionEntity> selectTreeList(PermissionTreeReq req);
    List<Long> selectExistingIds(@Param("ids") List<Long> ids);
    int countByParentId(Long parentId);
    int countDescendantByIdAndCandidate(@Param("id") Long id,
                                        @Param("candidateParentId") Long candidateParentId);
    int insert(SysPermissionEntity entity);
    int updateById(SysPermissionEntity entity);
    int deleteById(Long id);
}
