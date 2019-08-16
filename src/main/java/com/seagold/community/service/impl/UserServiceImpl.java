/**
 * FileName: UserServiceImpl
 * Author:   xjh
 * Date:     2019-08-15 14:55
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.seagold.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seagold.community.entity.User;
import com.seagold.community.mapper.UserMapper;
import com.seagold.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author xjh
 * @create 2019-08-15
 * @since 1.0.0
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    RedisTemplate<Object,Object> redisTemplate;

    @Override
    public void insert(User user) {
        userMapper.insert(user);
    }

    @Override
    public List<User> findAll() {
        return userMapper.selectList(null);
    }

    @Override
    public void autoLogin(User user) {
        System.out.println("redis存入");
        System.out.println(user);
        redisTemplate.opsForValue().set(user.getToken(), user,60*60*24*3, TimeUnit.SECONDS);
    }

    @Override
    public User findByAccountId(String id) {
        QueryWrapper<User> o = new QueryWrapper<>();
        o.eq("account_id", id);
        return userMapper.selectOne(o);
    }

    @Override
    public void updateById(User user) {
        userMapper.updateById(user);
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().invalidate();
        System.out.println("用户注销");
        Cookie[] cookies = request.getCookies();
        if(cookies != null || cookies.length != 0){
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")){
                    String token = cookie.getValue();
                    redisTemplate.delete(token);
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }

}