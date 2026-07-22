package com.yanque.modules.rbac.controller;

import com.yanque.commons.apires.PageResult;
import com.yanque.modules.rbac.pojo.vo.resvo.PermissionRes;
import com.yanque.modules.rbac.pojo.vo.resvo.PermissionTreeRes;
import com.yanque.modules.rbac.service.SysPermissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
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

class SysPermissionControllerTest {
    private SysPermissionService service;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        service = mock(SysPermissionService.class);
        mockMvc = standaloneSetup(new SysPermissionController(service)).build();
    }

    @Test
    void pageAndTreeEndpoints() throws Exception {
        when(service.page(any())).thenReturn(new PageResult<>(0L, 1, 10, List.of()));
        PermissionTreeRes root = new PermissionTreeRes();
        root.setId(1L);
        root.setPermissionName("系统管理");
        when(service.tree(any())).thenReturn(List.of(root));
        mockMvc.perform(get("/api/sysPermission").param("permissionType", "MENU"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.total").value(0));
        mockMvc.perform(get("/api/sysPermission/tree"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1));
    }

    @Test
    void detailEndpoint() throws Exception {
        PermissionRes permission = new PermissionRes();
        permission.setId(5L);
        when(service.detail(5L)).thenReturn(permission);
        mockMvc.perform(get("/api/sysPermission/5"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(5));
    }

    @Test
    void createUpdateDeleteEndpoints() throws Exception {
        when(service.create(any())).thenReturn(12L);
        doNothing().when(service).update(org.mockito.ArgumentMatchers.eq(12L), any());
        doNothing().when(service).delete(12L);
        String createJson = "{\"parentId\":0,\"permissionCode\":\"system:test\","
                + "\"permissionName\":\"测试菜单\",\"permissionType\":\"MENU\",\"sortNum\":1}";
        mockMvc.perform(post("/api/sysPermission").contentType(MediaType.APPLICATION_JSON).content(createJson))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data").value(12));
        mockMvc.perform(put("/api/sysPermission/12").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"permissionName\":\"新名称\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/sysPermission/12")).andExpect(status().isOk());
    }
}
