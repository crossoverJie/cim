package com.crossoverjie.cim.common.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2018/12/26 18:38
 * @since JDK 1.8
 */
public enum SystemCommandEnumType {

            ALL(":all       ","获取所有命令"),
    ONLINE_USER(":olu       ","获取所有在线用户"),
           QUIT(":q!        ","退出程序"),
          QUERY(":q         ","【:q 关键字】查询聊天记录"),
             AI(":ai        ","开启 AI 模式"),
            QAI(":qai       ","关闭 AI 模式"),
         PREFIX(":pu        ","模糊匹配用户")

    ;

    /** 枚举值码 */
    private final String commandType;

    /** 枚举描述 */
    private final String desc;


    /**
     * 构建一个 。
     * @param commandType 枚举值码。
     * @param desc 枚举描述。
     */
    private SystemCommandEnumType(String commandType, String desc) {
        this.commandType = commandType;
        this.desc = desc;
    }

    /**
     * 得到枚举值码。
     * @return 枚举值码。
     */
    public String getCommandType() {
        return commandType;
    }

    /**
     * 得到枚举描述。
     * @return 枚举描述。
     */
    public String getDesc() {
        return desc;
    }

    /**
     * 得到枚举值码。
     * @return 枚举值码。
     */
    public String code() {
        return commandType;
    }

    /**
     * 得到枚举描述。
     * @return 枚举描述。
     */
    public String message() {
        return desc;
    }

    /**
     * 获取全部枚举值码。
     *
     * @return 全部枚举值码。
     */
    public static Map<String,String> getAllStatusCode() {
        List<String> list = new ArrayList<String>();
        Map<String,String> map = new HashMap<String, String>(16) ;
        for (SystemCommandEnumType status : values()) {
            list.add(status.code());
            map.put(status.getCommandType(),status.getDesc()) ;
        }
        return map;
    }

}