package com.yanque.modules.rbac.controller;

import com.yanque.commons.apires.PageResult;
import com.yanque.modules.rbac.pojo.vo.resvo.RoleRes;
import com.yanque.modules.rbac.service.SysRoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class SysRoleControllerTest {
    private SysRoleService service;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        service = mock(SysRoleService.class);
        mockMvc = standaloneSetup(new SysRoleController(service)).build();
    }

    @Test
    void pageEndpoint() throws Exception {
        when(service.page(any())).thenReturn(new PageResult<>(0L, 1, 10, List.of()));
        mockMvc.perform(get("/api/sysRole").param("roleName", "管理员"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.pageNum").value(1));
    }

    @Test
    void detailEndpointContainsPermissionIds() throws Exception {
        RoleRes role = new RoleRes();
        role.setId(1L);
        role.setPermissionIds(List.of(1L, 2L));
        when(service.detail(1L)).thenReturn(role);
        mockMvc.perform(get("/api/sysRole/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.permissionIds[1]").value(2));
    }

    @Test
    void createUpdateDeleteEndpoints() throws Exception {
        when(service.create(any())).thenReturn(11L);
        doNothing().when(service).update(org.mockito.ArgumentMatchers.eq(11L), any());
        doNothing().when(service).delete(11L);
        mockMvc.perform(post("/api/sysRole").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"roleCode\":\"TEACHER\",\"roleName\":\"教师\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data").value(11));
        mockMvc.perform(put("/api/sysRole/11").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"roleName\":\"授课教师\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/sysRole/11")).andExpect(status().isOk());
    }

    @Test
    void assignPermissionsEndpoint() throws Exception {
        doNothing().when(service).assignPermissions(org.mockito.ArgumentMatchers.eq(1L), anyList());
        mockMvc.perform(put("/api/sysRole/1/permissions").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"permissionIds\":[1,2,3]}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
    }
}
