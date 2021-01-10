package com.kgc.kmall.service;

import com.kgc.kmall.bean.PmsBaseAttrInfo;
import com.kgc.kmall.bean.PmsBaseAttrValue;

import java.util.List;
import java.util.Set;

public interface AttrService {
    List<PmsBaseAttrInfo> attrInfoList(Long catalog3Id);
    Integer saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo);
    List<PmsBaseAttrValue> getAttrValueList(Long attrId);
    List<PmsBaseAttrInfo> selectAttrInfoValueListByValueId(Set<Long> valueIds);
}

