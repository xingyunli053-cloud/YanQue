package com.yanque.modules.users.controller;

import com.yanque.commons.apires.PageResult;
import com.yanque.modules.users.pojo.vo.resvo.LoginRes;
import com.yanque.modules.users.pojo.vo.resvo.UserRes;
import com.yanque.modules.users.service.SysUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

class SysUserControllerTest {

    private SysUserService sysUserService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        sysUserService = mock(SysUserService.class);
        mockMvc = standaloneSetup(new SysUserController(sysUserService)).build();
    }

    @Test
    void loginEndpoint() throws Exception {
        when(sysUserService.login(any())).thenReturn(new LoginRes("token", "secret"));

        mockMvc.perform(post("/api/sysUser/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value("token"));
    }

    @Test
    void pageEndpoint() throws Exception {
        when(sysUserService.page(any()))
                .thenReturn(new PageResult<>(0L, 1, 10, List.of()));

        mockMvc.perform(get("/api/sysUser").param("pageNum", "1").param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.pageNum").value(1));
    }

    @Test
    void detailEndpoint() throws Exception {
        UserRes user = new UserRes();
        user.setId(10L);
        when(sysUserService.detail(10L)).thenReturn(user);

        mockMvc.perform(get("/api/sysUser/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(10));
    }

    @Test
    void createEndpoint() throws Exception {
        when(sysUserService.create(any())).thenReturn(10L);

        mockMvc.perform(post("/api/sysUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"new-user\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(10));
    }

    @Test
    void updateEndpoint() throws Exception {
        doNothing().when(sysUserService).update(eq(10L), any());

        mockMvc.perform(put("/api/sysUser/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nickname\":\"新昵称\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void deleteEndpoint() throws Exception {
        doNothing().when(sysUserService).delete(10L);

        mockMvc.perform(delete("/api/sysUser/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
