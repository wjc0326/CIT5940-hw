import java.awt.*;
import java.util.*;

public class Game implements IGame {

    private int    max_depth;
    private Color  target;
    private IBlock root;


    public Game(int max_depth, Color target) {
        this.max_depth = max_depth;
        this.target = target;
        this.root = randomInit();
    }


    /**
     * @return the maximum dept of this blocky board.
     */
    @Override
    public int maxDepth() {
        return this.max_depth;
    }

    /**
     * initializes a random board game. Details about how to approach
     * this are available in the assignment instructions; there is no
     * specific output that you need to generate, but calls to this
     * method should generally result in "interesting" game boards.
     *
     * @return the root of the tree of blocks
     */
    @Override
    public IBlock randomInit() {
        root = new Block(new Point(0, 0), new Point(8, 8), 0, null);

        // Since smash the root block is not permitted
        // we need to add the blocks with depth 1 by hand
        Block tl = new Block(new Point(0, 0), new Point(4, 4), 1, null);
        int randColor = (int) (Math.random() * IBlock.COLORS.length);
        tl.setColor(IBlock.COLORS[randColor]);
        tl.setParent(root);

        Block tr = new Block(new Point(4, 0), new Point(8, 4), 1, null);
        randColor = (int) (Math.random() * IBlock.COLORS.length);
        tr.setColor(IBlock.COLORS[randColor]);
        tr.setParent(root);

        Block bl = new Block(new Point(0, 4), new Point(4, 8), 1, null);
        randColor = (int) (Math.random() * IBlock.COLORS.length);
        bl.setColor(IBlock.COLORS[randColor]);
        bl.setParent(root);

        Block br = new Block(new Point(4, 4), new Point(8, 8), 1, null);
        randColor = (int) (Math.random() * IBlock.COLORS.length);
        br.setColor(IBlock.COLORS[randColor]);
        br.setParent(root);

        ((Block)root).setTopLeftTree(tl);
        ((Block)root).setTopRightTree(tr);
        ((Block)root).setBotLeftTree(bl);
        ((Block)root).setBotRightTree(br);

        int upperBound = 4;

        // generate a random number between lower bound and upper bound (1, 2, 3, 4)
        Random rand = new Random();
        int randNum = rand.nextInt(upperBound) + 1;
        Block randBlock = (Block)getBlock(randNum);

        // if the random chosen block is not yet at max_depth, it can be subdivided
        while (randBlock.depth() < max_depth) {
            // if current block hasn't been smashed, update the upperBound and smash it
            if (randBlock.children().size() == 0) {
                upperBound += 4;
                randBlock.smash(max_depth);
            }
            randNum = rand.nextInt(upperBound) + 1;
            randBlock = (Block)getBlock(randNum);
        }

        // if this random picked block is not going to be subdivided, pick a random color
        randColor = (int) (Math.random() * IBlock.COLORS.length);
        randBlock.setColor(IBlock.COLORS[randColor]);

        return root;
    }

    /**
     * Traverse the tree of blocks to find a sub block based on its id
     *
     * @param pos the id of the block
     * @return the block with id pos or null
     */
    @Override
    public IBlock getBlock(int pos) {
        // Create a queue for BFS
        LinkedList<Block> queue = new LinkedList<Block>();

        // Initialize a counter
        int counter = 0;
        queue.offer((Block)root);

        while (!queue.isEmpty()) {
            // Dequeue from the queue to check whether it is the target block
            Block parent = queue.poll();
            if (counter == pos) {
                return parent;
            }

            // Enqueue the children of the dequeued block to the queue
            if (parent.children().size() != 0) {
                for (IBlock child : parent.children()) {
                    queue.offer((Block)child);
                }
            }

            counter += 1;
        }
        return null;
    }

    /**
     * @return the root of the quad tree representing this
     * blockly board
     *
     * @implNote getRoot() == getBlock(0)
     */
    @Override
    public IBlock getRoot() {
        return getBlock(0);
    }

    /**
     * The two blocks must be at the same level / have the same size.
     * We should be able to swap a block with no sub blocks with
     * one with sub blocks.
     *
     *
     * @param x the block to swap
     * @param y the other block to swap
     */
    @Override
    public void swap(int x, int y) {
        Block block1 = (Block)getBlock(x);
        Block block2 = (Block)getBlock(y);

        if (block1 != null && block2 != null && block1.depth() == block2.depth()) {
            // use moveXY() function to move these two blocks
            int dx = block2.getTopLeft().getX() - block1.getTopLeft().getX();
            int dy = block2.getBotRight().getY() - block1.getBotRight().getY();
            block1.moveXY(dx, dy);
            block2.moveXY(-dx, -dy);

            // swap the position in each parent
            Block parent1 = (Block)block1.getParent();
            Block parent2 = (Block)block2.getParent();

            // 0:tl, 1:tr, 2:br, 3:bl
            int position1 = parent1.children().indexOf(block1);
            int position2 = parent2.children().indexOf(block2);

            switch (position1) {
                case 0:
                    parent1.setTopLeftTree(block2);
                    break;
                case 1:
                    parent1.setTopRightTree(block2);
                    break;
                case 2:
                    parent1.setBotRightTree(block2);
                    break;
                default:
                    parent1.setBotLeftTree(block2);
                    break;
            }

            switch (position2) {
                case 0:
                    parent2.setTopLeftTree(block1);
                    break;
                case 1:
                    parent2.setTopRightTree(block1);
                    break;
                case 2:
                    parent2.setBotRightTree(block1);
                    break;
                default:
                    parent2.setBotLeftTree(block1);
                    break;
            }

            // set parents
            block1.setParent(parent2);
            block2.setParent(parent1);
        }
    }

    /**
     * Turns (flattens) the quadtree into a 2D-array of blocks.
     * Each cell in the array represents a unit cell.
     * This method should not mutate the tree.
     * @return a 2D array of the tree
     */
    @Override
    public IBlock[][] flatten() {
        int rowNum = (int)Math.pow(2, max_depth);
        int colNum = rowNum;
        IBlock[][] board = new IBlock[rowNum][colNum];

        // Create a queue for BFS
        LinkedList<Block> queue = new LinkedList<Block>();
        queue.offer((Block)root);

        while (!queue.isEmpty()) {
            // Dequeue from the queue and check whether the curr block hasn't been divided
            Block parent = queue.poll();
            double coeff = Math.pow(2, max_depth) / root.getBotRight().getX();

            if (parent.children().size() == 0) {
                for (int i = (int)(parent.getTopLeft().getY() * coeff);
                     i < parent.getBotRight().getY() * coeff; i++) {
                    for (int j = (int)(parent.getTopLeft().getX() * coeff);
                         j < parent.getBotRight().getX() * coeff; j++) {
                        board[i][j] = parent;
                    }
                }
            } else {
                // Enqueue the children of the dequeued block to the queue
                for (IBlock child : parent.children()) {
                    queue.offer((Block)child);
                }
            }
        }

        return board;
    }

    /**
     * computes the scores based on perimeter blocks of the same color
     * as the target color.
     * The quadtree must be flattened first
     *
     * @return the score of the user (corner blocs count twice)
     */
    @Override
    public int perimeterScore() {
        int score = 0;
        int i;
        int j;

        double coeff = Math.pow(2, max_depth) / root.getBotRight().getX();
        int maxBound = (int)(root.getBotRight().getX() * coeff);
        IBlock[][]board = flatten();

        // check the left bound
        for (j = 0; j < maxBound; j++) {
            if (board[0][j].getColor() == target) {
                score += 1;
            }
        }

        // check the right bound
        for (j = 0; j < maxBound; j++) {
            if (board[maxBound - 1][j].getColor() == target) {
                score += 1;
            }
        }

        // check the upper bound
        for (i = 0; i < maxBound; i++) {
            if (board[i][0].getColor() == target) {
                score += 1;
            }
        }

        // check the lower bound
        for (i = 0; i < maxBound; i++) {
            if (board[i][maxBound - 1].getColor() == target) {
                score += 1;
            }
        }

        return score;
    }

    /**
     * This method will be useful to test your code
     * @param root the root of this blocky board
     */
    @Override
    public void setRoot(IBlock root) {
        this.root = root;
    }
}
