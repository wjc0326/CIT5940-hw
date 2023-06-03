import java.awt.Color;

/**
 * the IGame class creates the blocky board 
 * It maintains the max_depth
 * === Class Attributes === 
 * maximum depth: 
 *      -   max_depth
 *          The deepest level/depth allowed in the overall block
 *          structure. 
 * 
 * The root of the quadtree
 *      -   root
 * 
 *The target color
 *      -   color
 * 
 * ===Constructor === 
 *
 * The Game class constructor have the following signature 
 * Game(int max_depth, Color target)
 * 
 * @author ericfouh
 *
 */

public interface IGame {

    public int maxDepth();

    public IBlock randomInit();


    public IBlock getBlock(int pos);


    public IBlock getRoot();

    public void swap(int x, int y);

    public IBlock[][] flatten();

    public int perimeterScore();

    public void setRoot(IBlock root);
    
}
