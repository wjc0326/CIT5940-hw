import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class IndexBuilder implements IIndexBuilder {
    @Override
    public Map<String, List<String>> parseFeed(List<String> feeds) {
        Map<String, List<String>> res = new HashMap<>();

        for (String feed : feeds) {
            // open the rss doc
            try {
                Document rssDoc = Jsoup.connect(feed).get();
                // get all the urls in the rss doc represented by "link"
                Elements urls = rssDoc.getElementsByTag("link");
                //retrieve the urls
                for (Element url : urls) {
                    List<String> wordsList = new ArrayList<>();
                    String urlText = url.text();

                    // open the html doc
                    Document htmlDoc = Jsoup.connect(urlText).get();
                    // get all the contents in the html doc represented by "body"
                    Elements contents = htmlDoc.getElementsByTag("body");

                    for (Element content : contents) {
                        String contentText = content.text();
                        String[] words = contentText.split(" ");
                        for (String word : words) {
                            String newWord = word.toLowerCase().replaceAll("[^a-z0-9 ]", "");
                            wordsList.add(newWord);
                        }
                    }
                    res.put(urlText, wordsList);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return res;
    }

    @Override
    public Map<String, Map<String, Double>> buildIndex(Map<String, List<String>> docs) {
        Map<String, Map<String, Double>> firstIndexMap = new HashMap<>();

        // STEP 1: build IDF map
        Map<String, Integer> idfMap = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : docs.entrySet()) {
            List<String> wordList = entry.getValue();
            // transfer the array to set (to avoid duplicates)
            Set<String> wordSet = new HashSet<>(wordList);
            for (String word : wordSet) {
                idfMap.put(word, idfMap.getOrDefault(word, 0) + 1);
            }
        }

        // STEP 2: get TF and use TF * IDF to get the final result
        for (Map.Entry<String, List<String>> entry : docs.entrySet()) {
            Integer totalNum = 0;
            Map<String, Double> tfIdfMap = new HashMap<>();

            // get the docName (will be the key of firstIndexMap)
            String docName = entry.getKey();
            // get the list of words in this doc (will be used to get TF and IDF)
            List<String> wordList = entry.getValue();

            // get the number of times word appears in this doc
            for (String word : wordList) {
                tfIdfMap.put(word, tfIdfMap.getOrDefault(word, 0.0) + 1.0);
                totalNum += 1;
            }

            for (Map.Entry<String, Double> entry1 : tfIdfMap.entrySet()) {
                // get TF-IDF
                double idfWord = Math.log(docs.size() / (double)idfMap.get(entry1.getKey()));
                entry1.setValue(entry1.getValue() / totalNum * idfWord);
            }

            // sort the value map by lexicographic order on the key
            TreeMap<String, Double> sorted = new TreeMap<>();
            sorted.putAll(tfIdfMap);

            firstIndexMap.put(docName, sorted);
        }

        return firstIndexMap;
    }

    public static Comparator<Map.Entry<String, Double>> byReverseValueOrder() {
        return new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> e1, Map.Entry<String, Double> e2) {
                return -(e1.getValue().compareTo(e2.getValue()));
            }
        };
    }

    @Override
    public Map<?, ?> buildInvertedIndex(Map<String, Map<String, Double>> index) {
        Map<String, ArrayList<Map.Entry<String, Double>>> invertIndexMap = new HashMap<>();
        for (Map.Entry<String, Map<String, Double>> entry : index.entrySet()) {
            String docName = entry.getKey();
            Map<String, Double> tagAndValueMap = entry.getValue();

            for (Map.Entry<String, Double> entry1 : tagAndValueMap.entrySet()) {
                String tag = entry1.getKey();
                Double value = entry1.getValue();
                if (invertIndexMap.containsKey(tag)) {
                    invertIndexMap.get(tag).add(
                            new AbstractMap.SimpleEntry<String, Double>(docName, value));
                } else {
                    ArrayList<Map.Entry<String, Double>> nameAndValueList = new ArrayList<>();
                    nameAndValueList.add(
                            new AbstractMap.SimpleEntry<String, Double>(docName, value));
                    invertIndexMap.put(tag, nameAndValueList);
                }
            }
        }

        // sort each value (ArrayList)
        for (Map.Entry<String, ArrayList<Map.Entry<String, Double>>> entry2 :
                invertIndexMap.entrySet()) {
            String key = entry2.getKey();
            ArrayList<Map.Entry<String, Double>> value = entry2.getValue();
            Collections.sort(value, byReverseValueOrder());
            invertIndexMap.put(key, value);
        }
        return invertIndexMap;
    }

    public static Comparator<Map.Entry<String, List<String>>> homePageOrder() {
        return new Comparator<Map.Entry<String, List<String>>>() {
            public int compare(Map.Entry<String, List<String>> e1,
                               Map.Entry<String, List<String>> e2) {
                if (e1.getValue().size() == e2.getValue().size()) {
                    return -(e1.getKey().compareTo(e2.getKey()));
                } else {
                    return -(e1.getValue().size() - e2.getValue().size());
                }
            }
        };
    }

    @Override
    public Collection<Map.Entry<String, List<String>>> buildHomePage(Map<?, ?> invertedIndex) {
        List<Map.Entry<String, List<String>>> homePageList = new ArrayList<>();

        for (Map.Entry<?, ?> entry : invertedIndex.entrySet()) {
            String tagName = (String)entry.getKey();
            ArrayList<Map.Entry<String, Double>> docAndValueList =
                    (ArrayList<Map.Entry<String, Double>>)entry.getValue();
            if (!STOPWORDS.contains(tagName)) {
                List<String> articles = new ArrayList<>();
                for (Map.Entry<String, Double> docAndValue : docAndValueList) {
                    articles.add(docAndValue.getKey());
                }
                homePageList.add(
                        new AbstractMap.SimpleEntry<String, List<String>>(tagName, articles));
            }
        }

        Collections.sort(homePageList, homePageOrder());
        return homePageList;
    }

    @Override
    public Collection<?> createAutocompleteFile(Collection<Map.Entry<String,
                                                List<String>>> homepage) {
        List<String> wordList = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : homepage) {
            wordList.add(entry.getKey());
        }

        // sort by lexicographic order
        Collections.sort(wordList);

        int num = wordList.size();

        // open the file to write
        try {
            File file = new File("autocomplete.txt");
            FileWriter r = new FileWriter(file);
            r.write(num + "\n");
            for (String word : wordList) {
                r.write("\t" + 0 + " " + word + "\n");
            }
            r.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return wordList;
    }

    @Override
    public List<String> searchArticles(String queryTerm, Map<?, ?> invertedIndex) {
        List<String> articleList = new ArrayList<>();
        for (Map.Entry<?, ?> entry : invertedIndex.entrySet()) {
            if (entry.getKey().equals(queryTerm)) {
                ArrayList<Map.Entry<String, Double>> docAndValueList =
                        (ArrayList<Map.Entry<String, Double>>)entry.getValue();
                for (Map.Entry<String, Double> docAndValue : docAndValueList) {
                    articleList.add(docAndValue.getKey());
                }
            }
        }
        return articleList;
    }
}
