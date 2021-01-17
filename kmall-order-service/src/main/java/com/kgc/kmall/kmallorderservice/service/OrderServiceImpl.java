package com.kgc.kmall.kmallorderservice.service;

import com.kgc.kmall.service.OrderService;
import com.kgc.kmall.util.RedisUtil;
import org.apache.dubbo.config.annotation.Service;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
    @Resource
    RedisUtil redisUtil;

    @Override
    public String genTradeCode(Long memberId) {
        Jedis jedis = redisUtil.getJedis();
        String tradeKey = "user:" + memberId + ":tradeCode";
        String tradeCode = UUID.randomUUID().toString();
        jedis.setex(tradeKey, 60 * 15, tradeCode);
        jedis.close();
        return tradeCode;
    }

    @Override
    public String checkTradeCode(Long aLong, String tradeCode) {
        Jedis jedis=redisUtil.getJedis();
        String tradeKey = "user:" + aLong + ":tradeCode";
        String code = jedis.get(tradeKey);
        jedis.close();
        if (code!=null&&code.equals(tradeCode)){
            jedis.del(tradeKey);
            return "success";
        }else{
            return "fail";
        }
    }

}
