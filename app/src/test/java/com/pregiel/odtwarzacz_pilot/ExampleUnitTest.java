package com.pregiel.odtwarzacz_pilot;

import com.pregiel.odtwarzacz_pilot.connection.WifiConnection;

import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void getMask() {
        int ip = (192 << 24) + (168 << 16) + (1 << 8) + 28;

        int maskip = (255 << 24) + (255 << 16) + (255 << 8) + 0;

        int zeros = 32 - Integer.toBinaryString(maskip).indexOf("0");

        int maxHosts = (int) Math.pow(2, zeros);

        int subnet = (ip & (maskip));

        System.out.println(Utils.intToIp(ip));
        System.out.println(Utils.intToIp(maskip));
        System.out.println(Utils.intToIp(subnet));


        int i = 0;
        while (i < maxHosts) {
//            System.out.println(Utils.intToIp(subnet+i));
            i++;
        }

    }

    @Test
    public void ipBytesFromString() {
        String ipString = "192.168.1.28";
        int ip = 0;
        String[] split = ipString.split("\\.");
        for (int i = 0; i < split.length; i++) {
            String s = split[i];
            int value = Integer.valueOf(s);
            ip += value << (8 * (3-i));
        }
        System.out.println(ip);
        System.out.println(Utils.intToIp(ip));
        System.out.println(Integer.toBinaryString(ip));
    }
}