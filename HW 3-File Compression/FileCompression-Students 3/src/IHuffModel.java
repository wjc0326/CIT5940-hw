import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.io.File;
/**
 * The interface for the model that can be attached to a HuffmanView. Most of
 * the work done in huffing (and unhuffing) will be via a class that implements
 * this interface. The interface may need to be extended depending on the design
 * of the huffman program.
 * <P>
 * 
 * @author Owen Astrachan, Eric Fouh
 */



public interface IHuffModel
    extends IHuffConstants
{
    

    /**
     * Write a compressed version of the data read by the InputStream parameter,
     * -- if the stream is not the same as the stream last passed to initialize,
     * then compression won't be optimal, but will still work. If force is
     * false, compression only occurs if it saves space. If force is true
     * compression results even if no bits are saved.
     * 
     * @param inFile is the input stream to be compressed
     * @param outFile   specifies the OutputStream/file to be written with compressed data
     * @param force  indicates if compression forced
     * @return the size of the compressed file
     */
    public int write(String inFile, String outFile, boolean force);


    /**
     * Uncompress a previously compressed file.
     * 
     * @param inFile  is the compressed file to be uncompressed
     * @param outFile is where the uncompressed bits will be written
     * @return the size of the uncompressed file
     */
    public int uncompress(String inFile, String outFile);

}
