import java.io.*;
import java.util.*;

public class DocumentsProcessor implements IDocumentsProcessor {
    @Override
    public Map<String, List<String>> processDocuments(String directoryPath, int n) {
        Map<String, List<String>> docMap = new HashMap<String, List<String>>();

        File docFolder = new File(directoryPath);
        File[] listOfDocs = docFolder.listFiles();

        if (listOfDocs != null) {
            for (File doc : listOfDocs) {
                if (doc.isFile()) {
                    try {
                        String docName = directoryPath + '/' + doc.getName();
                        if (doc.getName().equals(".DS_Store") ||
                                doc.getName().startsWith("final")) {
                            continue;
                        }

                        File newDoc = new File(docName);
                        FileReader reader = new FileReader(newDoc);
                        Reader r = new BufferedReader(reader);

                        DocumentIterator iterator = new DocumentIterator(r, n);
                        List<String> nGram = new ArrayList<String>();

                        while (iterator.hasNext()) {
                            nGram.add(iterator.next());
                        }

                        docMap.put(doc.getName(),nGram);

                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        return docMap;
    }

    @Override
    public List<Tuple<String, Integer>> storeNGrams(Map<String, List<String>> docs,
                                                    String nGramFilePath) {
        List<Tuple<String, Integer>> tupleNGramsList = new ArrayList<Tuple<String, Integer>>();
        Tuple<String, Integer> tupleNGrams = null;
        List<String> nGramList = null;

        File doc = new File(nGramFilePath);
        FileWriter docWriter = null;
        try {
            docWriter = new FileWriter(doc, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        BufferedWriter bw = null;

        try {
            for (Map.Entry<String, List<String>> docNGram : docs.entrySet()) {
                int length = 0;
                bw = new BufferedWriter(docWriter);
                String docName = docNGram.getKey();
                nGramList = docNGram.getValue();

                for (String nGram : nGramList) {
                    try {
                        bw.write(nGram);
                        length += nGram.length();
                        bw.write(" ");
                        length += 1;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                bw.flush();

                tupleNGrams = new Tuple<String, Integer>(docName, length);
                tupleNGramsList.add(tupleNGrams);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                docWriter.close();
                bw.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        return tupleNGramsList;
    }

    @Override
    public TreeSet<Similarities> computeSimilarities(String nGramFilePath,
                                                     List<Tuple<String, Integer>> fileIndex) {

        TreeSet<Similarities> similarSet = new TreeSet<Similarities>();
        // key: n-gram, value: list of filenames contain the key
        Map<String, List<String>> nGramFileMap = new HashMap<String, List<String>>();

        // STEP1: iterate through the nGramFile, get 1-gram and store into nGramFileMap
        File newDoc = new File(nGramFilePath);
        FileReader reader = null;
        String gramWord = null;
        try {
            reader = new FileReader(newDoc);
            Reader r = new BufferedReader(reader);
            DocumentIterator iterator = new DocumentIterator(r, 1);

            int currLength = 0;
            while (iterator.hasNext()) {
                gramWord = iterator.next();
                // also add the space
                currLength = currLength + gramWord.length() + 1;

                // find out the corresponding filename, using the second element in the tuple(index)
                int indexLength = 0;
                String currFileName = null;
                for (Tuple<String, Integer> fileGram : fileIndex) {
                    indexLength += (int)fileGram.getRight();
                    if (currLength <= indexLength) {
                        currFileName = fileGram.getLeft();
                        break;
                    }
                }

                // the 1-gram not exist in the hashmap, add it to the hashmap
                if (!nGramFileMap.containsKey(gramWord)) {
                    List<String> fileNameList = new ArrayList<>();
                    fileNameList.add(currFileName);
                    nGramFileMap.put(gramWord, fileNameList);
                } else {
                    // the 1-gram exists in the hashmap:
                    // 1.for each ele of value, create/update the Similarities object in similarSet
                    // 2.add the filename to the value(ArrayList)
                    List<String> fileNameList = nGramFileMap.get(gramWord);
                    for (String singleFileName : fileNameList) {
                        // avoid duplicates
                        if (currFileName != null && fileNameList.contains(currFileName)) {
                            continue ;
                        }
                        Similarities similarity = null;
                        assert currFileName != null;
                        if (singleFileName.compareTo(currFileName) > 0) {
                            similarity = new Similarities(currFileName, singleFileName);
                        } else {
                            similarity = new Similarities(singleFileName, currFileName);
                        }
                        boolean flag = false;
                        for (Similarities singleSimilarity : similarSet) {
                            if (similarity.compareTo(singleSimilarity) == 0) {
                                // exist in similarSet, increment its count
                                singleSimilarity.setCount(singleSimilarity.getCount() + 1);
                                flag = true;
                                break;
                            }
                        }
                        if (!flag) {
                            // not exist in similarSet, add it to the set
                            similarity.setCount(1);
                            similarSet.add(similarity);
                        }
                    }
                    List<String> fileNameListAppend = nGramFileMap.get(gramWord);
                    if (!fileNameListAppend.contains(currFileName)) {
                        fileNameListAppend.add(currFileName);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        return similarSet;
    }

    @Override
    public void printSimilarities(TreeSet<Similarities> sims, int threshold) {
        Comparator comparator = new SimilaritiesComparer();
        TreeSet<Similarities> printSimilarSet = new TreeSet<Similarities>(comparator);

        for (Similarities similarity : sims) {
            if (similarity.getCount() > threshold) {
                printSimilarSet.add(similarity);
            }
        }
        int i = 0;
        for (Similarities sortedSimi : printSimilarSet) {
            i++;
            String output = i + ": " + sortedSimi.getFile1() + ", " +
                    sortedSimi.getFile2() + ", " + String.valueOf(sortedSimi.getCount()) + "\n";
            System
                    .out.print(output);
        }

    }

    public List<Tuple<String, Integer>> processAndStore(String directoryPath,
                                                        String sequenceFile,
                                                        int n) {

        List<Tuple<String, Integer>> tupleNGramsList = new ArrayList<Tuple<String, Integer>>();
        Tuple<String, Integer> tupleNGrams = null;

        // get doc list
        File docFolder = new File(directoryPath);
        File[] listOfDocs = docFolder.listFiles();

        // write to doc
        File docToWrite = new File(sequenceFile);
        FileWriter docWriter = null;
        try {
            docWriter = new FileWriter(docToWrite, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        BufferedWriter bw = null;

        // iterate through the doc list, read from the doc, and write to docToWrite
        if (listOfDocs != null) {
            for (File docToRead : listOfDocs) {
                if (docToRead.isFile()) {
                    try {
                        String docName = directoryPath + '/' + docToRead.getName();
                        if (docToRead.getName().equals(".DS_Store") ||
                                docToRead.getName().startsWith("final")) {
                            continue;
                        }

                        // create a doc reader for each file
                        File newDoc = new File(docName);
                        FileReader reader = new FileReader(newDoc);
                        Reader r = new BufferedReader(reader);

                        DocumentIterator iterator = new DocumentIterator(r, n);
                        List<String> nGramListRead = new ArrayList<String>();

                        while (iterator.hasNext()) {
                            nGramListRead.add(iterator.next());
                        }

                        // create a doc writer for each file
                        bw = new BufferedWriter(docWriter);
                        int length = 0;
                        for (String nGram : nGramListRead) {
                            try {
                                bw.write(nGram);
                                length += nGram.length();
                                bw.write(" ");
                                length += 1;
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        bw.flush();

                        tupleNGrams = new Tuple<String, Integer>(docToRead.getName(), length);
                        tupleNGramsList.add(tupleNGrams);

                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                docWriter.close();
                assert bw != null;
                bw.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        return tupleNGramsList;
    }

    public static void main(String[] args) {
        DocumentsProcessor docProc = new DocumentsProcessor();
        Map<String, List<String>> docMap = docProc.processDocuments("test_files_part3", 3);
        String nGramFilePath = "test_files_part3/finalFile.txt";
        List<Tuple<String, Integer>> tupleNGramsList
                = docProc.storeNGrams(docMap, nGramFilePath);

        TreeSet<Similarities> similarSet = docProc.computeSimilarities(nGramFilePath,
                tupleNGramsList);
        for (Similarities similarity : similarSet) {
            System.
            out.println("similar (file1) is: " + similarity.getFile1());
            System.
            out.println("similar (file2) is: " + similarity.getFile2());
            System.
            out.println("similar (count) is: " + similarity.getCount());
        }

        Comparator comparator = new SimilaritiesComparer();
        TreeSet<Similarities> printSimilarSet = new TreeSet<Similarities>(comparator);
        printSimilarSet.addAll(similarSet);
        for (Similarities similarity : printSimilarSet) {
            System.
            out.println("similar (file1) is: " + similarity.getFile1());
            System.
            out.println("similar (file2) is: " + similarity.getFile2());
            System.
            out.println("similar (count) is: " + similarity.getCount());
        }
        docProc.printSimilarities(similarSet, 2);
    }
}

