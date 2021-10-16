package com.itheima.service;

import java.util.Map;

/**
 * @author ：seanyang
 * @date ：Created in 2019/7/2
 * @description ：业务报表接口
 * @version: 1.0
 */
public interface ReportService {
	/**
	 * 报表数据
	 *  todayNewMember -> number		// 今日新增会员
	 *  thisWeekNewMember -> number		// 本周新增会员
	 *  thisMonthNewMember -> number	// 本月新增会员
	 *  totalMember -> number			//  总会员
	 *  todayOrderNumber -> number		// 今日预约人数
	 *  thisWeekOrderNumber -> number	// 本周预约人数
	 *  thisMonthOrderNumber -> number	// 本月预约人数
	 *  todayVisitsNumber -> number		// 今日到诊人数
	 *  thisWeekVisitsNumber -> number	// 本周到诊人数
	 *  thisMonthVisitsNumber -> number	// 本月到诊人数
	 *  hotSetmeals -> List<Setmeal>	// 热门套餐
	 * @return
	 */
	Map<String,Object> getBusinessReportData();
}
