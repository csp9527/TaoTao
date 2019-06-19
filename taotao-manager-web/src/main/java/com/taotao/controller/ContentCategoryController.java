package com.taotao.controller;

import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.content.service.ContentCategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
public class ContentCategoryController {

    @Resource
    private ContentCategoryService contentCategoryService;

    @RequestMapping("/content/category/list")
    @ResponseBody
    public List<EasyUITreeNode> getContentCatList(@RequestParam(defaultValue = "0") Long id) {
        return contentCategoryService.getContentCatList(id);
    }

    @RequestMapping("/content/category/create")
    @ResponseBody
    public TaotaoResult insertContentCat(Long parentId, String name) {
        TaotaoResult taotaoResult = contentCategoryService.insertContentCat(parentId, name);
        return taotaoResult;
    }
}
