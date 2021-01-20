package com.kgc.kmall.kmallorderservice.service;

import com.kgc.kmall.bean.Order;
import com.kgc.kmall.bean.OrderExample;
import com.kgc.kmall.bean.OrderItem;
import com.kgc.kmall.kmallorderservice.mapper.OrderItemMapper;
import com.kgc.kmall.kmallorderservice.mapper.OrderMapper;
import com.kgc.kmall.service.OrderService;
import com.kgc.kmall.util.RedisUtil;
import org.apache.dubbo.config.annotation.Service;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
    @Resource
    RedisUtil redisUtil;
    @Resource
    OrderMapper orderMapper;
    @Resource
    OrderItemMapper orderItemMapper;

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

    @Override
    public void saveOrder(Order order) {
        // 保存订单表
        orderMapper.insertSelective(order);
        Long orderId = order.getId();
        // 保存订单详情
        List<OrderItem> omsOrderItems = order.getOrderItems();
        for (OrderItem orderItem : omsOrderItems) {
            orderItem.setOrderId(orderId);
            orderItemMapper.insertSelective(orderItem);
            // 删除购物车数据,暂时不进行购物车删除，因为需要频繁的测试
            // cartService.delCart();
        }
    }

    @Override
    public Order getOrderByOutTradeNo(String outTradeNo) {
        OrderExample example=new OrderExample();
        OrderExample.Criteria criteria = example.createCriteria();
        criteria.andOrderSnEqualTo(outTradeNo);
        List<Order> orders = orderMapper.selectByExample(example);
        if (orders!=null&&orders.size()>0) {
            return orders.get(0);
        }else{
            return null;
        }
    }

    @Override
    public void updateOrder(Order omsOrder) {
        OrderExample example=new OrderExample();
        OrderExample.Criteria criteria=example.createCriteria();
        criteria.andOrderSnEqualTo(omsOrder.getOrderSn());
        omsOrder.setStatus(1);
        System.out.println("修改");
        orderMapper.updateByExampleSelective(omsOrder,example);

    }


}
