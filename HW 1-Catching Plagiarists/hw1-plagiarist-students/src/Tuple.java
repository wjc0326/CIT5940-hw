
/**
 * @author ericfouh
 *
 * @param <L> the left value
 * @param <R> the right value
 */
public class Tuple<L, R> {
    private L left;
    private R right;


    /**
     * @param l
     * @param r
     */
    public Tuple(L l, R r) {
        this.setLeft(l);
        this.setRight(r);
    }


    /**
     * @return the right
     */
    public R getRight() {
        return right;
    }


    /**
     * @param right the right to set
     */
    public void setRight(R right) {
        this.right = right;
    }


    /**
     * @return the left
     */
    public L getLeft() {
        return left;
    }


    /**
     * @param left the left to set
     */
    public void setLeft(L left) {
        this.left = left;
    }

}
