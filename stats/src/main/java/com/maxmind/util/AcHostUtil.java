package com.maxmind.util;

import java.util.StringTokenizer;

public class AcHostUtil {

    public static final int AC = 1;

    public static final int EDU = 2;

    public static final int OTHER = 0;


    public static Integer getHostType(String host) {
        int[] ip = getIpIntArrayFromString(host);
        if (isAC(ip)) return 1;
        if (isEDU(ip)) return 2;
        return 0;
    }




    public static int[] getIpIntArrayFromString(String ip) {
        int[] ret = new int[4];
        StringTokenizer st = new StringTokenizer(ip, ".");
        try {
            ret[0] = Integer.parseInt(st.nextToken());
            ret[1] = Integer.parseInt(st.nextToken());
            ret[2] = Integer.parseInt(st.nextToken());
            ret[3] = Integer.parseInt(st.nextToken());
        } catch (Exception e) {

        }
        return ret;
    }

    /**
     * 科技网
     */
    public static boolean isAC(int[] ip) {

        int ip0 = ip[0] & 0xFF;
        int ip1 = ip[1] & 0xFF;
        int ip3 = ip[3] & 0x00;
        if (ip0 == 159 && ip1 == 226 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 202 && ip1 == 127 && (ip[2] & 0xFE) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 202 && ip1 == 127 && (ip[2] & 0xF0) == 16 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 202 && ip1 == 127 && (ip[2] & 0xF0) == 144 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 202 && ip1 == 127 && (ip[2] & 0xF8) == 160 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 202 && ip1 == 127 && (ip[2] & 0xF8) == 200 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 210 && ip1 == 72 && (ip[2] & 0x80) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 210 && ip1 == 72 && (ip[2] & 0xF0) == 128 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 210 && ip1 == 73 && (ip[2] & 0xC0) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 210 && ip1 == 73 && (ip[2] & 0xE0) == 128 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 210 && ip1 == 74 && (ip[2] & 0xE0) == 192 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 210 && ip1 == 75 && (ip[2] & 0xE0) == 224 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 210 && ip1 == 76 && (ip[2] & 0xE0) == 192 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 210 && ip1 == 77 && (ip[2] & 0xE0) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 210 && ip1 == 77 && (ip[2] & 0xE0) == 64 && ip3 == 0) {
            ip = null;
            return true;
        }

        return false;
    }

    /**
     * 教育网
     **/

    public static boolean isEDU(int[] ip) {
        int ip0 = ip[0] & 0xFF;
        int ip3 = ip[3] & 0x00;
        if (ip0 == 162 && (ip[1] & 0xFF) == 105 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 166 && (ip[1] & 0xFF) == 111 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 202 && (ip[1] & 0xFF) == 4 && (ip[2] & 0xE0) == 128 && ip3 == 0) {
            ip = null;
            return true;
        }

        if (ip0 == 202 && (ip[1] & 0xF8) == 112 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 202 && (ip[1] & 0xF0) == 192 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 202 && (ip[1] & 0xFF) == 38 && (ip[2] & 0xC0) == 192 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 202 && (ip[1] & 0xFF) == 38 && (ip[2] & 0xE0) == 96 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 202 && (ip[1] & 0xFF) == 38 && (ip[2] & 0xE0) == 64 && ip3 == 0) {
            ip = null;
            return true;
        }

        if (ip0 == 202 && (ip[1] & 0xFE) == 194 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }

        if (ip0 == 202 && (ip[1] & 0xFE) == 120 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 202 && (ip[1] & 0xFE) == 198 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }

        if (ip0 == 210 && (ip[1] & 0xFF) == 30 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }

        if (ip0 == 210 && (ip[1] & 0xFE) == 28 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 210 && (ip[1] & 0xF0) == 32 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 210 && (ip[1] & 0xFE) == 26 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 211 && (ip[1] & 0xFF) == 68 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 211 && (ip[1] & 0xFF) == 71 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 211 && (ip[1] & 0xFF) == 81 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 211 && (ip[1] & 0xFF) == 82 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }

        if (ip0 == 211 && (ip[1] & 0xFF) == 83 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 211 && (ip[1] & 0xFF) == 66 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 211 && (ip[1] & 0xFF) == 69 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 211 && (ip[1] & 0xFF) == 67 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 211 && (ip[1] & 0xFE) == 84 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }

        if (ip0 == 211 && (ip[1] & 0xFE) == 64 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 211 && (ip[1] & 0xFF) == 70 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 211 && (ip[1] & 0xFE) == 86 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 211 && (ip[1] & 0xFF) == 80 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }

        if (ip0 == 218 && (ip[1] & 0xFF) == 192 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 218 && (ip[1] & 0xFF) == 194 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 218 && (ip[1] & 0xFF) == 195 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 218 && (ip[1] & 0xFE) == 196 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 219 && (ip[1] & 0xFF) == 221 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 219 && (ip[1] & 0xFE) == 222 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 219 && (ip[1] & 0xFF) == 227 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 219 && (ip[1] & 0xFE) == 218 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 219 && (ip[1] & 0xFE) == 230 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 219 && (ip[1] & 0xFE) == 242 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 219 && (ip[1] & 0xFE) == 224 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 219 && (ip[1] & 0xFF) == 226 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }

        if (ip0 == 219 && (ip[1] & 0xFC) == 244 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 222 && (ip[1] & 0xFF) == 23 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 222 && (ip[1] & 0xFE) == 24 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 222 && (ip[1] & 0xFC) == 28 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }

        if (ip0 == 222 && (ip[1] & 0xFE) == 18 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }

        if (ip0 == 222 && (ip[1] & 0xFE) == 16 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }

        if (ip0 == 222 && (ip[1] & 0xFE) == 20 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 222 && (ip[1] & 0xFF) == 22 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }

        if (ip0 == 218 && (ip[1] & 0xFF) == 193 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 219 && (ip[1] & 0xFF) == 220 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }
        if (ip0 == 219 && (ip[1] & 0xFE) == 228 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }

        if (ip0 == 219 && (ip[1] & 0xFE) == 216 && (ip[2] & 0x00) == 0 && ip3 == 0) {
            ip = null;
            return true;
        }


        return false;
    }

}
