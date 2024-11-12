import java.io.FileReader;
import java.util.*;
import java.io.FileNotFoundException;

public class TrainNetwork {


    private ArrayList<Station> stations = new ArrayList<>();
    private ArrayList<Line> lines = new ArrayList<>();
    Map<Integer, ArrayList<Integer>> stationLinks = new HashMap<Integer, ArrayList<Integer>>();

    public ArrayList<Station> getStations() { return this.stations; } //Sean 17/3
    public ArrayList<Line> getLines() { return this.lines; }

    static class Station implements Node {
        String name;
        int ID;
        ArrayList<String> line;

        //constructor
        Station(String name, int ID, ArrayList<String> line) {
            this.name = name;
            this.line = line;
            this.ID = ID;
        }

        //getters
        public ArrayList<String> getLine() {
            return this.line;
        }

        @Override
        public int getNodeID() {
            return this.ID;
        }

        @Override
        public String getNodeName() {
            return this.name;
        }

        public void printDetails() {
            System.out.println("Name: " + name + ". ID: " + ID + ". Lines: " + line.toString());
        }
    }

    static class Line implements Edge {
        String color;
        int weight;
        Station node1;
        Station node2;

        //constructor
        Line(String color, int weight, Station node1, Station node2) {
            this.color = color;
            this.weight = weight;
            this.node1 = node1;
            this.node2 = node2;
        }

        //getters
        public String getColor() {
            return this.color;
        }

        public int getWeight() {
            return this.weight;
        }

        @Override
        public Station getNode1() {
            return this.node1;
        }

        @Override
        public Station getNode2() {
            return this.node2;
        }

        public void printDetails() {
            System.out.println("Station1: " + node1.getNodeID() + ". Station2: " + node2.getNodeID() + ". Weight: " + weight + ". Colour: " + color);
        }
    }

    //populate stations and connections variables
    public void makeStationsAndLinks() {
        try {
            FileReader myObj = new FileReader("Stations");
            Scanner scanner = new Scanner(myObj);

            //counter used for hashmap id values
            int counter = 0;
            while (scanner.hasNextLine()) {
                String info = scanner.nextLine();
                String[] station = info.split(" ");                   // split Stations file entries by " "
                String id = station[0];                                    // station[0] = id
                String name = station[1];                                 // station[1] = name
                ArrayList<Integer> connections = new ArrayList<>();
                ArrayList<String> lines = new ArrayList<>();
                //check if next parse is a connection or line Colour
                for (int i = 2; i < station.length; i++) {                 //station[2] onwards has line colour and connections
                    if (station[i].matches("-?\\d+")) {
                        try {
                            connections.add(Integer.parseInt(station[i]));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    } else {
                        lines.add(station[i]);
                    }
                }

                try {
                    stations.add(new Station(name, Integer.parseInt(id), lines));               //Create stations using information above
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                stationLinks.put(counter, new ArrayList<Integer>());                            //create stationLinks using connections
                for (int i = 0; i < connections.size(); i++) {
                    stationLinks.get(counter).add(connections.get(i));
                }
                counter++;
            }
        } catch (FileNotFoundException e) {
            //error if file doesnt exist
            e.printStackTrace();
        }
    }


    public void makeLines() {
        try {
            FileReader myObj = new FileReader("JourneyTimes");
            Scanner scanner = new Scanner(myObj);

            while (scanner.hasNextLine()) {
                String info = scanner.nextLine();
                String[] journey = info.split(" ");
                int station1ID = Integer.parseInt(journey[0]);
                int station2ID = Integer.parseInt(journey[1]);
                int time = Integer.parseInt(journey[2]);

                Station station1 = getStationFromID(station1ID);
                Station station2 = getStationFromID(station2ID);

                String lineColor = getLineColor(station1, station2);

                try {
                    lines.add(new Line(lineColor, time, station1, station2));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

            }

        } catch (FileNotFoundException e) {
            //error if file doesnt exist
            e.printStackTrace();
        }
    }

    public Station getStationFromID(int stationID){
        for(int i = 0; i < stations.size(); i++){
            if(stations.get(i).getNodeID() == stationID){
                return stations.get(i);
            }
        }
        return null;
    }

    public String getLineColor(Station station1, Station station2){
        for(int i = 0; i < station1.getLine().size(); i++){
            for(int j = 0; j < station2.getLine().size(); j++){
                if (station2.getLine().get(j).equals(station1.getLine().get(i))){
                    return station2.getLine().get(j);
                }
            }
        }
        return null;
    }

    public int getWeight(Station s1, Station s2) {
        for (int i = 0; i < lines.size(); i++) {
            if ((lines.get(i).getNode1().getNodeID() == s1.getNodeID() || lines.get(i).getNode1().getNodeID() == s2.getNodeID()) && (lines.get(i).getNode2().getNodeID() == s2.getNodeID() || lines.get(i).getNode2().getNodeID() == s1.getNodeID())) {
                return lines.get(i).weight;
            }
        }
        return -1;
    }

    public Boolean checkLineExists(Station s1, Station s2) {
        for (int i = 0; i < lines.size(); i++) {
            if (( s1.getNodeID() == lines.get(i).getNode1().getNodeID()  || s1.getNodeID() == lines.get(i).getNode2().getNodeID())  &&( s2.getNodeID() == lines.get(i).getNode1().getNodeID()  || s2.getNodeID() == lines.get(i).getNode2().getNodeID())) {
//                ((lines.get(i).getNode1().getNodeID() == s1.getNodeID() || lines.get(i).getNode2().getNodeID() == s2.getNodeID()) && (lines.get(i).getNode1().getNodeID() == s2.getNodeID() || lines.get(i).getNode2().getNodeID() == s1.getNodeID()))

                return true;
            }
        }
        return false;
    }

    public Boolean checkGraphExists() {
        if (stations == null || stationLinks == null || lines == null) {
            return false;
        }
        return true;
    }


    public void printTrainNetwork() {
        for (int i = 0; i < stations.size(); i++) {
            stations.get(i).printDetails();
        }

        for (int i = 0; i < stationLinks.size(); i++) {
            System.out.print("id: " + i + ". Links: ");
            for (int j = 0; j < stationLinks.get(i).size(); j++) {
                System.out.print(stationLinks.get(i).get(j) + " ");

            }
            System.out.println(" ");
        }

        for (int i = 0; i < stations.size(); i++) {
            lines.get(i).printDetails();
        }
    }

    public ArrayList<Integer> nextStates(Integer current){
        ArrayList<Integer> tempList= new ArrayList();;
        ArrayList<Integer> temp = stationLinks.get(current);
        for(int i=0; i<temp.size(); i++){
            if(getStationFromID(temp.get(i)).getNodeID() != 0) {
                tempList.add(getStationFromID(temp.get(i)).getNodeID());
            }
        }
        return tempList;
    }

    public ArrayList<Integer> extendPath(Integer current) {
        ArrayList<Integer> ans = new ArrayList();
        for(Integer i : nextStates(current)){
            //ans.add(p.get(0));
            ans.add(i);
        }
        //System.out.println(ans);
        return ans;
    }

    public ArrayList<Integer> findPath(Integer startID, Integer goalID) {
        ArrayList<Integer> visited = new ArrayList<>();
        ArrayList<Integer> agenda = new ArrayList<>();
        Hashtable<Integer, Integer> currentLength = new Hashtable<Integer, Integer>();
        Hashtable<Integer, Integer> nodeValue = new Hashtable<Integer, Integer>();
        Hashtable<Integer, Integer> nodeAncestors = new Hashtable<Integer, Integer>();

        agenda.add(startID);
        currentLength.put(startID, 0);
        nodeValue.put(startID, getHeuristic(startID, startID));

        while (!agenda.isEmpty()) {

            Integer currentNode = agenda.get(0);

            //Set currentNode to lowest on agenda
            for (Integer a : agenda){
                if (nodeValue.get(a) < nodeValue.get(currentNode)){
                    currentNode = a;
                }
            }
            agenda.remove(currentNode);

            if (currentNode == goalID){
                return getPath(startID, goalID, nodeAncestors);
            }


            ArrayList<Integer> nextNodes = extendPath(currentNode);

            for (Integer n : nextNodes){
                //Add distance and values of next states to dictionaries
                Integer distance = getWeight(getStationFromID(currentNode), getStationFromID(n)) + currentLength.get(currentNode);
                Integer heuristic = getHeuristic(currentNode, n);
                Integer value = distance + heuristic;

                Boolean smaller = true;

                for (Integer v : visited){
                    if ((n == v) && (nodeValue.get(v) <= value)){
                        smaller = false;
                    }
                }

                if (smaller == true){
                    if(!(agenda.contains(n))){
                        agenda.add(n);
                    }
                    if(nodeAncestors.get(currentNode) != n) {
                        if (nodeAncestors.containsKey(n)){
                            if (nodeValue.get(n) > value){
                                nodeAncestors.put(n, currentNode);

                                nodeValue.put(n, value);
                            }
                        } else {
                            nodeAncestors.put(n, currentNode);
                            nodeValue.put(n, value);
                        }
                    }

                    currentLength.put(n, distance);
                }
            }
            if(!(visited.contains(currentNode))){
                visited.add(currentNode);
            }

        }

        return new ArrayList<>();

    }

    public Integer getHeuristic(Integer previous, Integer current){
        ArrayList<Integer> nextNodes = extendPath(current);

        Integer quickest = getWeight(getStationFromID(nextNodes.get(0)), getStationFromID(current));

        for( Integer n: nextNodes){
            if((n != previous) && (getWeight(getStationFromID(n), getStationFromID(current)) < quickest)){
                quickest = getWeight(getStationFromID(n), getStationFromID(current)) ;
            }
        }

        //Returns the travel time to the quickest node
        return quickest;
    }

    public ArrayList<Integer> getPath(Integer startNode, Integer goalNode, Hashtable<Integer, Integer> nodeAncestors){
        ArrayList<Integer> path = new ArrayList<>();
        path.add(goalNode);
        while (true){
            if (path.get(0) == startNode){
                return path;
            }
            for ( Integer n : nodeAncestors.keySet()){
                if (n == goalNode){
                    path.add(0, nodeAncestors.get(n));
                    goalNode = nodeAncestors.get(n);
                    break;
                }
            }

        }
    }
}
