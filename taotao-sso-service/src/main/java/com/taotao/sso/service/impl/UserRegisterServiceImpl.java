package com.taotao.sso.service.impl;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.mapper.TbUserMapper;
import com.taotao.pojo.TbUser;
import com.taotao.pojo.TbUserExample;
import com.taotao.sso.service.UserRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

@Service
public class UserRegisterServiceImpl implements UserRegisterService {

    @Autowired
    private TbUserMapper tbUserMapper;

    @Override
    public TaotaoResult checkUserInfo(String param, int type) {
        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();

        if(type == 1) {
            criteria.andUsernameEqualTo(param);
        } else if(type == 2) {
            criteria.andPhoneEqualTo(param);
        } else if(type == 3) {
            criteria.andEmailEqualTo(param);
        }

        List<TbUser> list = tbUserMapper.selectByExample(example);

        if (list == null || list.size() == 0) {
            return TaotaoResult.ok(true);
        }
        return TaotaoResult.ok(false);
    }

    @Override
    public TaotaoResult createUser(TbUser user) {
        // 校验数据的合法性
        if (StringUtils.isBlank(user.getUsername())
                || StringUtils.isBlank(user.getPassword())) {
            return TaotaoResult.build(400, "用户名和密码不能为空");
        }
        // 校验用户名是否重复
        TaotaoResult taotaoResult = checkUserInfo(user.getUsername(), 1);
        boolean flag = (boolean) taotaoResult.getData();
        if (!flag) {
            return TaotaoResult.build(400, "用户名重复");
        }
        // 校验手机号是否重复
        if (user.getPhone() != null) { // 注意：空串也算有值
            taotaoResult = checkUserInfo(user.getPhone(), 2);
            if (!(boolean) taotaoResult.getData()) {
                return TaotaoResult.build(400, "手机号重复");
            }
        }
        // 校验邮箱是否重复
        if (user.getEmail() != null) { // 注意：空串也算有值
            taotaoResult = checkUserInfo(user.getEmail(), 3);
            if (!(boolean) taotaoResult.getData()) {
                return TaotaoResult.build(400, "邮箱重复");
            }
        }
        // 补全TbUser对象的属性
        user.setCreated(new Date());
        user.setUpdated(new Date());
        // 把密码进行MD5加密
        String md5Pass = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        user.setPassword(md5Pass);
        // 插入到数据库
        tbUserMapper.insert(user);
        // 返回结果
        return TaotaoResult.ok();
    }
}
