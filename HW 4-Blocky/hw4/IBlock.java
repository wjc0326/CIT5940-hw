import java.awt.Color;
import java.util.List;

/**
 * === Class Attributes === 
 * bounds: 
 *  - topLeft 
 *  - botRight 
 *
 *  color: 
 *  - color 
 *  If this block is not subdivided, <color> stores its color.
 *   Otherwise, <color> is <null> and this block's sub-blocks
 *   store their individual colors. 
 *   
 * level/depth:
 * 
 * children: 
 *  - topLeftTree 
 *  - topRightTree 
 *  - botLeftTree 
 *  - botRightTree 
 *  
 *  The blocks into which this block is subdivided. 
 *  
 *  The children are returned in this order: 
 * upper-left child (NE),
 * upper-right child (NW), 
 * lower-right child (SW), 
 * lower-left child (SE).
 * 
 * parent: 
 *  - parent 
 *  The block that this block is directly within. 
 *
 *  Create getter and setters for all private fields
 *  use eclipse naming suggestion.
 *  
 *  
 * ===Representation Invariants === 
 * 
 * - children.size() == 0 or children.size() == 4 
 * 
 * - If this Block has children, 
 *      - their max_depth is the same as that of this Block, 
 *      - their size is half that of this Block, 
 *      - their level is one greater than that of this Block, 
 *      - their position is determined by the position and
 *        size of this Block, as defined in the Assignment handout, and 
 *      - this Block's color is null 
 *      
 * - If this Block has no children, 
 *      - its color is not null 
 *      - level <= max_depth 
 *      
 *===Constructor === 
 *
 *The Block class constructor have the following signature 
 *Block(Point topLeft, Point botRight, int depth, Block parent)
 * 
 * @author ericfouh
 */
public interface IBlock
{
    /**
     * the list of colors
     */
    public static final Color[] COLORS = { Color.BLUE, Color.RED, Color.WHITE,
        Color.YELLOW, Color.CYAN, Color.GRAY, Color.GREEN, Color.PINK };


    /**
     * @return the depth of this block
     */
    public int depth();


    /**
     * smash this block into 4 sub block of random color. the depth of the new
     * blocks should be less than maximum depth
     * 
     * @param maxDepth the max depth of this board/quadtree
     */
    public void smash(int maxDepth);


    /**
     * used by {@link IGame#randomInit()} random_init
     * to keep track of sub blocks.
     * 
     * The children are returned in this order: 
     * upper-left child (NE),
     * upper-right child (NW), 
     * lower-right child (SW), 
     * lower-left child (SE).
     * 
     * @return the list of all the children of this block (clockwise order,
     *         starting with top left block)
     */
    public List<IBlock> children();


    /**
     * rotate this block clockwise.
     * 
     *  To rotate, first move the children's pointers
     * then recursively update the top left and
     *  bottom right points of each child.
     */
    public void rotate();


    /**
     * @return the color of this block
     */
    public Color getColor();


    /**
     * Changes the color of this block
     * 
     * @param c the new color
     */
    public void setColor(Color c);


    /**
     * @return top left point
     */
    public Point getTopLeft();


    /**
     * @return Bottom right point
     */
    public Point getBotRight();


    /**
     * @return true if this block has no children/sub blocks
     */
    public boolean isLeaf();
    
    /**
     * @return the top left sub block (NE)
     */
    public IBlock getTopLeftTree();

    /**
     * @return the top right sub block (NW)
     */
    public IBlock getTopRightTree();

    /**
     * @return the bottom left sub block (SE)
     */
    public IBlock getBotLeftTree();

    /**
     * @return the bottom right sub block (SW)
     */
    public IBlock getBotRightTree();
}
