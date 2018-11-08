package com.pinyougou.shop.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TypeTemplate;
import com.pinyougou.service.TypeTemplateService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/typeTemplate")
public class TypeTemplateController {

    @Reference(timeout = 10000)
    private TypeTemplateService typeTemplateService;

    @RequestMapping("/findOne")
    private TypeTemplate findOne(Long id){
        try{
            return typeTemplateService.findOne(id);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    @RequestMapping("/findSpecByTemplateId")
    public List<Map> findSpecByTemplateId(Long id){
        try {
            return typeTemplateService.findSpecByTemplateId(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
