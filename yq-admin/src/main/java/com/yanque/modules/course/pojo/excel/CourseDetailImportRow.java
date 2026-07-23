package com.yanque.modules.course.pojo.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/** Excel 导入行：只读取模板的 A、B、C 三列，右侧填写说明列会被忽略。 */
@Data
public class CourseDetailImportRow {
    /** 阶段名称。 */
    @ExcelProperty(index = 0)
    private String stageName;
    /** 学习天数。 */
    @ExcelProperty(index = 1)
    private Integer dayNumber;
    /** 当天上课内容。 */
    @ExcelProperty(index = 2)
    private String classContent;
}
