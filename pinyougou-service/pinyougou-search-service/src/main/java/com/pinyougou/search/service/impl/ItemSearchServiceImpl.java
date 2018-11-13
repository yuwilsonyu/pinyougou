package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.service.ItemSearchService;
import com.pinyougou.solr.SolrItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;

import java.util.HashMap;
import java.util.Map;

@Service(interfaceName = "com.pinyougou.service.ItemSearchService")
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;
    @Override
    public Map<String, Object> search(Map<String, Object> params) {
        Map<String,Object> resultMap = new HashMap<>();
        Query query = new SimpleQuery("*:*");
        String keywords = (String) params.get("keywords");
        if (StringUtils.isNoneBlank(keywords)){
            Criteria criteria = new Criteria("keywords").is(keywords);
            query.addCriteria(criteria);
        }
        ScoredPage<SolrItem> scoredPage = solrTemplate.queryForPage(query, SolrItem.class);
        resultMap.put("rows",scoredPage.getContent());
        return resultMap;
    }
}
