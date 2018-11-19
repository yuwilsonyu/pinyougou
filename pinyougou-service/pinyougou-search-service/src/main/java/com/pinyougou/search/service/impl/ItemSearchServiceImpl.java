package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.service.ItemSearchService;
import com.pinyougou.solr.SolrItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceName = "com.pinyougou.service.ItemSearchService")
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    //删除商品索引
    @Override
    public void delete(List<Long> goodsIds) {
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("goodsId").in(goodsIds);
        query.addCriteria(criteria);
        UpdateResponse response = solrTemplate.delete(query);
        if (response.getStatus() ==0){
            solrTemplate.commit();
        }else {
            solrTemplate.rollback();
        }
    }

    @Override
    public void saveOrUpdate(List<SolrItem> solrItems) {
        UpdateResponse updateResponse = solrTemplate.saveBeans(solrItems);
        if (updateResponse.getStatus() == 0){
            solrTemplate.commit();
        }else {
            solrTemplate.rollback();
        }
    }

    @Override
    public Map<String, Object> search(Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<>();
        String keywords = (String) params.get("keywords");
        //判断分页条件
        Integer page = (Integer) params.get("page");
        if (page == null) {
            page = 1;
        }
        Integer rows = (Integer) params.get("rows");
        if (rows == null) {
            rows = 20;
        }
        //判断是否存在关键搜索条件
        if (StringUtils.isNoneBlank(keywords)) {
            HighlightQuery highlightQuery = new SimpleHighlightQuery();
            //设置起始记录查询数
            highlightQuery.setOffset((page - 1) * rows);
            //设置每页查询记录数
            highlightQuery.setRows(rows);
            HighlightOptions highlightOptions = new HighlightOptions();
            highlightOptions.addField("title");
            highlightOptions.setSimplePrefix("<font color='red'>");
            highlightOptions.setSimplePostfix("</font>");
            highlightQuery.setHighlightOptions(highlightOptions);
            Criteria criteria = new Criteria("keywords").is(keywords);
            highlightQuery.addCriteria(criteria);
            //添加种类过滤
            if (!"".equals(params.get("category"))) {
                Criteria categoryCriteria = new Criteria("category").is(params.get("category"));
                highlightQuery.addFilterQuery(new SimpleFilterQuery(categoryCriteria));
            }
            //添加品牌过滤
            if (!"".equals(params.get("brand"))) {
                Criteria brandCriteria = new Criteria("brand").is(params.get("brand"));
                highlightQuery.addFilterQuery(new SimpleFilterQuery(brandCriteria));
            }
            //添加规格过滤
            if (params.get("spec") != null) {
                Map<String, String> specMap = (Map<String, String>) params.get("spec");
                for (String key : specMap.keySet()) {
                    Criteria specCriteria = new Criteria("spec_" + key).is(specMap.get(key));
                    highlightQuery.addFilterQuery(new SimpleFilterQuery(specCriteria));
                }
            }
            //添加价格过滤
            if (!"".equals(params.get("price"))) {
                String[] price = params.get("price").toString().split("-");
                if (!"0".equals(price[0])) {
                    Criteria priceCriteria = new Criteria("price").greaterThanEqual(price[0]);
                    highlightQuery.addFilterQuery(new SimpleFilterQuery(priceCriteria));
                }
                if (!"*".equals(price[1])) {
                    Criteria priceCriteria = new Criteria("price").lessThanEqual(price[1]);
                    highlightQuery.addFilterQuery(new SimpleFilterQuery(priceCriteria));
                }
            }
            //增加排序方法
            String sortField = (String) params.get("sortField");
            String sortValue = (String) params.get("sort");
            if(StringUtils.isNoneBlank(sortField) && StringUtils.isNoneBlank(sortValue)){
                Sort sort = new Sort("ASC".equalsIgnoreCase(sortValue) ?
                        Sort.Direction.ASC : Sort.Direction.DESC, sortField);
                highlightQuery.addSort(sort);
            }
            HighlightPage<SolrItem> solrItems = solrTemplate.queryForHighlightPage(highlightQuery, SolrItem.class);
            for (HighlightEntry<SolrItem> solrItemHighlightEntry : solrItems.getHighlighted()) {
                SolrItem solrItem = solrItemHighlightEntry.getEntity();
                if (solrItemHighlightEntry.getHighlights().size() > 0 && solrItemHighlightEntry.getHighlights().get(0).getSnipplets().size() > 0) {
                    solrItem.setTitle(solrItemHighlightEntry.getHighlights().get(0).getSnipplets().get(0));
                }
            }
            resultMap.put("rows", solrItems.getContent());
            //获取总页数
            resultMap.put("totalPages", solrItems.getTotalPages());
            //获取总记录数
            resultMap.put("total", solrItems.getTotalElements());
        } else {
            Query query = new SimpleQuery("*:*");
            //简单查询分页设置
            query.setOffset((page - 1) * rows);
            query.setRows(rows);
            ScoredPage<SolrItem> scoredPage = solrTemplate.queryForPage(query, SolrItem.class);
            resultMap.put("rows", scoredPage.getContent());
            resultMap.put("totalPages",scoredPage.getTotalPages());
            resultMap.put("total",scoredPage.getTotalElements());
        }

        return resultMap;
    }
}
