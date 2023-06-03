import org.junit.Before;

import java.awt.*;

import static org.junit.Assert.*;

public class BlockTest {
    Block root;
    Block tl;
    Block tr;
    Block bl;
    Block br;

    @Before
    public void setUp() {
        root = new Block(new Point(0, 0), new Point(8, 8), 0, null);
        tl = new Block(new Point(0, 0), new Point(4, 4), 1, null);
        tl.setColor(Color.BLUE);
        tl.setParent(root);
        tr = new Block(new Point(4, 0), new Point(8, 4), 1, null);
        tr.setColor(Color.RED);
        tr.setParent(root);
        bl = new Block(new Point(0, 4), new Point(4, 8), 1, null);
        bl.setColor(Color.YELLOW);
        bl.setParent(root);
        br = new Block(new Point(4, 4), new Point(8, 8), 1, null);
        br.setColor(Color.GRAY);
        br.setParent(root);
        root.setTopLeftTree(tl);
        root.setTopRightTree(tr);
        root.setBotLeftTree(bl);
        root.setBotRightTree(br);
    }

    @org.junit.Test
    public void depthTest() {
        assertEquals(0, root.depth());
        assertEquals(1, tl.depth());
        assertEquals(1, tr.depth());
        assertEquals(1, bl.depth());
        assertEquals(1, br.depth());
    }

    @org.junit.Test
    public void smashTest() {
        tr.smash(3);
        assertNull(tr.getColor());
        assertEquals(4, tr.children().size());
        assertEquals(tr, ((Block)tr.children().get(0)).getParent());
        assertEquals(2, tr.children().get(0).depth());
        assertFalse(tr.children().get(0).getColor() == null);
        assertFalse(tr.isLeaf());

        // NW
        assertEquals(tr.children().get(0), tr.getTopLeftTree());
        assertEquals(4, tr.children().get(0).getTopLeft().getX());
        assertEquals(0, tr.children().get(0).getTopLeft().getY());
        assertEquals(6, tr.children().get(0).getBotRight().getX());
        assertEquals(2, tr.children().get(0).getBotRight().getY());

        // NE
        assertEquals(tr.children().get(1), tr.getTopRightTree());
        assertEquals(6, tr.children().get(1).getTopLeft().getX());
        assertEquals(0, tr.children().get(1).getTopLeft().getY());
        assertEquals(8, tr.children().get(1).getBotRight().getX());
        assertEquals(2, tr.children().get(1).getBotRight().getY());

        // SE
        assertEquals(tr.children().get(2), tr.getBotRightTree());
        assertEquals(6, tr.children().get(2).getTopLeft().getX());
        assertEquals(2, tr.children().get(2).getTopLeft().getY());
        assertEquals(8, tr.children().get(2).getBotRight().getX());
        assertEquals(4, tr.children().get(2).getBotRight().getY());

        // SW
        assertEquals(tr.children().get(3), tr.getBotLeftTree());
        assertEquals(4, tr.children().get(3).getTopLeft().getX());
        assertEquals(2, tr.children().get(3).getTopLeft().getY());
        assertEquals(6, tr.children().get(3).getBotRight().getX());
        assertEquals(4, tr.children().get(3).getBotRight().getY());
    }

    @org.junit.Test
    public void childrenTest() {
        assertEquals(4, root.children().size());
        assertEquals(0, tr.children().size());
        tr.smash(3);
        assertEquals(4, tr.children().size());
    }

    @org.junit.Test
    public void moveXYTest() {
        tl = tl.moveXY(4, 0);
        assertEquals(tl.getTopLeft().getX(), tr.getTopLeft().getX());
        assertEquals(tl.getTopLeft().getY(), tr.getTopLeft().getY());
        assertEquals(tl.getBotRight().getX(), tr.getBotRight().getX());
        assertEquals(tl.getBotRight().getY(), tr.getBotRight().getY());
        assertNotSame(tl.getColor(), tr.getColor());
        tl = tl.moveXY(-4, 0);
    }

    @org.junit.Test
    public void rotateTest() {
        tr.smash(3);
        tr.getTopLeftTree().setColor(Color.GREEN);
        tr.getTopRightTree().setColor(Color.PINK);
        tr.getBotLeftTree().setColor(Color.RED);
        tr.getBotRightTree().setColor(Color.WHITE);

        // first rotate the whole board
        root.rotate();
        tl = (Block)root.getTopLeftTree();
        tr = (Block)root.getTopRightTree();
        br = (Block)root.getBotRightTree();
        bl = (Block)root.getBotLeftTree();

        assertEquals(Color.YELLOW, tl.getColor());
        assertEquals(Color.BLUE, tr.getColor());
        assertEquals(Color.GRAY, bl.getColor());
        assertNull(br.getColor());

        assertEquals(Color.GREEN, br.getTopLeftTree().getColor());
        assertEquals(Color.PINK, br.getTopRightTree().getColor());
        assertEquals(Color.WHITE, br.getBotRightTree().getColor());
        assertEquals(Color.RED, br.getBotLeftTree().getColor());

        // second rotate the br block
        br.rotate();
        assertEquals(Color.RED, br.getTopLeftTree().getColor());
        assertEquals(Color.GREEN, br.getTopRightTree().getColor());
        assertEquals(Color.PINK, br.getBotRightTree().getColor());
        assertEquals(Color.WHITE, br.getBotLeftTree().getColor());
    }
}