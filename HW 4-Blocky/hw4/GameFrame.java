import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 * @author ericfouh
 */
public class GameFrame
    extends JFrame
{
    private Squares squares;


    /**
     * 
     */
    public GameFrame()
    {
        super("Game Frame");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.squares = new Squares();
        getContentPane().add(this.squares);

    }

    // 删掉！！！！！！！
    public GameFrame(String name)
    {
        super(name);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.squares = new Squares();
        getContentPane().add(this.squares);

    }


    /**
     * @param q quadrant
     */
    public void addQuad(IBlock q)
    {
        this.squares.addQuadrant(q);
    }


    /**
     * 
     */
    public void display()
    {
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }


    /**
     * 
     */
    public void clear()
    {
        this.squares = new Squares();
    }


    /**
     * @param block
     */
    public void refresh_board(IBlock block)
    {
        this.addQuad(block);
        this.revalidate();
        this.repaint();
    }
}




/**
 * @author ericfouh
 */
class Squares
    extends JPanel
{
    /**
     * 
     */
    private static final long  serialVersionUID = 1L;
    private static final int   PREF_W           = 500;
    private static final int   PREF_H           = PREF_W;
    private List<ColoredBlock> squares          = new ArrayList<ColoredBlock>();


    /**
     * @param q the root of the quad tree to be displayed
     */
    public void addQuadrant(IBlock q)
    {
        if (q != null)
        {
            Polygon p = new Polygon();
            p.addPoint(q.getTopLeft().getX() * 50, q.getTopLeft().getY() * 50);
            p.addPoint(q.getBotRight().getX() * 50, q.getTopLeft().getY() * 50);
            p.addPoint(
                q.getBotRight().getX() * 50,
                q.getBotRight().getY() * 50);
            p.addPoint(q.getTopLeft().getX() * 50, q.getBotRight().getY() * 50);

            ColoredBlock cb = new ColoredBlock(p, q.getColor());
            this.squares.add(cb);
            addQuadrant(q.getTopLeftTree());
            addQuadrant(q.getTopRightTree());
            addQuadrant(q.getBotLeftTree());
            addQuadrant(q.getBotRightTree());
        }
    }


    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(PREF_W, PREF_H);
    }


    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;

        for (ColoredBlock rect : this.squares)
        {
            g2.setColor(Color.BLACK);
            g2.drawPolygon(rect.getPolygon());
            g2.setColor(rect.getColor());
            g2.fillPolygon(rect.getPolygon());
        }
    }

}




/**
 * @author ericfouh
 */
class ColoredBlock
{
    private Polygon p;
    private Color   col;


    /**
     * @param p
     * @param col
     */
    public ColoredBlock(Polygon p, Color col)
    {
        this.p = p;
        this.col = col;
    }


    /**
     * @return the p
     */
    public Polygon getPolygon()
    {
        return this.p;
    }


    /**
     * @return the col
     */
    public Color getColor()
    {
        return this.col;
    }

}
