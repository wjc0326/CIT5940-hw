import org.junit.Test;

import static org.junit.Assert.*;

public class SimilaritiesComparerTest {

    private SimilaritiesComparer similaritiesComparer = new SimilaritiesComparer();

    @Test
    public void testEqual() {
        Similarities s1 = new Similarities("abc", "def");
        Similarities s2 = new Similarities("abc", "def");
        int result = similaritiesComparer.compare(s1, s2);
        assertTrue("expected to be equal", result == 0);
    }

    @Test
    public void testLessThan() {
        Similarities s3 = new Similarities("abc", "def");
        Similarities s4 = new Similarities("abc", "def");
        s3.setCount(1);
        int result = similaritiesComparer.compare(s3, s4);
        assertTrue("expected to be less than", result <= -1);

        Similarities s5 = new Similarities("abc", "def");
        Similarities s6 = new Similarities("ABC", "abc");
        result = similaritiesComparer.compare(s5, s6);
        assertTrue("expected to be less than", result <= -1);
    }

    @Test
    public void testGreaterThan() {
        Similarities s7 = new Similarities("abc", "def");
        Similarities s8 = new Similarities("abc", "DEF");
        s8.setCount(1);
        int result = similaritiesComparer.compare(s7, s8);
        assertTrue("expected to be greater than", result >= 1);
    }

}


