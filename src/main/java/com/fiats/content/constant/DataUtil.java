package com.fiats.content.constant;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

public class DataUtil {

    public static boolean isNullOrEmpty(final Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNullOrEmpty(final Object[] collection) {
        return collection == null || collection.length == 0;
    }

    public static boolean isNullOrEmpty(Object object) {
        return object == null || object.toString().trim().equals("");
    }

    public static boolean isNullOrEmpty(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNullOrZero(Long value) {
        return value == null || value.equals(0L);
    }

    public static boolean isNull(Integer value) {
        return value == null;
    }

    public static String safeToString(Object object) {
        return safeToString(object, "");
    }

    public static String safeToString(Object object, String defaultValue) {
        if (object == null) {
            return defaultValue;
        }
        return object.toString();
    }

    public static Long safeToLong(Object object) {
        return safeToLong(object, null);
    }

    public static Long safeToLong(Object object, Long defaultValue) {
        Long result = defaultValue;
        if (object != null) {
            if (object instanceof BigDecimal) {
                return ((BigDecimal) object).longValue();
            }
            try {
                result = Long.parseLong(object.toString());
            } catch (Exception exception) {

            }
        }

        return result;
    }

    public static int safeToInt(Object object) {
        Integer result = null;
        if (object != null) {
            try {
                result = Integer.parseInt(object.toString());
            } catch (Exception e) {
            }
        }
        return result;
    }


    public static Date safeStringToDate(Object object) {
        Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (object == null) {
            return null;
        }
        try {
            date = dateFormat.parse(object.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}

