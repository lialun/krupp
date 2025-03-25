package ai.bailian.taple;

import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * A tuple of two elements.
 */
public class Pair<A, B> implements Serializable {
    private final A value0;
    private final B value1;

    public static <A, B> Pair<A, B> with(final A value0, final B value1) {
        return new Pair<>(value0, value1);
    }

    public Pair(final A value0, final B value1) {
        this.value0 = value0;
        this.value1 = value1;
    }

    public A getValue0() {
        return this.value0;
    }

    public B getValue1() {
        return this.value1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pair)) {
            return false;
        }
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(getValue0(), pair.getValue0()) &&
                Objects.equals(getValue1(), pair.getValue1());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue0(), getValue1());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Pair.class.getSimpleName() + "[", "]")
                .add("value0=" + value0)
                .add("value1=" + value1)
                .toString();
    }
}