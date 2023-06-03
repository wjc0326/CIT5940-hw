
public class HuffTree
    implements Comparable
{
    private IHuffBaseNode root;


    /**
     * Constructors
     *
     * @param el
     * @param wt
     */
    public HuffTree(int el, int wt)
    {
        root = new HuffLeafNode(el, wt);

    }


    // ----------------------------------------------------------
    /**
     * Create a new HuffTree object.
     *
     * @param l
     * @param r
     * @param wt
     */
    public HuffTree(IHuffBaseNode l, IHuffBaseNode r, int wt)
    {
        root = new HuffInternalNode(l, r, wt);
    }


    // ----------------------------------------------------------
    /**
     * Place a description of your method here.
     *
     * @return the root
     */
    public IHuffBaseNode root()
    {
        return root;
    }


    /**
     * @param r the new root
     */
    public void setRoot(IHuffBaseNode r)
    {
        root = r;
    }


    // ----------------------------------------------------------
    /**
     * Place a description of your method here.
     *
     * @return
     */
    public int weight() // Weight of tree is weight of root
    {
        return root.weight();
    }


    /**
     * @return the size of the huffman tree when written into a file
     */
    public int size()
    {
        return size(root);
    }


    private int size(IHuffBaseNode n)
    {
        if (n == null)
            return 0;
        if (n instanceof HuffLeafNode)
            return 10;
        return 1 + size(((HuffInternalNode)n).left())
            + size(((HuffInternalNode)n).right());
    }


    @Override
    public int compareTo(Object t)
    {
        HuffTree that = (HuffTree)t;
        if (root.weight() < that.weight())
            return -1;
        else if (root.weight() == that.weight())
            return 0;
        else
            return 1;
    }

}
