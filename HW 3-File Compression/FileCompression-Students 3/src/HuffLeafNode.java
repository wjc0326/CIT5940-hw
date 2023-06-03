
/**
 * @author ericfouh
 *
 */
public class HuffLeafNode
    implements IHuffBaseNode
{

    private int element; // Element for this node
    private int  weight;  // Weight for this node


    /** Constructor
     * @param el
     * @param wt */
    public HuffLeafNode(int el, int wt)
    {
        element = el;
        weight = wt;
    }


    /** @return The element value */
    int element()
    {
        return element;
    }


    /** @return The weight */
    public int weight()
    {
        return weight;
    }


    /** Return true */
    public boolean isLeaf()
    {
        return true;
    }


    @Override
    public int compareTo(Object o)
    {
        if (o instanceof HuffLeafNode)
            return this.weight() - ((HuffLeafNode)o).weight();
        return 0;
    }

}
