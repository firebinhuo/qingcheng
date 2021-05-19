package com.fire.service.order;

import com.fire.pojo.order.CategoryReport;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CategoryReportService {

    List<CategoryReport> categoryReport(LocalDate date);//根据日期统计类目

    void createData();//创建

    /**
     * 统计从startDate到stopDate的统计类目
     *
     * @param startDate 开始日期
     * @param stopDate  截止日期
     */
    public List<Map> category1Count(String startDate, String stopDate);
}
