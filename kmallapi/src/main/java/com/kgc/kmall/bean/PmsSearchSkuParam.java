package com.kgc.kmall.bean;

import java.io.Serializable;
import java.util.List;

public class PmsSearchSkuParam implements Serializable {
    private String keyword;
    private String catalog3Id;
   // private List<PmsSkuAttrValue> skuAttrValueList;
   private String[] valueId;

    public String[] getValueId() {
        return valueId;
    }

    public void setValueId(String[] valueId) {
        this.valueId = valueId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getCatalog3Id() {
        return catalog3Id;
    }

    public void setCatalog3Id(String catalog3Id) {
        this.catalog3Id = catalog3Id;
    }


}
