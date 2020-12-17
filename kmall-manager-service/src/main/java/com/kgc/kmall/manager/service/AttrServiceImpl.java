package com.kgc.kmall.manager.service;

import com.kgc.kmall.bean.PmsBaseAttrInfo;
import com.kgc.kmall.bean.PmsBaseAttrInfoExample;
import com.kgc.kmall.bean.PmsBaseAttrValue;
import com.kgc.kmall.bean.PmsBaseAttrValueExample;
import com.kgc.kmall.manager.mapper.PmsBaseAttrInfoMapper;
import com.kgc.kmall.manager.mapper.PmsBaseAttrValueMapper;
import com.kgc.kmall.service.AttrService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@Service

public class AttrServiceImpl implements AttrService {
    @Resource
    PmsBaseAttrInfoMapper pmsBaseAttrInfoMapper;
    @Resource
    PmsBaseAttrValueMapper pmsBaseAttrValueMapper;

    @Override
    public List<PmsBaseAttrInfo> attrInfoList(Long catalog3Id) {
        PmsBaseAttrInfoExample pmsBaseAttrInfoExample=new PmsBaseAttrInfoExample();
        List<PmsBaseAttrInfo> pmsBaseAttrInfos;
        if(catalog3Id!=null){
            pmsBaseAttrInfoExample.createCriteria().andCatalog3IdEqualTo(catalog3Id);
            pmsBaseAttrInfos=pmsBaseAttrInfoMapper.selectByExample(pmsBaseAttrInfoExample);
        }else{
            pmsBaseAttrInfos=pmsBaseAttrInfoMapper.selectByExample(null);
        }

        return pmsBaseAttrInfos;
    }

   /* @Override
    public Integer saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo) {
        int i=0;
        //判断是添加还是修改
        if (pmsBaseAttrInfo.getId()==null){
            //添加
            i = pmsBaseAttrInfoMapper.insert(pmsBaseAttrInfo);
        }else{
            //修改
            i=pmsBaseAttrInfoMapper.updateByPrimaryKey(pmsBaseAttrInfo);
        }
        //删除原属性值
        PmsBaseAttrValueExample example=new PmsBaseAttrValueExample();
        PmsBaseAttrValueExample.Criteria criteria = example.createCriteria();
        criteria.andAttrIdEqualTo(pmsBaseAttrInfo.getId());
        i = pmsBaseAttrValueMapper.deleteByExample(example);
        //添加新属性值
        if (pmsBaseAttrInfo.getAttrValueList().size()>0){
            i=pmsBaseAttrValueMapper.insertBatch(pmsBaseAttrInfo.getId(),pmsBaseAttrInfo.getAttrValueList());
        }
        //System.out.println(i);
        return i;
    }

    @Override
    public List<PmsBaseAttrValue> getAttrValueList(Long attrId) {
        PmsBaseAttrValueExample pmsBaseAttrValueExample=new PmsBaseAttrValueExample();
        List<PmsBaseAttrValue> pmsBaseAttrValues;
        if(attrId!=0){
            pmsBaseAttrValueExample.createCriteria().andAttrIdEqualTo(attrId);
            pmsBaseAttrValues=pmsBaseAttrValueMapper.selectByExample(pmsBaseAttrValueExample);
        }else{
            pmsBaseAttrValues=pmsBaseAttrValueMapper.selectByExample(null);
        }
        return pmsBaseAttrValues;
    }*/

}
