package com.crossoverjie.cim.common.route.algorithm.consistenthash;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

/**
 * Function: Consistent hash algorithm abstract class
 *
 * @author crossoverJie
 * Date: 2019-02-27 00:35
 * @since JDK 1.8
 */
public abstract class AbstractConsistentHash {

    /**
     * Add a new node
     * @param key
     * @param value
     */
    protected abstract void add(long key, String value);

    /**
     * remove node
     * @param value node
     * @return current data
     */
    protected abstract Map<String, String> remove(String value);

    /**
     * Clear old data in the structure
     */
    protected abstract void clear();

    /**
     * Sort nodes, data structures that support sorting natively may override this
     */
    protected void sort() { }

    /**
     * Get a node based on the current key using consistent hash algorithm
     * @param value
     * @return
     */
    protected abstract String getFirstNodeValue(String value);

    /**
     * Get a service node from the node list and client key
     * @param values
     * @param key
     * @return
     */
    public String process(List<String> values, String key) {
        // fix https://github.com/crossoverJie/cim/issues/79
        clear();
        for (String value : values) {
            add(hash(value), value);
        }
        sort();

        return getFirstNodeValue(key);
    }

    /**
     * Hash computation
     * @param value
     * @return
     */
    public Long hash(String value) {
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
