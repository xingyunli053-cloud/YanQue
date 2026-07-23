package com.yanque.modules.course.controller;

import com.yanque.commons.apires.ApiResponse;
import com.yanque.commons.apires.PageResult;
import com.yanque.modules.course.pojo.vo.reqvo.CoursePageReq;
import com.yanque.modules.course.pojo.vo.reqvo.CourseDetailSaveReq;
import com.yanque.modules.course.pojo.vo.reqvo.CourseSaveReq;
import com.yanque.modules.course.pojo.vo.resvo.CourseDetailRes;
import com.yanque.modules.course.pojo.vo.resvo.CourseRes;
import com.yanque.modules.course.service.CourseService;
import com.yanque.modules.rbac.annotation.PermissionMeta;
import com.yanque.modules.rbac.annotation.PermissionMetas;
import com.yanque.modules.rbac.enums.PermissionTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 课程和阶段详情的查询接口。
 * 权限元数据会在启动时幂等写入 sys_permission，不会覆盖人工授权关系。
 */
@Tag(name = "课程管理")
@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
@PermissionMetas({
        @PermissionMeta(value = "teaching", name = "教学管理", type = PermissionTypeEnum.MENU, sort = 20),
        @PermissionMeta(value = "teaching:course", name = "课程管理", type = PermissionTypeEnum.MENU,
                parentCode = "teaching", sort = 2020)
})
public class CourseController {
    private final CourseService courseService;

    /** 按课程名称关键词分页查询课程主表。 */
    @Operation(summary = "分页查询课程")
    @GetMapping
    @PermissionMetas({
            @PermissionMeta(value = "api:course:page", name = "分页查询课程", type = PermissionTypeEnum.API,
                    parentCode = "teaching:course", sort = 2211),
            @PermissionMeta(value = "teaching:course:query", name = "查询课程", type = PermissionTypeEnum.BUTTON,
                    parentCode = "teaching:course", sort = 2021)
    })
    public ApiResponse<PageResult<CourseRes>> page(@Valid CoursePageReq req) {
        return ApiResponse.success(courseService.page(req));
    }

    /** 查询课程主表信息，不携带阶段明细。 */
    @Operation(summary = "查询课程详情")
    @GetMapping("/{id}")
    @PermissionMeta(value = "api:course:detail", name = "查询课程主表详情", type = PermissionTypeEnum.API,
            parentCode = "teaching:course", sort = 2212)
    public ApiResponse<CourseRes> detail(@PathVariable Long id) {
        return ApiResponse.success(courseService.detail(id));
    }

    /** 查询一个课程下的全部阶段；线上课程只返回阶段名称。 */
    @Operation(summary = "查询课程详情列表")
    @GetMapping("/{courseId}/details")
    @PermissionMeta(value = "api:course:details", name = "查询课程详情列表", type = PermissionTypeEnum.API,
            parentCode = "teaching:course", sort = 2213)
    public ApiResponse<List<CourseDetailRes>> detailList(@PathVariable Long courseId) {
        return ApiResponse.success(courseService.detailList(courseId));
    }

    /** 根据课程详情 ID 查询单条阶段明细。 */
    @Operation(summary = "查询单条课程详情")
    @GetMapping("/details/{id}")
    @PermissionMeta(value = "api:course:details:detail", name = "查询单条课程详情", type = PermissionTypeEnum.API,
            parentCode = "teaching:course", sort = 2214)
    public ApiResponse<CourseDetailRes> detailItem(@PathVariable Long id) {
        return ApiResponse.success(courseService.detailItem(id));
    }

    /** 新增课程主表；线下课程总天数初始由后续阶段详情自动汇总。 */
    @Operation(summary = "新增课程")
    @PostMapping
    @PermissionMetas({
            @PermissionMeta(value = "api:course:create", name = "新增课程接口", type = PermissionTypeEnum.API,
                    parentCode = "teaching:course", sort = 2215),
            @PermissionMeta(value = "teaching:course:create", name = "新增课程", type = PermissionTypeEnum.BUTTON,
                    parentCode = "teaching:course", sort = 2022)
    })
    public ApiResponse<Long> create(@Valid @RequestBody CourseSaveReq req) {
        return ApiResponse.success(courseService.create(req));
    }

    /** 修改课程主表；线下课程总天数会使用当前详情的天数总和。 */
    @Operation(summary = "修改课程")
    @PutMapping("/{id}")
    @PermissionMetas({
            @PermissionMeta(value = "api:course:update", name = "修改课程接口", type = PermissionTypeEnum.API,
                    parentCode = "teaching:course", sort = 2216),
            @PermissionMeta(value = "teaching:course:update", name = "修改课程", type = PermissionTypeEnum.BUTTON,
                    parentCode = "teaching:course", sort = 2023)
    })
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody CourseSaveReq req) {
        courseService.update(id, req);
        return ApiResponse.success();
    }

    /** 为指定课程添加阶段详情，服务层按课程类型校验字段。 */
    @Operation(summary = "新增课程详情")
    @PostMapping("/{courseId}/details")
    @PermissionMetas({
            @PermissionMeta(value = "api:course:details:create", name = "新增课程详情接口", type = PermissionTypeEnum.API,
                    parentCode = "teaching:course", sort = 2217),
            @PermissionMeta(value = "teaching:course:details:create", name = "新增课程详情", type = PermissionTypeEnum.BUTTON,
                    parentCode = "teaching:course", sort = 2024)
    })
    public ApiResponse<Long> createDetail(@PathVariable Long courseId, @Valid @RequestBody CourseDetailSaveReq req) {
        return ApiResponse.success(courseService.createDetail(courseId, req));
    }

    /** 修改单条阶段详情，所属课程不可通过此接口变更。 */
    @Operation(summary = "修改课程详情")
    @PutMapping("/details/{id}")
    @PermissionMetas({
            @PermissionMeta(value = "api:course:details:update", name = "修改课程详情接口", type = PermissionTypeEnum.API,
                    parentCode = "teaching:course", sort = 2218),
            @PermissionMeta(value = "teaching:course:details:update", name = "修改课程详情", type = PermissionTypeEnum.BUTTON,
                    parentCode = "teaching:course", sort = 2025)
    })
    public ApiResponse<Void> updateDetail(@PathVariable Long id, @Valid @RequestBody CourseDetailSaveReq req) {
        courseService.updateDetail(id, req);
        return ApiResponse.success();
    }

    /** 课程下无详情时才允许删除课程主表。 */
    @Operation(summary = "删除课程")
    @DeleteMapping("/{id}")
    @PermissionMetas({
            @PermissionMeta(value = "api:course:delete", name = "删除课程接口", type = PermissionTypeEnum.API,
                    parentCode = "teaching:course", sort = 2219),
            @PermissionMeta(value = "teaching:course:delete", name = "删除课程", type = PermissionTypeEnum.BUTTON,
                    parentCode = "teaching:course", sort = 2026)
    })
    public ApiResponse<Void> delete(@PathVariable Long id) {
        courseService.delete(id);
        return ApiResponse.success();
    }

    /** 删除单条课程详情。 */
    @Operation(summary = "删除课程详情")
    @DeleteMapping("/details/{id}")
    @PermissionMetas({
            @PermissionMeta(value = "api:course:details:delete", name = "删除课程详情接口", type = PermissionTypeEnum.API,
                    parentCode = "teaching:course", sort = 2220),
            @PermissionMeta(value = "teaching:course:details:delete", name = "删除课程详情", type = PermissionTypeEnum.BUTTON,
                    parentCode = "teaching:course", sort = 2027)
    })
    public ApiResponse<Void> deleteDetail(@PathVariable Long id) {
        courseService.deleteDetail(id);
        return ApiResponse.success();
    }

    /** 按模板覆盖导入一个课程的全部阶段详情。 */
    @Operation(summary = "Excel导入课程详情")
    @PostMapping("/{courseId}/details/import")
    @PermissionMetas({
            @PermissionMeta(value = "api:course:details:import", name = "导入课程详情接口", type = PermissionTypeEnum.API,
                    parentCode = "teaching:course", sort = 2221),
            @PermissionMeta(value = "teaching:course:details:import", name = "导入课程详情", type = PermissionTypeEnum.BUTTON,
                    parentCode = "teaching:course", sort = 2028)
    })
    public ApiResponse<Integer> importDetails(@PathVariable Long courseId,
                                               @RequestParam("file") MultipartFile file) {
        return ApiResponse.success(courseService.importDetails(courseId, file));
    }
}
