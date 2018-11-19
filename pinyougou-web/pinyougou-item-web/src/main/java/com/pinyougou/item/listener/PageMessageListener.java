package com.pinyougou.item.listener;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.service.GoodsService;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;

public class PageMessageListener implements SessionAwareMessageListener<TextMessage> {

    @Value("${page.dir}")
    private String pageDir;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Reference(timeout = 10000)
    private GoodsService goodsService;

    @Override
    public void onMessage(TextMessage textMessage, Session session) throws JMSException {
        String goodsId = textMessage.getText();
        try {
            Template template = freeMarkerConfigurer.getConfiguration().getTemplate("item.ftl");
            Map<String, Object> data = goodsService.getGoods(Long.valueOf(goodsId));
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(pageDir + goodsId + ".html"), "UTF-8");
            template.process(data,outputStreamWriter);
            outputStreamWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
