package com.crossoverjie.netty.action.common.pojo;

import java.io.Serializable;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 17/05/2018 17:50
 * @since JDK 1.8
 */
public class CustomProtocol implements Serializable{

    private static final long serialVersionUID = 4671171056588401542L;
    private long id ;
    private String content ;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CustomProtocol() {
    }

    public CustomProtocol(long id, String content) {
        this.id = id;
        this.content = content;
    }

    @Override
    public String toString() {
        return "CustomProtocol{" +
                "id=" + id +
                ", content='" + content + '\'' +
                '}';
    }
}
