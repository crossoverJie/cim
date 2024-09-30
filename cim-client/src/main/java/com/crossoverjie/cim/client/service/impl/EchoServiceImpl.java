package com.crossoverjie.cim.client.service.impl;

import com.crossoverjie.cim.client.config.AppConfiguration;
import com.crossoverjie.cim.client.sdk.Client;
import com.crossoverjie.cim.client.sdk.Event;
import com.crossoverjie.cim.client.service.MsgLogger;
import com.vdurmont.emoji.EmojiParser;
import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.stereotype.Service;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2019-08-27 22:37
 * @since JDK 1.8
 */
@Service
public class EchoServiceImpl implements Event {

    private static final String PREFIX = "$";

    @Resource
    private AppConfiguration appConfiguration;

    @Resource
    private MsgLogger msgLogger;

    @Override
    public void debug(String msg, Object... replace) {
        msgLogger.log(String.format("Debug[%s]", msg));
    }

    @Override
    public void info(String msg, Object... replace) {
        // Make terminal can display the emoji
        msg = EmojiParser.parseToUnicode(msg);
        String date = LocalDate.now() + " " + LocalTime.now().withNano(0).toString();

        msg = "[" + date + "] \033[31;4m" + appConfiguration.getUserName() + PREFIX + "\033[0m" + " " + msg;

        String log = print(msg, replace);

        System.out.println(log);
    }

    @Override
    public void warn(String msg, Object... replace) {
        info(String.format("Warn##%s##", msg), replace);
    }

    @Override
    public void error(String msg, Object... replace) {
        info(String.format("Error!!%s!!", msg), replace);
    }

    @Override
    public void fatal(Client client) {
        info("{} fatal error, shutdown client", client.getAuth());
    }


    /**
     * print msg
     *
     * @param msg
     * @param place
     * @return
     */
    private String print(String msg, Object... place) {
        StringBuilder sb = new StringBuilder();
        int k = 0;
        for (int i = 0; i < place.length; i++) {
            int index = msg.indexOf("{}", k);

            if (index == -1) {
                return msg;
            }

            if (index != 0) {
                sb.append(msg, k, index);
                sb.append(place[i]);

                if (place.length == 1) {
                    sb.append(msg, index + 2, msg.length());
                }

            } else {
                sb.append(place[i]);
                if (place.length == 1) {
                    sb.append(msg, index + 2, msg.length());
                }
            }

            k = index + 2;
        }
        if (sb.toString().equals("")) {
            return msg;
        } else {
            return sb.toString();
        }
    }
}
