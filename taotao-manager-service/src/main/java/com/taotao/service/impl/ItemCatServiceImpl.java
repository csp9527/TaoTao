package com.taotao.service.impl;

import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.mapper.TbItemCatMapper;
import com.taotao.pojo.TbItemCat;
import com.taotao.pojo.TbItemCatExample;
import com.taotao.service.ItemCatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemCatServiceImpl implements ItemCatService {

    @Autowired
    private TbItemCatMapper tbItemCatMapper;

    @Override
    public List<EasyUITreeNode> getItemCatList(long parentId) {
        // 根据父节点ID查询子节点列表
        TbItemCatExample tbItemCatExample = new TbItemCatExample();
        TbItemCatExample.Criteria  criteria = tbItemCatExample.createCriteria();
        criteria.andParentIdEqualTo(parentId); // 查询条件：父节点id = parentId
        // 执行查询（不需要分页）
        List<TbItemCat> list = tbItemCatMapper.selectByExample(tbItemCatExample);
        // 转换为EasyUITreeNode列表
        List<EasyUITreeNode> result = new ArrayList<>();
        for (TbItemCat tbItemCat : list) {
            EasyUITreeNode node = new EasyUITreeNode();
            node.setId(tbItemCat.getId());
            node.setText(tbItemCat.getName());
            node.setState(tbItemCat.getIsParent()? "closed" : "open");

            result.add(node);
        }
        return result;
    }
}
