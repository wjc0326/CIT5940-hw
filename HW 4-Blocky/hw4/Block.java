import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Block implements IBlock {

    /**
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
     */

    //  The top left and bottom right points delimiting this block
    private Point topLeft;
    private Point botRight;

    /**
     *  If this block is not subdivided, <color> stores its color.
     *   Otherwise, <color> is <null> and this  block's sub-blocks
     *   store their individual colors.
     */
    private Color color;

    // The level of this block within the overall block structure.
    //    * The outermost block corresponding to the root of the tree is at level/depth zero.
    //    * If a block is at level i, its children are at level i+1.
    private int depth;

    private IBlock topLeftTree;
    private IBlock topRightTree;
    private IBlock botLeftTree;
    private IBlock botRightTree;

    private IBlock parent;

    /**
     * No-argument constructor. This should:
     * - Initialize the top left and bottom right to two dummy points at (0, 0)
     * - Set the depth to be 0
     * - Set all parent and child pointers to null
     *
     * Even if you don't use this constructor anywhere, it may be useful for testing.
     */
    public Block() {
        this.topLeft = new Point(0, 0);
        this.botRight = new Point(0, 0);
        this.color = null;
        this.depth = 0;
        this.parent = null;
        this.topLeftTree = null;
        this.topRightTree = null;
        this.botLeftTree = null;
        this.botRightTree = null;
    }

    // ----------------------------------------------------------
    /**
     * Create a new Quad object.
     *
     * @param topL   top left point / bound of this block
     * @param botR   bottom right point / bound of this block
     * @param depth  of this block
     * @param parent of this block
     */
    public Block(Point topL, Point botR, int depth, Block parent) {
        // call constructor with no arguments: Block()
        this();
        this.topLeft = topL;
        this.botRight = botR;
        this.depth = depth;
        this.parent = parent;
    }

    @Override
    public int depth() {
        return this.depth;
    }

    /**
     * smash this block into 4 sub block of random color. the depth of the new
     * blocks should be less than maximum depth
     *
     * @param maxDepth the max depth of this board/quadtree
     */
    @Override
    public void smash(int maxDepth) {
        // 1. the block is not top-level
        // 2. the depth of the block should be less than maxDepth, so that the depth of
        //    new blocks can be less than maxDepth
        // 3. only leaf nodes can be smashed (i.e. has no children)
        if (depth < maxDepth && children().size() == 0) {
            // calculate the center coordinate
            Point center = new Point(topLeft.getX() + ((botRight.getX() - topLeft.getX()) / 2),
                                     topLeft.getY() + ((botRight.getY() - topLeft.getY()) / 2));

            // create the sub-blocks
            topLeftTree = new Block(topLeft, center, depth + 1, this);
            topRightTree = new Block(new Point(center.getX(), topLeft.getY()),
                                     new Point(botRight.getX(), center.getY()),
                               depth + 1, this);
            botRightTree = new Block(center, botRight, depth + 1, this);
            botLeftTree = new Block(new Point(topLeft.getX(), center.getY()),
                                    new Point(center.getX(), botRight.getY()),
                              depth + 1, this);

            // deal with the color
            color = null;
            int colorNE = (int) (Math.random() * IBlock.COLORS.length);
            topLeftTree.setColor(IBlock.COLORS[colorNE]);

            int colorNW = (int) (Math.random() * IBlock.COLORS.length);
            topRightTree.setColor(IBlock.COLORS[colorNW]);

            int colorSW = (int) (Math.random() * IBlock.COLORS.length);
            botRightTree.setColor(IBlock.COLORS[colorSW]);

            int colorSE = (int) (Math.random() * IBlock.COLORS.length);
            botLeftTree.setColor(IBlock.COLORS[colorSE]);
        }
    }

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
    @Override
    public List<IBlock> children() {
        List<IBlock> childrenList = new ArrayList<>();
        // Internal node of Quad Tree has 0 or 4 children
        // so we only need to check whether one child is null
        if (this.topLeftTree != null) {
            childrenList.add(this.topLeftTree);
            childrenList.add(this.topRightTree);
            childrenList.add(this.botRightTree);
            childrenList.add(this.botLeftTree);
        }
        return childrenList;
    }

    /**
     * helper function to recursively move the children block
     */
    public Block moveXY(int dx, int dy) {
        // move the current block
        int newTopLeftX = this.getTopLeft().getX() + dx;
        int newTopLeftY = this.getTopLeft().getY() + dy;
        this.topLeft = new Point(newTopLeftX, newTopLeftY);

        int newBotRightX = this.getBotRight().getX() + dx;
        int newBotRightY = this.getBotRight().getY() + dy;
        this.botRight = new Point(newBotRightX, newBotRightY);

        // recursively move the children blocks, no need to rotate the children blocks
        if (children().size() != 0) {
            topLeftTree = ((Block)topLeftTree).moveXY(dx, dy);
            topRightTree = ((Block)topRightTree).moveXY(dx, dy);
            botLeftTree = ((Block)botLeftTree).moveXY(dx, dy);
            botRightTree = ((Block)botRightTree).moveXY(dx, dy);
        }

        return this;
    }

    /**
     * rotate this block clockwise.
     *
     *  To rotate, first move the children's pointers
     *  then recursively update the top left and
     *  bottom right points of each child.
     *
     *  You may want to write a helper method that
     *  takes in a Block and its new topLeft and botRight and
     *  sets these values for the current Block before calculating
     *  the values for its children and recursively setting them.
     */
    @Override
    public void rotate() {
        if (children().size() != 0) {
            Point topLeftoftopLeftTree = topLeftTree.getTopLeft();
            Point topLeftoftopRightTree = topRightTree.getTopLeft();
            int dist = topLeftoftopRightTree.getX() - topLeftoftopLeftTree.getX();

            Block topLeftTreeNew = ((Block)botLeftTree).moveXY(0, -dist);
            Block topRightTreeNew = ((Block)topLeftTree).moveXY(dist, 0);
            Block botRightTreeNew = ((Block)topRightTree).moveXY(0, dist);
            Block botLeftTreeNew = ((Block)botRightTree).moveXY(-dist, 0);
            setTopLeftTree(topLeftTreeNew);
            setTopRightTree(topRightTreeNew);
            setBotRightTree(botRightTreeNew);
            setBotLeftTree(botLeftTreeNew);
        }
    }



    /*
     * ========================
     *  Block getters
     *  You should implement these yourself.
     *  The implementations should be very simple.
     * ========================
     */

    @Override
    public Color getColor() {
        return this.color;
    }

    @Override
    public Point getTopLeft() {
        return this.topLeft;
    }

    @Override
    public Point getBotRight() {
        return this.botRight;
    }

    @Override
    public boolean isLeaf() {
        return this.children().size() == 0;
    }

    @Override
    public IBlock getTopLeftTree() {
        return this.topLeftTree;
    }

    @Override
    public IBlock getTopRightTree() {
        return this.topRightTree;
    }

    @Override
    public IBlock getBotLeftTree() {
        return this.botLeftTree;
    }

    @Override
    public IBlock getBotRightTree() {
        return this.botRightTree;
    }

    public IBlock getParent() {
        return this.parent;
    }

    /*
     * ========================
     *  Provided setters
     *  Don't delete these!
     *  Necessary for testing.
     * ========================
     */

    @Override
    public void setColor(Color c) {
        this.color = c;
    }

    public void setTopLeftTree(IBlock topLeftTree) {
        this.topLeftTree = topLeftTree;
    }

    public void setTopRightTree(IBlock topRightTree) {
        this.topRightTree = topRightTree;
    }

    public void setBotLeftTree(IBlock botLeftTree) {
        this.botLeftTree = botLeftTree;
    }

    public void setBotRightTree(IBlock botRightTree) {
        this.botRightTree = botRightTree;
    }

    public void setParent(IBlock parent) {
        this.parent = parent;
    }
}
