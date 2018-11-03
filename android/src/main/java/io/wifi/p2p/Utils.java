package io.wifi.p2p;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by zyusk on 03.11.2018.
 */

public class Utils {
    public static final String CHARSET = "UTF-8";
    public static boolean copyBytes(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            out.close();
            inputStream.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
        return true;
    }
}
