package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.MemberDao;
import com.itheima.pojo.Member;
import com.itheima.service.MemberService;
import com.itheima.utils.MD5Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author ：seanyang
 * @date ：Created in 2019/6/29
 * @description ：
 * @version: 1.0
 */
@Service
@Slf4j
public class MemberServiceImpl implements MemberService {

	@Autowired
	private MemberDao memberDao;

	@Transactional
	@Override
	public Member smsLogin(String telephone) {
		// 根据手机号获取会员信息
		Member member = memberDao.findByTelephone(telephone);
		if( member == null){
			// 不是会员，自动注册
			member = new Member();
			member.setPhoneNumber(telephone);
			member.setRegTime(new Date());
			// 调用Service保存会员
			memberDao.add(member);
		}
		return member;
	}

	@Override
	public List<Integer> findMemberCountByMonth(List<String> monthList) {
		List<Integer> memberCountList = new ArrayList<>();
		for (String month : monthList){
			month = month + "-28"; // yyyy.mm.31
			Integer count = memberDao.findMemberCountBeforeDate(month);
			memberCountList.add(count);
		}
		return memberCountList;
	}

	//根据手机号查询会员
	public Member findByTelephone(String telephone) {
		return memberDao.findByTelephone(telephone);
	}
	//新增会员
	public void add(Member member) {
		if(member.getPassword() != null){
			member.setPassword(MD5Utils.md5(member.getPassword()));
		}
		memberDao.add(member);
	}
}
