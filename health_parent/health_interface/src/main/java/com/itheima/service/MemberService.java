package com.itheima.service;

import com.itheima.pojo.Member;

import java.util.List;

/**
 * @author ：seanyang
 * @date ：Created in 2019/6/29
 * @description ：会员业务接口
 * @version: 1.0
 */
public interface MemberService {
	/**
	 * 会员基于手机号登录
	 * @param telephone
	 */
	Member smsLogin(String telephone);

	/**
	 * 获取月份列表的会员数量
	 * @param monthList 月份列表
	 * @return
	 */
	public List<Integer> findMemberCountByMonth(List<String> monthList);

	public void add(Member member);

	public Member findByTelephone(String telephone);
}
