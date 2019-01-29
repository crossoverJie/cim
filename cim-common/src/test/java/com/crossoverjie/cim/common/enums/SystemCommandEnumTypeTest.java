package com.crossoverjie.cim.common.enums;

import org.junit.Test;

import java.util.Map;

public class SystemCommandEnumTypeTest {


    @Test
    public void getAllStatusCode() throws Exception {
        Map<String, String> allStatusCode = SystemCommandEnum.getAllStatusCode();
        for (Map.Entry<String, String> stringStringEntry : allStatusCode.entrySet()) {
            String key = stringStringEntry.getKey();
            String value = stringStringEntry.getValue();
            System.out.println(key + "----->" + value);
        }

    }

}