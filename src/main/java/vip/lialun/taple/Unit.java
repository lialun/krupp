package vip.lialun.taple;

import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * A tuple of one element.
 *
 * @author lialun
 */
public class Unit<A> implements Serializable {
    private final A value0;

    public static <A> Unit<A> with(final A value0) {
        return new Unit<>(value0);
    }

    public Unit(final A value0) {
        this.value0 = value0;
    }

    public A getValue0() {
        return this.value0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Unit<?> unit = (Unit<?>) o;
        return Objects.equals(value0, unit.value0);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value0);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Unit.class.getSimpleName() + "[", "]")
                .add("value0=" + value0)
                .toString();
    }
}