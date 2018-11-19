package com.pinyougou.service;

public interface SmsService {
    boolean sendSms (String phone,String signName,String templateCode, String templateParam);
}
