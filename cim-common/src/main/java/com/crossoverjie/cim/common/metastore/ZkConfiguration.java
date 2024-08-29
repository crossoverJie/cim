package com.crossoverjie.cim.common.metastore;

import org.apache.curator.RetryPolicy;

/**
 * @author crossoverJie
 */
public class ZkConfiguration extends AbstractConfiguration<RetryPolicy> {
    ZkConfiguration(String metaServiceUri, int timeout, RetryPolicy retryPolicy) {
        super(metaServiceUri, timeout, retryPolicy);
    }
}
