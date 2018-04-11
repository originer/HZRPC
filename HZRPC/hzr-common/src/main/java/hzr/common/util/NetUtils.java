package hzr.common.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetUtils {
    /**
     * 获取本机IP地址
     * @return
     */
    public static String getLocalIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }
}