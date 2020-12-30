package com.kgc.kmall.service;

import com.kgc.kmall.bean.PmsSkuInfo;

public interface SkuService {
    String saveSkuInfo(PmsSkuInfo skuInfo);

    PmsSkuInfo selectBySkuId(Long skuId);
}
