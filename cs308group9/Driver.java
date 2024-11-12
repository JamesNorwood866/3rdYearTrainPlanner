import org.junit.Assert;

import java.util.ArrayList;
import java.util.Hashtable;

public class Driver {
    public static void main(String[] args) {

        TrainNetwork trainNetwork = new TrainNetwork();

        GUI gui = new GUI();


        trainNetwork.makeStationsAndLinks();
        trainNetwork.makeLines();
        trainNetwork.printTrainNetwork();

        //System.out.println(trainNetwork.findPath(1, 2));

        /*
        ArrayList<TrainNetwork.Station> stations = trainNetwork.getStations();
         ArrayList<TrainNetwork.Line> lines = trainNetwork.getLines();

        TrainNetwork.Station station1 =  stations.get(117);
       TrainNetwork.Station station2 =  stations.get(5);


        System.out.println( station1.getNodeID());
        System.out.println( station2.getNodeID());
        System.out.println(trainNetwork.checkLineExists(station1,station2));
*/


    }

}
