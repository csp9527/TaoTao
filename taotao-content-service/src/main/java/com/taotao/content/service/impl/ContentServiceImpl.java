package com.taotao.content.service.impl;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.content.service.ContentService;
import com.taotao.mapper.TbContentMapper;
import com.taotao.pojo.TbContent;
import com.taotao.pojo.TbContentExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ContentServiceImpl implements ContentService {
    @Autowired
    private TbContentMapper contentMapper;

    @Override
    public TaotaoResult insertContent(TbContent content) {
        // 补全pojo的属性
        content.setCreated(new Date());
        content.setUpdated(new Date());
        // 向内容表中插入数据
        contentMapper.insert(content);
        return TaotaoResult.ok();
    }

    @Override
    public List<TbContent> getContentList(long cid) {
        TbContentExample example = new TbContentExample();

        TbContentExample.Criteria criteria = example.createCriteria();

        criteria.andCategoryIdEqualTo(cid);

        List<TbContent> list = contentMapper.selectByExample(example);

        return list;
    }
}
