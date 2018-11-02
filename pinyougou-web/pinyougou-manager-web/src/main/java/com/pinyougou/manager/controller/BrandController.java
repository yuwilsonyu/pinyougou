package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Brand;

import com.pinyougou.service.BrandService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference(timeout = 10000)
    private BrandService brandService;

    @GetMapping("/findAll")
    public List<Brand> findAll(){
        List<Brand> brandList = brandService.findAll();
        return brandList;
    }

    @PostMapping("/save")
    public boolean save(@RequestBody Brand brand){
        try{
            brandService.save(brand);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    @PostMapping("/update")
    public boolean update(@RequestBody Brand brand){
        try{
            brandService.update(brand);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    @GetMapping("/findByPage")
    public PageResult findByPage(Brand brand,int page, int rows){
        try {
            if (brand != null && StringUtils.isNoneBlank(brand.getName())) {
                brand.setName(new String(brand.getName().getBytes("ISO8859-1"),"UTF-8"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return brandService.findByPage(brand,page,rows);
    }
    
    @GetMapping("/delete")
    public boolean delete(Long[] ids){
        try {
            brandService.deleteAll(ids);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
