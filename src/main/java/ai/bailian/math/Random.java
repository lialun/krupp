package ai.bailian.math;

/**
 * 生成随机数
 */
public class Random {

    /**
     * 生成范围为 (min,max) 的随机数
     *
     * @param min 最小值
     * @param max 最大值
     * @return 随机数, 范围(min, max)
     */
    public static int nextIntOpen(int min, int max) {
        if (min == max) {
            throw new IllegalArgumentException("min and max can't equal");
        }
        if (min > max) {
            throw new IllegalArgumentException("min must be lesser than max");
        }
        return new java.util.Random().nextInt(max - min - 1) + min + 1;
    }

    /**
     * 生成范围为 [min,max] 的随机数
     *
     * @param min 最小值
     * @param max 最大值
     * @return 随机数, 范围[min, max]
     */
    public static int nextIntClose(int min, int max) {
        if (min == max) {
            return min;
        }
        if (min > max) {
            throw new IllegalArgumentException("min must be lesser than max");
        }
        return new java.util.Random().nextInt(max - min + 1) + min;
    }

    /**
     * 生成范围为 (min,max] 的随机数
     *
     * @param min 最小值
     * @param max 最大值
     * @return 随机数, 范围(min, max]
     */
    public static int nextIntOpenClose(int min, int max) {
        if (min == max) {
            throw new IllegalArgumentException("min and max can't equal");
        }
        if (min > max) {
            throw new IllegalArgumentException("min must be lesser than max");
        }
        return new java.util.Random().nextInt(max - min) + min + 1;
    }

    /**
     * 生成范围为 [min,max) 的随机数
     *
     * @param min 最小值
     * @param max 最大值
     * @return 随机数, 范围[min, max)
     */
    public static int nextIntCloseOpen(int min, int max) {
        if (min == max) {
            throw new IllegalArgumentException("min and max can't equal");
        }
        if (min > max) {
            throw new IllegalArgumentException("min must be lesser than max");
        }
        return new java.util.Random().nextInt(max - min) + min;
    }
}
