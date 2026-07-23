package com.yanque.modules.campus.service;

import com.yanque.commons.apires.PageResult;
import com.yanque.modules.campus.pojo.vo.reqvo.CampusPageReq;
import com.yanque.modules.campus.pojo.vo.reqvo.CampusSaveReq;
import com.yanque.modules.campus.pojo.vo.resvo.CampusRes;

/** 校区业务服务。 */
public interface CampusService {
    PageResult<CampusRes> page(CampusPageReq req);

    CampusRes detail(Long id);

    Long create(CampusSaveReq req);

    void update(Long id, CampusSaveReq req);

    void delete(Long id);
}
