package com.yanque.modules.users.mapper;


import com.yanque.modules.users.pojo.entity.SysUserEntity;
import com.yanque.modules.users.pojo.vo.reqvo.UserPageReq;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysUserMapper {

    SysUserEntity selectByUsername(String username);

    int insert(SysUserEntity user);

    SysUserEntity selectById(Long id);

    List<SysUserEntity> selectList(UserPageReq req);

    int updateById(SysUserEntity user);

    int deleteById(Long id);
}
