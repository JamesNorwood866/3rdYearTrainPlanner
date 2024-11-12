import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

import static org.junit.Assert.*;
public class test {

    private TrainNetwork trainNetwork;

    @Before
    public void init() {
        trainNetwork = new TrainNetwork();
        trainNetwork.makeStationsAndLinks();
        trainNetwork.makeLines();
    }

    @Test
    public void findPathTest() {
        ArrayList<Integer>  expected = new ArrayList<>();
        expected.add(0);
        expected.add(3);

        Assert.assertEquals(expected, trainNetwork.findPath(0, 3));
    }

    @Test
    public void getHeuristicTest() {
        Integer expected = 2;

        Assert.assertEquals(expected, trainNetwork.getHeuristic(33, 30));
    }

    @Test
    public void extendPathTest() {
        ArrayList<Integer>  expected = new ArrayList<>();
        expected.add(7);
        expected.add(14);
        Assert.assertEquals(expected, trainNetwork.extendPath(10));
    }

    @Test
    public void nextStatesTest() {
        ArrayList<Integer>  expected = new ArrayList<>();
        expected.add(6);
        expected.add(11);

        Assert.assertEquals(expected, trainNetwork.nextStates(9));
    }

    @Test
    public void checkGraphExistsTest() {
        Assert.assertEquals(true, trainNetwork.checkGraphExists());
    }

    @Test
    public void checkLineExistsTest() {
        ArrayList<TrainNetwork.Station> stations = trainNetwork.getStations();

        TrainNetwork.Station station1 =  stations.get(34);
        TrainNetwork.Station station2 =  stations.get(37);

        Assert.assertEquals(true, trainNetwork.checkLineExists(station1,station2));
    }

    @Test
    public void getWeightTest() {
        ArrayList<TrainNetwork.Station> stations = trainNetwork.getStations();

        TrainNetwork.Station station1 =  stations.get(20);
        TrainNetwork.Station station2 =  stations.get(22);

        Assert.assertEquals(2, trainNetwork.getWeight(station1,station2));
    }

    @Test
    public void getLineColorTest() {
        ArrayList<TrainNetwork.Station> stations = trainNetwork.getStations();

        TrainNetwork.Station station1 =  stations.get(7);
        TrainNetwork.Station station2 =  stations.get(8);

        Assert.assertEquals("Red", trainNetwork.getLineColor(station1,station2));
    }

    @Test
    public void getStationFromIDTest() {
        ArrayList<TrainNetwork.Station> stations = trainNetwork.getStations();

        TrainNetwork.Station station =  stations.get(10);

        Assert.assertEquals(station, trainNetwork.getStationFromID(10));
    }

    @Test
    public void getStationsTest() {
        ArrayList<TrainNetwork.Line> lines = new ArrayList<>();
        ArrayList<TrainNetwork.Station> stations = trainNetwork.getStations();

        Assert.assertEquals(stations, trainNetwork.getStations());
    }

    @Test
    public void gettersStationTest() {
        ArrayList<TrainNetwork.Station> stations = trainNetwork.getStations();

        //getLine
        Assert.assertEquals(stations.get(1).getLine(), Arrays.asList("Orange"));

        //getNodeID
        Assert.assertEquals(stations.get(1).getNodeID(), 1);

        //getNodeName
        Assert.assertEquals(stations.get(1).getNodeName(), "OakGrove");

        //printDetails
        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));

        stations.get(1).printDetails();

        Assert.assertEquals(outputStreamCaptor.toString().trim(), "Name: OakGrove. ID: 1. Lines: [Orange]");
    }

    @Test
    public void gettersLineTest() {
        ArrayList<TrainNetwork.Line> lines = trainNetwork.getLines();
        ArrayList<TrainNetwork.Station> stations = trainNetwork.getStations();

        //Station1: 1. Station2: 2. Weight: 3. Colour: Orange  <----- lines(0)
        //getColor
        Assert.assertEquals(lines.get(0).getColor(), "Orange");

        //getWeight
        Assert.assertEquals(lines.get(0).getWeight(), 3);

        //getNode1
        Assert.assertEquals(lines.get(0).getNode1(), stations.get(1));

        //getNode2
        Assert.assertEquals(lines.get(0).getNode2(), stations.get(2));

        //printDetails
        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));

        lines.get(0).printDetails();

        Assert.assertEquals(outputStreamCaptor.toString().trim(), "Station1: 1. Station2: 2. Weight: 3. Colour: Orange");
    }

    @Test
    public void getPathTest(){
        Hashtable<Integer, Integer> nodeAncestors = new Hashtable<>();
        nodeAncestors.put(1, 0);
        nodeAncestors.put(2, 1);
        nodeAncestors.put(3, 2);
        nodeAncestors.put(4, 3);

        ArrayList<Integer> expected = new ArrayList<>();
        Collections.addAll(expected, 0,1,2,3);

        Assert.assertEquals(trainNetwork.getPath(0 , 3, nodeAncestors), expected);

        ArrayList<Integer> expected2 = new ArrayList<>();
        Collections.addAll(expected2, 2,3,4);

        Assert.assertEquals(trainNetwork.getPath(2 , 4, nodeAncestors), expected2);
    }
}
