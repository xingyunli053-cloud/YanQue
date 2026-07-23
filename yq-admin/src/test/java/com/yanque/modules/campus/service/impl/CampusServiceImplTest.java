package com.yanque.modules.campus.service.impl;

import com.yanque.commons.apires.CommonErrorCode;
import com.yanque.commons.exception.BusinessException;
import com.yanque.modules.campus.mapper.SysCampusMapper;
import com.yanque.modules.campus.pojo.entity.SysCampusEntity;
import com.yanque.modules.campus.pojo.vo.reqvo.CampusSaveReq;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** 校区关键业务规则的单元测试。 */
class CampusServiceImplTest {

    @Test
    void createTrimsFieldsAndReturnsGeneratedId() {
        SysCampusMapper mapper = mock(SysCampusMapper.class);
        when(mapper.insert(any(SysCampusEntity.class))).thenAnswer(invocation -> {
            invocation.getArgument(0, SysCampusEntity.class).setId(8L);
            return 1;
        });
        CampusServiceImpl service = new CampusServiceImpl(mapper);

        Long id = service.create(request(" 滨江校区 ", " 张老师 ", " 13800000000 "));

        ArgumentCaptor<SysCampusEntity> captor = ArgumentCaptor.forClass(SysCampusEntity.class);
        verify(mapper).insert(captor.capture());
        assertEquals(8L, id);
        assertEquals("滨江校区", captor.getValue().getCampusLocation());
        assertEquals("张老师", captor.getValue().getManagerName());
    }

    @Test
    void updateThrowsWhenAffectedRowsIsZero() {
        SysCampusMapper mapper = mock(SysCampusMapper.class);
        when(mapper.selectByCampusLocation("校区")).thenReturn(null);
        when(mapper.updateById(any(SysCampusEntity.class))).thenReturn(0);
        CampusServiceImpl service = new CampusServiceImpl(mapper);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.update(99L, request("校区", "负责人", "13800000000")));

        assertEquals(CommonErrorCode.CAMPUS_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    void createRejectsDuplicateCampusLocation() {
        SysCampusMapper mapper = mock(SysCampusMapper.class);
        when(mapper.selectByCampusLocation("滨江校区")).thenReturn(campus(7L));
        CampusServiceImpl service = new CampusServiceImpl(mapper);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.create(request("滨江校区", "负责人", "13800000000")));

        assertEquals(CommonErrorCode.CAMPUS_LOCATION_ALREADY_EXISTS.getCode(), exception.getCode());
        verify(mapper, never()).insert(any());
    }

    @Test
    void deleteRejectsCampusWithClasses() {
        SysCampusMapper mapper = mock(SysCampusMapper.class);
        when(mapper.selectById(1L)).thenReturn(campus(1L));
        when(mapper.countClassByCampusId(1L)).thenReturn(1);
        CampusServiceImpl service = new CampusServiceImpl(mapper);

        BusinessException exception = assertThrows(BusinessException.class, () -> service.delete(1L));

        assertEquals(CommonErrorCode.CAMPUS_HAS_CLASSES.getCode(), exception.getCode());
        verify(mapper, never()).deleteById(1L);
    }

    private CampusSaveReq request(String location, String name, String phone) {
        CampusSaveReq request = new CampusSaveReq();
        request.setCampusLocation(location);
        request.setManagerName(name);
        request.setManagerPhone(phone);
        return request;
    }

    private SysCampusEntity campus(Long id) {
        SysCampusEntity entity = new SysCampusEntity();
        entity.setId(id);
        return entity;
    }
}
