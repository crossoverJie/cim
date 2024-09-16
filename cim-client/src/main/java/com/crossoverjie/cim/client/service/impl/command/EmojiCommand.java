package com.crossoverjie.cim.client.service.impl.command;

import com.crossoverjie.cim.client.sdk.Event;
import com.crossoverjie.cim.client.service.InnerCommand;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2019-01-27 19:37
 * @since JDK 1.8
 */
@Service
public class EmojiCommand implements InnerCommand {

    @Resource
    private Event event ;


    @Override
    public void process(String msg) {
        if (msg.split(" ").length <=1){
            event.info("incorrect commond, :emoji [option]") ;
            return ;
        }
        String value = msg.split(" ")[1];
        if (value != null) {
            int index = Integer.parseInt(value);
            List<Emoji> all = (List<Emoji>) EmojiManager.getAll();
            all = all.subList(5 * index, 5 * index + 5);

            for (Emoji emoji : all) {
                event.info(EmojiParser.parseToAliases(emoji.getUnicode()) + "--->" + emoji.getUnicode());
            }
        }

    }
}
