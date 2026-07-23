package com.yanque.modules.course.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yanque.commons.apires.CommonErrorCode;
import com.yanque.commons.apires.PageResult;
import com.yanque.commons.exception.BusinessException;
import com.yanque.modules.course.mapper.SysCourseDetailMapper;
import com.yanque.modules.course.mapper.SysCourseMapper;
import com.yanque.modules.course.listener.CourseDetailImportListener;
import com.yanque.modules.course.pojo.entity.SysCourseDetailEntity;
import com.yanque.modules.course.pojo.entity.SysCourseEntity;
import com.yanque.modules.course.pojo.excel.CourseDetailImportRow;
import com.yanque.modules.course.pojo.vo.reqvo.CoursePageReq;
import com.yanque.modules.course.pojo.vo.reqvo.CourseDetailSaveReq;
import com.yanque.modules.course.pojo.vo.reqvo.CourseSaveReq;
import com.yanque.modules.course.pojo.vo.resvo.CourseDetailRes;
import com.yanque.modules.course.pojo.vo.resvo.CourseRes;
import com.yanque.modules.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/** 课程查询服务实现。 */
@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final SysCourseMapper courseMapper;
    private final SysCourseDetailMapper courseDetailMapper;

    @Override
    public PageResult<CourseRes> page(CoursePageReq req) {
        // 去除用户输入的首尾空格，避免因复制粘贴导致查询不到课程。
        req.setKeyword(StrUtil.trim(req.getKeyword()));
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<SysCourseEntity> entities = courseMapper.selectList(req);
        PageInfo<SysCourseEntity> pageInfo = new PageInfo<>(entities);
        return new PageResult<>(pageInfo.getTotal(), req.getPageNum(), req.getPageSize(),
                entities.stream().map(this::toCourseRes).toList());
    }

    @Override
    public CourseRes detail(Long id) {
        return toCourseRes(getCourseOrThrow(id));
    }

    @Override
    public List<CourseDetailRes> detailList(Long courseId) {
        // 即使详情为空也必须先确认课程存在，防止不存在的课程被误判为“暂无详情”。
        getCourseOrThrow(courseId);
        return courseDetailMapper.selectByCourseId(courseId).stream().map(this::toCourseDetailRes).toList();
    }

    @Override
    public CourseDetailRes detailItem(Long id) {
        SysCourseDetailEntity entity = id == null ? null : courseDetailMapper.selectById(id);
        if (entity == null) throw BusinessException.of(CommonErrorCode.COURSE_DETAIL_NOT_FOUND);
        return toCourseDetailRes(entity);
    }

    @Override
    @Transactional
    public Long create(CourseSaveReq req) {
        SysCourseEntity entity = BeanUtil.copyProperties(req, SysCourseEntity.class);
        normalizeCourse(entity);
        if (courseMapper.insert(entity) != 1) {
            throw BusinessException.of(CommonErrorCode.COURSE_OPERATION_FAILED);
        }
        return entity.getId();
    }

    @Override
    @Transactional
    public void update(Long id, CourseSaveReq req) {
        getCourseOrThrow(id);
        SysCourseEntity entity = BeanUtil.copyProperties(req, SysCourseEntity.class);
        entity.setId(id);
        normalizeCourse(entity);
        if (courseMapper.updateById(entity) != 1) {
            throw BusinessException.of(CommonErrorCode.COURSE_OPERATION_FAILED);
        }
    }

    @Override
    @Transactional
    public Long createDetail(Long courseId, CourseDetailSaveReq req) {
        SysCourseEntity course = getCourseOrThrow(courseId);
        SysCourseDetailEntity entity = toDetailEntity(course, req);
        entity.setCourseId(courseId);
        if (courseDetailMapper.insert(entity) != 1) {
            throw BusinessException.of(CommonErrorCode.COURSE_OPERATION_FAILED);
        }
        return entity.getId();
    }

    @Override
    @Transactional
    public void updateDetail(Long id, CourseDetailSaveReq req) {
        SysCourseDetailEntity existing = id == null ? null : courseDetailMapper.selectById(id);
        if (existing == null) throw BusinessException.of(CommonErrorCode.COURSE_DETAIL_NOT_FOUND);
        SysCourseEntity course = getCourseOrThrow(existing.getCourseId());
        SysCourseDetailEntity entity = toDetailEntity(course, req);
        entity.setId(id);
        entity.setCourseId(existing.getCourseId());
        if (courseDetailMapper.updateById(entity) != 1) {
            throw BusinessException.of(CommonErrorCode.COURSE_OPERATION_FAILED);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        getCourseOrThrow(id);
        // 课程仍有详情时禁止删除，要求先逐条删除详情，避免误删教学内容。
        if (courseDetailMapper.countByCourseId(id) > 0) {
            throw BusinessException.of(CommonErrorCode.COURSE_HAS_DETAILS);
        }
        if (courseMapper.deleteById(id) != 1) {
            throw BusinessException.of(CommonErrorCode.COURSE_DELETE_FAILED);
        }
    }

    @Override
    @Transactional
    public void deleteDetail(Long id) {
        SysCourseDetailEntity detail = id == null ? null : courseDetailMapper.selectById(id);
        if (detail == null) throw BusinessException.of(CommonErrorCode.COURSE_DETAIL_NOT_FOUND);
        if (courseDetailMapper.deleteById(id) != 1) {
            throw BusinessException.of(CommonErrorCode.COURSE_DELETE_FAILED);
        }
        // 线下课程详情删除后，重新汇总主表课程天数。
    }

    @Override
    @Transactional
    public int importDetails(Long courseId, MultipartFile file) {
        SysCourseEntity course = getCourseOrThrow(courseId);
        if (file == null || file.isEmpty()) {
            throw BusinessException.of(CommonErrorCode.PARAM_VALID_FAILED, "导入文件不能为空");
        }
        String filename = file.getOriginalFilename();
        if (filename == null || !(filename.endsWith(".xlsx") || filename.endsWith(".xls"))) {
            throw BusinessException.of(CommonErrorCode.PARAM_VALID_FAILED, "仅支持Excel格式的导入文件");
        }

        CourseDetailImportListener listener = new CourseDetailImportListener();
        try {
            // 模板首行是表头；A-C 映射数据对象，D-F 填写说明列由监听器忽略。
            EasyExcel.read(file.getInputStream(), CourseDetailImportRow.class, listener).sheet().doRead();
        } catch (Exception exception) {
            if (exception instanceof BusinessException businessException) throw businessException;
            throw BusinessException.of(CommonErrorCode.PARAM_VALID_FAILED, "Excel文件无法读取，请使用导入模板");
        }
        validateImportHeaders(listener.getHeaders());
        List<CourseDetailImportRow> rows = listener.getRows();
        if (rows == null || rows.isEmpty()) {
            throw BusinessException.of(CommonErrorCode.PARAM_VALID_FAILED, "Excel中没有可导入的课程详情");
        }
        if (!isOffline(course)) {
            throw BusinessException.of(CommonErrorCode.PARAM_VALID_FAILED, "当前Excel模板仅支持线下课程导入");
        }

        List<SysCourseDetailEntity> entities = validateImportRows(course, courseId, rows);
        // 所有行校验通过后才覆盖旧数据；任一步失败由事务回滚，不会出现半导入状态。
        courseDetailMapper.deleteByCourseId(courseId);
        if (courseDetailMapper.batchInsert(entities) != entities.size()) {
            throw BusinessException.of(CommonErrorCode.COURSE_OPERATION_FAILED);
        }
        return entities.size();
    }

    private SysCourseEntity getCourseOrThrow(Long id) {
        SysCourseEntity entity = id == null ? null : courseMapper.selectById(id);
        if (entity == null) throw BusinessException.of(CommonErrorCode.COURSE_NOT_FOUND);
        return entity;
    }

    private SysCourseDetailEntity toDetailEntity(SysCourseEntity course, CourseDetailSaveReq req) {
        if (StrUtil.isBlank(req.getStageName())) {
            throw BusinessException.of(CommonErrorCode.PARAM_VALID_FAILED, "阶段名称不能为空");
        }
        SysCourseDetailEntity entity = BeanUtil.copyProperties(req, SysCourseDetailEntity.class);
        entity.setStageName(StrUtil.trim(req.getStageName()));
        if (isOffline(course)) {
            if (req.getDayNumber() == null) {
                throw BusinessException.of(CommonErrorCode.PARAM_VALID_FAILED, "线下课程第几天不能为空");
            }
            if (StrUtil.isBlank(req.getClassContent())) {
                throw BusinessException.of(CommonErrorCode.PARAM_VALID_FAILED, "线下课程上课内容不能为空");
            }
            entity.setClassContent(StrUtil.trim(req.getClassContent()));
        } else {
            // 线上课程只维护阶段名称，避免错误保留线下字段。
            entity.setDayNumber(null);
            entity.setClassContent(null);
        }
        return entity;
    }

    private List<SysCourseDetailEntity> validateImportRows(SysCourseEntity course, Long courseId,
                                                             List<CourseDetailImportRow> rows) {
        List<SysCourseDetailEntity> entities = new ArrayList<>();
        Set<String> completedStages = new HashSet<>();
        String currentStage = null;
        for (int index = 0; index < rows.size(); index++) {
            CourseDetailImportRow row = rows.get(index);
            int excelRow = index + 2;
            try {
                if (StrUtil.isBlank(row.getStageName())) {
                    throw BusinessException.of(CommonErrorCode.PARAM_VALID_FAILED, "阶段名称不能为空");
                }
                if (row.getDayNumber() == null || !Integer.valueOf(index + 1).equals(row.getDayNumber())) {
                    throw BusinessException.of(CommonErrorCode.PARAM_VALID_FAILED, "学习天数必须从1开始连续递增");
                }
                if (StrUtil.isBlank(row.getClassContent())) {
                    throw BusinessException.of(CommonErrorCode.PARAM_VALID_FAILED, "上课内容不能为空");
                }
                SysCourseDetailEntity entity = new SysCourseDetailEntity();
                entity.setStageName(StrUtil.trim(row.getStageName()));
                entity.setDayNumber(row.getDayNumber());
                entity.setClassContent(StrUtil.trim(row.getClassContent()));
                // 模板要求同一阶段的行连续排列，防止阶段在导入表中被拆散。
                if (!entity.getStageName().equals(currentStage)) {
                    if (completedStages.contains(entity.getStageName())) {
                        throw BusinessException.of(CommonErrorCode.PARAM_VALID_FAILED, "同一阶段的行必须连续放在一起");
                    }
                    if (currentStage != null) completedStages.add(currentStage);
                    currentStage = entity.getStageName();
                }
                entity.setCourseId(courseId);
                entities.add(entity);
            } catch (BusinessException exception) {
                throw BusinessException.of(CommonErrorCode.PARAM_VALID_FAILED,
                        "Excel第" + excelRow + "行：" + exception.getMessage());
            }
        }
        return entities;
    }

    private void validateImportHeaders(java.util.Map<Integer, String> headers) {
        if (!"阶段名称".equals(StrUtil.trim(headers.get(0)))
                || !"第几天".equals(StrUtil.trim(headers.get(1)))
                || !"上课内容".equals(StrUtil.trim(headers.get(2)))) {
            throw BusinessException.of(CommonErrorCode.PARAM_VALID_FAILED,
                    "Excel表头必须依次为：阶段名称、第几天、上课内容");
        }
    }

    private void normalizeCourse(SysCourseEntity entity) {
        entity.setCourseName(StrUtil.trim(entity.getCourseName()));
        entity.setMaterialPath(StrUtil.trim(entity.getMaterialPath()));
        entity.setTeachingMode(StrUtil.trim(entity.getTeachingMode()));
    }

    private boolean isOffline(SysCourseEntity course) {
        return "OFFLINE".equals(course.getTeachingMode());
    }

    private CourseRes toCourseRes(SysCourseEntity entity) {
        return BeanUtil.copyProperties(entity, CourseRes.class);
    }

    private CourseDetailRes toCourseDetailRes(SysCourseDetailEntity entity) {
        return BeanUtil.copyProperties(entity, CourseDetailRes.class);
    }
}
