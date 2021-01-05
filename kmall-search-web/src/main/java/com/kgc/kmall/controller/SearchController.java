package com.kgc.kmall.controller;

import com.kgc.kmall.bean.PmsSearchSkuInfo;
import com.kgc.kmall.bean.PmsSearchSkuParam;
import com.kgc.kmall.bean.PmsSkuAttrValue;
import com.kgc.kmall.service.SearchService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
public class SearchController {
    @Reference
    SearchService searchService;
    @RequestMapping("index.html")
    public String index(){
        return "index";
    }

    @RequestMapping("/list.html")
    public String list(PmsSearchSkuParam pmsSearchSkuParam, Model model){
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = searchService.list(pmsSearchSkuParam);

        //获取平台属性valueId
        Set<Long> valueIdSet=new HashSet<>();

        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {
            for (PmsSkuAttrValue pmsSkuAttrValue : pmsSearchSkuInfo.getSkuAttrValueList()) {
                valueIdSet.add(pmsSkuAttrValue.getValueId());
                System.out.println(pmsSkuAttrValue.getValueId());
            }
        }
        System.out.println(Arrays.toString(valueIdSet.toArray()));
        model.addAttribute("skuLsInfoList",pmsSearchSkuInfos);
        return "list";
    }
}
