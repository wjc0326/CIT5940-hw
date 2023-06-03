
// -------------------------------------------------------------------------
/**
 * Write a one-sentence summary of your class here. Follow it with additional
 * details about its purpose, what abstraction it represents, and how to use it.
 *
 * @author ericfouh
 * @version Mar 16, 2016
 */
public interface IHuffBaseNode extends Comparable
{
    // ----------------------------------------------------------
    /**
     * Place a description of your method here.
     *
     * @return
     */
    public boolean isLeaf();


    // ----------------------------------------------------------
    /**
     * Place a description of your method here.
     *
     * @return
     */
    public int weight();
}
