package com.crossoverjie.cim.client.service.impl.command;

import com.crossoverjie.cim.client.sdk.Client;
import com.crossoverjie.cim.client.sdk.Event;
import com.crossoverjie.cim.client.service.InnerCommand;
import com.crossoverjie.cim.common.data.construct.TrieTree;
import com.crossoverjie.cim.common.pojo.CIMUserInfo;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2019-01-27 19:37
 * @since JDK 1.8
 */
@Slf4j
@Service
public class PrefixSearchCommand implements InnerCommand {


    @Resource
    private Client client ;
    @Resource
    private Event event ;

    @Override
    public void process(String msg) {
        try {
            Set<CIMUserInfo> onlineUsers = client.getOnlineUser();
            TrieTree trieTree = new TrieTree();
            for (CIMUserInfo onlineUser : onlineUsers) {
                trieTree.insert(onlineUser.getUserName());
            }

            String[] split = msg.split(" ");
            String key = split[1];
            List<String> list = trieTree.prefixSearch(key);

            for (String res : list) {
                res = res.replace(key, "\033[31;4m" + key + "\033[0m");
                event.info(res) ;
            }

        } catch (Exception e) {
            log.error("Exception", e);
        }
    }
}
