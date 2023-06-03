
// -------------------------------------------------------------------------
/**
 * Write a one-sentence summary of your class here. Follow it with additional
 * details about its purpose, what abstraction it represents, and how to use it.
 *
 * @author ericfouh
 * @version Mar 16, 2016
 */
public class HuffInternalNode
    implements IHuffBaseNode
{
    private int           weight;
    private IHuffBaseNode left;
    private IHuffBaseNode right;


    /** Constructor */
    HuffInternalNode(IHuffBaseNode l, IHuffBaseNode r, int wt)
    {
        left = l;
        right = r;
        weight = wt;
    }


    /** @return The left child */
    IHuffBaseNode left()
    {
        return left;
    }


    // ----------------------------------------------------------
    /**
     * Place a description of your method here.
     *
     * @param l
     */
    public void setLeft(IHuffBaseNode l)
    {
        left = l;
    }


    /** @return The right child */
    IHuffBaseNode right()
    {
        return right;
    }


    // ----------------------------------------------------------
    /**
     * Place a description of your method here.
     * @param r
     */
    public void setRight(IHuffBaseNode r)
    {
        right = r;
    }


    @Override
    public boolean isLeaf()
    {

        return false;
    }


    @Override
    public int weight()
    {

        return weight;
    }


    @Override
    public int compareTo(Object o)
    {
        // TODO Auto-generated method stub
        if (o instanceof HuffInternalNode)
            return this.weight() - ((HuffInternalNode)o).weight();
        return 0;
    }

}
