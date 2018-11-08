package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.mapper.TypeTemplateMapper;
import com.pinyougou.pojo.TypeTemplate;
import com.pinyougou.service.TypeTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Service(interfaceName = "com.pinyougou.service.TypeTemplateService")
@Transactional
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Autowired
    private TypeTemplateMapper typeTemplateMapper;
    /**
     * 添加方法
     *
     * @param typeTemplate
     */
    @Override
    public void save(TypeTemplate typeTemplate) {
        typeTemplateMapper.insertSelective(typeTemplate);
    }

    /**
     * 修改方法
     *
     * @param typeTemplate
     */
    @Override
    public void update(TypeTemplate typeTemplate) {
        typeTemplateMapper.updateByPrimaryKeySelective(typeTemplate);
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
        Example example = new Example(TypeTemplate.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(ids));
        typeTemplateMapper.deleteByExample(example);
    }

    /**
     * 根据主键id查询
     *
     * @param id
     */
    @Override
    public TypeTemplate findOne(Serializable id) {
        try {
            return typeTemplateMapper.selectByPrimaryKey(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 查询全部
     */
    @Override
    public List<TypeTemplate> findAll() {
        return null;
    }

    /**
     * 多条件分页查询
     *
     * @param typeTemplate
     * @param page
     * @param rows
     */
    @Override
    public PageResult findByPage(TypeTemplate typeTemplate, int page, int rows) {
        PageInfo<TypeTemplate> pageInfo = PageHelper.startPage(page, rows).doSelectPageInfo(new ISelect() {
            @Override
            public void doSelect() {
                typeTemplateMapper.findAll(typeTemplate);
            }
        });
        return new PageResult(pageInfo.getTotal(),pageInfo.getList());
    }
}
