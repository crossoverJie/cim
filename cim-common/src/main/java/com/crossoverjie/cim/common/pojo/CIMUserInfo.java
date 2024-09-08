package com.crossoverjie.cim.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Function: 用户信息
 *
 * @author crossoverJie
 *         Date: 2018/12/24 02:33
 * @since JDK 1.8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CIMUserInfo {
    private Long userId ;
    private String userName ;

}
