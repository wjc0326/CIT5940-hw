import java.awt.Color;
import java.util.Scanner;

/**
 * @author ericfouh
 */
public class TestBlockly
{

    /**
     * Read the id of two blocks  and perform the swap operation
     * @param in Scanner
     * @param game the game instance
     * @param root the root of the tree of blocks
     */
    public static void swap_operation(Scanner in, IGame game, IBlock root)
    {
        System.out.println("ID of first block to swap");
        int id = in.nextInt();
        System.out.println("ID of second block to swap");
        int id2 = in.nextInt();
        game.swap( id, id2);
    }


    /**
     * Read the id of a block and perform the rotate operation
     * @param in Scanner
     * @param game the game instance
     * @param root the root of the tree of blocks
     */
    public static void rotate_operation(Scanner in, IGame game, IBlock root)
    {
        System.out.println("ID of block to rotate");
        int id = in.nextInt();
        IBlock b = game.getBlock(id);
        b.rotate();
    }


    /**
     * Read the id of a block and perform a smash operation
     * @param in Scanner
     * @param game the game instance
     * @param root the root of the tree of blocks
     */
    public static void smash_operation(Scanner in, IGame game, IBlock root)
    {
        System.out.println("ID of block to smash");
        // in = new Scanner(System.in);
        int id = in.nextInt();
        IBlock b = game.getBlock(id);
        b.smash(game.maxDepth()); // pass max depth variable
    }


    /**
     * Main method
     * @param args N/A
     */
    public static void main(String[] args)
    {
        //(1) create a new board/Game with the max depth 
        
        IGame board = new Game(2, Color.RED);
       
        //(2) randomly initialize the board
        // and get the root of the quad tree
        
        IBlock root = board.getRoot();
        //(3) create a new game frame (GUI) 
        GameFrame game = new GameFrame();
        //(4)Add the root of the quadtree to the GUI
        game.addQuad(root);
        //(5) display the GUI
        game.display();
        int id = 0;

        //(6) initialize a loop that will:
        //ask the user to select an operation 
        // (1 for swap, 2 for rotate, 3 for smash)
        Scanner in = new Scanner(System.in);
        while (true)
        {
            System.out.println(
                "ID of the operation 1 for swap, 2 for rotate, 3 for smash");
            // Scanner in = new Scanner(System.in);
            id = in.nextInt();
            switch (id)
            {
                case 1:
                    swap_operation(in, board, root);
                    game.refresh_board(root);
                    break;
                case 2:
                    rotate_operation(in, board, root);
                    game.refresh_board(root);
                    break;
                case 3:
                    smash_operation(in, board, root);
                    game.refresh_board(root);
                    break;
                default:
                    break;
            }

            int score = board.perimeterScore();
            System.out.println("Score:  " + score);
            
        }
        
    }

}
