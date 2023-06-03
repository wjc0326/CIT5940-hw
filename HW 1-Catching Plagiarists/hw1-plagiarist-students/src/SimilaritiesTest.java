import org.junit.Test;

import static org.junit.Assert.*;

public class SimilaritiesTest {

    @Test
    public void compareTo() {
        Similarities s1 = new Similarities("abc", "def");
        Similarities s2 = new Similarities("abc", "def");
        assertEquals(0, s1.compareTo(s2));
        assertEquals(0, s2.compareTo(s1));

        Similarities s4 = new Similarities("def", "abc");
        assertEquals(0, s4.compareTo(s1));

        Similarities s3 = new Similarities("abc", "lmn");
        assertTrue(s1.compareTo(s3) < 0);
        assertTrue(s2.compareTo(s3) < 0);
    }
}
