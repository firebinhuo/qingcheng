package com.fire.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.fire.dao.CategoryReportMapper;
import com.fire.pojo.order.CategoryReport;
import com.fire.service.order.CategoryReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = CategoryReportService.class)
public class CategoryReportServiceImpl implements CategoryReportService {
    @Autowired
    private CategoryReportMapper categoryReportMapper;

    @Override
    public List<CategoryReport> categoryReport(LocalDate date) {
        return categoryReportMapper.categoryReport(date);
    }

    @Override
    @Transactional
    public void createData() {
        //查询昨天的类目统计数据
        LocalDate localDate = LocalDate.now().minusDays(1);
        //保存到tb_category_report中
        List<CategoryReport> categoryReportList = categoryReportMapper.categoryReport(localDate);
        for (CategoryReport categoryReport : categoryReportList) {
            categoryReportMapper.insert(categoryReport);
        }


    }

    @Override
    public List<Map> category1Count(String startDate, String stopDate) {
        return categoryReportMapper.category1Count(startDate, stopDate);
    }
}
