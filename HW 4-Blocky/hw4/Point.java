
// -------------------------------------------------------------------------
/**
 *  A 2D Point class
 *
 *  @author ericfouh
 *  @version Jan 17, 2018
 */
public class Point
{
    private int x;
    private int y;
    // ----------------------------------------------------------
    /**
     * Create a new Point object.
     * @param x
     * @param y
     */
    Point(int x, int y)
    {
        this.setX(x);
        this.setY(y);
    }
    // ----------------------------------------------------------
    /**
     * @return the x
     */
    public int getX()
    {
        return x;
    }
    // ----------------------------------------------------------
    /**
     * @param x the x to set
     */
    public void setX(int x)
    {
        this.x = x;
    }
    // ----------------------------------------------------------
    /**
     * @return the y
     */
    public int getY()
    {
        return y;
    }
    // ----------------------------------------------------------
    /**
     * @param y the y to set
     */
    public void setY(int y)
    {
        this.y = y;
    }
    
    public String toString() {
	return "x: " + this.x + ", y: " + this.y;
    }

}
