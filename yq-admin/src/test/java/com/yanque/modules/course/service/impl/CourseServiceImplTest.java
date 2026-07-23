package com.yanque.modules.course.service.impl;

import com.yanque.commons.apires.CommonErrorCode;
import com.yanque.commons.exception.BusinessException;
import com.yanque.modules.course.mapper.SysCourseDetailMapper;
import com.yanque.modules.course.mapper.SysCourseMapper;
import com.yanque.modules.course.pojo.entity.SysCourseDetailEntity;
import com.yanque.modules.course.pojo.entity.SysCourseEntity;
import com.yanque.modules.course.pojo.vo.reqvo.CourseDetailSaveReq;
import com.yanque.modules.course.pojo.vo.resvo.CourseDetailRes;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;

/** 课程查询关键规则测试。 */
class CourseServiceImplTest {

    @Test
    void detailListThrowsWhenCourseDoesNotExist() {
        SysCourseMapper courseMapper = mock(SysCourseMapper.class);
        CourseServiceImpl service = new CourseServiceImpl(courseMapper, mock(SysCourseDetailMapper.class));

        BusinessException exception = assertThrows(BusinessException.class, () -> service.detailList(9L));

        assertEquals(CommonErrorCode.COURSE_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    void onlineCourseDetailKeepsOfflineFieldsNull() {
        SysCourseMapper courseMapper = mock(SysCourseMapper.class);
        SysCourseDetailMapper detailMapper = mock(SysCourseDetailMapper.class);
        when(courseMapper.selectById(1L)).thenReturn(course(1L));
        SysCourseDetailEntity detail = new SysCourseDetailEntity();
        detail.setId(2L);
        detail.setCourseId(1L);
        detail.setStageName("Java 基础");
        when(detailMapper.selectByCourseId(1L)).thenReturn(List.of(detail));
        CourseServiceImpl service = new CourseServiceImpl(courseMapper, detailMapper);

        List<CourseDetailRes> result = service.detailList(1L);

        assertEquals("Java 基础", result.get(0).getStageName());
        assertNull(result.get(0).getDayNumber());
        assertNull(result.get(0).getClassContent());
    }

    @Test
    void offlineCourseDetailRequiresDayAndClassContent() {
        SysCourseMapper courseMapper = mock(SysCourseMapper.class);
        SysCourseDetailMapper detailMapper = mock(SysCourseDetailMapper.class);
        SysCourseEntity course = course(1L);
        course.setTeachingMode("OFFLINE");
        when(courseMapper.selectById(1L)).thenReturn(course);
        CourseServiceImpl service = new CourseServiceImpl(courseMapper, detailMapper);
        CourseDetailSaveReq request = new CourseDetailSaveReq();
        request.setStageName("第一阶段");

        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.createDetail(1L, request));

        assertEquals(CommonErrorCode.PARAM_VALID_FAILED.getCode(), exception.getCode());
    }

    @Test
    void detailCreationDoesNotOverwriteManuallyMaintainedCourseDays() {
        SysCourseMapper courseMapper = mock(SysCourseMapper.class);
        SysCourseDetailMapper detailMapper = mock(SysCourseDetailMapper.class);
        SysCourseEntity course = course(1L);
        course.setTeachingMode("OFFLINE");
        when(courseMapper.selectById(1L)).thenReturn(course);
        when(detailMapper.insert(any(SysCourseDetailEntity.class))).thenAnswer(invocation -> {
            invocation.getArgument(0, SysCourseDetailEntity.class).setId(5L);
            return 1;
        });
        CourseServiceImpl service = new CourseServiceImpl(courseMapper, detailMapper);
        CourseDetailSaveReq request = new CourseDetailSaveReq();
        request.setStageName("第一阶段");
        request.setDayNumber(3);
        request.setClassContent("Java 基础");

        Long id = service.createDetail(1L, request);

        assertEquals(5L, id);
        verify(courseMapper, never()).updateCourseDays(1L, 3);
    }

    private SysCourseEntity course(Long id) {
        SysCourseEntity entity = new SysCourseEntity();
        entity.setId(id);
        return entity;
    }
}
