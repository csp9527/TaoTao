package com.taotao.sso.service.impl;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.JsonUtils;
import com.taotao.jedis.JedisClient;
import com.taotao.mapper.TbUserMapper;
import com.taotao.pojo.TbUser;
import com.taotao.pojo.TbUserExample;
import com.taotao.sso.service.UserLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;
import java.util.UUID;

@Service
public class UserLoginServiceImpl implements UserLoginService {

    @Autowired
    private TbUserMapper tbUserMapper;

    @Autowired
    private JedisClient jedisClient;

    @Value("${SESSION_PRE}")
    private String SESSION_PRE;

    @Value("${SESSION_EXPIRE}")
    private Integer SESSION_EXPIRE;

    @Override
    public TaotaoResult login(String username, String password) {
        // 判断用户名密码是否正确
        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(username);
        List<TbUser> list = tbUserMapper.selectByExample(example);

        if (list == null || list.size() == 0) {
            return TaotaoResult.build(400, "用户不存在");
        }

        // 校验密码
        TbUser user = list.get(0);
        if (!DigestUtils.md5DigestAsHex(password.getBytes()).equals(user.getPassword())) {
            return TaotaoResult.build(400, "用户名或密码错误");
        }

        // 生成 token
        String token = UUID.randomUUID().toString();
        // 把用户信息保存到redis数据库中去
        // key就是token， value就是用户对象转成json
        user.setPassword(null);
        jedisClient.set(SESSION_PRE + ":" + token, JsonUtils.objectToJson(user));
        // 设置key的过期时间
        jedisClient.expire(SESSION_PRE + ":" + token, SESSION_EXPIRE);

        return TaotaoResult.ok(token);

    }

    @Override
    public TaotaoResult getUserByToken(String token) {
        // 根据token到redis数据库去查询用户信息
        String json = jedisClient.get(SESSION_PRE + ":" + token);

        if (StringUtils.isBlank(json)) {
            return TaotaoResult.build(400, "此用户登录已过期");
        }

        // 重置过期时间
        jedisClient.expire(SESSION_PRE + ":" + token, SESSION_EXPIRE);
        return TaotaoResult.ok(JsonUtils.jsonToPojo(json, TbUser.class));
    }

    @Override
    public TaotaoResult logout(String token) {
        jedisClient.expire(SESSION_PRE + ":" + token, 0);
        return TaotaoResult.ok();
    }
}
