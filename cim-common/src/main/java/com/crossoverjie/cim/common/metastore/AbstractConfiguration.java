package com.crossoverjie.cim.common.metastore;

import lombok.Builder;
import lombok.Data;

/**
 * @author crossverJie
 */
@Data
@Builder
public class AbstractConfiguration<RETRY> {

    private String metaServiceUri;
    private int timeoutMs;
    private RETRY retryPolicy;
}
