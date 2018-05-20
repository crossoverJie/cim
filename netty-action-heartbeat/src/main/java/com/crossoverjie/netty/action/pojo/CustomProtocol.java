package com.crossoverjie.netty.action.pojo;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 17/05/2018 17:50
 * @since JDK 1.8
 */
public class CustomProtocol {

    private long header ;
    private String content ;

    public long getHeader() {
        return header;
    }

    public void setHeader(long header) {
        this.header = header;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CustomProtocol(long header, String content) {
        this.header = header;
        this.content = content;
    }

    public CustomProtocol() {
    }

    @Override
    public String toString() {
        return "CustomProtocol{" +
                "header=" + header +
                ", content='" + content + '\'' +
                '}';
    }
}
