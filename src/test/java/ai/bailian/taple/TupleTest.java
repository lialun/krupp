package ai.bailian.taple;

import ai.bailian.BaseTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class TupleTest extends BaseTest {
    @Test
    public void testUnit() {
        String val0 = "a";
        Unit<String> unit = new Unit<>(val0);
        Unit<String> unit2 = Unit.with(val0);
        assertEquals(unit, unit2);
        assertEquals(val0, unit.getValue0());
    }

    @Test
    public void testPair() {
        String val0 = "a";
        int val1 = 1;
        Pair<String, Integer> pair = new Pair<>(val0, val1);
        Pair<String, Integer> pair2 = Pair.with(val0, val1);
        assertEquals(pair, pair2);
        assertEquals(val0, pair.getValue0());
        assertEquals((Integer) val1, pair.getValue1());
    }

    @Test
    public void testTriplet() {
        String val0 = "a";
        int val1 = 1;
        Boolean val2 = false;
        Triplet<String, Integer, Boolean> triplet = new Triplet<>(val0, val1, val2);
        Triplet<String, Integer, Boolean> triplet2 = Triplet.with(val0, val1, val2);
        assertEquals(triplet, triplet2);
        assertEquals(val0, triplet.getValue0());
        assertEquals((Integer) val1, triplet.getValue1());
        assertEquals(val2, triplet.getValue2());
    }

    @Test
    public void testQuartet() {
        String val0 = "a";
        int val1 = 1;
        Boolean val2 = false;
        Double val3 = Double.MAX_VALUE;
        Quartet<String, Integer, Boolean, Double> quartet = new Quartet<>(val0, val1, val2, val3);
        Quartet<String, Integer, Boolean, Double> quartet2 = Quartet.with(val0, val1, val2, val3);
        assertEquals(quartet, quartet2);
        assertEquals(val0, quartet.getValue0());
        assertEquals((Integer) val1, quartet.getValue1());
        assertEquals(val2, quartet.getValue2());
        assertEquals(val3, quartet.getValue3());
    }
}
