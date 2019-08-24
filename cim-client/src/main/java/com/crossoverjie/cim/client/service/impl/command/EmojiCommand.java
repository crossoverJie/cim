package com.crossoverjie.cim.client.service.impl.command;

import com.crossoverjie.cim.client.service.InnerCommand;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2019-01-27 19:37
 * @since JDK 1.8
 */
@Service
public class EmojiCommand implements InnerCommand {
    private final static Logger LOGGER = LoggerFactory.getLogger(EmojiCommand.class);


    @Override
    public void process(String msg) {
        String value = msg.split(" ")[1];
        if (value != null) {
            Integer index = Integer.parseInt(value);
            List<Emoji> all = (List<Emoji>) EmojiManager.getAll();
            all = all.subList(5 * index, 5 * index + 5);

            for (Emoji emoji : all) {
                System.out.println(EmojiParser.parseToAliases(emoji.getUnicode()) + "--->" + emoji.getUnicode());
            }
        }

    }
}
