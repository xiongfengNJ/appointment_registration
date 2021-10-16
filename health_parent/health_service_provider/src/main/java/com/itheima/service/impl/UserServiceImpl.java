package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.PermissionDao;
import com.itheima.dao.RoleDao;
import com.itheima.dao.UserDao;
import com.itheima.pojo.Permission;
import com.itheima.pojo.Role;
import com.itheima.pojo.User;
import com.itheima.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

/**
 * @author ：seanyang
 * @date ：Created in 2019/6/7
 * @description ：
 * @version: 1.0
 */
@Service(interfaceClass = UserService.class)
@Slf4j
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDao userDao;
	@Autowired
	private RoleDao roleDao;
	@Autowired
	private PermissionDao permissionDao;

	@Override
	public boolean login(String username, String password) {
		log.debug("service_provide...u:{},p:{}",username,password);
		if("admin".equals(username) && "123".equals(password)){
			return true;
		}
		return false;
	}

	/**
	 * 1.根据用户名获取用户对象
	 * 2.根据用户ID，提前角色列表
	 * 3.根据角色ID，提前权限列表
	 * 4.把数据封装到User对象
	 * @param username
	 * @return
	 */
	@Override
	public User findByUsername(String username) {
		// 根据用户名获取用户对象
		User user = userDao.findByUsername(username);
		if(user == null){
			return  null;
		}
		// 根据用户ID，提前角色列表
		Set<Role>  roles = roleDao.findByUserId(user.getId());
		user.setRoles(roles);
		// 根据角色ID，提前权限列表
		for (Role role:roles){
			Integer roleId = role.getId();
			Set<Permission> permissionSet = permissionDao.findByRoleId(roleId);
			role.setPermissions(permissionSet);
		}
		return user;
	}
}
