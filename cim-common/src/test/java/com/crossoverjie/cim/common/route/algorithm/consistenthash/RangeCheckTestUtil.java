package com.crossoverjie.cim.common.route.algorithm.consistenthash;

import org.junit.Assert;

/**
 * @description: TODO
 * @author: zhangguoa
 * @date: 2024/9/12 9:58
 * @project: cim
 */
public class RangeCheckTestUtil {
    public static void assertInRange (int value, int l, int r) {
        Assert.assertTrue(value >= l && value <= r);
    }
}
