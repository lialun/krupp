package ai.bailian.taple;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * A tuple of three elements.
 *
 * @author lialun
 */
public class Triplet<A, B, C> {
    private final A value0;
    private final B value1;
    private final C value2;

    public static <A, B, C> Triplet<A, B, C> with(final A value0, final B value1, final C value2) {
        return new Triplet<>(value0, value1, value2);
    }

    public Triplet(final A value0, final B value1, final C value2) {
        this.value0 = value0;
        this.value1 = value1;
        this.value2 = value2;
    }

    public A getValue0() {
        return this.value0;
    }

    public B getValue1() {
        return this.value1;
    }

    public C getValue2() {
        return this.value2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Triplet)) {
            return false;
        }
        Triplet<?, ?, ?> triplet = (Triplet<?, ?, ?>) o;
        return Objects.equals(getValue0(), triplet.getValue0()) &&
                Objects.equals(getValue1(), triplet.getValue1()) &&
                Objects.equals(getValue2(), triplet.getValue2());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue0(), getValue1(), getValue2());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Triplet.class.getSimpleName() + "[", "]")
                .add("value0=" + value0)
                .add("value1=" + value1)
                .add("value2=" + value2)
                .toString();
    }
}
