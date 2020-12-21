package com.kgc.kmall.manager.service;

import com.kgc.kmall.bean.*;
import com.kgc.kmall.manager.mapper.PmsBaseCatalog1Mapper;
import com.kgc.kmall.manager.mapper.PmsBaseCatalog2Mapper;
import com.kgc.kmall.manager.mapper.PmsBaseCatalog3Mapper;
import com.kgc.kmall.service.CatalogService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Service
@Component
public class CatalogServiceImpl implements CatalogService {
    @Resource
    PmsBaseCatalog1Mapper pmsBaseCatalog1Mapper;
    @Resource
    PmsBaseCatalog2Mapper pmsBaseCatalog2Mapper;
    @Resource
    PmsBaseCatalog3Mapper pmsBaseCatalog3Mapper;

    @Override
    public List<PmsBaseCatalog1> getCatalog1() {
        return pmsBaseCatalog1Mapper.selectByExample(null);
    }
    @Override
    public List<PmsBaseCatalog2> getCatalog2(Integer catalog1Id) {
        PmsBaseCatalog2Example pmsBaseCatalog2Example=new PmsBaseCatalog2Example();
        List<PmsBaseCatalog2> pmsBaseCatalog2s;
        if(catalog1Id!=null){
            pmsBaseCatalog2Example.createCriteria().andCatalog1IdEqualTo(catalog1Id);
            pmsBaseCatalog2s=pmsBaseCatalog2Mapper.selectByExample(pmsBaseCatalog2Example);
        }else{
            pmsBaseCatalog2s=pmsBaseCatalog2Mapper.selectByExample(null);
        }
        return pmsBaseCatalog2s;
    }

    @Override
    public List<PmsBaseCatalog3> getCatalog3(Long catalog2Id) {
        PmsBaseCatalog3Example pmsBaseCatalog3Example=new PmsBaseCatalog3Example();
        List<PmsBaseCatalog3> pmsBaseCatalog3s;
        if(catalog2Id!=null){
            pmsBaseCatalog3Example.createCriteria().andCatalog2IdEqualTo(catalog2Id);
            pmsBaseCatalog3s=pmsBaseCatalog3Mapper.selectByExample(pmsBaseCatalog3Example);
        }else{
            pmsBaseCatalog3s=pmsBaseCatalog3Mapper.selectByExample(null);
        }
        return pmsBaseCatalog3s;
    }
}
