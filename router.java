import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
public class router extends mapper{
    mapper map; //creates an object for the class mapper that will keep track of edges ect.
    int totalMiles; //Total miles
    int totalTime; //Total time
    HashSet<String> totalCities; //stores all the cities, we want to use a hashset because a list would not be efficient
    Hashtable<String, Integer> cost;//keeps track of the total cost of the path
    Hashtable<String, String> totalAttractions; //Stores the attraction with its location for all attractions
    Hashtable<String, Boolean> citiesVisited; //Stores all the cities and if they are visited
    Hashtable<String, String> previous; //stores the current location and the previous location
    public router() {
        map = new mapper();
        totalMiles = 0;
        totalTime = 0;
        totalAttractions = new Hashtable<>(143);
        citiesVisited = new Hashtable<>();
        previous = new Hashtable<>();
        cost = new Hashtable<>();
        totalCities = new HashSet<>();
    }
    List<String> route(String start_location, String end_location, List<String> attractions) {
        //currentRoute will allow a return of the route from the start through the attractions to the end
        ArrayList<String> currentRoute = new ArrayList<>();
        //start at the beginning
        map.addEdge(start_location, start_location, 0, 0);
        //set up the hashes
        for (String city : totalCities) {
            if (city != null) {
                citiesVisited.put(city, false);
                cost.put(city, Integer.MAX_VALUE);
                //this makes the cost infinite and all of the boolean citiesVisited values to false
            }
        }
        //start at the starting location
        cost.put(start_location, 0);
        //this is going to loop through all the cities
        for (String city : totalCities) {
            //this is going to loop through all the non visited cities
            while (!citiesVisited.get(city)) {
                String vertex = seedV();
                if (vertex != null) {
                    citiesVisited.put(vertex, true);
                }
                for (String vertex2 : map.neighbors.get(vertex)) {
                    int weight = roadDistance(vertex, vertex2);
                    if (cost.get(vertex2) > cost.get(vertex) + weight && !vertex2.equals(vertex)) {
                        //original vertex cost is greater
                        cost.put(vertex2, cost.get(vertex) + weight);
                        previous.put(vertex2, vertex);
                    }
                }
            }
        }
        //we need a temporary array, and we can't use a hashset because we need to sort this later
        ArrayList<Integer> temp = new ArrayList<>();
        //the next temp we are going to use a hashtable in order to be able to store both values
        Hashtable<Integer, String> temp2 = new Hashtable<>();
        for (String attraction : attractions) {
            temp.add(cost.get(totalAttractions.get(attraction)));
            temp2.put(cost.get(totalAttractions.get(attraction)), attraction);
        }
        ArrayList<String> orderedAttractions = new ArrayList<>();
        Collections.sort(temp);
        for (int i : temp) {
            orderedAttractions.add(totalAttractions.get(temp2.get(i)));
        }
        orderedAttractions.add(0, start_location);
        //check if the end location is already present in the attractions
        if (orderedAttractions.contains(end_location)) {
            orderedAttractions.remove(end_location);
            orderedAttractions.add(end_location);
        }
        Stack flip = new Stack();
        for (int i = 0; i < orderedAttractions.size()-1; i++) {
            String current = orderedAttractions.get(i);
            String next = orderedAttractions.get(i + 1);
            String put = orderedAttractions.get(i + 1);
            flip.add(next);
            while (!current.equals(next)) {
                String prev = previous.get(next);
                totalMiles += roadDistance(next, prev);
                totalTime += roadTime(next, prev);
                flip.add(prev);
                next = prev;
            }
            while (!flip.isEmpty()) {
                currentRoute.add((String) flip.pop());
            }
            //reset all three hashtables
            citiesVisited = new Hashtable<>();
            previous = new Hashtable<>();
            cost = new Hashtable<>();
            for (String city : totalCities) {
                if (city != null) {
                    citiesVisited.put(city, false);
                    cost.put(city, Integer.MAX_VALUE);
                }
            }
            cost.put(put, 0);
            for (String city : totalCities) {
                while (!citiesVisited.get(city)) {
                    String vertex = seedV();
                    if (vertex != null) {
                        citiesVisited.put(vertex, true);
                    }
                    for (String v : map.neighbors.get(vertex)) {
                        int dist = roadDistance(vertex, v);
                        if (cost.get(v) > cost.get(vertex) + dist && !v.equals(vertex)) {
                            cost.put(v, cost.get(vertex) + dist);
                            previous.put(v, vertex);
                        }
                    }
                }
            }
        }
        return currentRoute;
    }
    private String seedV() {
        //the seed input for above
        String v="";
        int min = Integer.MAX_VALUE;
        for (String city : totalCities) {
            if (!citiesVisited.get(city) && cost.get(city) <= min) {
                min = cost.get(city);
                v = city;
            }
        }
        return v;
    }
    public int roadDistance(String v1, String v2) {
        //this function finds the distance of a road
        int dist = 0;
        for (Edge road : map.edges) {
            if ((road.first.equals(v1)) && (road.second.equals(v2))) {
                return road.weight;
            }
            else if ((road.first.equals(v2)) && (road.second.equals(v1))) {
                return road.weight;
            }
        }
        return dist;
    }
    public int roadTime(String v1, String v2) {
        //this function finds the time of a road
        int time = 0;
        for (Edge road : map.edges) {
            if ((road.first.equals(v1)) && (road.second.equals(v2))) {
                return road.minutes;
            }
            else if ((road.first.equals(v2)) && (road.second.equals(v1))) {
                return road.minutes;
            }
        }
        return time;
    }
    private int getTotalMiles(){
        return totalMiles;
    }
    private int getTotalTime(){
        return totalTime;
    }
    public static void main(String args[]) throws FileNotFoundException {
        router myRoute = new router();
        String fileName = "roads.csv";
        String fileName2 = "attractions.csv";
        //by combining we will only need one scanner
        File[] files = new File[]{new File(fileName), new File(fileName2)};
        //get and read the files
        int counter = 0;
        for (File f : files) {
            Scanner scanner = new Scanner(f);
            if (counter == 0) {
                scanner.useDelimiter("\r");
                while (scanner.hasNextLine() && scanner.hasNext()) {
                    String line = scanner.next();
                    if (line.strip().length() == 0) {
                        break;
                    }
                    String[] road = line.split(",");
                    String start = road[0].replace("\n", "");
                    String end = road[1];
                    int miles = Integer.parseInt(road[2]);
                    if (road[3].equals("10a")) {
                        road[3] = "101";
                    }
                    int minutes = Integer.parseInt(road[3]);
                    if (start != null && end != null) {
                        myRoute.map.addEdge(start, end, miles, minutes);
                        myRoute.totalCities.add(start);
                        myRoute.totalCities.add(end);
                    }
                }
                counter++;
            }
            else {
                scanner.useDelimiter("\n");
                int lineNumber = 0;
                while (scanner.hasNextLine() && scanner.hasNext()) {
                    String line = scanner.next();
                    if (lineNumber == 0) {
                        lineNumber++;
                    }
                    else {
                        String[] attraction = line.split(",");
                        String interest = attraction[0];
                        String location = attraction[1];
                        myRoute.totalAttractions.put(interest, location);
                    }
                }
            }
        }

        List<String> attractions = new ArrayList<>();
        attractions.add("Disney World");
        attractions.add("Paul Bunyon and Babe the Blue Ox");
        attractions.add("Las Vegas Strip");
        attractions.add("Speedboat sightseeing");
        attractions.add("Boston Old Town Trolley Tour");
        attractions.add("Comedy Tour of Charlotte");
        List<String> path = myRoute.route("San Diego CA", "San Francisco CA", attractions);
        System.out.println(path.toString());
        System.out.println("+----------------------------------------------+");
        System.out.println("Distance: " + myRoute.getTotalMiles() + " miles");
        System.out.println("Time: " + myRoute.getTotalTime() + " minutes");
    }


}
