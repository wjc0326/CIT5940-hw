import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Autocomplete implements IAutocomplete {

    private Node root;
    private int k;

    public Autocomplete() {
        this.root = new Node("", 0);
    }

    private boolean isValid(String prefix) {
        for (int i = 0; i < prefix.length(); i++) {
            if (!Character.isLetter(prefix.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void addWord(String word, long weight) {
        // If the word contains an invalid character, simply do nothing.
        if (!isValid(word)) {
            return;
        }

        Node node = root;
        // convert the string to lowercase
        String lowerWord = word.toLowerCase();

        for (int j = 0; j < lowerWord.length(); j++) {
            char c = lowerWord.charAt(j);
            int pos = c - 'a';

            // add 1 to prefix of curr node
            node.setPrefixes(node.getPrefixes() + 1);

            // If the curr char not in corresponding position of curr node's reference list,
            // add it to the reference
            if (node.getReferences()[pos] == null) {
                // change the reference of curr node
                Node newNode = new Node();
                Node[] newRef = node.getReferences();
                newRef[pos] = newNode;
                node.setReferences(newRef);
            }
            node = node.getReferences()[pos];
        }
        // reach the last char
        node.setTerm(new Term(lowerWord, weight));
        node.setPrefixes(1);
        node.setWords(1);
    }

    @Override
    public Node buildTrie(String filename, int k) {
        // set the maximum number of suggestions that should be displayed
        this.k = k;

        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = br.readLine()) != null) {
                String[] content = line.trim().split("\t");
                if (content.length == 2) {
                    // process the line
                    long weight = Long.parseLong(content[0]);
                    String term = content[1];
                    addWord(term, weight);
                }
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return root;
    }

    public int numberSuggestions() {
        return k;
    }

    @Override
    public Node getSubTrie(String prefix) {
        if (prefix == null || !isValid(prefix)) {
            return null;
        }
        if (prefix.equals("")) {
            return root;
        }

        Node node = root;
        // convert the string to lowercase
        String lowerWord = prefix.toLowerCase();

        for (int j = 0; j < lowerWord.length(); j++) {
            char c = lowerWord.charAt(j);
            int pos = c - 'a';

            // If the curr char not in corresponding position of curr node's reference list,
            // return null -> not found
            if (node.getReferences()[pos] == null) {
                return null;
            }
            node = node.getReferences()[pos];
        }

        // reach the last char
        return node;
    }

    @Override
    public int countPrefixes(String prefix) {
        if (prefix == null || !isValid(prefix)) {
            return 0;
        }
        if (prefix.equals("")) {
            return root.getPrefixes();
        }

        Node subTrieNode = getSubTrie(prefix);
        return subTrieNode.getPrefixes();
    }

    @Override
    public List<ITerm> getSuggestions(String prefix) {
        List<ITerm> sug = new ArrayList<>();

        if (prefix == null || !isValid(prefix)) {
            return sug;
        }
        Node subTrieRoot = getSubTrie(prefix);
        if (subTrieRoot == null) {
            return sug;
        }

        // Create a queue for BFS
        LinkedList<Node> queue = new LinkedList<Node>();
        queue.offer(subTrieRoot);

        while (!queue.isEmpty()) {
            // Dequeue from the queue and check whether the curr block hasn't been divided
            Node curr = queue.poll();

            // if meet the word node
            if (curr.getWords() == 1) {
                sug.add(new Term(curr.getTerm().getTerm(), curr.getTerm().getWeight()));
            }
            for (int i = 0; i < 26; i++) {
                if (curr.getReferences()[i] != null) {
                    queue.offer(curr.getReferences()[i]);
                }
            }
        }
        Collections.sort(sug);

        return sug;
    }

    /**
     * used for testing
     */
    public Node getRoot() {
        return this.root;
    }
}
