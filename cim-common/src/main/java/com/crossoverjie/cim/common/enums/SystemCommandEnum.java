package com.crossoverjie.cim.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2018/12/26 18:38
 * @since JDK 1.8
 */
public enum SystemCommandEnum {

            ALL(":all       ","获取所有命令","PrintAllCommand"),
    ONLINE_USER(":olu       ","获取所有在线用户","PrintOnlineUsersCommand"),
           QUIT(":q!        ","退出程序","ShutDownCommand"),
          QUERY(":q         ","【:q 关键字】查询聊天记录","QueryHistoryCommand"),
             AI(":ai        ","开启 AI 模式","OpenAIModelCommand"),
            QAI(":qai       ","关闭 AI 模式","CloseAIModelCommand"),
         PREFIX(":pu        ","模糊匹配用户","PrefixSearchCommand"),
          EMOJI(":emoji     ","emoji 表情列表","EmojiCommand"),
           INFO(":info      ","获取客户端信息","EchoInfoCommand"),
      DELAY_MSG(":delay     ","delay message, :delay [msg] [delayTime]","DelayMsgCommand")

    ;

    /** 枚举值码 */
    private final String commandType;

    /** 枚举描述 */
    private final String desc;

    /**
     * 实现类
     */
    private final String clazz ;


    /**
     * 构建一个 。
     * @param commandType 枚举值码。
     * @param desc 枚举描述。
     */
    private SystemCommandEnum(String commandType, String desc, String clazz) {
        this.commandType = commandType;
        this.desc = desc;
        this.clazz = clazz ;
    }

    /**
     * 得到枚举值码。
     * @return 枚举值码。
     */
    public String getCommandType() {
        return commandType;
    }
    /**
     * 获取 class。
     * @return class。
     */
    public String getClazz() {
        return clazz;
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
        Map<String,String> map = new HashMap<String, String>(16) ;
        for (SystemCommandEnum status : values()) {
            map.put(status.getCommandType(),status.getDesc()) ;
        }
        return map;
    }

    public static Map<String,String> getAllClazz() {
        Map<String,String> map = new HashMap<String, String>(16) ;
        for (SystemCommandEnum status : values()) {
            map.put(status.getCommandType().trim(),"com.crossoverjie.cim.client.service.impl.command." + status.getClazz()) ;
        }
        return map;
    }



}