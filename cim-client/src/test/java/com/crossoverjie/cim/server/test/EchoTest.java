package com.crossoverjie.cim.server.test;

import org.junit.Assert;
import org.junit.Test;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2019-08-28 01:47
 * @since JDK 1.8
 */
public class EchoTest {
    @Test
    public void echo() {
        String msg = "{} say,you {}";
        String[] place = {"zhangsan", "haha"};

        String log = log(msg, place);
        System.out.println(log);
        Assert.assertEquals(log,"zhangsan say,you haha");
    }

    @Test
    public void echo2() {
        String msg = "{} say,you {},zhangsan say {}";
        String[] place = {"zhangsan", "haha", "nihao"};

        String log = log(msg, place);
        System.out.println(log);
        Assert.assertEquals(log,"zhangsan say,you haha,zhangsan say nihao");
    }

    @Test
    public void echo3() {
        String msg = "see you {},zhangsan say";
        String[] place = {"zhangsan"};

        String log = log(msg, place);
        System.out.println(log);
        Assert.assertEquals(log,"see you zhangsan,zhangsan say");
    }
    @Test
    public void echo4() {
        String msg = "{}see you,zhangsan say";
        String[] place = {"!!!"};

        String log = log(msg, place);
        System.out.println(log);
        Assert.assertEquals(log,"!!!see you,zhangsan say");
    }
    @Test
    public void echo5() {
        String msg = "see you,zhangsan say{}";
        String[] place = {"!!!"};

        String log = log(msg, place);
        System.out.println(log);
        Assert.assertEquals(log,"see you,zhangsan say!!!");
    }

    @Test
    public void echo6() {
        String msg = "see you,zhangsan say";
        String[] place = {""};

        String log = log(msg, place);
        System.out.println(log);
        Assert.assertEquals(log,"see you,zhangsan say");
    }

    private String log(String msg, String... place) {
        StringBuilder sb = new StringBuilder();
        int k = 0;
        for (int i = 0; i < place.length; i++) {
            int index = msg.indexOf("{}", k);

            if (index == -1){
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
        return sb.toString();
    }
}
