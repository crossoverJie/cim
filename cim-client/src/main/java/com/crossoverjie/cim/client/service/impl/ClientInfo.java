package com.crossoverjie.cim.client.service.impl;


import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2019-01-21 23:35
 * @since JDK 1.8
 */
@Component
public class ClientInfo {

    private Info info = new Info() ;

    public Info get(){
        return info ;
    }

    public ClientInfo saveUserInfo(long userId,String userName){
        info.setUserId(userId);
        info.setUserName(userName);
        return this;
    }


    public ClientInfo saveServiceInfo(String serviceInfo){
        info.setServiceInfo(serviceInfo);
        return this;
    }

    public ClientInfo saveStartDate(){
        info.setStartDate(new Date());
        return this;
    }

    public class Info{
        private String userName;
        private long userId ;
        private String serviceInfo ;
        private Date startDate ;

        public Info() {
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public long getUserId() {
            return userId;
        }

        public void setUserId(long userId) {
            this.userId = userId;
        }

        public String getServiceInfo() {
            return serviceInfo;
        }

        public void setServiceInfo(String serviceInfo) {
            this.serviceInfo = serviceInfo;
        }

        public Date getStartDate() {
            return startDate;
        }

        public void setStartDate(Date startDate) {
            this.startDate = startDate;
        }
    }
}
