package com.yanque.modules.campus.controller;

import com.yanque.commons.apires.ApiResponse;
import com.yanque.commons.apires.PageResult;
import com.yanque.modules.campus.pojo.vo.reqvo.CampusPageReq;
import com.yanque.modules.campus.pojo.vo.reqvo.CampusSaveReq;
import com.yanque.modules.campus.pojo.vo.resvo.CampusRes;
import com.yanque.modules.campus.service.CampusService;
import com.yanque.modules.rbac.annotation.PermissionMeta;
import com.yanque.modules.rbac.annotation.PermissionMetas;
import com.yanque.modules.rbac.enums.PermissionTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 校区管理接口。
 * 类上的菜单元数据会在项目启动时幂等同步到 sys_permission；方法元数据用于 API 鉴权。
 */
@Tag(name = "校区管理")
@RestController
@RequestMapping("/api/campus")
@RequiredArgsConstructor
@PermissionMetas({
        @PermissionMeta(value = "teaching", name = "教学管理", type = PermissionTypeEnum.MENU, sort = 20),
        @PermissionMeta(value = "teaching:campus", name = "校区管理", type = PermissionTypeEnum.MENU,
                parentCode = "teaching", sort = 2010)
})
public class CampusController {
    private final CampusService campusService;

    /** 按地点、负责人或负责人电话进行关键词分页查询。 */
    @Operation(summary = "分页查询校区")
    @GetMapping
    @PermissionMetas({
            @PermissionMeta(value = "api:campus:page", name = "分页查询校区", type = PermissionTypeEnum.API,
                    parentCode = "teaching:campus", sort = 2111),
            @PermissionMeta(value = "teaching:campus:query", name = "查询校区", type = PermissionTypeEnum.BUTTON,
                    parentCode = "teaching:campus", sort = 2011)
    })
    public ApiResponse<PageResult<CampusRes>> page(@Valid CampusPageReq req) {
        return ApiResponse.success(campusService.page(req));
    }

    /** 根据校区 ID 查询详情。 */
    @Operation(summary = "查询校区详情")
    @GetMapping("/{id}")
    @PermissionMeta(value = "api:campus:detail", name = "查询校区详情", type = PermissionTypeEnum.API,
            parentCode = "teaching:campus", sort = 2112)
    public ApiResponse<CampusRes> detail(@PathVariable Long id) {
        return ApiResponse.success(campusService.detail(id));
    }

    /** 校验必填字段后创建校区。 */
    @Operation(summary = "新增校区")
    @PostMapping
    @PermissionMetas({
            @PermissionMeta(value = "api:campus:create", name = "新增校区接口", type = PermissionTypeEnum.API,
                    parentCode = "teaching:campus", sort = 2113),
            @PermissionMeta(value = "teaching:campus:create", name = "新增校区", type = PermissionTypeEnum.BUTTON,
                    parentCode = "teaching:campus", sort = 2012)
    })
    public ApiResponse<Long> create(@Valid @RequestBody CampusSaveReq req) {
        return ApiResponse.success(campusService.create(req));
    }

    /** 用路径参数指定的校区 ID 更新信息。 */
    @Operation(summary = "修改校区")
    @PutMapping("/{id}")
    @PermissionMetas({
            @PermissionMeta(value = "api:campus:update", name = "修改校区接口", type = PermissionTypeEnum.API,
                    parentCode = "teaching:campus", sort = 2114),
            @PermissionMeta(value = "teaching:campus:update", name = "修改校区", type = PermissionTypeEnum.BUTTON,
                    parentCode = "teaching:campus", sort = 2013)
    })
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody CampusSaveReq req) {
        campusService.update(id, req);
        return ApiResponse.success();
    }

    /** 校区下存在班级时，服务层会拒绝删除。 */
    @Operation(summary = "删除校区")
    @DeleteMapping("/{id}")
    @PermissionMetas({
            @PermissionMeta(value = "api:campus:delete", name = "删除校区接口", type = PermissionTypeEnum.API,
                    parentCode = "teaching:campus", sort = 2115),
            @PermissionMeta(value = "teaching:campus:delete", name = "删除校区", type = PermissionTypeEnum.BUTTON,
                    parentCode = "teaching:campus", sort = 2014)
    })
    public ApiResponse<Void> delete(@PathVariable Long id) {
        campusService.delete(id);
        return ApiResponse.success();
    }
}
