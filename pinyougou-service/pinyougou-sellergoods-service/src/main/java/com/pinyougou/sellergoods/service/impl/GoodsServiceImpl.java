package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.GoodsDescMapper;
import com.pinyougou.mapper.GoodsMapper;
import com.pinyougou.pojo.Goods;
import com.pinyougou.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

@Service(interfaceName ="com.pinyougou.service.GoodsService" )
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsDescMapper goodsDescMapper;

    /**
     * 添加方法
     *
     * @param goods
     */
    @Override
    public void save(Goods goods) {
        try{
            goods.setAuditStatus("0");
            goodsMapper.insertSelective(goods);
            goods.getGoodsDesc().setGoodsId(goods.getId());
            goodsDescMapper.insertSelective(goods.getGoodsDesc());
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /**
     * 修改方法
     *
     * @param goods
     */
    @Override
    public void update(Goods goods) {

    }

    /**
     * 根据主键id删除
     *
     * @param id
     */
    @Override
    public void delete(Serializable id) {

    }

    /**
     * 批量删除
     *
     * @param ids
     */
    @Override
    public void deleteAll(Serializable[] ids) {

    }

    /**
     * 根据主键id查询
     *
     * @param id
     */
    @Override
    public Goods findOne(Serializable id) {
        return null;
    }

    /**
     * 查询全部
     */
    @Override
    public List<Goods> findAll() {
        return null;
    }

    /**
     * 多条件分页查询
     *
     * @param goods
     * @param page
     * @param rows
     */
    @Override
    public List<Goods> findByPage(Goods goods, int page, int rows) {
        return null;
    }
}
