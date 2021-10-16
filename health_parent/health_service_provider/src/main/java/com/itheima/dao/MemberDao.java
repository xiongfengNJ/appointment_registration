package com.itheima.dao;

import com.itheima.pojo.Member;
import org.apache.ibatis.annotations.Param;

/**
 * @author ：seanyang
 * @date ：Created in 2019/6/12
 * @description ：会员Dao
 * @version: 1.0
 */
public interface MemberDao {
	/**
	 * 根据电话查找是否有该会员
	 * @param telephone
	 * @return
	 */
	Member findByTelephone(String telephone);

	/**
	 * 保存会员信息
	 * @param member
	 */
	void add(Member member);

	/**
	 *
	 * @param date
	 * @return
	 */
	Integer findMemberCountBeforeDate(@Param("date") String date);

	/**
	 * 统计某一日期的会员数据量
	 * @param date
	 * @return
	 */
	Integer findMemberCountByDate(@Param("date") String date);

	/**
	 * 统计某一日期到现在的会员数据量
	 * @param date
	 * @return
	 */
	Integer findMemberCountAfterDate(@Param("date") String date);

	/**
	 * 统计全部会员
	 * @return
	 */
	Integer findMemberTotalCount();

}
