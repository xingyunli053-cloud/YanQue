package com.yanque.modules.rbac.mapper;

import com.yanque.modules.rbac.pojo.entity.SysUserRoleEntity;
import com.yanque.modules.rbac.pojo.vo.reqvo.UserRolePageReq;
import com.yanque.modules.rbac.pojo.vo.resvo.UserRoleRes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysUserRoleMapper {
    SysUserRoleEntity selectById(Long id);
    UserRoleRes selectResById(Long id);
    List<UserRoleRes> selectList(UserRolePageReq req);
    List<Long> selectRoleIdsByUserId(Long userId);
    List<Long> selectUserIdsByRoleId(Long roleId);
    int countUnique(@Param("userId") Long userId,
                    @Param("roleId") Long roleId,
                    @Param("excludeId") Long excludeId);
    int insert(SysUserRoleEntity entity);
    int batchInsert(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);
    int updateById(SysUserRoleEntity entity);
    int deleteById(Long id);
    int deleteByRoleId(Long roleId);
    int deleteByUserId(Long userId);
}
