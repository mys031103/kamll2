package com.kgc.kmall.service;

import com.kgc.kmall.bean.PmsSkuInfo;

import java.math.BigDecimal;
import java.util.List;

public interface SkuService {
    String saveSkuInfo(PmsSkuInfo skuInfo);

    PmsSkuInfo selectBySkuId(Long skuId);
    List<PmsSkuInfo> selectBySpuId(Long spuId);

    List<PmsSkuInfo> getAllSku();

    //校验价格
    boolean checkPrice(Long productSkuId, BigDecimal price);
}
