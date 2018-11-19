package com.pinyougou.item.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.listener.SessionAwareMessageListener;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.io.File;

public class DeleteMessageListener implements SessionAwareMessageListener<ObjectMessage> {

    @Value("${page.dir}")
    private String pageDir;
    @Override
    public void onMessage(ObjectMessage objectMessage, Session session) throws JMSException {
        Long[] ids= (Long[]) objectMessage.getObject();
        try {
            for (Long id : ids) {
                File file = new File(pageDir+id+".html");
                if (file.exists()){
                    file.delete();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
