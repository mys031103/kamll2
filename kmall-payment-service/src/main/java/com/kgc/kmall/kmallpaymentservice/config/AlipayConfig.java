package com.kgc.kmall.kmallpaymentservice.config;

/**
 *
 */
public class AlipayConfig {
    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号，此处是沙箱环境的appid
    public static String app_id = "2016102600766406";

    // 商户私钥，您的PKCS8格式RSA2私钥，需要使用支付宝平台开发助手生成私钥和公钥
    public static String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCQ0vGLHtZy+xWTK8FOxDzxUA8RjezGH2tdyaG46xUnJrHe9jwy2xFnLrYL+j2EUN8Qebq1E5mmVAaebOc9i8fd93iBFzX1Uy1Sx787+HG0APRY9moRjMrEIlGZvM/GESjEiP4fBtB74U6PNTtURwsXPuwcEUVjlEYT11p9jgpb304SoJBmSS72gxKpUdekOBUMWuUBFCtYcjW36BuKRZOYpSEM0e+7dW7Zz4pUQYFFyftfeGOX04w5V7PLI7iwlgWpMsRGyEwrUjcElmCdnLkBhFx9IGkavoEMq7c0fil9o5aUOJrBys3N6S9fixGwbyI+SgeZpDKb0mpln40CDVaVAgMBAAECggEAYv/QHkn2TqNVHEXM3wtGna8cneNm+AiCXJp48u/AhgLtlHy+qgUW4HIOACIP62NdGOYdu6qqXgkSfTn02R20gweoWJaFTC6PzQa+s0AieeotHqNT3qtf8LOFFJL9dWhFdhQvowYFfqmGCl9ZL0NhUYKx5H3p8ShqvyV3tlxiuGnlAXk0SILsvfxo+lY860SR8zoUlQlY0XCtUgeMO3DNQNX1DyvN3P7krWyFv29SPR3ajtIZYq8neUHkaTUzl8QJa7HQR6QTvKB82rWCq0XGCh+Gf9DMyPDulVfvHad6tMkS5+DOcmWAcZhV0K9IoK4y2EOb2bnN3wVUXbBpiwGgAQKBgQDXtu7tOCKGjHVmqmW+mvNprn040gZytNCBV34wW+Orsmk26pv8qCNSfgs14SQhYuwV//HMBz48KiLcr3xosegHfqpJOcLouhJhfr9qterqCbNE4LHVHeXHw7znOcyMX8rlr4Miat0cbmTgXoZfx1eGhmhq9Kp3odMKbBI1umB5iQKBgQCr3tAM3KWfRCZRWf1WGhUkdwgNAv8yRNt+meT90MbLFwnelZayqNMyx43paO/szEiMnXEmn4dbiAV+W993yJoN/B1ZcUsVW71PsuaEtkZfVZc24iZ12h3kZz/5uu4u4ATmAglGIIGEQh47YwApaGnITGp0+stskmQEtju+zaVNrQKBgQCbpRPaOBG5UWl4TWSiVf1h+JPDxs7+PvbfDHkxfWvVvCJsjHVGpS7vW6DNHIjMSiscaJNX6rX+BU0oPLqgdBV+KufFdBbtgyy2ChUlcuNwQNWH0jSeGDdunw4TLMIpWwj0F2KgF8k4tXK17Z45R4UgvKRk3+qL7UhwEbBF8hegOQKBgBrYsgzgRmqrXYTllqHlbxoEEdjed/hlMxgCd/eBrhu5HIyNNz3vCxZqf2tPPfLyMf7s9OjAkIcmpx0GwtBWIGJA2GDJRp4ud1LEsidWxYVMvFMRXkqYaITt79tVbLEbwwgel05B85kyLsC+BR9ytNtZvGNLbY0nDMKDqK90Nfb9AoGBANMpJHR1sfDUxQ+UY++G75Oea6H7hKSXeUOWop2Cq3sjAzTNtWjs7BpBqgyLiorg5qW4FzSS0ShR2UWRkqSXyLGT3BFbWkc3C956nx3KVLIam4Fk0/4+pQ9qqRr/OucebW2JYjJdA1EPCNiKrIZsylrjXEokF8Jwlit157jB/g6Y";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkKzkMErIqyt1QImKn3Ez6gtB0RY9KigKKoYrAgVPOOD9qKeWfyxCsab87y3G8oPaxQ+oun4YrA+pRqCOAIcATIWjIV7oI8vhVbcvkqfuuNq71/DVB144lGwC/vk7oIkBXiXyQkpcYlWo/oMCAhEkYJiQe4OCpBR2E3QuytIjTtHDf7cV8N7wD09UOgNKWoB2xEarP+FXPiBj6gDmD5k5dZTYnMKSZLEFUItNtHZy22ZatnAL44wNJG891GLTww5+/QCkQBUWtICWRcqxzX1SoOKLbW4CPoKE+gjL1U9gNutcxpFgYMBMDZQ3gPWPnvNiWl1Ayzf8Qmn71ECO+6vNVQIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url ="http://60.205.215.91/alipay/callback/notify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://payment.kmall.com:8088/alipay/callback/return";


    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝沙箱网关
    public static String gatewayUrl ="https://openapi.alipaydev.com/gateway.do";
}
