import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class CharCounterTest {

    ICharCounter cc = new CharCounter();
    InputStream ins;
    {
        try {
            ins = new ByteArrayInputStream("charcountertest".getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    int actualSize;
    {
        try {
            actualSize = cc.countAll(ins);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @org.junit.Test
    public void getCount() {
//        Throwable exception =
//                assertThrows(IllegalArgumentException.class, () -> {
//                    cc.getCount(-1);
//                });
//        assertEquals("Invalid char", exception.getMessage());
//        // Test for PSEUDO_EOF
//        Throwable exception2 =
//                assertThrows(IllegalArgumentException.class, () -> {
//                    cc.getCount((1 << 8));
//                });
//        assertEquals("Invalid char", exception2.getMessage());
        assertEquals(2, cc.getCount('c'));
        assertEquals(1, cc.getCount('s'));
        assertEquals(3, cc.getCount('t'));
        assertEquals(0, cc.getCount('d'));
    }

    @org.junit.Test
    public void countAll() {
        assertEquals(15, actualSize);
    }

    @org.junit.Test
    public void add() {
        // the exist key
        cc.add('s');
        assertEquals(2, cc.getCount('s'));

        // the non-exist key
        cc.add('b');
        assertEquals(1, cc.getCount('b'));
    }

    @org.junit.Test
    public void set() {
        cc.set('s', 1);
        assertEquals(1, cc.getCount('s'));
    }

    @org.junit.Test
    public void clear() {
        cc.clear();
        assertEquals(0, cc.getTable().size());
    }

    @org.junit.Test
    public void getTable() {
        try {
            cc.countAll(ins);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(10, cc.getTable().size());
    }
}