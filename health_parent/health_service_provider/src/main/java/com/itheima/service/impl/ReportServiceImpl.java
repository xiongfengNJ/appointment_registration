package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.MemberDao;
import com.itheima.dao.OrderDao;
import com.itheima.pojo.Member;
import com.itheima.service.ReportService;
import com.itheima.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * @author ：seanyang
 * @date ：Created in 2019/7/2
 * @description ：业务报表实现类
 * @version: 1.0
 */
@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
	@Autowired
	private MemberDao memberDao;

	@Autowired
	private OrderDao orderDao;
	/**
	 * reportDate:null,
	 * todayNewMember :0,
	 * totalMember :0,
	 * thisWeekNewMember :0,
	 * thisMonthNewMember :0,
	 * todayOrderNumber :0,
	 * todayVisitsNumber :0,
	 * thisWeekOrderNumber :0,
	 * thisWeekVisitsNumber :0,
	 * thisMonthOrderNumber :0,
	 * thisMonthVisitsNumber :0,
	 * hotSetmeal:[]
	 * @return
	 */
	@Override
	public Map<String, Object> getBusinessReportData() {
		Map<String,Object> resultMap = new HashMap<>();
		try{
			// 获取今日
			String today = DateUtils.parseDate2String(DateUtils.getToday());
			// 获取本周第1天
			String thisWeekMonday = DateUtils.parseDate2String(DateUtils.getThisWeekMonday());
			// 获取本月第1天
			String thisFirstDayInMonth = DateUtils.parseDate2String(DateUtils.getFirstDay4ThisMonth());
			// 报表日期
			resultMap.put("reportDate",today);
			// 获取会员相关数据
			// * todayNewMember :0, totalMember :0, thisWeekNewMember :0, thisMonthNewMember :0,
			resultMap.put("todayNewMember",memberDao.findMemberCountByDate(today));
			resultMap.put("totalMember",memberDao.findMemberTotalCount());
			resultMap.put("thisWeekNewMember",memberDao.findMemberCountAfterDate(thisWeekMonday));
			resultMap.put("thisMonthNewMember",memberDao.findMemberCountAfterDate(thisFirstDayInMonth));
			/**
			 *  todayOrderNumber :0,
			 *  todayVisitsNumber :0,
			 *  thisWeekOrderNumber :0,
			 *  thisWeekVisitsNumber :0,
			 *  thisMonthOrderNumber :0,
			 *  thisMonthVisitsNumber :0,
			 */
			// 预约相关数据
			resultMap.put("todayOrderNumber",orderDao.findOrderCountByDate(today));
			resultMap.put("thisWeekOrderNumber",orderDao.findOrderCountByAfterDate(thisWeekMonday));
			resultMap.put("thisMonthOrderNumber",orderDao.findOrderCountByAfterDate(thisFirstDayInMonth));

			// 到诊相关数据
			resultMap.put("todayVisitsNumber",orderDao.findVisitsCountByDate(today));
			resultMap.put("thisWeekVisitsNumber",orderDao.findVisitsCountByAfterDate(thisWeekMonday));
			resultMap.put("thisMonthVisitsNumber",orderDao.findVisitsCountByAfterDate(thisFirstDayInMonth));

			// 获取热门数据 hotSetmeal:[]
			List<Map<String,Object>> list = orderDao.findHotSetmeal();
			for(Map<String,Object> one:list){
					Object value = one.get("setmeal_count");
					log.debug(">>>>obj:{} type:{}",value,value.getClass());

			}
			resultMap.put("hotSetmeal",list);
		}catch(Exception e){
		    e.printStackTrace();
		}

		return resultMap;
	}
}
