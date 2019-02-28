package com.crossoverjie.cim.common.route.algorithm.consistenthash;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Function:一致性 hash 算法抽象类
 *
 * @author crossoverJie
 * Date: 2019-02-27 00:35
 * @since JDK 1.8
 */
public abstract class AbstractConsistentHash {

    /**
     * 新增节点
     * @param key
     * @param value
     */
    protected abstract void add(long key,String value);

    /**
     * 排序节点，数据结构自身支持排序可以不用重写
     */
    protected void sort(){}

    /**
     * 根据当前的 key 通过一致性 hash 算法的规则取出一个节点
     * @param value
     * @return
     */
    protected abstract String getFirstNodeValue(String value);

    /**
     * 传入节点列表以及客户端信息获取一个服务节点
     * @param values
     * @param key
     * @return
     */
    public String process(List<String> values,String key){

        for (String value : values) {
            add(hash(value), value);
        }
        sort();

        return getFirstNodeValue(key) ;
    }

    /**
     * hash 运算
     * @param value
     * @return
     */
    public Long hash(String value){
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 not supported", e);
        }
        md5.reset();
        byte[] keyBytes = null;
        try {
            keyBytes = value.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unknown string :" + value, e);
        }

        md5.update(keyBytes);
        byte[] digest = md5.digest();

        // hash code, Truncate to 32-bits
        long hashCode = ((long) (digest[3] & 0xFF) << 24)
                | ((long) (digest[2] & 0xFF) << 16)
                | ((long) (digest[1] & 0xFF) << 8)
                | (digest[0] & 0xFF);

        long truncateHashCode = hashCode & 0xffffffffL;
        return truncateHashCode;
    }
}
