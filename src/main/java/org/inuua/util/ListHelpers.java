package org.inuua.util;

import java.util.ArrayList;
import java.util.List;

public final class ListHelpers {

    public static byte[] byteArray(int... byteList) {
        byte[] ret = new byte[byteList.length];
        for (int i = 0; i < byteList.length; i++) {
            ret[i] = (byte) byteList[i];
        }
        return ret;
    }

    public static String implode(List<String> array, String delim) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < array.size(); i++) {
            if (i != 0) {
                out.append(delim);
            }
            out.append(array.get(i));
        }
        return out.toString();
    }

    public static List<Byte> rangeToListOfBytes(byte[] arr, int fromIndex, int toIndex) {
        List<Byte> l = new ArrayList<Byte>(toIndex - fromIndex);
        for (int i = fromIndex; i < toIndex; i++) {
            l.add(arr[i]);
        }
        return l;
    }

    public static List<Byte> toListOfBytes(byte[] arr) {
        List<Byte> l = new ArrayList<Byte>(arr.length);
        for (int i = 0; i < arr.length; i++) {
            l.add(arr[i]);
        }
        return l;
    }

    public static List<Long> toListOfLongs(long[] arr) {
        List<Long> l = new ArrayList<Long>(arr.length);
        for (int i = 0; i < arr.length; i++) {
            l.add(arr[i]);
        }
        return l;
    }

    public static byte[] toPrimitiveByteArray(List<Byte> l) {
        byte[] arr = new byte[l.size()];
        for (int i = 0; i < l.size(); i++) {
            arr[i] = l.get(i);
        }
        return arr;
    }

    public static long[] toPrimitiveLongArray(List<Long> l) {
        long[] arr = new long[l.size()];
        for (int i = 0; i < l.size(); i++) {
            arr[i] = l.get(i);
        }
        return arr;
    }
}
