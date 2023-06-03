

/**
 * Encapsulate reading/writing a header of a compressed file in Huffman coding.
 * Clients should ensure that reading/writing the header in the same way
 * are encapsulated appropriately. For example, the simplest approach
 * to implementing this interface is likely storing 256 int values (or, more
 * generally ALPH_SIZE values) in the compressed file, where the i-th
 * int value represents the number of occurrences of the i-th character,
 * e.g., for ASCII 65 represents 'A' so the 66-th value written would be
 * the number of ocurrences of 'A' in a text file. In general, since binary
 * files can be compressed, the i-th value written is the number of occurrences
 * of the int i in the data, where 0 <= i < ALPH_SIZE.
 * <P>
 * In this simple scenario, an object would be intialized/constructed from
 * a source of counts, the counts would e written along with a magic number
 * from <code>writeHeader<code> and the magic number and counts would be
 * read from<code>readHeader</code>.
 * <P>
 * @author Owen Astrachan
 *
 */
import java.io.IOException;

public interface IHuffHeader extends IHuffConstants {
    
    /**
     * The number of bits in the header using the implementation, including
     * the magic number presumably stored.
     * @return the number of bits in the header
     */
    public int headerSize();
    
    /**
     * Write the header, including magic number and all bits needed to
     * reconstruct a tree, e.g., using <code>readHeader</code>.
     * @param out is where the header is written
     * @return the size of the header
     */
    public int writeHeader(BitOutputStream out);
    
    /**
     * Read the header and return an ITreeMaker object corresponding to
     * the information/header read.
     * @param in is source of bits for header
     * @return an ITreeMaker object representing the tree stored in the header
     * @throws IOException if the header is bad, e.g., wrong MAGIC_NUMBER, wrong
     * number of bits, I/O error occurs reading
     */
    public HuffTree readHeader(BitInputStream in) throws IOException;
}
