package com.taotao.content.service.impl;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.JsonUtils;
import com.taotao.content.jedis.JedisClient;
import com.taotao.content.jedis.JedisClientPool;
import com.taotao.content.service.ContentService;
import com.taotao.mapper.TbContentMapper;
import com.taotao.pojo.TbContent;
import com.taotao.pojo.TbContentExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Service
public class ContentServiceImpl implements ContentService {
    private static final String CONTENT_KEY = "CONTENT_KEY";
    @Autowired
    private TbContentMapper contentMapper;

    @Autowired
    private JedisClient jedisClient;

    @Override
    public TaotaoResult insertContent(TbContent content) {
        // 补全pojo的属性
        content.setCreated(new Date());
        content.setUpdated(new Date());
        // 向内容表中插入数据
        contentMapper.insert(content);

        // 做缓存同步
        jedisClient.hdel(CONTENT_KEY, content.getCategoryId().toString());
        return TaotaoResult.ok();
    }

    @Override
    public List<TbContent> getContentList(long cid) {
        // 查询数据库之前，先查询缓存， 并且添加缓存不影响正常业务逻辑
        try {
            String json = jedisClient.hget(CONTENT_KEY, cid + "");
            // 判断缓存是否命中
            if(!StringUtils.isEmpty(json)) {
                // 把json转成list集合
                List<TbContent> list = JsonUtils.jsonToList(json, TbContent.class);
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        TbContentExample example = new TbContentExample();

        TbContentExample.Criteria criteria = example.createCriteria();

        criteria.andCategoryIdEqualTo(cid);

        List<TbContent> list = contentMapper.selectByExample(example);

        // 向缓存保存结果， 并且不影响业务逻辑
        try {
            jedisClient.hset(CONTENT_KEY, cid + "", JsonUtils.objectToJson(list));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
