package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Specification;
import com.pinyougou.service.SpecificationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/specification")
public class SpecificationController {

    @Reference(timeout = 10000)
    private SpecificationService specificationService;

    @GetMapping("/findByPage")
    public PageResult findByPage(Specification specification,Integer page,Integer rows){
        if (specification != null && StringUtils.isNoneBlank(specification.getSpecName())){
            try {
                specification.setSpecName(new String(specification.getSpecName().getBytes("ISO8859-1"),"UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return specificationService.findByPage(specification,page,rows);
    }

    @PostMapping("/save")
    public boolean save(@RequestBody Specification specification){
        try {
            specificationService.save(specification);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
