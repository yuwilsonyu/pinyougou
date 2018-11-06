package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Seller;
import com.pinyougou.service.SellerService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
public class SellerController {

    @Reference(timeout = 10000)
    private SellerService sellerService;


    @RequestMapping("/findByPage")
    public PageResult findByPage (Seller seller,Integer page,Integer rows){
        return sellerService.findByPage(seller,page,rows);
    }

    @RequestMapping("/updateStatus")
    public boolean updateStatus(String sellerId,String status){
        try{
            sellerService.updateStatus(sellerId,status);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
}
