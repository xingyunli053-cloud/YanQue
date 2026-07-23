package com.yanque.modules.course.service;

import com.yanque.commons.apires.PageResult;
import com.yanque.modules.course.pojo.vo.reqvo.CoursePageReq;
import com.yanque.modules.course.pojo.vo.reqvo.CourseDetailSaveReq;
import com.yanque.modules.course.pojo.vo.reqvo.CourseSaveReq;
import com.yanque.modules.course.pojo.vo.resvo.CourseDetailRes;
import com.yanque.modules.course.pojo.vo.resvo.CourseRes;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/** 课程及课程详情的查询服务。 */
public interface CourseService {
    PageResult<CourseRes> page(CoursePageReq req);

    CourseRes detail(Long id);

    List<CourseDetailRes> detailList(Long courseId);

    CourseDetailRes detailItem(Long id);

    Long create(CourseSaveReq req);

    void update(Long id, CourseSaveReq req);

    Long createDetail(Long courseId, CourseDetailSaveReq req);

    void updateDetail(Long id, CourseDetailSaveReq req);

    void delete(Long id);

    void deleteDetail(Long id);

    int importDetails(Long courseId, MultipartFile file);
}
