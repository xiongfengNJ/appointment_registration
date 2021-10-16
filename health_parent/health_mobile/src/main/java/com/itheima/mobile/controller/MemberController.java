package com.itheima.mobile.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.itheima.constant.MessageConst;
import com.itheima.constant.RedisConst;
import com.itheima.entity.Result;
import com.itheima.pojo.Member;
import com.itheima.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

/**
 * @author ：seanyang
 * @date ：Created in 2019/6/29
 * @description ：登录控制器
 * @version: 1.0
 */
@RestController
@RequestMapping("/mobile/member")
public class MemberController {

	@Autowired
	private JedisPool jedisPool;

	@Reference
	private MemberService memberService;

	/**
	 * 1. 获取客户端手机号及验证码
	 * 2. 根据手机号获取redis验证码
	 * 3. 判断用户验证码与redis验证码是否匹配
	 *    1. 如果匹配失败，返回验证码错误
	 *    2.如果成功，根据手机号获取会员信息
	 *        1. 如果有会员，登录成功
	 *        2. 如果不是会员，封装Member对象，调用Service保存
	 * @param map
	 * @return
	 */
	@RequestMapping("/smsLogin")
	public Result smsLogin(HttpServletResponse response, @RequestBody Map<String,String> map){
		try{
			// 获取客户端手机号及验证码
			String telephone = map.get("telephone");
			String validateCode = map.get("validateCode");
			// 读取Redis验证码
			Jedis jedis = jedisPool.getResource();
			String codeInRedis = jedis.get(telephone+ RedisConst.SENDTYPE_LOGIN);
			if(codeInRedis == null || codeInRedis.length()==0 ){
				return new Result(false, "验证码失效");
			}
			if ( !codeInRedis.equals(validateCode) ){
				return new Result(false, "验证码错误");
			}
			Member member = memberService.smsLogin(telephone);
			Cookie cookie = new Cookie("login_member_telephone",telephone);
			cookie.setPath("/");//路径
			cookie.setMaxAge(60*60*24*30);//有效期30天
			response.addCookie(cookie);
			//保存会员信息到Redis中
			String json = JSON.toJSON(member).toString();
			jedis.setex(telephone,60*30,json);
			jedis.close();
			return new Result(true,MessageConst.LOGIN_SUCCESS);
		}catch(Exception e){
		    e.printStackTrace();
		    return new Result(false,MessageConst.ACTION_FAIL);
		}
	}
}
