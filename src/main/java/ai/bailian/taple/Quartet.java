package ai.bailian.taple;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * A tuple of four elements.
 *
 * @author lialun
 */
public class Quartet<A, B, C, D> {
    private final A value0;
    private final B value1;
    private final C value2;
    private final D value3;

    public static <A, B, C, D> Quartet<A, B, C, D> with(final A value0, final B value1, final C value2, final D value3) {
        return new Quartet<>(value0, value1, value2, value3);
    }

    public Quartet(final A value0, final B value1, final C value2, final D value3) {
        this.value0 = value0;
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
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

    public D getValue3() {
        return this.value3;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Quartet)) {
            return false;
        }
        Quartet<?, ?, ?, ?> quartet = (Quartet<?, ?, ?, ?>) o;
        return Objects.equals(getValue0(), quartet.getValue0()) &&
                Objects.equals(getValue1(), quartet.getValue1()) &&
                Objects.equals(getValue2(), quartet.getValue2()) &&
                Objects.equals(getValue3(), quartet.getValue3());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue0(), getValue1(), getValue2(), getValue3());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Quartet.class.getSimpleName() + "[", "]")
                .add("value0=" + value0)
                .add("value1=" + value1)
                .add("value2=" + value2)
                .add("value3=" + value3)
                .toString();
    }
}
