package com.crossoverjie.cim.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Function: User information
 *
 * @author crossoverJie
 *         Date: 2018/12/24 02:33
 * @since JDK 1.8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CIMUserInfo {
    private Long userId;
    private String userName;

}
