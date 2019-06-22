package com.taotao.sso.controller;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.CookieUtils;
import com.taotao.pojo.TbUser;
import com.taotao.sso.service.UserLoginService;
import com.taotao.sso.service.UserRegisterService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class UserController {

    @Resource
    private UserRegisterService userRegisterService;

    @Resource
    private UserLoginService userLoginService;

    @Value("${COOKIE_TOKEN_KEY}")
    private String COOKIE_TOKEN_KEY;

    @RequestMapping(value = "/user/check/{param}/{type}", method = RequestMethod.GET)
    @ResponseBody
    public TaotaoResult checkUserInfo(@PathVariable String param, @PathVariable Integer type) {
        TaotaoResult taotaoResult = userRegisterService.checkUserInfo(param, type);
        return taotaoResult;
    }

    @RequestMapping(value = "/user/register", method = RequestMethod.POST)
    @ResponseBody
    public TaotaoResult registerUser(TbUser user) {
        TaotaoResult taotaoResult = userRegisterService.createUser(user);
        return taotaoResult;
    }

    @RequestMapping(value = "/user/login", method = RequestMethod.POST)
    @ResponseBody
    public TaotaoResult userLogin(String username, String password, HttpServletRequest request, HttpServletResponse response) {
        TaotaoResult taotaoResult = userLoginService.login(username, password);

        // 取出token
        String token = taotaoResult.getData().toString();

        CookieUtils.setCookie(request, response, COOKIE_TOKEN_KEY, token);

        return taotaoResult;
    }

    @RequestMapping("/user/token/{token}")
    @ResponseBody
    public TaotaoResult getUserByToken(@PathVariable String token) {
        TaotaoResult taotaoResult = userLoginService.getUserByToken(token);
        return taotaoResult;
    }

    @RequestMapping("/user/logout/{token}")
    @ResponseBody
    public TaotaoResult logout(@PathVariable String token) {
        TaotaoResult taotaoResult = userLoginService.logout(token);
        return taotaoResult;
    }
}
