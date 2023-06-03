import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * @author ericfouh
 */
public interface IDocumentsProcessor {
    /**
     * @param directoryPath - the path to the directory
     * @param n    - the size of the n-grams
     * @return collection of files with their n-grams
     */
    Map<String, List<String>> processDocuments(String directoryPath, int n);


    /**
     * We write n-grams sequentially in the file. They are separated by
     * a space.
     * 
     * @param docs - map of string with list of all n-grams
     * @param nGramFilePath of the file to store the n-grams
     * @return a list of file and size (in byte) of characters written in file
     *         path
     */
    List<Tuple<String, Integer>> storeNGrams(
        Map<String, List<String>> docs,
        String nGramFilePath);


    /**
     * @param nGramFilePath  of the file to store the n-grams
     * @param fileIndex - a list of tuples representing each file and its size
     *                  in nGramFile
     * @return a TreeSet of file similarities. Each Similarities instance
     *         encapsulates the files (two) and the number of n-grams
     *         they have in common
     */

    public TreeSet<Similarities> computeSimilarities(
        String nGramFilePath,
        List<Tuple<String, Integer>> fileIndex);


    /**
     * @param sims      - the TreeSet of Similarities
     * @param threshold - only Similarities with a count greater than threshold
     *                  are printed
     */
    public void printSimilarities(TreeSet<Similarities> sims, int threshold);

    public List<Tuple<String, Integer>> processAndStore(String directoryPath,
                                                        String sequenceFile,
                                                        int n);
}
