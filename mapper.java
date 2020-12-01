import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
//this class is going to create a "map" or graph of what we want by using vertexes connected via edges
public class mapper {
    Hashtable<String, List<String>> neighbors;
    List<Edge> edges;
    public mapper() {
        neighbors = new Hashtable<>();
        edges = new ArrayList<>();
    }
    public class Edge {
        String first;
        String second;
        int weight;
        int minutes;
        public Edge (String firstInput, String secondInput, int distInput, int timeInput) {
            first = firstInput;
            second = secondInput;
            weight = distInput;
            minutes = timeInput;
        }
    }
    public void addVertex(String location) {
        neighbors.putIfAbsent(location, new ArrayList<>());
    }
    @Override
    public String toString () {
        String ret = "";
        for (Edge edge : edges) {
            ret += edge.first + " | " + edge.second + "  " + edge.weight + "\n";
        }
        return ret;
    }
    public void addEdge(String start, String end, int dist, int time) {
        addVertex(start);
        addVertex(end);
        neighbors.get(start).add(end);
        neighbors.get(end).add(start);
        edges.add(new Edge(start, end, dist, time));
    }
}
