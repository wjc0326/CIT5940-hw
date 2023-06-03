import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class AutocompleteTest {

    @Test
    public void addWord() {
        Autocomplete auto = new Autocomplete();
        auto.addWord("add", 10);

        // test for root node
        assertEquals(1, auto.getRoot().getPrefixes());
        assertEquals(0, auto.getRoot().getWords());

        // test for added nodes
        assertEquals(26, auto.getRoot().getReferences().length);
        Node aNode = auto.getRoot().getReferences()[0];
        assertNotNull(aNode);
        assertNull(auto.getRoot().getReferences()[1]);

        // 'a'
        assertEquals("", aNode.getTerm().getTerm());
        assertEquals(0, aNode.getTerm().getWeight());
        assertEquals(1, aNode.getPrefixes());
        assertEquals(0, aNode.getWords());

        assertEquals(26, aNode.getReferences().length);
        Node dNode = aNode.getReferences()[3];
        assertNotNull(dNode);
        assertNull(aNode.getReferences()[0]);

        // 'd'
        assertEquals("", dNode.getTerm().getTerm());
        assertEquals(0, dNode.getTerm().getWeight());
        assertEquals(1, dNode.getPrefixes());
        assertEquals(0, dNode.getWords());

        assertEquals(26, dNode.getReferences().length);
        Node ddNode = dNode.getReferences()[3];
        assertNotNull(ddNode);
        assertNull(dNode.getReferences()[0]);

        // 'd'
        assertEquals("add", ddNode.getTerm().getTerm());
        assertEquals(10, ddNode.getTerm().getWeight());
        assertEquals(1, ddNode.getPrefixes());
        assertEquals(1, ddNode.getWords());

        assertEquals(26, ddNode.getReferences().length);
        assertNull(ddNode.getReferences()[0]);
        assertNull(ddNode.getReferences()[3]);

        // test for another word
        auto.addWord("Addy", 15);
        assertEquals(2, auto.getRoot().getPrefixes());
        assertEquals(2, aNode.getPrefixes());
        assertEquals(2, ddNode.getPrefixes());
        assertEquals(1, ddNode.getWords());

        Node yNode = ddNode.getReferences()[24];
        assertNotNull(yNode);
        assertEquals(15, yNode.getTerm().getWeight());
        assertEquals(1, yNode.getWords());
        assertEquals("addy", yNode.getTerm().getTerm());
    }

    @Test
    public void buildTrie() {
        Autocomplete auto = new Autocomplete();
        Node root = auto.buildTrie("./test.txt", 5);
//        Node root = auto.buildTrie("/autograder/submission/test.txt", 5);
        Node aNode = root.getReferences()[0];
        Node dNode = aNode.getReferences()[3];
        Node ddNode = dNode.getReferences()[3];
        Node yNode = ddNode.getReferences()[24];

        assertEquals(3, aNode.getPrefixes());
        assertEquals(2, ddNode.getPrefixes());
        assertEquals(1, ddNode.getWords());
        assertEquals("add", ddNode.getTerm().getTerm());

        assertEquals(15, yNode.getTerm().getWeight());
        assertEquals(1, yNode.getWords());
        assertEquals("addy", yNode.getTerm().getTerm());
    }

    @Test
    public void getSubTrie() {
        Autocomplete auto = new Autocomplete();
        Node root = auto.buildTrie("./test.txt", 5);
//        Node root = auto.buildTrie("/autograder/submission/test.txt", 5);

        // test for null
        Node subTrieNode = auto.getSubTrie(null);
        assertNull(subTrieNode);

        // test for root
        subTrieNode = auto.getSubTrie("");
        assertEquals(root, subTrieNode);

        subTrieNode = auto.getSubTrie("app");
        assertEquals(1, subTrieNode.getWords());
        assertEquals("app", subTrieNode.getTerm().getTerm());
        assertEquals(20, subTrieNode.getTerm().getWeight());
        assertEquals(1, subTrieNode.getPrefixes());
    }

    @Test
    public void countPrefixes() {
        Autocomplete auto = new Autocomplete();
        Node root = auto.buildTrie("./test.txt", 5);
//        Node root = auto.buildTrie("/autograder/submission/test.txt", 5);

        // test for null
        int count = auto.countPrefixes(null);
        assertEquals(0, count);

        // test for root
        count = auto.countPrefixes("");
        assertEquals(4, count);

        count = auto.countPrefixes("a");
        assertEquals(3, count);
    }

    @Test
    public void getSuggestions() {
        Autocomplete auto = new Autocomplete();
        Node root = auto.buildTrie("./test.txt", 5);
//        Node root = auto.buildTrie("/autograder/submission/test.txt", 5);


        // check for edge case
        List<ITerm> resInvalid = auto.getSuggestions("_");
        assertEquals(0, resInvalid.size());

        List<ITerm> res = auto.getSuggestions("a");
        assertEquals(3, res.size());
        assertEquals(10, res.get(0).getWeight());
        assertEquals("add", res.get(0).getTerm());
        assertEquals(15, res.get(1).getWeight());
        assertEquals("addy", res.get(1).getTerm());
        assertEquals(20, res.get(2).getWeight());
        assertEquals("app", res.get(2).getTerm());
    }
}