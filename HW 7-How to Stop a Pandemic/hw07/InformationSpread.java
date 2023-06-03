import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class InformationSpread implements IInformationSpread {
    private Graph graph = new GraphL();
    private Graph immuGraph;
    private Graph generationsGraph;
    private Graph highDegLowCCGraph;
    private List<Integer> vertices;
    private int totalNumOfNodes;
    private HashMap<Integer, Integer> generationMap;
    private double tau;

    @Override
    public int loadGraphFromDataSet(String filePath, double tau) {
        // use set to store the vertices with edge
        Set<Integer> verticesSet = new HashSet<>();
        this.tau = tau;

        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line = br.readLine();
            int n = Integer.parseInt(line.split(" ")[0]);
            totalNumOfNodes = n;
            // take vertex with id 0 into consideration when initializing
            graph.init(n + 1);

            line = br.readLine();
            while (line != null) {
                String[] contents = line.split(" ");
                int from = Integer.parseInt(contents[0]);
                int to = Integer.parseInt(contents[1]);
                double weight = Double.parseDouble(contents[2]);

                // both vertices should not be 0, the weight should >= tau
                if ((from != 0) && (to != 0) && (weight >= tau)) {
                    int wgt = (int) (weight * 100);
                    // add both directions of each edge to the graph to make it undirected
                    graph.addEdge(from, to, wgt);
                    graph.addEdge(to, from, wgt);
                    verticesSet.add(from);
                    verticesSet.add(to);
                }
                line = br.readLine();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        vertices = new ArrayList<>(verticesSet);
        return vertices.size();
    }

    @Override
    public int[] getNeighbors(int id) {
        return graph.neighbors(id);
    }

    public List<Integer> getVertices() {
        return vertices;
    }

    public HashMap<Integer, PathVertexInfo> dijkstraShortestPath(Graph graph, int startVertex) {
        // Create the HashMap for vertex information
        HashMap<Integer, PathVertexInfo> info = new HashMap<>();

        // Put all graph vertices in both the info HashMap and the PriorityQueue
        // of unvisited vertices
        PriorityQueue<PathVertexInfo> unvisited = new PriorityQueue<>();
        for (int vertex = 1; vertex <= totalNumOfNodes; vertex++) {
            PathVertexInfo vertexInfo = new PathVertexInfo(vertex);
            unvisited.add(vertexInfo);
            info.put(vertex, vertexInfo);
        }

        // startVertex has a distance of 0 from itself
        info.get(startVertex).distance = 0;

        // Iterate through all vertices in the priority queue
        while (unvisited.size() > 0) {
            // Get info about the vertex with the shortest distance from startVertex
            PathVertexInfo currentInfo = unvisited.peek();
            unvisited.remove();

            // Check potential path lengths from the current vertex to all neighbors
            for (int neighbor : graph.neighbors(currentInfo.vertex)) {
                int edgeWeight = graph.weight(currentInfo.vertex, neighbor);
                double alternativePathDistance = currentInfo.distance + edgeWeight;

                // If a shorter path from startVertex to adjacentVertex is found,
                // update adjacentVertex's distance and predecessor
                PathVertexInfo adjacentInfo = info.get(neighbor);
                if (alternativePathDistance < adjacentInfo.distance) {
                    unvisited.remove(adjacentInfo);
                    adjacentInfo.distance = alternativePathDistance;
                    adjacentInfo.predecessor = currentInfo.vertex;
                    unvisited.add(adjacentInfo);
                }
            }
        }

        return info;
    }

    @Override
    public List<Integer> path(int source, int destination) {
        List<Integer> shortestPath = new ArrayList<>();

        // STEP 1: transform the weights of the edges
        for (int i = 0; i < graph.nodeCount(); i++) {
            int[] neighbors = getNeighbors(i);
            // if the current node has neighbors
            if (neighbors.length >= 1) {
                for (int j = 0; j < neighbors.length; j++) {
                    int currentWeight = graph.weight(i, neighbors[j]);
                    int newWeight = (int) ((-Math.log((double)currentWeight / 100)) * 100);
                    graph.removeEdge(i, neighbors[j]);
                    graph.addEdge(i, neighbors[j], newWeight);
                }
            }
        }

        // STEP 2: use Dijk to calculate the shortest and pred
        HashMap<Integer, PathVertexInfo> dijkMap = dijkstraShortestPath(graph, source);

        // STEP 3: build the path
        int currentVertex = destination;
        while (currentVertex != source) {
            shortestPath.add(currentVertex);
            currentVertex = dijkMap.get(currentVertex).predecessor;
        }
        shortestPath.add(source);
        Collections.reverse(shortestPath);
        return shortestPath;
    }

    @Override
    public double avgDegree() {
        double avg = 0.0;
        for (int vertex = 1; vertex <= totalNumOfNodes; vertex++) {
            avg += graph.neighbors(vertex).length;
        }
        avg /= totalNumOfNodes;
        return avg;
    }

    @Override
    public double rNumber() {
        return tau * avgDegree() * 1;
    }

    public List<Integer> breadthFirstSearch(Graph graph, int startVertex, int targetNum) {
        HashSet<Integer> discoveredSet = new HashSet<>();
        Queue<Integer> frontierQueue = new LinkedList<>();
        List<Integer> visitedList = new ArrayList<>();

        generationMap = new HashMap<>();
        // startVertex has a distance of 0 from itself
        generationMap.put(startVertex, 0);

        frontierQueue.add(startVertex); // Enqueue startVertex in frontierQueue
        discoveredSet.add(startVertex); // Add startVertex to discoveredSet

        while (frontierQueue.size() > 0) {
            int currentVertex = frontierQueue.remove();
            visitedList.add(currentVertex);
            for (int neighbor : graph.neighbors(currentVertex)) {
                if (!discoveredSet.contains(neighbor)) {
                    frontierQueue.add(neighbor);
                    discoveredSet.add(neighbor);

                    // Distance of neighbor is 1 more than currentVertex
                    generationMap.put(neighbor, generationMap.get(currentVertex) + 1);
                }
            }
            if (visitedList.size() >= targetNum) {
                break;
            }
        }
        return visitedList;
    }

    @Override
    public int generations(int seed, double threshold) {
        // if the threshold is negative or greater than 1, return -1
        // if the seed is outside the bounds of the nodes, return -1
        if (threshold < 0.0 || threshold > 1.0 || seed > totalNumOfNodes || seed <= 0) {
            return -1;
        }
        // if the seed is valid but the threshold is 0, return 0
        if (threshold == 0.0) {
            return 0;
        }
        int targetNum = (int)(threshold * totalNumOfNodes);
        List<Integer> bfsList = breadthFirstSearch(graph, seed, targetNum);

        // if the target threshold cannot be reached, return -1
        if (bfsList.size() < targetNum) {
            return -1;
        }
        return generationMap.get(bfsList.get(bfsList.size() - 1));
    }


    // ********************
    // FIRST APPROACH: remove edges incident to nodes with high degree
    // ********************
    @Override
    public int degree(int n) {
        // if the node is not in the network, return -1
        if (n > totalNumOfNodes || n <= 0) {
            return -1;
        }
        return graph.neighbors(n).length;
    }

    @Override
    public Collection<Integer> degreeNodes(int d) {
        List<Integer> nodes = new ArrayList<>();
        for (int vertex = 1; vertex <= totalNumOfNodes; vertex++) {
            if (degree(vertex) == d) {
                nodes.add(vertex);
            }
        }
        return nodes;
    }

    public Graph copyGraph(Graph prev) {
        Graph copy = new GraphL();
        int nodeCount = prev.nodeCount();
        copy.init(nodeCount);
        for (int i = 0; i < prev.nodeCount(); i++) {
            for (int neighbor : prev.neighbors(i)) {
                copy.addEdge(i, neighbor, prev.weight(i, neighbor));
            }
        }
        return copy;
    }

    @Override
    public int generationsDegree(int seed, double threshold, int d) {
        immuGraph = copyGraph(graph);
        boolean flag = false;
        List<Integer> removeNode = new ArrayList<>();

        // first store the nodes need to be removed into List
        for (int i = 0; i < immuGraph.nodeCount(); i++) {
            // immunize all nodes with the given degree
            if (immuGraph.neighbors(i).length == d) {
                flag = true;
                removeNode.add(i);
            }
        }
        // for each node to be removed, delete the edge
        for (int j = 0; j < removeNode.size(); j++) {
            for (int neighbor : immuGraph.neighbors(removeNode.get(j))) {
                immuGraph.removeEdge(removeNode.get(j), neighbor);
                immuGraph.removeEdge(neighbor, removeNode.get(j));
            }
        }

        // if seed is removed as part of the immunization, return 0
        if (removeNode.contains(seed)) {
            return 0;
        }

        // if there is no node in the graph with the given degree, return -1
        if (!flag) {
            return -1;
        }
        // run generations on this immuGraph

        // if the threshold is negative or greater than 1, return -1
        // if the seed is outside the bounds of the nodes, return -1
        // the return value of nodeCount() is including the "0" node
        if (threshold < 0.0 || threshold > 1.0 || seed > (immuGraph.nodeCount() - 1) || seed <= 0) {
            return -1;
        }
        // if the seed is valid but the threshold is 0, return 0
        if (threshold == 0.0) {
            return 0;
        }
        int targetNum = (int)(threshold * (immuGraph.nodeCount() - 1));
        List<Integer> bfsList = breadthFirstSearch(immuGraph, seed, targetNum);

        // if the target threshold cannot be reached, return -1
        if (bfsList.size() < targetNum) {
            return -1;
        }
        return generationMap.get(bfsList.get(bfsList.size() - 1));
    }

    @Override
    public double rNumberDegree(int d) {
        // call generationsDegree() to build the immuGraph
        int res = generationsDegree(1, 0.0, d);

        double avg = 0.0;
        for (int vertex = 1; vertex < immuGraph.nodeCount(); vertex++) {
            avg += immuGraph.neighbors(vertex).length;
        }
        avg /= (immuGraph.nodeCount() - 1);
        return tau * avg;
    }


    // ********************
    // SECOND APPROACH: clustering coefficient immunization strategy
    // ********************
    @Override
    public double clustCoeff(int n) {
        // if the node is not in the network, return -1
        if (n > totalNumOfNodes || n <= 0) {
            return -1.0;
        }

        int[] neighbors = graph.neighbors(n);
        int numOfNeighbors = neighbors.length;
        // nodes with degree 0 or 1 have a cc of 0
        if (numOfNeighbors <= 1) {
            return 0.0;
        }

        int totalPossibleEdges = numOfNeighbors * (numOfNeighbors - 1) / 2;
        int actualEdges = 0;
        for (int neighbor : neighbors) {
            for (int j : neighbors) {
                // skip the neighbor itself
                if (j != neighbor) {
                    if (graph.hasEdge(j, neighbor)) {
                        actualEdges += 1;
                    }
                }
            }
        }
        // we counted each edge twice
        actualEdges /= 2.0;

        return (double)actualEdges / (double)totalPossibleEdges;
    }

    @Override
    public Collection<Integer> clustCoeffNodes(double low, double high) {
        List<Integer> nodes = new ArrayList<>();
        // if the threshold is invalid, return an empty list
        if (low < 0.0 || low > 1.0 || high < 0.0 || high > 1.0 || low > high) {
            return nodes;
        }
        for (int i = 0; i <= totalNumOfNodes; i++) {
            if (((int)(clustCoeff(i) * 100) >= (int)(low * 100) - 1) &&
                    ((int)(clustCoeff(i) * 100) <= (int)(high * 100) + 1)) {
                nodes.add(i);
            }
        }
        return nodes;
    }

    @Override
    public int generationsCC(int seed, double threshold, double low, double high) {
        generationsGraph = copyGraph(graph);
        List<Integer> removeNode = (List<Integer>) clustCoeffNodes(low, high);

        // for each node to be removed, delete the edge
        for (int j = 0; j < removeNode.size(); j++) {
            for (int neighbor : generationsGraph.neighbors(removeNode.get(j))) {
                generationsGraph.removeEdge(removeNode.get(j), neighbor);
                generationsGraph.removeEdge(neighbor, removeNode.get(j));
            }
        }

        // if seed is removed as part of the immunization, return 0
        if (removeNode.contains(seed)) {
            return 0;
        }

        // if there is no node in the graph with the clustering coefficient within the range
        // return -1
        if (removeNode.size() == 0) {
            return -1;
        }
        // run generations on this generationsGraph

        // if the seed is outside the bounds of the nodes, return -1
        // the return value of nodeCount() is including the "0" node
        if (threshold < 0.0 || threshold > 1.0 ||
                seed > (generationsGraph.nodeCount() - 1) || seed <= 0) {
            return -1;
        }
        // if the seed is valid but the threshold is 0, return 0
        if (threshold == 0.0) {
            return 0;
        }
        int targetNum = (int)(threshold * (generationsGraph.nodeCount() - 1));
        List<Integer> bfsList = breadthFirstSearch(generationsGraph, seed, targetNum);

        // if the target threshold cannot be reached, return -1
        if (bfsList.size() < targetNum) {
            return -1;
        }
        return generationMap.get(bfsList.get(bfsList.size() - 1));
    }

    @Override
    public double rNumberCC(double low, double high) {
        // call generationsCC() to build the generationsGraph
        int res = generationsCC(1, 0.0, low, high);

        double avg = 0.0;
        for (int vertex = 1; vertex < generationsGraph.nodeCount(); vertex++) {
            avg += generationsGraph.neighbors(vertex).length;
        }
        avg /= (generationsGraph.nodeCount() - 1);
        return tau * avg;
    }

    // ********************
    // THIRD APPROACH: clustering coefficient & degree immunization strategy
    // ********************
    @Override
    public Collection<Integer> highDegLowCCNodes(int lowBoundDegree, double upBoundCC) {
        List<Integer> lowCC = (List<Integer>) clustCoeffNodes(0.0, upBoundCC);
        List<Integer> highDegree = new ArrayList<>();
        List<Integer> res = new ArrayList<>();

        // find the largest degree
        int maxDegree = 0;
        for (int vertex = 1; vertex <= totalNumOfNodes; vertex++) {
            maxDegree = Math.max(maxDegree, degree(vertex));
        }

        for (int degree = lowBoundDegree; degree <= maxDegree; degree++) {
            List<Integer> nodesOfDegree = (List<Integer>) degreeNodes(degree);
            highDegree.addAll(nodesOfDegree);
        }

        for (int i = 1; i <= totalNumOfNodes; i++) {
            if (lowCC.contains(i) && highDegree.contains(i)) {
                res.add(i);
            }
        }
        return res;
    }

    @Override
    public int generationsHighDegLowCC(int seed, double threshold, int lowBoundDegree,
                                       double upBoundCC) {
        highDegLowCCGraph = copyGraph(graph);
        List<Integer> removeNode = (List<Integer>) highDegLowCCNodes(lowBoundDegree, upBoundCC);

        // for each node to be removed, delete the edge
        for (int j = 0; j < removeNode.size(); j++) {
            for (int neighbor : highDegLowCCGraph.neighbors(removeNode.get(j))) {
                highDegLowCCGraph.removeEdge(removeNode.get(j), neighbor);
                highDegLowCCGraph.removeEdge(neighbor, removeNode.get(j));
            }
        }

        // if seed is removed as part of the immunization, return 0
        if (removeNode.contains(seed)) {
            return 0;
        }

        // if there is no node in the graph satisfied within the range
        // return -1
        if (removeNode.size() == 0) {
            return -1;
        }
        // run generations on this generationsGraph

        // if the seed is outside the bounds of the nodes, return -1
        // the return value of nodeCount() is including the "0" node
        if (threshold < 0.0 || threshold > 1.0 ||
                seed > (highDegLowCCGraph.nodeCount() - 1) || seed <= 0) {
            return -1;
        }
        // if the seed is valid but the threshold is 0, return 0
        if (threshold == 0.0) {
            return 0;
        }
        int targetNum = (int)(threshold * (highDegLowCCGraph.nodeCount() - 1));
        List<Integer> bfsList = breadthFirstSearch(highDegLowCCGraph, seed, targetNum);

        // if the target threshold cannot be reached, return -1
        if (bfsList.size() < targetNum) {
            return -1;
        }
        return generationMap.get(bfsList.get(bfsList.size() - 1));
    }

    @Override
    public double rNumberDegCC(int lowBoundDegree, double upBoundCC) {
        // call generationsCC() to build the generationsGraph
        int res = generationsHighDegLowCC(1, 0.0, lowBoundDegree, upBoundCC);

        double avg = 0.0;
        for (int vertex = 1; vertex < highDegLowCCGraph.nodeCount(); vertex++) {
            avg += highDegLowCCGraph.neighbors(vertex).length;
        }
        avg /= (highDegLowCCGraph.nodeCount() - 1);
        return tau * avg;
    }

    static class PathVertexInfo implements Comparable<PathVertexInfo> {
        private int vertex;
        private double distance;
        private int predecessor;

        public PathVertexInfo(int vertex) {
            this.setVertex(vertex);
            setDistance(Double.POSITIVE_INFINITY);
            setPredecessor(-1);
        }

        public int compareTo(PathVertexInfo other) {
            if (getDistance() > other.getDistance()) {
                return 1;
            } else if (getDistance() < other.getDistance()) {
                return -1;
            }
            return 0;
        }

        public int getVertex() {
            return vertex;
        }

        public void setVertex(int vertex) {
            this.vertex = vertex;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public int getPredecessor() {
            return predecessor;
        }

        public void setPredecessor(int predecessor) {
            this.predecessor = predecessor;
        }
    }
}
