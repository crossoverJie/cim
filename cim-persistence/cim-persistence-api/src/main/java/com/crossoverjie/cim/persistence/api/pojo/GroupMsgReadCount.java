package com.crossoverjie.cim.persistence.mysql.sharding;

public class ShardingStrategy {

    private static final int SHARDING_TABLE_COUNT = 8;

    private ShardingStrategy() {
    }

    public static String getOfflineMsgTableName(Long userId) {
        if (userId == null) {
            return "offline_msg_0";
        }
        long shard = Math.abs(userId % SHARDING_TABLE_COUNT);
        return "offline_msg_" + shard;
    }

    public static String getP2PMsgReadTableName(Long userId) {
        if (userId == null) {
            return "p2p_msg_read_status_0";
        }
        long shard = Math.abs(userId % SHARDING_TABLE_COUNT);
        return "p2p_msg_read_status_" + shard;
    }

    public static String getGroupMsgReadTableName(Long groupId) {
        if (groupId == null) {
            return "group_msg_read_status_0";
        }
        long shard = Math.abs(groupId % SHARDING_TABLE_COUNT);
        return "group_msg_read_status_" + shard;
    }

    public static String getGroupMsgReadCountTableName(Long groupId) {
        if (groupId == null) {
            return "group_msg_read_count_0";
        }
        long shard = Math.abs(groupId % SHARDING_TABLE_COUNT);
        return "group_msg_read_count_" + shard;
    }

    public static String getConversationReadTableName(Long userId) {
        if (userId == null) {
            return "conversation_read_record_0";
        }
        long shard = Math.abs(userId % SHARDING_TABLE_COUNT);
        return "conversation_read_record_" + shard;
    }

    public static int getShardingTableCount() {
        return SHARDING_TABLE_COUNT;
    }
}
