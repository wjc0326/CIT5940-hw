import java.io.IOException;
import java.io.InputStream;

/**
 * Abstraction/interface to allow trees to be made in more
 * than one setting, and to export the treemaker as an object.
 * For example, a tree can be made from character-counts using
 * a priority queue. Alternatively, a tree could be made from
 * an input stream when unhuffing if the huff process stored the
 * tree in the file in some format.
 * <P>
 * Typically classes that implement this interface will supply
 * state to the class via a constuctor or some other method so
 * that makeRoot will work (since makeRoot has no parameters).
 *
 * @author Owen Astrachan, Eric Fouh
 */
public interface ITreeMaker {
    /**
     * Return the  Huffman/coding tree.
     * @return the Huffman tree
     */
    public HuffTree makeHuffTree(InputStream stream) throws IOException;
}
