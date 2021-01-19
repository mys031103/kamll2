package com.kgc.kmall.kmallorderweb.controller;

import com.kgc.kmall.annotations.LoginRequired;
import com.kgc.kmall.bean.Member_Receive_Address;
import com.kgc.kmall.bean.OmsCartItem;
import com.kgc.kmall.bean.Order;
import com.kgc.kmall.bean.OrderItem;
import com.kgc.kmall.service.CartService;
import com.kgc.kmall.service.MemberService;
import com.kgc.kmall.service.OrderService;
import com.kgc.kmall.service.SkuService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
public class OrderController {

    @Reference
    MemberService memberService;

    @Reference
    CartService cartService;

    @Reference
    OrderService orderService;
    @Reference
    SkuService skuService;

    @RequestMapping("/toTrade")
    @LoginRequired(value = true)
    public String toTrade(HttpServletRequest request, Model model) {
        //从拦截器中获取用户memberid和nickname
        Integer memberId = (Integer) request.getAttribute("memberId");
        String nickname = (String) request.getAttribute("nickname");

        // 收件人地址列表
        List<Member_Receive_Address> receiveAddressByMemberId = memberService.getReceiveAddressByMemberId(Long.valueOf(memberId));
        model.addAttribute("userAddressList", receiveAddressByMemberId);

        // 将购物车集合转化为页面计算清单集合
        List<OmsCartItem> omsCartItems = cartService.cartList(memberId.toString());
        List<OrderItem> omsOrderItems = new ArrayList<>();
        for (OmsCartItem omsCartItem : omsCartItems) {
// 每循环一个购物车对象，就封装一个商品的详情到OmsOrderItem
            if (omsCartItem.getIsChecked() == 1) {
                OrderItem omsOrderItem = new OrderItem();
                omsOrderItem.setProductName(omsCartItem.getProductName());
                omsOrderItem.setProductPic(omsCartItem.getProductPic());
                omsOrderItems.add(omsOrderItem);
            }
        }
        model.addAttribute("omsOrderItems", omsOrderItems);
        model.addAttribute("totalAmount", getTotalAmount(omsCartItems));

        //生成交易码
        String tradeCode = orderService.genTradeCode(Long.valueOf(memberId));
        System.out.println(tradeCode);
        model.addAttribute("tradeCode", tradeCode);
        return "trade";
    }

    private BigDecimal getTotalAmount(List<OmsCartItem> omsCartItems) {
        BigDecimal totalAmount = new BigDecimal("0");

        for (OmsCartItem omsCartItem : omsCartItems) {
            omsCartItem.setTotalPrice(omsCartItem.getPrice().multiply(new BigDecimal(omsCartItem.getQuantity())));
            BigDecimal totalPrice = omsCartItem.getTotalPrice();

            if (omsCartItem.getIsChecked() == 1) {
                totalAmount = totalAmount.add(totalPrice);
            }
        }

        return totalAmount;
    }

    @RequestMapping("submitOrder")
    @LoginRequired(value = true)
    public String submitOrder(Model model, String receiveAddressId, BigDecimal totalAmount, String tradeCode, HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap modelMap) {
        //从拦截器中获取用户memberid和nickname
        Integer memberId = (Integer) request.getAttribute("memberId");
        String nickname = (String) request.getAttribute("nickname");
        // 检查交易码
        String success = orderService.checkTradeCode(Long.valueOf(memberId), tradeCode);
        if (success.equals("success")) {
            System.out.println("提交订单");
            System.out.println(receiveAddressId);
            System.out.println(totalAmount);
            /*提交订单*/
            List<OrderItem> omsOrderItems = new ArrayList<>();
            //订单对象
            Order omsOrder = new Order();
            omsOrder.setAutoConfirmDay(7);//默认自动确认时间（天）
            omsOrder.setCreateTime(new Date());//提交时间
            omsOrder.setDiscountAmount(null);//管理员后台调整订单使用的折扣金额
            //omsOrder.setFreightAmount(); 运费，支付后，在生成物流信息时
            omsOrder.setMemberId(Long.valueOf(memberId));
            omsOrder.setMemberUsername(nickname);

            omsOrder.setNote("生成外部订单编号");
            String outTradeNo = "kmall";
            outTradeNo = outTradeNo + System.currentTimeMillis();// 将毫秒时间戳拼接到外部订单号
            SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMDDHHmmss");
            outTradeNo = outTradeNo + sdf.format(new Date());// 将时间字符串拼接到外部订单号
            omsOrder.setOrderSn(outTradeNo);//外部订单号

            omsOrder.setPayAmount(totalAmount);//应付金额（实际支付金额）
            omsOrder.setOrderType(1);//订单类型：0->正常订单；1->秒杀订单

            /*用户地址表选中的地址写入订单表中*/
            Member_Receive_Address umsMemberReceiveAddress = memberService.getReceiveAddressById(Long.parseLong(receiveAddressId));
            omsOrder.setReceiverCity(umsMemberReceiveAddress.getCity());//城市
            omsOrder.setReceiverDetailAddress(umsMemberReceiveAddress.getDetailAddress());//详细地址
            omsOrder.setReceiverName(umsMemberReceiveAddress.getName());//收货人姓名
            omsOrder.setReceiverPhone(umsMemberReceiveAddress.getPhoneNumber());//收货人电话
            omsOrder.setReceiverPostCode(umsMemberReceiveAddress.getPostCode());//收货人邮编
            omsOrder.setReceiverProvince(umsMemberReceiveAddress.getProvince());//省份/直辖市
            omsOrder.setReceiverRegion(umsMemberReceiveAddress.getRegion());//区

            // 当前日期加一天，一天后配送
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, 1);
            /*配送时间*/
            Date time = c.getTime();
            omsOrder.setReceiveTime(time);//确认收货时间
            omsOrder.setSourceType(0);//订单来源：0->PC订单；1->app订单
            omsOrder.setStatus(0);//订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单
            omsOrder.setOrderType(0);//订单类型：0->正常订单；1->秒杀订单
            omsOrder.setTotalAmount(totalAmount);//订单总金额

            // 根据用户id获得要购买的商品列表(购物车)，和总价格
            List<OmsCartItem> omsCartItems = cartService.cartList(memberId.toString());
            for (OmsCartItem omsCartItem : omsCartItems) {
                if (omsCartItem.getIsChecked()==1) {
                    // 获得订单详情列表
                    OrderItem omsOrderItem = new OrderItem();
                    // 检价
                    boolean b = skuService.checkPrice(omsCartItem.getProductSkuId(), omsCartItem.getPrice());
                    //判断数据库和购物车订单价格是否一样
                    if (b == false) {
                        model.addAttribute("errMsg", "数据库值不一样");
                        return "tradeFail";
                    }
                    // 验库存,远程调用库存系统
                    omsOrderItem.setProductPic(omsCartItem.getProductPic());//商品主图
                    omsOrderItem.setProductName(omsCartItem.getProductName());//商品名称

                    omsOrderItem.setOrderSn(outTradeNo);// 外部订单号，用来和其他系统进行交互，防止重复 订单编号
                    omsOrderItem.setProductCategoryId(omsCartItem.getProductCategoryId());//商品分类id
                    omsOrderItem.setProductPrice(omsCartItem.getPrice());//添加到购物车的价格
                    omsOrderItem.setRealAmount(omsCartItem.getTotalPrice());
                    omsOrderItem.setProductQuantity(omsCartItem.getQuantity());//购买数量
                    omsOrderItem.setProductSkuCode("111111111111");//商品sku条码
                    omsOrderItem.setProductSkuId(omsCartItem.getProductSkuId());
                    omsOrderItem.setProductId(omsCartItem.getProductId());
                    omsOrderItem.setProductSn("仓库对应的商品编号");// 在仓库中的skuId
                   /*写入到omsOrderItems中*/
                    omsOrderItems.add(omsOrderItem);
                }
            }
            omsOrder.setOrderItems(omsOrderItems);

            // 将订单和订单详情写入数据库
            // 删除购物车的对应商品,暂时不进行删除，因为接下来需要频繁的测试
            orderService.saveOrder(omsOrder);

            //重定向到支付系统
            return "redirect:http://payment.kmall.com:8088/index?outTradeNo="+outTradeNo+"&totalAmount="+totalAmount;
                } else {
                    model.addAttribute("errMsg", "获取用户订单信息失败");
                    return "tradeFail";
                }
            }
        }
