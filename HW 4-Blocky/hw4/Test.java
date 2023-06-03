import java.awt.*;

public class Test
{

    public static void main(String[] args)
    {
        Block root = new Block(new Point(0, 0), new Point(8, 8), 0, null);
        Block tl = new Block(new Point(0, 0), new Point(4, 4), 0, null);
        tl.setColor(Color.BLUE);
        tl.setParent(root);
        Block tr = new Block(new Point(4, 0), new Point(8, 4), 0, null);
        tr.setColor(Color.RED);
        tr.setParent(root);
        Block bl = new Block(new Point(0, 4), new Point(4, 8), 0, null);
        bl.setColor(Color.YELLOW);
        bl.setParent(root);
        Block br = new Block(new Point(4, 4), new Point(8, 8), 0, null);
        br.setColor(Color.GRAY);
        br.setParent(root);
        root.setTopLeftTree(tl);
        root.setTopRightTree(tr);
        root.setBotLeftTree(bl);
        root.setBotRightTree(br);
        GameFrame game = new GameFrame();
        //(4)Add the root of the quadtree to the GUI
        game.addQuad(root);
        game.display();

    }

}
