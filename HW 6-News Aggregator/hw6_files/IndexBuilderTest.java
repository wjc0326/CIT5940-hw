import java.util.*;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * @author ericfouh
 */
public class IndexBuilderTest {

    IndexBuilder ib = new IndexBuilder();
    @org.junit.Test
    public void parseFeedTest() {
        List<String> feeds = new ArrayList<>();
        feeds.add("https://www.cis.upenn.edu/~cit5940/sample_rss_feed.xml");
        Map<String, List<String>> testMap = ib.parseFeed(feeds);
        assertEquals(5, testMap.size());

        assertTrue(testMap.containsKey("https://www.seas.upenn.edu/~cit5940/page1.html"));
        assertTrue(testMap.containsKey("https://www.seas.upenn.edu/~cit5940/page2.html"));
        assertTrue(testMap.containsKey("https://www.seas.upenn.edu/~cit5940/page3.html"));
        assertTrue(testMap.containsKey("https://www.seas.upenn.edu/~cit5940/page4.html"));
        assertTrue(testMap.containsKey("https://www.seas.upenn.edu/~cit5940/page5.html"));

        assertEquals(10, testMap.get("https://www.seas.upenn.edu/~cit5940/page1.html").size());
        assertEquals(55, testMap.get("https://www.seas.upenn.edu/~cit5940/page2.html").size());
        assertEquals(33, testMap.get("https://www.seas.upenn.edu/~cit5940/page3.html").size());
        assertEquals(22, testMap.get("https://www.seas.upenn.edu/~cit5940/page4.html").size());
        assertEquals(18, testMap.get("https://www.seas.upenn.edu/~cit5940/page5.html").size());

        assertTrue(testMap.get("https://www.seas.upenn.edu/~cit5940/page3.html")
                .contains("redblack"));
        assertTrue(testMap.get("https://www.seas.upenn.edu/~cit5940/page5.html")
                .contains("lets"));
        assertTrue(testMap.get("https://www.seas.upenn.edu/~cit5940/page5.html")
                .contains("cit594"));
    }

    @org.junit.Test
    public void buildIndexTest() {
        List<String> feeds = new ArrayList<>();
        feeds.add("https://www.cis.upenn.edu/~cit5940/sample_rss_feed.xml");
        Map<String, List<String>> parseMap = ib.parseFeed(feeds);
        Map<String, Map<String, Double>> firstIndexMap = ib.buildIndex(parseMap);

        assertEquals(5, firstIndexMap.size());
        assertEquals(0.1021, firstIndexMap
                .get("https://www.seas.upenn.edu/~cit5940/page1.html")
                .get("data"), 0.0001);
        assertEquals(0.04877, firstIndexMap
                .get("https://www.seas.upenn.edu/~cit5940/page3.html")
                .get("implement"), 0.00001);
        assertEquals(0.0894, firstIndexMap
                .get("https://www.seas.upenn.edu/~cit5940/page5.html")
                .get("random"), 0.0001);
    }

    @org.junit.Test
    public void buildInvertedIndexTest() {
        List<String> feeds = new ArrayList<>();
        feeds.add("https://www.cis.upenn.edu/~cit5940/sample_rss_feed.xml");
        Map<String, List<String>> parseMap = ib.parseFeed(feeds);
        Map<String, Map<String, Double>> firstIndexMap = ib.buildIndex(parseMap);
        Map<?,?> invertIndexMap = ib.buildInvertedIndex(firstIndexMap);

        assertEquals(3, ((ArrayList<Map.Entry<String, Double>>)invertIndexMap.get("and")).size());
        assertEquals("https://www.seas.upenn.edu/~cit5940/page5.html",
                ((ArrayList<Map.Entry<String, Double>>)invertIndexMap.get("and")).get(0).getKey());
        assertEquals("https://www.seas.upenn.edu/~cit5940/page3.html",
                ((ArrayList<Map.Entry<String, Double>>)invertIndexMap.get("and")).get(1).getKey());
        assertEquals("https://www.seas.upenn.edu/~cit5940/page2.html",
                ((ArrayList<Map.Entry<String, Double>>)invertIndexMap.get("and")).get(2).getKey());
    }

    @org.junit.Test
    public void buildHomePageTest() {
        // Manually build a easier test
        Map<String, List<String>> parseMap = new HashMap<>();
        String[] doc1List = new String[]{"hello", "world", "i", "hope", "all", "is", "well"};
        List<String> doc1 = Arrays.asList(doc1List);
        String[] doc2List = new String[]{"the", "semester", "is", "almost", "over", "indexing"
                , "is", "our", "next", "topic"};
        List<String> doc2 = Arrays.asList(doc2List);
        String[] doc3List = new String[]{"list", "stack", "heaps", "binary", "trees"
                , "are", "interesting", "data", "structures", "this", "semester"};
        List<String> doc3 = Arrays.asList(doc3List);
        parseMap.put("doc1.txt", doc1);
        parseMap.put("doc2.txt", doc2);
        parseMap.put("doc3.txt", doc3);

        Map<String, Map<String, Double>> firstIndexMap = ib.buildIndex(parseMap);
        Map<?,?> invertIndexMap = ib.buildInvertedIndex(firstIndexMap);
        List<Map.Entry<String, List<String>>> homePageList =
                (List<Map.Entry<String, List<String>>>)ib.buildHomePage(invertIndexMap);

        assertEquals(14, homePageList.size());
        assertEquals("semester", homePageList.get(0).getKey());
        assertEquals(2, homePageList.get(0).getValue().size());
        assertTrue(homePageList.get(0).getValue().contains("doc2.txt"));
        assertTrue(homePageList.get(0).getValue().contains("doc3.txt"));
        assertFalse(homePageList.get(0).getValue().contains("doc1.txt"));

        assertEquals("world", homePageList.get(1).getKey());
        assertEquals(1, homePageList.get(1).getValue().size());
    }

    @org.junit.Test
    public void createAutocompleteFileTest() {
        List<String> feeds = new ArrayList<>();
        feeds.add("https://www.cis.upenn.edu/~cit5940/sample_rss_feed.xml");
        Map<String, List<String>> parseMap = ib.parseFeed(feeds);
        Map<String, Map<String, Double>> firstIndexMap = ib.buildIndex(parseMap);
        Map<?,?> invertIndexMap = ib.buildInvertedIndex(firstIndexMap);
        List<Map.Entry<String, List<String>>> homePageList =
                (List<Map.Entry<String, List<String>>>)ib.buildHomePage(invertIndexMap);

        List<String> wordList = (List<String>)ib.createAutocompleteFile(homePageList);

        assertEquals(57, wordList.size());
        assertEquals("allows", wordList.get(0));
    }

    @org.junit.Test
    public void searchArticlesTest() {
        List<String> feeds = new ArrayList<>();
        feeds.add("https://www.cis.upenn.edu/~cit5940/sample_rss_feed.xml");
        Map<String, List<String>> parseMap = ib.parseFeed(feeds);
        Map<String, Map<String, Double>> firstIndexMap = ib.buildIndex(parseMap);
        Map<?,?> invertIndexMap = ib.buildInvertedIndex(firstIndexMap);
        List<String> articleList = ib.searchArticles("and", invertIndexMap);

        assertEquals(3, articleList.size());
        assertTrue(articleList.contains("https://www.seas.upenn.edu/~cit5940/page5.html"));
        assertTrue(articleList.contains("https://www.seas.upenn.edu/~cit5940/page3.html"));
        assertTrue(articleList.contains("https://www.seas.upenn.edu/~cit5940/page2.html"));

        assertFalse(articleList.contains("https://www.seas.upenn.edu/~cit5940/page1.html"));
        assertFalse(articleList.contains("https://www.seas.upenn.edu/~cit5940/page4.html"));
    }


}
