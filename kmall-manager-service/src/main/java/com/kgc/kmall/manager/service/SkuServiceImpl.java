package com.kgc.kmall.manager.service;

import com.alibaba.fastjson.JSON;
import com.kgc.kmall.bean.*;
import com.kgc.kmall.manager.mapper.PmsSkuAttrValueMapper;
import com.kgc.kmall.manager.mapper.PmsSkuImageMapper;
import com.kgc.kmall.manager.mapper.PmsSkuInfoMapper;
import com.kgc.kmall.manager.mapper.PmsSkuSaleAttrValueMapper;
import com.kgc.kmall.service.SkuService;
import com.kgc.kmall.util.RedisUtil;
import org.apache.dubbo.config.annotation.Service;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;

@Service
@Component
public class SkuServiceImpl implements SkuService {
    @Resource
    PmsSkuAttrValueMapper pmsSkuAttrValueMapper;
    @Resource
    PmsSkuImageMapper pmsSkuImageMapper;
    @Resource
    PmsSkuInfoMapper pmsSkuInfoMapper;
    @Resource
    PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;
    @Resource
    RedissonClient redissonClient;

    RedisUtil redisUtil = new RedisUtil();
    @Override
    public String saveSkuInfo(PmsSkuInfo skuInfo) {
        pmsSkuInfoMapper.insert(skuInfo);
        Long skuInfoId = skuInfo.getId();
        for (PmsSkuImage pmsSkuImage : skuInfo.getSkuImageList()) {
            pmsSkuImage.setSkuId(skuInfoId);
            pmsSkuImageMapper.insert(pmsSkuImage);
        }
        for (PmsSkuAttrValue pmsSkuAttrValue : skuInfo.getSkuAttrValueList()) {
            pmsSkuAttrValue.setSkuId(skuInfoId);
            pmsSkuAttrValueMapper.insert(pmsSkuAttrValue);
        }
        for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuInfo.getSkuSaleAttrValueList()) {
            pmsSkuSaleAttrValue.setSkuId(skuInfoId);
            pmsSkuSaleAttrValueMapper.insert(pmsSkuSaleAttrValue);
        }
        return "success";
    }

    //    @Override
//    public PmsSkuInfo selectBySkuId(Long skuId) {
//        PmsSkuInfo pmsSkuInfo = null;
//        Jedis jedis = redisUtil.getJedis();
//        String key = "sku:" + skuId + ":info";
//        String skuJson = jedis.get(key);
//        if (skuJson != null) {
//            System.out.println("缓存");
//            //缓存中有数据
//            pmsSkuInfo = JSON.parseObject(skuJson, PmsSkuInfo.class);
//            jedis.close();
//            return pmsSkuInfo;
//        } else {
//            //获取分布式锁
//            System.out.println("获取分布式锁");
//            String skuLockKey = "sku:" + skuId + "lock";
//            String skuLockValue= UUID.randomUUID().toString();
//           // String lock = jedis.set(skuLockKey, skuLockValue, "NX", "PX",60*1000);
//            String set = jedis.set(key, "OK", "NX", "PX", 60 * 1000);
//            //拿到分布式锁
//            if (set.equals("OK")) {
//                System.out.println("拿到分布式锁");
//                System.out.println("数据库");
//                //缓存中无数据，从数据库读取并缓存
//                pmsSkuInfo = pmsSkuInfoMapper.selectByPrimaryKey(skuId);
//
//                if (pmsSkuInfo != null) {
//                    String json = JSON.toJSONString(pmsSkuInfo);
//                    //缓存写入随机有效期，防止缓存雪崩
//                    System.out.println("缓存写入有效期防止崩");
//                    Random random = new Random();
//                    int i = random.nextInt(10);
//                    jedis.setex(key, i * 60 * 1000, json);
//                } else {
//                    //如果数据库和缓存都没有数据就写入缓存中一条数据，设置有效期，防止缓存穿透
//                    System.out.println("写入数据库没有的缓存数据");
//                    jedis.setex(key, 5 * 60 * 1000, "empty");
//                }
//                /*//写完缓存删除分布式锁，获取锁的值，并且对比原来的值
//                System.out.println("删除分布式锁");
//                //刚刚做完判断过期
//                jedis.del(skuLockKey);*/
//                String script ="if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
//                jedis.eval(script, Collections.singletonList(skuLockKey),Collections.singletonList(skuLockValue));
//
//            } else {
//                //未拿到锁睡眠三秒（3s），递归调用
//                System.out.println("锁睡眠三秒（3s），递归调用");
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                selectBySpuId(skuId);
//            }
//
//            jedis.close();
//        }
//        return pmsSkuInfo;
//    }
    @Override
    public PmsSkuInfo selectBySkuId(Long skuId) {
        redisUtil.initPool("192.168.60.132",6379,0);
        PmsSkuInfo pmsSkuInfo = null;
        Jedis jedis = redisUtil.getJedis();
        String key = "sku:" + skuId + ":info";
        String skuJson = jedis.get(key);
        if (skuJson != null) {
            System.out.println("缓存");
            //缓存中有数据
            pmsSkuInfo = JSON.parseObject(skuJson, PmsSkuInfo.class);
            jedis.close();
            return pmsSkuInfo;
        } else {
            Lock lock = redissonClient.getLock("lock");//声明锁
            lock.lock();//上锁
            try {
            System.out.println("数据库");
            //缓存中无数据，从数据库读取并缓存
            pmsSkuInfo = pmsSkuInfoMapper.selectByPrimaryKey(skuId);

            if (pmsSkuInfo != null) {
                String json = JSON.toJSONString(pmsSkuInfo);
                //缓存写入随机有效期，防止缓存雪崩
                System.out.println("缓存写入有效期防止崩");
                Random random = new Random();
                int i = random.nextInt(10);
                jedis.setex(key, i * 60 * 1000, json);
            } else {
                //如果数据库和缓存都没有数据就写入缓存中一条数据，设置有效期，防止缓存穿透
                System.out.println("写入数据库没有的缓存数据");
                jedis.setex(key, 5 * 60 * 1000, "empty");
            }
            jedis.close();
            } finally {
                lock.unlock();//解锁
            }
        }
        return pmsSkuInfo;
    }

    @Override
    public List<PmsSkuInfo> selectBySpuId(Long spuId) {

        return pmsSkuInfoMapper.selectBySpuId(spuId);
    }

    @Override
    public List<PmsSkuInfo> getAllSku() {
        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.selectByExample(null);
        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfos) {
            PmsSkuAttrValueExample example=new PmsSkuAttrValueExample();
            PmsSkuAttrValueExample.Criteria criteria = example.createCriteria();
            criteria.andSkuIdEqualTo(pmsSkuInfo.getId());
            List<PmsSkuAttrValue> pmsSkuAttrValues = pmsSkuAttrValueMapper.selectByExample(example);
            pmsSkuInfo.setSkuAttrValueList(pmsSkuAttrValues);
        }
        return pmsSkuInfos;
    }

    @Override
    public boolean checkPrice(Long productSkuId, BigDecimal price) {
        //验证数据库和购物车订单价格是否一样
        boolean b = false;


        PmsSkuInfo pmsSkuInfo1 = pmsSkuInfoMapper.selectByPrimaryKey(productSkuId);

        BigDecimal price1 = new BigDecimal(pmsSkuInfo1.getPrice());

        if(price.compareTo(price1)==0){
            b = true;
        }

        return b;
    }

}
