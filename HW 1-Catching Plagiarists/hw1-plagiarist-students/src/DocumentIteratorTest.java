import org.junit.Test;
import java.io.*;
import static org.junit.Assert.*;

public class DocumentIteratorTest {

    Reader r = new StringReader("This      is        a        file.");
    DocumentIterator iterator = new DocumentIterator(r, 2);
    @Test
    public void hasNext() {
        assertTrue(iterator.hasNext());
    }

    @Test
    public void next() {
        assertEquals("thisis", iterator.next());
        assertEquals("isa", iterator.next());
        assertEquals("afile", iterator.next());
    }
}