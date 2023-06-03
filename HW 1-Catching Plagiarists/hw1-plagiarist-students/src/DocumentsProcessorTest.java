import org.junit.rules.ExpectedException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class DocumentsProcessorTest {

    private ExpectedException exception = ExpectedException.none();

    DocumentsProcessor docProc = new DocumentsProcessor();
    String directoryPath = null;
    Map<String, List<String>> docMap = null;
    String nGramFilePath = null;

    @org.junit.Test
    public void processDocuments() {
        directoryPath = "test_files_part1";
        docMap = docProc.processDocuments(directoryPath, 2);
        assertTrue(docMap.containsKey("file1.txt"));
        assertTrue(docMap.containsKey("file2.txt"));
        assertFalse(docMap.containsKey("file3.txt"));

        assertEquals(2,docMap.size());
        assertEquals(3,docMap.get("file1.txt").size());
        assertEquals("isanother", docMap.get("file2.txt").get(1));

        // Test for the path and directory that doesn't exist
        directoryPath = "test_files_part";
        docMap = docProc.processDocuments(directoryPath, 2);
        assertEquals(0,docMap.size());
    }

    @org.junit.Test
    public void storeNGrams() {
        directoryPath = "test_files_part2";
        nGramFilePath = "test_files_part2/finalFile.txt";
        docMap = docProc.processDocuments(directoryPath, 4);
        List<Tuple<String, Integer>> tupleNGramsList = docProc.storeNGrams(docMap, nGramFilePath);
        assertEquals(2,tupleNGramsList.size());

        File file = new File("test_files_part2/finalFile.txt");
        assertTrue(file.exists());

        assertEquals("file1.txt",tupleNGramsList.get(0).getLeft());
        assertEquals(28,(int)tupleNGramsList.get(0).getRight());
        assertEquals("file2.txt",tupleNGramsList.get(1).getLeft());
        assertEquals(42,(int)tupleNGramsList.get(1).getRight());

        directoryPath = "test_files_part";
        docMap = docProc.processDocuments(directoryPath, 4);
        tupleNGramsList = docProc.storeNGrams(docMap, nGramFilePath);
        exception.expect(NullPointerException.class);
        exception.expectMessage("Cannot invoke \"java.io.BufferedWriter.close()" +
                "\" because \"bw\" is null");
    }

    @org.junit.Test
    public void computeSimilarities() {
        directoryPath = "test_files_part3";
        nGramFilePath = "test_files_part3/finalFile.txt";
        docMap = docProc.processDocuments(directoryPath, 3);
        List<Tuple<String, Integer>> tupleNGramsList = docProc.storeNGrams(docMap, nGramFilePath);
        TreeSet<Similarities> similarSet =
                docProc.computeSimilarities(nGramFilePath, tupleNGramsList);
        assertEquals(3, similarSet.size());

//        for (Similarities similarity : similarSet) {
//            System
//                    .out.println("similar (file1) is: " + similarity.getFile1());
//            System
//                    .out.println("similar (file2) is: " + similarity.getFile2());
//            System
//                    .out.println("similar (count) is: " + similarity.getCount());
//        }

        Similarities similarity1 = new Similarities("file2.txt", "file3.txt");
        similarity1.setCount(3);
        Similarities similarity2 = new Similarities("file1.txt", "file3.txt");
        similarity2.setCount(3);
        Similarities similarity3 = new Similarities("file1.txt", "file2.txt");
        similarity3.setCount(3);

        assertTrue(similarSet.contains(similarity2));
        assertTrue(similarSet.contains(similarity3));
        assertTrue(similarSet.contains(similarity1));
    }

    @org.junit.Test
    public void printSimilarities() {
        directoryPath = "test_files_part3";
        nGramFilePath = "test_files_part3/finalFile.txt";
        docMap = docProc.processDocuments(directoryPath, 3);
        List<Tuple<String, Integer>> tupleNGramsList = docProc.storeNGrams(docMap, nGramFilePath);
        TreeSet<Similarities> similarSet =
                docProc.computeSimilarities(nGramFilePath, tupleNGramsList);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
        docProc.printSimilarities(similarSet, 2);
        assertEquals(output.toString(), "1: file2.txt, file3.txt, 3\n" +
                "2: file1.txt, file3.txt, 3\n" +
                "3: file1.txt, file2.txt, 3\n");
    }

    @org.junit.Test
    public void processAndStore() {
        directoryPath = "test_files_part2";
        nGramFilePath = "test_files_part2/finalFile.txt";
        docMap = docProc.processDocuments(directoryPath, 4);
        List<Tuple<String, Integer>> tupleNGramsList =
                docProc.processAndStore(directoryPath, nGramFilePath, 4);
        assertEquals(2,tupleNGramsList.size());
    }
}