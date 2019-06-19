package com.taotao.controller;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.content.service.ContentService;
import com.taotao.pojo.TbContent;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class ContentController {

    @Resource
    private ContentService contentService;

    @RequestMapping("/content/save")
    @ResponseBody
    public TaotaoResult addContent(TbContent content) {
        TaotaoResult result = contentService.insertContent(content);
        return result;
    }
}
