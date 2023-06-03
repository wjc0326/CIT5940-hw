import java.util.List;
import static org.junit.Assert.*;

public class InformationSpreadTest {

    @org.junit.Test
    public void loadGraphFromDataSet() {
        InformationSpread info = new InformationSpread();
        int numOfVertices = info.loadGraphFromDataSet("./test_graph.mtx", 0.01);
        assertEquals(12, numOfVertices);
        numOfVertices = info.loadGraphFromDataSet("./test_graph.mtx", 0.99);
        assertEquals(0, numOfVertices);
        numOfVertices = info.loadGraphFromDataSet("./test_graph.mtx", 0.55);
        assertEquals(8, numOfVertices);
        List<Integer> vertices = info.getVertices();
        assertTrue(vertices.contains(2));
        assertTrue(vertices.contains(3));
        assertTrue(vertices.contains(7));
        assertTrue(vertices.contains(9));
        assertTrue(vertices.contains(10));
        assertTrue(vertices.contains(12));
        assertFalse(vertices.contains(4));
    }

    @org.junit.Test
    public void getNeighbors() {
        InformationSpread info = new InformationSpread();
        int numOfVertices = info.loadGraphFromDataSet("./test_graph.mtx", 0.01);
        int[] res = info.getNeighbors(7);
        assertEquals(3, res.length);
        assertEquals(2, res[0]);
        assertEquals(3, res[1]);
        assertEquals(8, res[2]);

        numOfVertices = info.loadGraphFromDataSet("./test_graph.mtx", 0.85);
        res = info.getNeighbors(7);
        assertEquals(2, res.length);
        assertEquals(2, res[0]);
        assertEquals(3, res[1]);
    }

    @org.junit.Test
    public void path() {
        InformationSpread info = new InformationSpread();
        int numOfVertices = info.loadGraphFromDataSet("./simple_test.mtx", 0.55);
        List<Integer> res = info.path(1, 2);
        assertEquals(3, res.size());
        assertEquals(1, (int)res.get(0));
        assertEquals(3, (int)res.get(1));
        assertEquals(2, (int)res.get(2));
    }

    @org.junit.Test
    public void avgDegree() {
        InformationSpread info = new InformationSpread();
        int numOfVertices = info.loadGraphFromDataSet("./simple_test.mtx", 0.55);
        double avg = info.avgDegree();
        assertEquals(2.0, avg, 0.0);
        numOfVertices = info.loadGraphFromDataSet("./test_graph.mtx", 0.85);
        avg = info.avgDegree();
        assertEquals(0.66, avg, 0.01);      // 8/12
    }

    @org.junit.Test
    public void rNumber() {
        InformationSpread info = new InformationSpread();
        int numOfVertices = info.loadGraphFromDataSet("./simple_test.mtx", 0.55);
        double rNum = info.rNumber();
        assertEquals(1.1, rNum, 0.0);
    }

    @org.junit.Test
    public void generations() {
        InformationSpread info = new InformationSpread();
        int numOfVertices = info.loadGraphFromDataSet("./seed1_test.mtx", 0.0);
        assertEquals(10, numOfVertices);
        int res = info.generations(1, 1.0);
        assertEquals(3, res);
        res = info.generations(1, 0.6);
        assertEquals(2, res);
        res = info.generations(1, 0.2);
        assertEquals(1, res);

        numOfVertices = info.loadGraphFromDataSet("./seed2_test.mtx", 0.0);
        assertEquals(10, numOfVertices);

        // test for cannot reach the target threshold
        res = info.generations(1, 1.0);
        assertEquals(-1, res);
        res = info.generations(1, 0.8);
        assertEquals(-1, res);

        // regular test
        res = info.generations(1, 0.6);
        assertEquals(3, res);
        res = info.generations(1, 0.4);
        assertEquals(2, res);
        res = info.generations(1, 0.2);
        assertEquals(1, res);

        // test for seed outside the bounds
        res = info.generations(11, 0.2);
        assertEquals(-1, res);

        // test for threshold is negative
        res = info.generations(1, -0.2);
        assertEquals(-1, res);

        // test for threshold > 1
        res = info.generations(1, 1.2);
        assertEquals(-1, res);

        // test for threshold is 0
        res = info.generations(1, 0);
        assertEquals(0, res);
    }

    @org.junit.Test
    public void degree() {
        InformationSpread info = new InformationSpread();
        int numOfVertices = info.loadGraphFromDataSet("./test_graph.mtx", 0.85);
        int deg = info.degree(1);
        assertEquals(0, deg);
        deg = info.degree(7);
        assertEquals(2, deg);
        deg = info.degree(2);
        assertEquals(1, deg);
        deg = info.degree(15);
        assertEquals(-1, deg);
        deg = info.degree(-1);
        assertEquals(-1, deg);
    }

    @org.junit.Test
    public void degreeNodes() {
        InformationSpread info = new InformationSpread();
        int numOfVertices = info.loadGraphFromDataSet("./seed1_test.mtx", 0.0);
        List<Integer> nodes = (List<Integer>) info.degreeNodes(5);
        assertEquals(1, nodes.size());
        assertTrue(nodes.contains(2));
        assertFalse(nodes.contains(1));

        nodes = (List<Integer>) info.degreeNodes(2);
        assertEquals(5, nodes.size());
        assertTrue(nodes.contains(5));
        assertFalse(nodes.contains(9));

        nodes = (List<Integer>) info.degreeNodes(6);
        assertEquals(0, nodes.size());
    }

    @org.junit.Test
    public void generationsDegree() {
        InformationSpread info = new InformationSpread();
        int numOfVertices = info.loadGraphFromDataSet("./seed1_test.mtx", 0.0);
        int res = info.generationsDegree(2, 0.4, 8);
        assertEquals(-1, res);

        // test for seed is removed when immunization
        res = info.generationsDegree(1, 0.4, 1);
        assertEquals(0, res);

        res = info.generationsDegree(2, 0.4, 1);
        assertEquals(1, res);

        res = info.generationsDegree(2, 0.6, 1);
        assertEquals(2, res);

        res = info.generationsDegree(2, 0.8, 1);
        assertEquals(-1, res);
    }

    @org.junit.Test
    public void rNumberDegree() {
        InformationSpread info = new InformationSpread();
        int numOfVertices = info.loadGraphFromDataSet("./seed1_test.mtx", 0.1);
        double rNumber = info.rNumberDegree(1);
        assertEquals(0.16, rNumber, 0.01);

        rNumber = info.rNumberDegree(2);
        assertEquals(0.08, rNumber, 0.01);
    }

    @org.junit.Test
    public void clustCoeff() {
        InformationSpread info = new InformationSpread();
        int numOfVertices = info.loadGraphFromDataSet("./cluster1.mtx", 0.1);
        assertEquals(4, numOfVertices);
        double res = info.clustCoeff(1);
        assertEquals(0.0, res, 0.01);

        numOfVertices = info.loadGraphFromDataSet("./cluster2.mtx", 0.1);
        assertEquals(4, numOfVertices);
        res = info.clustCoeff(1);
        assertEquals(1.0, res, 0.01);

        numOfVertices = info.loadGraphFromDataSet("./seed1_test.mtx", 0.1);
        assertEquals(10, numOfVertices);
        res = info.clustCoeff(2);
        assertEquals(0.1, res, 0.01);
        // test for edge cases
        res = info.clustCoeff(11);
        assertEquals(-1.0, res, 0.01);
        res = info.clustCoeff(0);
        assertEquals(-1.0, res, 0.01);
        res = info.clustCoeff(1);
        assertEquals(0.0, res, 0.01);
    }

    @org.junit.Test
    public void clustCoeffNodes() {
        InformationSpread info = new InformationSpread();
        int numOfVertices = info.loadGraphFromDataSet("./seed1_test.mtx", 0.1);
        List<Integer> res = (List<Integer>)info.clustCoeffNodes(0.1, 0.5);
        assertEquals(2, res.size());
        assertTrue(res.contains(2));
        assertTrue(res.contains(6));
        assertFalse(res.contains(4));

        res = (List<Integer>)info.clustCoeffNodes(0.1, 1.0);
        assertEquals(3, res.size());
        assertTrue(res.contains(4));

        // test for edge case
        res = (List<Integer>)info.clustCoeffNodes(-1.0, 1.0);
        assertEquals(0, res.size());
    }

    @org.junit.Test
    public void generationsCC() {
        InformationSpread info = new InformationSpread();
        int numOfVertices = info.loadGraphFromDataSet("./seed1_test.mtx", 0.1);
        int res = info.generationsCC(3, 0.2, 0.1, 0.5);
        assertEquals(1, res);

        // cannot reach the target threshold
        res = info.generationsCC(3, 1.0, 0.1, 0.5);
        assertEquals(-1, res);

        // node out of bound
        res = info.generationsCC(11, 0.2, 0.1, 0.5);
        assertEquals(-1, res);

        // invalid threshold
        res = info.generationsCC(3, -0.1, 0.1, 0.5);
        assertEquals(-1, res);
        res = info.generationsCC(3, 1.1, 0.1, 0.5);
        assertEquals(-1, res);

        // no node within the range
        res = info.generationsCC(2, 0.2, 0.3, 0.5);
        assertEquals(-1, res);

        // remove node contains seed
        res = info.generationsCC(2, 0.2, 0.1, 0.5);
        assertEquals(0, res);

        res = info.generationsCC(3, 0.0, 0.1, 0.5);
        assertEquals(0, res);
    }

    @org.junit.Test
    public void rNumberCC() {
        InformationSpread info = new InformationSpread();
        int numOfVertices = info.loadGraphFromDataSet("./seed1_test.mtx", 0.1);
        double res = info.rNumberCC(0.1, 0.5);
        assertEquals(0.06, res, 0.01);
    }

    @org.junit.Test
    public void highDegLowCCNodes() {
        InformationSpread info = new InformationSpread();
        int numOfVertices = info.loadGraphFromDataSet("./seed1_test.mtx", 0.1);
        List<Integer> res = (List<Integer>) info.highDegLowCCNodes(3, 0.5);
        assertEquals(2, res.size());
        assertTrue(res.contains(2));
        assertTrue(res.contains(6));
        assertFalse(res.contains(4));
    }

    @org.junit.Test
    public void generationsHighDegLowCC() {
        InformationSpread info = new InformationSpread();
        int numOfVertices = info.loadGraphFromDataSet("./seed1_test.mtx", 0.1);
        int res = info.generationsHighDegLowCC(3, 0.1, 3, 0.5);
        assertEquals(0, res);

        res = info.generationsHighDegLowCC(3, 0.2, 3, 0.5);
        assertEquals(1, res);

        res = info.generationsHighDegLowCC(3, 0.5, 3, 0.5);
        assertEquals(-1, res);

        res = info.generationsHighDegLowCC(11, 0.2, 3, 0.5);
        assertEquals(-1, res);

        res = info.generationsHighDegLowCC(3, -0.2, 3, 0.5);
        assertEquals(-1, res);

        res = info.generationsHighDegLowCC(3, 1.2, 3, 0.5);
        assertEquals(-1, res);

        // if no node meets the requirement
        res = info.generationsHighDegLowCC(3, 0.2, 6, 0.5);
        assertEquals(-1, res);

        res = info.generationsHighDegLowCC(2, 0.2, 3, 0.5);
        assertEquals(0, res);
    }

    @org.junit.Test
    public void rNumberDegCC() {
        InformationSpread info = new InformationSpread();
        int numOfVertices = info.loadGraphFromDataSet("./seed1_test.mtx", 0.1);
        double res = info.rNumberDegCC(3, 0.5);
        assertEquals(0.06, res, 0.01);
    }
}