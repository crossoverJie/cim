package com.crossoverjie.cim.common.msg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author chenqwwq
 * @date 2026/4/9
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelAuthReq implements Serializable {

    private Long userId;
    private String userName;
    private String authToken;
}
