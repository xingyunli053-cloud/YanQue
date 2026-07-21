package com.yanque.modules.users.mapper;


import com.yanque.modules.users.pojo.entity.SysUserEntity;
import com.yanque.modules.users.pojo.vo.resvo.LoginRes;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysUserMapper {

    SysUserEntity selectByUsername(String username);

    int insert(SysUserEntity user);

    SysUserEntity selectById(Long id);
}
