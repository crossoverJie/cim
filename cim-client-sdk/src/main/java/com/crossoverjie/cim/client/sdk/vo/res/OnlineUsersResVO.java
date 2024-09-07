package com.crossoverjie.cim.client.sdk.vo.res;

import java.util.List;
import lombok.Data;

@Data
public class OnlineUsersResVO {


    /**
     * code : 9000
     * message : success
     * reqNo : null
     * dataBody : [{"userId":1545574841528,"userName":"zhangsan"},{"userId":1545574871143,"userName":"crossoverJie"}]
     */

    private String code;
    private String message;
    private Object reqNo;
    private List<DataBodyBean> dataBody;


    @Data
    public static class DataBodyBean {
        /**
         * userId : 1545574841528
         * userName : zhangsan
         */

        private long userId;
        private String userName;
    }
}
