package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.Goods;
import com.pinyougou.pojo.GoodsDesc;
import com.pinyougou.pojo.Item;
import com.pinyougou.pojo.ItemCat;
import com.pinyougou.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.*;

@Service(interfaceName ="com.pinyougou.service.GoodsService" )
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsDescMapper goodsDescMapper;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private ItemCatMapper itemCatMapper;
    @Autowired
    private SellerMapper sellerMapper;


    /**
     * 添加方法
     *
     * @param goods
     */
    /** 添加商品 */
    public void save(Goods goods){
        try{
            /** 设置未审核状态 */
            goods.setAuditStatus("0");
            /** 添加SPU商品表 */
            goodsMapper.insertSelective(goods);
            /** 添加商品描述表 */
            goods.getGoodsDesc().setGoodsId(goods.getId());
            goodsDescMapper.insertSelective(goods.getGoodsDesc());

            /** 迭代所有的SKU具体商品集合，往SKU表插入数据 */
            for (Item item : goods.getItems()){
                /** 定义SKU商品的标题 */
                StringBuilder title = new StringBuilder();
                title.append(goods.getGoodsName());
                /** 把规格选项JSON字符串转化成Map集合 */
                Map<String,Object> spec = JSON.parseObject(item.getSpec());
                for (Object value : spec.values()) {
                    /** 拼接规格选项到SKU商品标题 */
                    title.append(" " + value);
                }
                /** 设置SKU商品的标题 */
                item.setTitle(title.toString());
                /** 设置SKU商品图片地址 */
                List<Map> imageList = JSON.parseArray(
                        goods.getGoodsDesc().getItemImages(), Map.class);
                if (imageList != null && imageList.size() > 0){
                    /** 取第一张图片 */
                    item.setImage((String)imageList.get(0).get("url"));
                }
                /** 设置SKU商品的分类(三级分类) */
                item.setCategoryid(goods.getCategory3Id());
                /** 设置SKU商品的创建时间 */
                item.setCreateTime(new Date());
                /** 设置SKU商品的修改时间 */
                item.setUpdateTime(item.getCreateTime());
                /** 设置SPU商品的编号 */
                item.setGoodsId(goods.getId());
                /** 设置商家编号 */
                item.setSellerId(goods.getSellerId());
                /** 设置商品分类名称 */
                item.setCategory(itemCatMapper
                        .selectByPrimaryKey(goods.getCategory3Id()).getName());
                /** 设置品牌名称 */
                item.setBrand(brandMapper
                        .selectByPrimaryKey(goods.getBrandId()).getName());
                /** 设置商家店铺名称 */
                item.setSeller(sellerMapper.selectByPrimaryKey(
                        goods.getSellerId()).getNickName());

                itemMapper.insertSelective(item);
            }
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

    //根据商品id查找sku
    @Override
    public List<Item> findItemByGoodsId(Long[] ids) {
        try{
            Example example =  new Example(Item.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("goodsId",Arrays.asList(ids));
            return itemMapper.selectByExample(example);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateMarkerable(Long[] ids, String status) {
        goodsMapper.updateStatus("is_marketable",ids, status);
    }

    //根据商品spu id查询商品
    @Override
    public Map<String, Object> getGoods(Long goodsId) {
        try {
            Map<String,Object> data = new HashMap<>();
            Goods goods = goodsMapper.selectByPrimaryKey(goodsId);
            data.put("goods",goods);
            GoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            data.put("goodsDesc",goodsDesc);
            if (goods != null && goods.getCategory3Id() != null){
                String itemCat1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
                String itemCat2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
                String itemCat3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();
                data.put("itemCat1",itemCat1);
                data.put("itemCat2",itemCat2);
                data.put("itemCat3",itemCat3);
            }
            /** 查询SKU数据 */
            Example example = new Example(Item.class);
            /** 查询条件对象 */
            Example.Criteria criteria = example.createCriteria();
            /** 状态码为：1 */
            criteria.andEqualTo("status", "1");
            /** 条件: SPU ID */
            criteria.andEqualTo("goodsId", goodsId);
            /** 按是否默认降序(保证第一个为默认) */
            example.orderBy("isDefault").desc();
            /** 根据条件查询SKU商品数据 */
            List<Item> itemList = itemMapper.selectByExample(example);
            data.put("itemList", JSON.toJSONString(itemList));
            return data;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateStatus(Long[] ids, String status) {
        try {
            goodsMapper.updateStatus(null,ids, status);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 多条件分页查询
     *
     * @param goods
     * @param page
     * @param rows
     */
    @Override
    public PageResult findByPage(Goods goods, int page, int rows) {
        try{
            /** 开始分页 */
            PageInfo<Map<String,Object>> pageInfo =
                    PageHelper.startPage(page, rows)
                            .doSelectPageInfo(new ISelect() {
                                @Override
                                public void doSelect() {
                                    goodsMapper.findAll(goods);
                                }
                            });
            /** 循环查询到的商品 */
            for (Map<String,Object> map : pageInfo.getList()){
                ItemCat itemCat1 =
                        itemCatMapper.selectByPrimaryKey(map.get("category1Id"));
                map.put("category1Name", itemCat1 != null ? itemCat1.getName() : "");
                ItemCat itemCat2 =
                        itemCatMapper.selectByPrimaryKey(map.get("category2Id"));
                map.put("category2Name", itemCat2 != null ? itemCat2.getName() : "");
                ItemCat itemCat3 =
                        itemCatMapper.selectByPrimaryKey(map.get("category3Id"));
                map.put("category3Name", itemCat3 != null ? itemCat3.getName() : "");
            }
            return new PageResult(pageInfo.getTotal(),pageInfo.getList());
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }

    }
}
