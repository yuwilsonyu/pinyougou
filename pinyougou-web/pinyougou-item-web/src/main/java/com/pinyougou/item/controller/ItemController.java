package com.pinyougou.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.service.GoodsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
public class ItemController {

    @Reference(timeout = 10000)
    private GoodsService goodsService;

    @RequestMapping("/{goodsId}")
    public String getGoods(@PathVariable("goodsId") Long goodsId , Model model){
        Map<String,Object> data = goodsService.getGoods(goodsId);
        model.addAllAttributes(data);
        return "item";
    }
}
