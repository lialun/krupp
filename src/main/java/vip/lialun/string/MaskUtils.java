package vip.lialun.string;

public class MaskUtils {

    /**
     * 通用的字符串遮蔽方法
     *
     * @param str          需要遮蔽的字符串
     * @param prefixLength 保留前缀长度
     * @param suffixLength 保留后缀长度
     * @param maskChar     用于遮蔽的字符
     * @return 遮蔽后的字符串
     */
    public static String maskString(String str, int prefixLength, int suffixLength, char maskChar) {
        if (str == null) {
            return null;
        }
        int length = str.length();
        if (length <= prefixLength + suffixLength) {
            return str;
        }
        return str.substring(0, prefixLength) +
                String.valueOf(maskChar).repeat(Math.max(0, length - prefixLength - suffixLength)) +
                str.substring(length - suffixLength);
    }

    /**
     * 遮蔽手机号码
     *
     * @param phoneNumber 手机号码
     * @return 遮蔽后的手机号码
     */
    public static String maskPhoneNumber(String phoneNumber) {
        return maskString(phoneNumber, 3, 4, '*');
    }

    /**
     * 遮蔽邮箱地址
     *
     * @param email 邮箱地址
     * @return 遮蔽后的邮箱地址
     */
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        String localPart = maskString(parts[0], 1, 1, '*');
        return localPart + "@" + parts[1];
    }

    /**
     * 遮蔽身份证号码
     *
     * @param idNumber 身份证号码
     * @return 遮蔽后的身份证号码
     */
    public static String maskIdNumber(String idNumber) {
        return maskString(idNumber, 6, 4, '*');
    }
}