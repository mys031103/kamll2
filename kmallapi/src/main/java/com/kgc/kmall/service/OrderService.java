package com.kgc.kmall.service;

public interface OrderService {
    String genTradeCode(Long memberId);

    String checkTradeCode(Long aLong, String tradeCode);
}
