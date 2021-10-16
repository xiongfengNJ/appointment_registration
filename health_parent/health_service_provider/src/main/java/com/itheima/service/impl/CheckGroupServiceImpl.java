package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.dao.CheckGroupDao;
import com.itheima.entity.PageResult;
import com.itheima.pojo.CheckGroup;
import com.itheima.service.CheckGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：seanyang
 * @date ：Created in 2019/6/9
 * @description ：
 * @version: 1.0
 */
@Service
@Slf4j
public class CheckGroupServiceImpl implements CheckGroupService {
	@Autowired
	private CheckGroupDao checkGroupDao;

	//添加检查组合，同时需要设置检查组合和检查项的关联关系
	public void add(CheckGroup checkGroup, Integer[] checkitemIds) {
		checkGroupDao.add(checkGroup);
		setCheckGroupAndCheckItem(checkGroup.getId(),checkitemIds);
	}
	//设置检查组合和检查项的关联关系
	public void setCheckGroupAndCheckItem(Integer checkGroupId,Integer[] checkitemIds){
		if(checkitemIds != null && checkitemIds.length > 0){
			for (Integer checkitemId : checkitemIds) {
				Map<String,Integer> map = new HashMap<>();
				map.put("checkgroup_id",checkGroupId);
				map.put("checkitem_id",checkitemId);
				checkGroupDao.setCheckGroupAndCheckItem(map);
			}
		}
	}

	@Override
	public PageResult pageQuery(Integer currentPage, Integer pageSize, String queryString) {
		PageHelper.startPage(currentPage,pageSize);
		Page<CheckGroup> page = checkGroupDao.selectByCondition(queryString);
		PageResult pageResult = new PageResult(page.getTotal(),page.getResult());
		return pageResult;
	}

	@Override
	public CheckGroup findById(Integer id) {
		return checkGroupDao.findById(id);
	}

	@Override
	public List<Integer> findCheckItemsByCheckGroupId(Integer id) {
		return checkGroupDao.findCheckItemIdsByCheckGroupId(id);
	}

	@Transactional
	@Override
	public void edit(CheckGroup checkGroup, Integer[] checkitemIds) {
		// 保存检查组信息
		checkGroupDao.edit(checkGroup);
		// 删除检查组之前关联关系
		checkGroupDao.deleteCheckItemsListByIds(checkGroup.getId());
		// 保存新的关系
		for (Integer checkItemId :checkitemIds) {
			Map maps = new HashMap();
			maps.put("checkgroup_id",checkGroup.getId());
			maps.put("checkitem_id",checkItemId);
			checkGroupDao.setCheckGroupAndCheckItem(maps);
		}
	}

	@Override
	public List<CheckGroup> findAll() {
		return checkGroupDao.findAll();
	}
}
