import java.util.Comparator;

/**
 * @author ericfouh
 */
public interface ITerm
    extends Comparable<ITerm> {

    /**
     * Compares the two terms in descending order by weight.
     * 
     * @return comparator Object
     */
    public static Comparator<ITerm> byReverseWeightOrder() {
        return new Comparator<ITerm>() {
            @Override
            public int compare(ITerm a, ITerm b) {
                return -(int)(a.getWeight() - b.getWeight());
            }
        };
    }


    /**
     * Compares the two terms in lexicographic order but using only the first r
     * characters of each query.
     * 
     * @param r
     * @return comparator Object
     */
    public static Comparator<ITerm> byPrefixOrder(int r) {
        if (r < 0) {
            throw new IllegalArgumentException();
        }

        return new Comparator<ITerm>() {
            @Override
            public int compare(ITerm a, ITerm b) {
                int minLength = Math.min(a.getTerm().length(), b.getTerm().length());
                int newR = Math.min(r, minLength);

                // get the substring of 2 terms
                String aSub = a.getTerm().substring(0, newR);
                String bSub = b.getTerm().substring(0, newR);

                return aSub.compareTo(bSub);

            }
        };
    }

    // Compares the two terms in lexicographic order by query(term).
    public int compareTo(ITerm that);


    // Returns a string representation of this term in the following format:
    // the weight, followed by a tab, followed by the query(term).
    public String toString();

    // Required getters.
    public long getWeight();
    public String getTerm();

    // Required setters (mostly for autograding purposes)
    public void setWeight(long weight);
    public void setTerm(String term);

}
