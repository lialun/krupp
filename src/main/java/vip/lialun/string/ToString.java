package vip.lialun.string;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 将任何数据转换为String，便于读取
 *
 * @author lialun
 */
@SuppressWarnings({"unused", "Duplicates", "WeakerAccess"})
public class ToString {
    private static final String NULL = "null";
    private static final String TO_STRING_METHOD = "toString";
    private static final String LEFT_ROUND_BRACKET = "(";
    private static final String RIGHT_ROUND_BRACKET = ")";
    private static final String LEFT_SQUARE_BRACKET = "[";
    private static final String RIGHT_SQUARE_BRACKET = "]";
    private static final String LEFT_BURLY_BRACE = "{";
    private static final String RIGHT_BURLY_BRACE = "}";
    private static final String EQUAL = "=";
    private static final String ELEMENT_SEPARATOR = ", ";

    private static final List LEAVES = Arrays.asList(
            Boolean.class, Character.class, Byte.class, Short.class,
            Integer.class, Long.class, Float.class, Double.class, Void.class,
            String.class);

    private static final String BYTE_ARRAY_CLASS_NAME_SUFFIX = "[B";
    private static final String SHORT_ARRAY_CLASS_NAME_SUFFIX = "[S";
    private static final String INT_ARRAY_CLASS_NAME_SUFFIX = "[I";
    private static final String LONG_ARRAY_CLASS_NAME_SUFFIX = "[J";
    private static final String FLOAT_ARRAY_CLASS_NAME_SUFFIX = "[F";
    private static final String DOUBLE_ARRAY_CLASS_NAME_SUFFIX = "[D";
    private static final String BOOLEAN_ARRAY_CLASS_NAME_SUFFIX = "[Z";
    private static final String CHAR_ARRAY_CLASS_NAME_SUFFIX = "[C";

    public static String from(Object object) {
        return from(object, false);
    }

    public static String from(Object obj, boolean useInternalToString) {
        if (obj == null) {
            return NULL;
        }
        if (LEAVES.contains(obj.getClass())) {
            return obj.toString();
        }
        if (obj instanceof Map) {
            return from((Map) obj, useInternalToString);
        }
        if (obj instanceof Iterable) {
            return from((Iterable) obj, useInternalToString);
        }
        if (obj.getClass().isArray()) {
            if (obj.getClass().getName().endsWith(BYTE_ARRAY_CLASS_NAME_SUFFIX)) {
                return from((byte[]) obj);
            } else if (obj.getClass().getName().endsWith(SHORT_ARRAY_CLASS_NAME_SUFFIX)) {
                return from((short[]) obj);
            } else if (obj.getClass().getName().endsWith(INT_ARRAY_CLASS_NAME_SUFFIX)) {
                return from((int[]) obj);
            } else if (obj.getClass().getName().endsWith(LONG_ARRAY_CLASS_NAME_SUFFIX)) {
                return from((long[]) obj);
            } else if (obj.getClass().getName().endsWith(FLOAT_ARRAY_CLASS_NAME_SUFFIX)) {
                return from((float[]) obj);
            } else if (obj.getClass().getName().endsWith(DOUBLE_ARRAY_CLASS_NAME_SUFFIX)) {
                return from((double[]) obj);
            } else if (obj.getClass().getName().endsWith(BOOLEAN_ARRAY_CLASS_NAME_SUFFIX)) {
                return from((boolean[]) obj);
            } else if (obj.getClass().getName().endsWith(CHAR_ARRAY_CLASS_NAME_SUFFIX)) {
                return from((char[]) obj);
            } else {
                return from((Object[]) obj, useInternalToString);
            }
        }
        if (useInternalToString && hasOverrideToString(obj)) {
            return obj.toString();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(obj.getClass().getSimpleName());
        if (obj.getClass().isEnum()) {
            sb.append(LEFT_ROUND_BRACKET).append(((Enum) obj).name()).append(RIGHT_ROUND_BRACKET);
        }
        sb.append(LEFT_BURLY_BRACE);
        String nextSeparator = "";
        for (Field field : obj.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            sb.append(nextSeparator);
            nextSeparator = ELEMENT_SEPARATOR;
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            sb.append(field.getName()).append(EQUAL);
            try {
                sb.append(from(field.get(obj), useInternalToString));
            } catch (IllegalAccessException ignored) {
            }
        }
        sb.append(RIGHT_BURLY_BRACE);
        return sb.toString();
    }

    private static String from(Map map, boolean useInternalToString) {
        if (map == null) {
            return NULL;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(map.getClass().getSimpleName()).append(LEFT_BURLY_BRACE);
        String nextSeparator = "";
        for (Object key : map.keySet()) {
            sb.append(nextSeparator).append(from(key, useInternalToString))
                    .append(EQUAL).append(from(map.get(key), useInternalToString));
            nextSeparator = ELEMENT_SEPARATOR;
        }
        sb.append(RIGHT_BURLY_BRACE);
        return sb.toString();
    }

    private static String from(Iterable iterable, boolean useInternalToString) {
        if (iterable == null) {
            return NULL;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(iterable.getClass().getSimpleName()).append(LEFT_SQUARE_BRACKET);
        String nextSeparator = "";
        for (Object obj : iterable) {
            sb.append(nextSeparator).append(from(obj, useInternalToString));
            nextSeparator = ELEMENT_SEPARATOR;
        }
        sb.append(RIGHT_SQUARE_BRACKET);
        return sb.toString();
    }

    private static String from(Object[] array, boolean useInternalToString) {
        if (array == null) {
            return NULL;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(array.getClass().getSimpleName()).append(LEFT_SQUARE_BRACKET);
        String nextSeparator = "";
        for (Object obj : array) {
            sb.append(nextSeparator).append(from(obj, useInternalToString));
            nextSeparator = ELEMENT_SEPARATOR;
        }
        sb.append(RIGHT_SQUARE_BRACKET);
        return sb.toString();
    }

    private static String from(byte[] array) {
        if (array == null) {
            return NULL;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(array.getClass().getSimpleName()).append(LEFT_SQUARE_BRACKET);
        String nextSeparator = "";
        for (byte b : array) {
            sb.append(nextSeparator).append(new String(new byte[]{b}));
            nextSeparator = ELEMENT_SEPARATOR;
        }
        sb.append(RIGHT_SQUARE_BRACKET);
        return sb.toString();
    }

    private static String from(short[] array) {
        if (array == null) {
            return NULL;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(array.getClass().getSimpleName()).append(LEFT_SQUARE_BRACKET);
        String nextSeparator = "";
        for (short s : array) {
            sb.append(nextSeparator).append(s);
            nextSeparator = ELEMENT_SEPARATOR;
        }
        sb.append(RIGHT_SQUARE_BRACKET);
        return sb.toString();
    }

    private static String from(int[] array) {
        if (array == null) {
            return NULL;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(array.getClass().getSimpleName()).append(LEFT_SQUARE_BRACKET);
        String nextSeparator = "";
        for (int i : array) {
            sb.append(nextSeparator).append(i);
            nextSeparator = ELEMENT_SEPARATOR;
        }
        sb.append(RIGHT_SQUARE_BRACKET);
        return sb.toString();
    }

    private static String from(long[] array) {
        if (array == null) {
            return NULL;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(array.getClass().getSimpleName()).append(LEFT_SQUARE_BRACKET);
        String nextSeparator = "";
        for (long l : array) {
            sb.append(nextSeparator).append(l);
            nextSeparator = ELEMENT_SEPARATOR;
        }
        sb.append(RIGHT_SQUARE_BRACKET);
        return sb.toString();
    }

    private static String from(float[] array) {
        if (array == null) {
            return NULL;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(array.getClass().getSimpleName()).append(LEFT_SQUARE_BRACKET);
        String nextSeparator = "";
        for (float f : array) {
            sb.append(nextSeparator).append(f);
            nextSeparator = ELEMENT_SEPARATOR;
        }
        sb.append(RIGHT_SQUARE_BRACKET);
        return sb.toString();
    }

    private static String from(double[] array) {
        if (array == null) {
            return NULL;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(array.getClass().getSimpleName()).append(LEFT_SQUARE_BRACKET);
        String nextSeparator = "";
        for (double d : array) {
            sb.append(nextSeparator).append(d);
            nextSeparator = ELEMENT_SEPARATOR;
        }
        sb.append(RIGHT_SQUARE_BRACKET);
        return sb.toString();
    }

    private static String from(boolean[] array) {
        if (array == null) {
            return NULL;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(array.getClass().getSimpleName()).append(LEFT_SQUARE_BRACKET);
        String nextSeparator = "";
        for (boolean b : array) {
            sb.append(nextSeparator).append(b);
            nextSeparator = ELEMENT_SEPARATOR;
        }
        sb.append(RIGHT_SQUARE_BRACKET);
        return sb.toString();
    }

    private static String from(char[] array) {
        if (array == null) {
            return NULL;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(array.getClass().getSimpleName()).append(LEFT_SQUARE_BRACKET);
        String nextSeparator = "";
        for (char c : array) {
            sb.append(nextSeparator).append(c);
            nextSeparator = ELEMENT_SEPARATOR;
        }
        sb.append(RIGHT_SQUARE_BRACKET);
        return sb.toString();
    }

    private static boolean hasOverrideToString(Object obj) {
        try {
            return obj.getClass().getMethod(TO_STRING_METHOD).getDeclaringClass().equals(obj.getClass());
        } catch (NoSuchMethodException impossible) {
            return false;
        }
    }
}
