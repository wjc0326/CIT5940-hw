import org.junit.Test;
import java.awt.*;
import java.util.*;

import static org.junit.Assert.*;

public class GameTest {

    Game game = new Game(3, Color.RED);  // will call randomInit()

    @Test
    public void randomInitTest() {
        Block root = (Block)game.getRoot();
        assertEquals(0, root.depth());
        assertEquals(3, game.maxDepth());
        assertNotNull(root.getTopLeftTree());
        assertEquals(4, root.children().size());
    }

    @Test
    public void getBlockTest() {
        assertEquals(game.getRoot(), game.getBlock(0));
        assertEquals(game.getRoot().getTopLeftTree(), game.getBlock(1));
        assertEquals(game.getRoot().getTopRightTree(), game.getBlock(2));
        assertEquals(game.getRoot().getBotRightTree(), game.getBlock(3));
        assertEquals(game.getRoot().getBotLeftTree(), game.getBlock(4));
        assertNull(game.getBlock(100));
    }

    @Test
    public void swapTest() {
        Block tlOld = (Block)game.getRoot().getTopLeftTree();
        Block trOld = (Block)game.getRoot().getTopRightTree();

        game.swap(1, 2);
        assertEquals(game.getRoot().getTopRightTree(), tlOld);
        assertEquals(game.getRoot().getTopLeftTree(), trOld);

        // build a board manually
        game = new Game(3, Color.RED);
        game.setRoot(new Block(new Point(0, 0), new Point(8, 8), 0, null));
        Block root = (Block)game.getRoot();
        root.setTopLeftTree(new Block(new Point(0, 0), new Point(4, 4), 1, null));
        root.getTopLeftTree().setColor(Color.BLUE);
        ((Block)root.getTopLeftTree()).setParent(root);

        root.setTopRightTree(new Block(new Point(4, 0), new Point(8, 4), 1, null));
        root.getTopRightTree().setColor(Color.RED);
        ((Block)root.getTopRightTree()).setParent(root);

        root.setBotLeftTree(new Block(new Point(0, 4), new Point(4, 8), 1, null));
        root.getBotLeftTree().setColor(Color.YELLOW);
        ((Block)root.getBotLeftTree()).setParent(root);

        root.setBotRightTree(new Block(new Point(4, 4), new Point(8, 8), 1, null));
        root.getBotRightTree().setColor(Color.GRAY);
        ((Block)root.getBotRightTree()).setParent(root);

        Block tr = (Block)root.getTopRightTree();
        tr.smash(3);
        tr.getTopLeftTree().setColor(Color.GREEN);
        tr.getTopRightTree().setColor(Color.PINK);
        tr.getBotLeftTree().setColor(Color.RED);
        tr.getBotRightTree().setColor(Color.WHITE);

        game.swap(1, 4);
        assertEquals(Color.YELLOW, root.getTopLeftTree().getColor());
        assertEquals(Color.BLUE, root.getBotLeftTree().getColor());
        game.swap(4, 1);
        game.swap(1, 2);
        assertEquals(4, root.getTopLeftTree().children().size());
        assertEquals(0, root.getTopRightTree().children().size());
        assertEquals(Color.GREEN, root.getTopLeftTree().getTopLeftTree().getColor());
        assertNull(game.getBlock(1).getColor());
        assertEquals(Color.GREEN, game.getBlock(5).getColor());
        game.swap(2, 1);

        game.swap(6, 7);
        assertEquals(Color.WHITE, game.getBlock(6).getColor());
        assertEquals(Color.PINK, game.getBlock(7).getColor());
        game.swap(7, 6);
    }

    @Test
    public void flattenTest() {
        assertEquals(8, game.flatten().length);
        game = new Game(2, Color.RED);
        assertEquals(4, game.flatten().length);

        // build board manually
        game.setRoot(new Block(new Point(0, 0), new Point(8, 8), 0, null));
        Block root = (Block)game.getRoot();
        root.setTopLeftTree(new Block(new Point(0, 0), new Point(4, 4), 1, null));
        root.getTopLeftTree().setColor(Color.BLUE);
        ((Block)root.getTopLeftTree()).setParent(root);

        root.setTopRightTree(new Block(new Point(4, 0), new Point(8, 4), 1, null));
        root.getTopRightTree().setColor(Color.RED);
        ((Block)root.getTopRightTree()).setParent(root);

        root.setBotLeftTree(new Block(new Point(0, 4), new Point(4, 8), 1, null));
        root.getBotLeftTree().setColor(Color.YELLOW);
        ((Block)root.getBotLeftTree()).setParent(root);

        root.setBotRightTree(new Block(new Point(4, 4), new Point(8, 8), 1, null));
        root.getBotRightTree().setColor(Color.GRAY);
        ((Block)root.getBotRightTree()).setParent(root);

        IBlock[][] board = game.flatten();
        assertEquals(4, board.length);
        assertEquals(root.getTopLeftTree(), board[0][0]);
        assertEquals(root.getTopRightTree(), board[0][2]);
        assertEquals(root.getBotLeftTree(), board[2][0]);
        assertEquals(root.getBotRightTree(), board[2][2]);

    }

    @Test
    public void perimeterScoreTest() {
        game = new Game(2, Color.RED);

        // build board manually
        game.setRoot(new Block(new Point(0, 0), new Point(8, 8), 0, null));
        Block root = (Block)game.getRoot();
        root.setTopLeftTree(new Block(new Point(0, 0), new Point(4, 4), 1, null));
        root.getTopLeftTree().setColor(Color.BLUE);
        ((Block)root.getTopLeftTree()).setParent(root);

        root.setTopRightTree(new Block(new Point(4, 0), new Point(8, 4), 1, null));
        root.getTopRightTree().setColor(Color.RED);
        ((Block)root.getTopRightTree()).setParent(root);

        root.setBotLeftTree(new Block(new Point(0, 4), new Point(4, 8), 1, null));
        root.getBotLeftTree().setColor(Color.YELLOW);
        ((Block)root.getBotLeftTree()).setParent(root);

        root.setBotRightTree(new Block(new Point(4, 4), new Point(8, 8), 1, null));
        root.getBotRightTree().setColor(Color.GRAY);
        ((Block)root.getBotRightTree()).setParent(root);

        assertEquals(4, game.perimeterScore());
    }
}