package com.yanque.modules.rbac.pojo.vo.resvo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/** 权限树节点。 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PermissionTreeRes extends PermissionRes {
    /** 当前权限节点的直接子节点。 */
    private List<PermissionTreeRes> children = new ArrayList<>();
}
