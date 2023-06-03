import org.junit.Test;

import static org.junit.Assert.*;

public class NodeTest {

    @Test
    public void testNode() {
        // test for constructor with no arguments, and getters
        Node node1 = new Node();
        assertEquals("", node1.getTerm().getTerm());
        assertEquals(0, node1.getTerm().getWeight());
        assertEquals(0, node1.getPrefixes());
        assertEquals(0, node1.getWords());
        assertEquals(26, node1.getReferences().length);
        assertNull(node1.getReferences()[0]);

        // test for constructor with arguments, and setters
        Term term = new Term("an", 10);
        Node[] reference = new Node[26];
        reference[3] = new Node("and", 5);
        node1.setTerm(term);
        node1.setPrefixes(1);
        node1.setWords(1);
        node1.setReferences(reference);
        assertEquals("an", node1.getTerm().getTerm());
        assertEquals(10, node1.getTerm().getWeight());
        assertEquals(1, node1.getPrefixes());
        assertEquals(1, node1.getWords());
        assertEquals(26, node1.getReferences().length);
        assertEquals("and", node1.getReferences()[3].getTerm().getTerm());
        assertEquals(5, node1.getReferences()[3].getTerm().getWeight());
        assertNull(node1.getReferences()[0]);
    }
}