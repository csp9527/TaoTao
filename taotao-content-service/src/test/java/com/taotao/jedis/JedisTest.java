package com.taotao.jedis;

import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class JedisTest {

    public void testJedisSingle() {
        // 创建一个Jedis对象
        Jedis jedis = new Jedis("192.168.203.134", 6379);
        // 使用jedis对象操作数据库
        jedis.set("mytest", "1000");

        // 打印结果
        System.out.println(jedis.get("mytest"));
        // 关闭jedis
        jedis.close();
    }

    public void testJedisPool() {
        // 创建一个连接池对象
        JedisPool jedisPool = new JedisPool("192.168.203.134", 6379);
        // 从连接池获得连接
        Jedis jedis = jedisPool.getResource();
        System.out.println(jedis.get("mytest"));
        jedis.close();
        jedisPool.close();
    }
}
