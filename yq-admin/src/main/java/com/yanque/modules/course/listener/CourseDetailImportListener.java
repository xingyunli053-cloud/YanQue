package com.yanque.modules.course.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.yanque.commons.apires.CommonErrorCode;
import com.yanque.commons.exception.BusinessException;
import com.yanque.modules.course.pojo.excel.CourseDetailImportRow;
import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * EasyExcel 课程详情解析监听器。
 * 只负责逐行收集数据和保留表头；业务规则统一在 Service 中校验，便于事务内保证覆盖式导入原子性。
 */
@Getter
public class CourseDetailImportListener extends AnalysisEventListener<CourseDetailImportRow> {
    private static final int MAX_ROWS = 1000;

    private final List<CourseDetailImportRow> rows = new ArrayList<>();
    private Map<Integer, String> headers = new LinkedHashMap<>();

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        headers = new LinkedHashMap<>(headMap);
    }

    @Override
    public void invoke(CourseDetailImportRow data, AnalysisContext context) {
        if (rows.size() >= MAX_ROWS) {
            throw BusinessException.of(CommonErrorCode.PARAM_VALID_FAILED, "单次最多导入1000条课程详情");
        }
        rows.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 所有数据在 Service 中统一校验并批量入库。
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) {
        if (exception instanceof ExcelDataConvertException convertException) {
            throw BusinessException.of(CommonErrorCode.PARAM_VALID_FAILED,
                    "Excel第" + (convertException.getRowIndex() + 1) + "行数据格式不正确");
        }
        if (exception instanceof BusinessException businessException) {
            throw businessException;
        }
        throw BusinessException.of(CommonErrorCode.PARAM_VALID_FAILED, "Excel文件解析失败");
    }
}
