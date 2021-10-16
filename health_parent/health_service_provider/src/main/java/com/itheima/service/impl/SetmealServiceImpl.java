package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.constant.RedisConst;
import com.itheima.dao.SetmealDao;
import com.itheima.entity.PageResult;
import com.itheima.pojo.Setmeal;
import com.itheima.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：seanyang
 * @date ：Created in 2019/6/10
 * @description ：
 * @version: 1.0
 */
@Service
public class SetmealServiceImpl implements SetmealService {
	@Autowired
	private JedisPool jedisPool;

	@Autowired
	private SetmealDao setmealDao;

	@Transactional
	@Override
	//新增套餐
	public void add(Setmeal setmeal, Integer[] checkgroupIds) {
		setmealDao.add(setmeal);
		if(checkgroupIds != null && checkgroupIds.length > 0){
			//绑定套餐和检查组的多对多关系
			setSetmealAndCheckGroup(setmeal.getId(),checkgroupIds);
		}
		//将图片名称保存到Redis
		savePic2Redis(setmeal.getImg());
	}
	//将图片名称保存到Redis
	private void savePic2Redis(String pic){
		Jedis jedis = jedisPool.getResource();
		jedis.sadd(RedisConst.SETMEAL_PIC_DB_RESOURCES,pic);
		jedis.close();
	}

	//绑定套餐和检查组的多对多关系
	private void setSetmealAndCheckGroup(Integer id, Integer[] checkgroupIds) {
		for (Integer checkgroupId : checkgroupIds) {
			Map<String,Integer> map = new HashMap<>();
			map.put("setmealId",id);
			map.put("checkgroupId",checkgroupId);
			setmealDao.setSetmealAndCheckGroup(map);
		}
	}

	@Override
	public PageResult pageQuery(Integer currentPage, Integer pageSize, String queryString) {
		PageHelper.startPage(currentPage,pageSize);
		Page<Setmeal> pageSetmeal = setmealDao.selectByCondition(queryString);
		return new PageResult(pageSetmeal.getTotal(),pageSetmeal.getResult());
	}

	@Override
	public List<Setmeal> findAll() {
		return setmealDao.findAll();
	}

	@Override
	public Setmeal findById(Integer id) {
		return setmealDao.findById(id);
	}



}
