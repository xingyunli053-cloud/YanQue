package com.yanque.modules.rbac.controller;

import com.yanque.commons.apires.PageResult;
import com.yanque.modules.rbac.pojo.vo.resvo.RolePermissionRes;
import com.yanque.modules.rbac.pojo.vo.resvo.UserRoleRes;
import com.yanque.modules.rbac.service.SysRolePermissionService;
import com.yanque.modules.rbac.service.SysUserRoleService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class SysRelationControllerTest {

    @Test
    void rolePermissionPageAndCreateEndpoints() throws Exception {
        SysRolePermissionService service = mock(SysRolePermissionService.class);
        when(service.page(any())).thenReturn(new PageResult<>(0L, 1, 10, List.of()));
        when(service.create(any())).thenReturn(20L);
        MockMvc mockMvc = standaloneSetup(new SysRolePermissionController(service)).build();
        mockMvc.perform(get("/api/sysRolePermission").param("roleId", "1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.pageSize").value(10));
        mockMvc.perform(post("/api/sysRolePermission").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"roleId\":1,\"permissionId\":2}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data").value(20));
    }

    @Test
    void userRolePageAndCreateEndpoints() throws Exception {
        SysUserRoleService service = mock(SysUserRoleService.class);
        when(service.page(any())).thenReturn(new PageResult<>(0L, 1, 10, List.of()));
        when(service.create(any())).thenReturn(21L);
        MockMvc mockMvc = standaloneSetup(new SysUserRoleController(service)).build();
        mockMvc.perform(get("/api/sysUserRole").param("userId", "1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.pageNum").value(1));
        mockMvc.perform(post("/api/sysUserRole").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"roleId\":2}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data").value(21));
    }

    @Test
    void userRoleBulkQueryAndAssignEndpoints() throws Exception {
        SysUserRoleService service = mock(SysUserRoleService.class);
        when(service.getRoleIdsByUserId(1L)).thenReturn(List.of(1L, 2L));
        doNothing().when(service).assignRoles(org.mockito.ArgumentMatchers.eq(1L), any());
        MockMvc mockMvc = standaloneSetup(new SysUserRoleController(service)).build();

        mockMvc.perform(get("/api/sysUserRole/user/1/roles"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data[1]").value(2));
        mockMvc.perform(put("/api/sysUserRole/user/1/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"roleIds\":[1,2]}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
    }
}
