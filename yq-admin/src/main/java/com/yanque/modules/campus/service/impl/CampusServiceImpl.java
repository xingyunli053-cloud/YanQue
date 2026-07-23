package com.yanque.modules.campus.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yanque.commons.apires.CommonErrorCode;
import com.yanque.commons.apires.PageResult;
import com.yanque.commons.exception.BusinessException;
import com.yanque.modules.campus.mapper.SysCampusMapper;
import com.yanque.modules.campus.pojo.entity.SysCampusEntity;
import com.yanque.modules.campus.pojo.vo.reqvo.CampusPageReq;
import com.yanque.modules.campus.pojo.vo.reqvo.CampusSaveReq;
import com.yanque.modules.campus.pojo.vo.resvo.CampusRes;
import com.yanque.modules.campus.service.CampusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** 校区 CRUD 的业务实现。 */
@Service
@RequiredArgsConstructor
public class CampusServiceImpl implements CampusService {
    private final SysCampusMapper campusMapper;

    @Override
    public PageResult<CampusRes> page(CampusPageReq req) {
        // 关键词前后空白不参与匹配，避免用户复制输入时查询不到数据。
        req.setKeyword(StrUtil.trim(req.getKeyword()));
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<SysCampusEntity> entities = campusMapper.selectList(req);
        PageInfo<SysCampusEntity> pageInfo = new PageInfo<>(entities);
        List<CampusRes> records = entities.stream().map(this::toRes).toList();
        return new PageResult<>(pageInfo.getTotal(), req.getPageNum(), req.getPageSize(), records);
    }

    @Override
    public CampusRes detail(Long id) {
        return toRes(getCampusOrThrow(id));
    }

    @Override
    @Transactional
    public Long create(CampusSaveReq req) {
        SysCampusEntity entity = toEntity(req);
        // 同一地点只能维护一个校区，去除首尾空格后再进行比对。
        ensureCampusLocationUnique(entity.getCampusLocation(), null);
        if (campusMapper.insert(entity) != 1) {
            throw BusinessException.of(CommonErrorCode.CAMPUS_OPERATION_FAILED);
        }
        return entity.getId();
    }

    @Override
    @Transactional
    public void update(Long id, CampusSaveReq req) {
        // 将路径中的 id 写入实体，确保更新目标不能由请求体伪造。
        SysCampusEntity entity = toEntity(req);
        entity.setId(id);
        // 修改地点时同样需要保证不会与其他校区重复。
        ensureCampusLocationUnique(entity.getCampusLocation(), id);
        // 影响行数为 0 时，表示对应校区已经不存在。
        if (campusMapper.updateById(entity) != 1) {
            throw BusinessException.of(CommonErrorCode.CAMPUS_NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        getCampusOrThrow(id);
        // 班级仍归属该校区时禁止删除，避免产生无归属班级。
        if (campusMapper.countClassByCampusId(id) > 0) {
            throw BusinessException.of(CommonErrorCode.CAMPUS_HAS_CLASSES);
        }
        if (campusMapper.deleteById(id) != 1) {
            throw BusinessException.of(CommonErrorCode.CAMPUS_OPERATION_FAILED);
        }
    }

    private SysCampusEntity getCampusOrThrow(Long id) {
        SysCampusEntity entity = id == null ? null : campusMapper.selectById(id);
        if (entity == null) throw BusinessException.of(CommonErrorCode.CAMPUS_NOT_FOUND);
        return entity;
    }

    private void ensureCampusLocationUnique(String campusLocation, Long currentId) {
        SysCampusEntity sameLocation = campusMapper.selectByCampusLocation(campusLocation);
        if (sameLocation != null && !sameLocation.getId().equals(currentId)) {
            throw BusinessException.of(CommonErrorCode.CAMPUS_LOCATION_ALREADY_EXISTS);
        }
    }

    private SysCampusEntity toEntity(CampusSaveReq req) {
        SysCampusEntity entity = BeanUtil.copyProperties(req, SysCampusEntity.class);
        entity.setCampusLocation(StrUtil.trim(req.getCampusLocation()));
        entity.setManagerName(StrUtil.trim(req.getManagerName()));
        entity.setManagerPhone(StrUtil.trim(req.getManagerPhone()));
        return entity;
    }

    private CampusRes toRes(SysCampusEntity entity) {
        return BeanUtil.copyProperties(entity, CampusRes.class);
    }
}
