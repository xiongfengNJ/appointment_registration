package com.itheima.dao;

import com.itheima.pojo.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author ：seanyang
 * @date ：Created in 2019/6/12
 * @description ：
 * @version: 1.0
 */
public interface OrderDao {
	/**
	 * 保存订单
	 * @param order 订单数据
	 */
	void add(Order order);

	/**
	 *  基于条件查找订单数据
	 * @param order
	 * @return
	 */
	List<Order> findByCondition(Order order);

	/**
	 * 基于ID获取预约详情
	 * @param id
	 * @return
	 */
	Map<String,Object> findById4Detail(Integer id);

	/**
	 * 查询某一日期预约数据
	 * @param date
	 * @return
	 */
	Integer findOrderCountByDate(@Param("date") String date);

	/**
	 * 查询某一日期到现在的预约数据
	 * @param date
	 * @return
	 */
	Integer findOrderCountByAfterDate(@Param("date") String date);

	/**
	 * 查询某一日期到访数据
	 * @param date
	 * @return
	 */
	Integer findVisitsCountByDate(@Param("date") String date);

	/**
	 * 查询某一日期到现在的到访数据
	 * @param date
	 * @return
	 */
	Integer findVisitsCountByAfterDate(String date);

	/**
	 * 获取热门套餐列表
	 * @return
	 */
	List<Map<String,Object>> findHotSetmeal();

	/**
	 * 基于套餐名称分类汇总
	 * @return
	 */
	List<Map<String,Object>> findSetmealCount();

}
