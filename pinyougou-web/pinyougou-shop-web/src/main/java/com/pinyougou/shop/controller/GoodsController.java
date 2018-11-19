package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Goods;
import com.pinyougou.service.GoodsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference(timeout = 10000)
    private GoodsService goodsService;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Destination solrQueue;

    @Autowired
    private Destination solrDeleteQueue;

    @Autowired
    private Destination pageTopic;

    @Autowired
    private Destination pageDeleteTopic;

    @RequestMapping("/save")
    public boolean save(@RequestBody Goods goods){
        try {
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            goods.setSellerId(sellerId);
            goodsService.save(goods);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    @RequestMapping("/findByPage")
    public PageResult findByPage(Goods goods,Integer page,Integer rows){
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(sellerId);
        if (StringUtils.isNoneBlank(goods.getGoodsName())){
            try {
                goods.setGoodsName(new String(goods.getGoodsName().getBytes("ISO8859-1"),"UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return goodsService.findByPage(goods,page,rows);
    }

    //商品上下架
    @GetMapping("/updateMarkerable")
    public boolean updateMarkerable(Long[] ids, String status){
        try{
            goodsService.updateMarkerable(ids,status);
            if ("1".equals(status)){ //商品上架
                jmsTemplate.send(solrQueue, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return session.createObjectMessage(ids);
                    }
                });
                for (Long id : ids) {
                    jmsTemplate.send(pageTopic, new MessageCreator() {
                        @Override
                        public Message createMessage(Session session) throws JMSException {
                            return session.createTextMessage(id.toString());
                        }
                    });
                }
            }else {  //商品下架
                jmsTemplate.send(solrDeleteQueue, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return session.createObjectMessage(ids);
                    }
                });
                jmsTemplate.send(pageDeleteTopic, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return session.createObjectMessage(ids);
                    }
                });
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
