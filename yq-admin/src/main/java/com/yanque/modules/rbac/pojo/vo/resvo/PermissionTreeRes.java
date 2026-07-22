package com.yanque.modules.rbac.pojo.vo.resvo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/** 权限树节点。 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PermissionTreeRes extends PermissionRes {
    private List<PermissionTreeRes> children = new ArrayList<>();
}
