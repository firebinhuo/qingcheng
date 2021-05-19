package com.fire.controller.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.fire.service.order.CategoryReportService;
import com.fire.service.order.OrderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class OrderTask {
    @Reference
    private OrderService orderService;

    @Reference
    private CategoryReportService categoryReportService;

    @Scheduled(cron = "0 0/2 * * * ?")
    public void orderTimeOutLogic() {
        System.out.println("每两分钟执行一次任务" + new Date());
        orderService.orderTimeLogic();//定时添加
    }

    @Scheduled(cron = "0 0 1 * * ?")//每天晚上凌晨一点统计数据
    public void createCategoryReportDate() {
        System.out.println("生成类目统计数据 ");
        categoryReportService.createData();
    }
}
