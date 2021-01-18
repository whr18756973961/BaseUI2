package com.whr.baseui.utils;

import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    public static boolean isBlank(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static String toString(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    public static String trimToEmpty(String str) {
        return str == null ? "" : str.trim();
    }

    public static String getTextValue(TextView textView) {
        return (textView != null && textView.getText() != null) ? trimToEmpty(textView
                .getText().toString()) : "";
    }

    public static String strNullToEmpty(String str) {
        if (EmptyUtils.isEmpty(str)) return "";
        return str;
    }

    /**
     * 全局判断是否是手机号吗
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobile(String mobiles) {
//        Pattern p = Pattern.compile("^(1[123456789])\\d{9}$");
//        Matcher m = p.matcher(mobiles);
//        return m.matches();
        if (mobiles.length() > 11 || mobiles.length() < 8) return false;
        else return true;
    }

    public static String hideMobile(String mobile) {
        if (EmptyUtils.isEmpty(mobile)) return "";
        try {
            if (mobile.length() >= 11) {
                return mobile.substring(0, 3) + "****" + mobile.substring(7, 11);
            } else if (mobile.length() <= 4) {
                return "****";
            } else {
                return mobile.substring(0, mobile.length() - 4) + "****";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "****";
        }
    }

    public static String hideBankNum4(String bankNum) {
        if (EmptyUtils.isEmpty(bankNum)) return "";
        else if (bankNum.length() <= 4) return bankNum;
        else
            return bankNum.substring(bankNum.length() - 4);
    }

    public static boolean isIdCard(String cardcode) {
        int i = 0;
        String r = "error";
        String lastnumber = "";
        i += Integer.parseInt(cardcode.substring(0, 1)) * 7;
        i += Integer.parseInt(cardcode.substring(1, 2)) * 9;
        i += Integer.parseInt(cardcode.substring(2, 3)) * 10;
        i += Integer.parseInt(cardcode.substring(3, 4)) * 5;
        i += Integer.parseInt(cardcode.substring(4, 5)) * 8;
        i += Integer.parseInt(cardcode.substring(5, 6)) * 4;
        i += Integer.parseInt(cardcode.substring(6, 7)) * 2;
        i += Integer.parseInt(cardcode.substring(7, 8));
        i += Integer.parseInt(cardcode.substring(8, 9)) * 6;
        i += Integer.parseInt(cardcode.substring(9, 10)) * 3;
        i += Integer.parseInt(cardcode.substring(10, 11)) * 7;
        i += Integer.parseInt(cardcode.substring(11, 12)) * 9;
        i += Integer.parseInt(cardcode.substring(12, 13)) * 10;
        i += Integer.parseInt(cardcode.substring(13, 14)) * 5;
        i += Integer.parseInt(cardcode.substring(14, 15)) * 8;
        i += Integer.parseInt(cardcode.substring(15, 16)) * 4;
        i += Integer.parseInt(cardcode.substring(16, 17)) * 2;
        i = i % 11;
        lastnumber = cardcode.substring(17, 18);
        if (i == 0) {
            r = "1";
        }
        if (i == 1) {
            r = "0";
        }
        if (i == 2) {
            r = "x";
        }
        if (i == 3) {
            r = "9";
        }
        if (i == 4) {
            r = "8";
        }
        if (i == 5) {
            r = "7";
        }
        if (i == 6) {
            r = "6";
        }
        if (i == 7) {
            r = "5";
        }
        if (i == 8) {
            r = "4";
        }
        if (i == 9) {
            r = "3";
        }
        if (i == 10) {
            r = "2";
        }
        if (r.equals(lastnumber.toLowerCase())) {
            return true;
        }
        return false;
    }

    public static String doubleFormat(Double num) {
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(num);
    }

    public static int countSameStr(String text, String sub) {
        int count = 0, start = 0;
        while ((start = text.indexOf(sub, start)) >= 0) {
            start += sub.length();
            count++;
        }
        return count;
    }
}
