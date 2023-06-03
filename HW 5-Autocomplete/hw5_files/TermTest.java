import org.junit.Before;

import java.util.ArrayList;
import java.util.*;

import static org.junit.Assert.*;

public class TermTest {

    List<ITerm> l;

    @Before
    public void setUp() throws Exception {
        l = new ArrayList<ITerm>();
        ITerm t1 = new Term("apple", 3);
        ITerm t2 = new Term("addition", 5);
        ITerm t3 = new Term("before", 6);
        ITerm t4 = new Term("application", 4);
        t4.setTerm("application");
        t4.setWeight(4);
        l.add(t1);
        l.add(t2);
        l.add(t3);
        l.add(t4);
    }

    @org.junit.Test
    public void compareTo() {
        Collections.sort(l, ITerm.byReverseWeightOrder());
        ITerm tt1 = l.get(0);
        assertEquals("before", tt1.getTerm());
        assertEquals(6, tt1.getWeight());
        ITerm tt2 = l.get(1);
        assertEquals("addition", tt2.getTerm());
        assertEquals(5, tt2.getWeight());
        ITerm tt3 = l.get(2);
        assertEquals("application", tt3.getTerm());
        assertEquals(4, tt3.getWeight());
        ITerm tt4 = l.get(3);
        assertEquals("apple", tt4.getTerm());
        assertEquals(3, tt4.getWeight());

        Collections.sort(l, ITerm.byPrefixOrder(15));
        tt1 = l.get(0);
        assertEquals("addition", tt1.getTerm());
        assertEquals(5, tt1.getWeight());
        tt2 = l.get(1);
        assertEquals("apple", tt2.getTerm());
        assertEquals(3, tt2.getWeight());
        tt3 = l.get(2);
        assertEquals("application", tt3.getTerm());
        assertEquals(4, tt3.getWeight());
        tt4 = l.get(3);
        assertEquals("before", tt4.getTerm());
        assertEquals(6, tt4.getWeight());

        Collections.sort(l, ITerm.byPrefixOrder(3));
        tt1 = l.get(0);
        assertEquals("addition", tt1.getTerm());
        assertEquals(5, tt1.getWeight());
        tt2 = l.get(1);
        assertEquals("apple", tt2.getTerm());
        assertEquals(3, tt2.getWeight());
        tt3 = l.get(2);
        assertEquals("application", tt3.getTerm());
        assertEquals(4, tt3.getWeight());
        assertEquals("before", tt4.getTerm());
        assertEquals(6, tt4.getWeight());

        // test for compareTo() method in Term class
        Collections.sort(l);
        tt1 = l.get(0);
        assertEquals("addition", tt1.getTerm());
        assertEquals(5, tt1.getWeight());
        tt2 = l.get(1);
        assertEquals("apple", tt2.getTerm());
        assertEquals(3, tt2.getWeight());
        tt3 = l.get(2);
        assertEquals("application", tt3.getTerm());
        assertEquals(4, tt3.getWeight());
        tt4 = l.get(3);
        assertEquals("before", tt4.getTerm());
        assertEquals(6, tt4.getWeight());
    }

    @org.junit.Test
    public void testToString() {
        Collections.sort(l);
        assertEquals("5" + "\t" + "addition", l.get(0).toString());
    }
}