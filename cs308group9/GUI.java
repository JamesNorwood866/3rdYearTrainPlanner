import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class GUI {
    //handle user inputs
    JFrame frame;
    JPanel menuPanel;
    JPanel mapPanel;
    JPanel routePanel;
    JPanel favouritePanel;
    JPanel drawRoutePanel;
    ArrayList<JButton> menuButtons;
    JButton mapButton;
    JButton routePlannerButton;
    JButton favourtiesButton;
    TrainNetwork trainNetwork;
    //setup our dropdown menus for our route panel
    JComboBox fromDropDown;
    JComboBox toDropDown;


    //constructor
    GUI(){
        trainNetwork = new TrainNetwork();
        frame = new JFrame();
        //setup Panels
        menuPanel = new JPanel(new GridLayout(0, 1));
        mapPanel = new JPanel();
        routePanel = new JPanel( new FlowLayout(FlowLayout.CENTER, 0, 0) );
        drawRoutePanel = new JPanel();
        drawRoutePanel.setPreferredSize(new Dimension(500, 600));
        favouritePanel = new JPanel();
        //initialise stations etc
        trainNetwork.makeStationsAndLinks();
        trainNetwork.makeLines();

        ArrayList<TrainNetwork.Station> stations = trainNetwork.getStations();
        String[] stationNames = new String[stations.size()];
        for(int i = 0; i < stations.size(); i++) {
            stationNames[i] = stations.get(i).getNodeName();
        }
        fromDropDown = new JComboBox(stationNames);
        fromDropDown.setFont(new Font("", Font.PLAIN, 20));
        toDropDown = new JComboBox(stationNames);
        toDropDown.setFont(new Font("", Font.PLAIN, 20));

        //setup routePanel
        setupRoutePanel();
        //setup favourite panel
        setupFavouritePanel();
        //initialise buttons - colors can be changed at a later date
        menuButtons = new ArrayList<>();
        setupButtons();


        //setup map Panel
        menuPanel.setBackground(Color.LIGHT_GRAY);
        BufferedImage myPic = getMapImage("/images/subway-map.jpeg");
        JLabel picLabel = new JLabel(new ImageIcon(myPic));
        mapPanel.add(picLabel);
        mapPanel.setSize(myPic.getWidth(), myPic.getHeight());

        //add panels to our frame
        frame.add(menuPanel, BorderLayout.LINE_START);
        frame.add(mapPanel, BorderLayout.CENTER);
        //need to change how we are setting size
        menuPanel.setPreferredSize(new Dimension(myPic.getWidth() / 3, myPic.getHeight()));
        frame.setSize(myPic.getWidth() + 400, myPic.getHeight());
        frame.setVisible(true);
    }

    private void setupFavouritePanel(){
        favouritePanel.setLayout(new BoxLayout(favouritePanel, BoxLayout.Y_AXIS));
        JLabel header = new JLabel("Favourites");
        header.setFont(new Font("", Font.BOLD, 30));
        favouritePanel.add(header);

        ArrayList<String []> favourites = getFavourites();
        for(String [] s: favourites){
            TrainNetwork.Station from = trainNetwork.getStationFromID(Integer.parseInt(s[0]));
            TrainNetwork.Station to = trainNetwork.getStationFromID(Integer.parseInt(s[1]));
            JButton b = new JButton("From: " + from.getNodeName() + " To: " + to.getNodeName());
            b.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setContentPanel("Route Planner");
                    fromDropDown.setSelectedIndex(Integer.parseInt(s[0]));
                    toDropDown.setSelectedIndex(Integer.parseInt(s[1]));

                }
            });
            favouritePanel.add(b);
            System.out.println(Arrays.toString(s));
        }
    }

    public ArrayList<String []> getFavourites(){
        ArrayList<String []> favourites = new ArrayList<>();
        try {
          File myObj = new File("favourites.txt");
          Scanner myReader = new Scanner(myObj);
          while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            String [] ids = data.split(",");
            favourites.add(ids);
          }
          myReader.close();
        } catch (FileNotFoundException e) {
          System.out.println("An error occurred.");
          e.printStackTrace();
        }
        return favourites;
    }

    private void setupRoutePanel(){
        routePanel.setLayout(new GridBagLayout());
        JLabel fromLabel = new JLabel("From");
        fromLabel.setFont(new Font("", Font.PLAIN, 20));
        JLabel toLabel = new JLabel("To");
        toLabel.setFont(new Font("", Font.PLAIN, 20));
        JLabel pathLabel = new JLabel();
        pathLabel.setSize(600, 100);
        Button planButton = new Button("Plan");
        planButton.setFont(new Font("", Font.PLAIN, 20));

        planButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                planButton.setForeground(Color.RED);
                ArrayList<Integer> path = trainNetwork.findPath(fromDropDown.getSelectedIndex(), toDropDown.getSelectedIndex());
                ArrayList<TrainNetwork.Station> stations = new ArrayList<>();

                //add initial station
                TrainNetwork.Station initialStation = trainNetwork.getStationFromID(path.get(0));
                stations.add(initialStation);
                for(int i = 0; i < path.size(); i++){
                    TrainNetwork.Station currentStation = trainNetwork.getStationFromID(path.get(i));
                    if(currentStation.getLine().size() == 1){
                        continue;
                    }
                    if(!stations.contains(currentStation)){
                        stations.add(currentStation);
                    }
                }
                //add destination in if not already
                TrainNetwork.Station destinationStation = trainNetwork.getStationFromID(path.get(path.size()-1));
                if(!stations.contains(destinationStation)){
                    stations.add(destinationStation);
                }
                //show route to user
                drawRoute(stations);
            }
        });
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 0;
        c.weighty = 1;
        c.gridwidth = 1;
        c.gridheight = 5;

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        routePanel.add(fromLabel,c);
        c.gridx = 1;
        routePanel.add(fromDropDown,c);
        c.gridx = 2;
        routePanel.add(toLabel,c);
        c.gridx = 3;
        routePanel.add(toDropDown,c);
        c.gridx = 4;
        routePanel.add(planButton,c);
        c.gridwidth = 5;
        c.gridheight = 3;
        c.gridx = 0;
        c.gridy = 3;
        routePanel.add(pathLabel,c);

        c.gridy = 4;
        routePanel.add(drawRoutePanel, c);
    }


    public void setupButtons(){
        mapButton = new JButton("Map");
        mapButton.setFont(new Font("", Font.BOLD, 20));
        routePlannerButton = new JButton("Route Planner");
        routePlannerButton.setFont(new Font("", Font.BOLD, 20));
        favourtiesButton = new JButton("Favourites");
        favourtiesButton.setFont(new Font("", Font.BOLD, 20));
        menuButtons.add(mapButton);
        menuButtons.add(routePlannerButton);
        menuButtons.add(favourtiesButton);

        //setup buttons
        for(JButton button: menuButtons){
            button.setOpaque(false);
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    button.setForeground(Color.RED);
                    //make sure we're resetting colors of other buttons
                    setContentPanel(button.getText());
                    for(JButton b: menuButtons){
                        if(b.getText() != button.getText()){
                            b.setForeground(Color.BLACK);
                        }
                    }
                }
            });
            GridBagConstraints c = new GridBagConstraints();
            menuPanel.add(button, c);

        }
    }

    public void setContentPanel(String panel){
        if(panel.equals("Route Planner")){
            frame.remove(mapPanel);
            frame.remove(favouritePanel);
            frame.add(routePanel);
        }
        else if(panel.equals("Map")){
            frame.remove(routePanel);
            frame.remove(favouritePanel);
            frame.add(mapPanel);
        }
        else if(panel.equals("Favourites")){
            frame.remove(routePanel);
            frame.remove(mapPanel);
            frame.add(favouritePanel);
        }
        frame.revalidate();
        frame.repaint();
    }

    public BufferedImage getMapImage(String pathname){
        try {
            //For some reason when I merged, this bit broke, and I had to delete the image and re add it ¯\_(ツ)_/¯
            System.out.println(pathname);
            System.out.println(getClass().getResource(pathname));
            return ImageIO.read(getClass().getResource(pathname));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setupRouteLabels(){
        JPanel labelPanel = new JPanel(new GridLayout());
        JLabel lineLabel = new JLabel("Line");
        lineLabel.setFont(new Font("", Font.PLAIN, 30));
        GridBagConstraints x = new GridBagConstraints();
        x.weightx = 1;
        x.weighty = 1;
        x.gridx = 0;
        x.gridy = 0;
        labelPanel.add(lineLabel, x);
        x.gridx = 1;
        JLabel station = new JLabel("Station");
        station.setFont(new Font("", Font.PLAIN, 30));
        labelPanel.add(station, x);

        drawRoutePanel.add(labelPanel);
    }

    public void drawRoute(ArrayList<TrainNetwork.Station> stations){
        drawRoutePanel.removeAll();

//        drawRoutePanel.setLayout(new FlowLayout());
        System.out.println(stations.size()+1);
        drawRoutePanel.setLayout(new GridLayout(stations.size()+2, 2));
        //setup labels for user
        setupRouteLabels();

        JPanel [] panels = new JPanel[stations.size()];
        for(int i = 0; i < stations.size(); i++){
            panels[i] = new JPanel(new GridBagLayout());
            panels[i].setSize(new Dimension(10, 50));


            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.ipadx = 20;
            gbc.weightx = 1;
            gbc.weighty = 1;
            gbc.gridx = 0;
            gbc.gridy = 0;

            //show line color to user
            Circle c1 = new Circle(0, 0, stations.get(i).getLine().get(0));
            for(int j = 0; j < stations.get(i).getLine().size(); j++){
                if(stations.get(stations.size()-1).getLine().contains(stations.get(i).getLine().get(j))){

                    c1 = new Circle(0, 0, stations.get(i).getLine().get(j));
                }
            }
            panels[i].add(c1, gbc);
            if(i < stations.size()-1){
                JLabel arrow = new JLabel(" ↓");
                arrow.setFont(new Font("", Font.BOLD, 40));
                gbc.gridy = 1;
                panels[i].add(arrow, gbc);
            }

            gbc.gridx = 1;
            gbc.gridy = 0;
            JLabel stationLabel = new JLabel();
            stationLabel.setFont(new Font("",1,20));
            stationLabel.setText(stations.get(i).getNodeName());
            panels[i].add(stationLabel, gbc);

            drawRoutePanel.add(panels[i]);

        }
        JButton addToFavourites = new JButton("Add to favourites");
        addToFavourites.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                writeToFavourites(stations);
            }
        });
        drawRoutePanel.add(addToFavourites);
        frame.revalidate();
        frame.repaint();
    }

    private void writeToFavourites(ArrayList<TrainNetwork.Station> stations){

        try {
              FileWriter myWriter = new FileWriter("src/favourites.txt", true);
//              myWriter.write("Files in Java might be tricky, but it is fun enough!");
//                myWriter.append(stations.toString()).append("\n");
                int initID = stations.get(0).getNodeID();
                int endID = stations.get(stations.size()-1).getNodeID();
                myWriter.append(Integer.toString(initID)).append(",").append(Integer.toString(endID)).append("\n");
              myWriter.close();
              System.out.println("Successfully wrote to the file.");
            } catch (IOException e) {
              System.out.println("An error occurred.");
              e.printStackTrace();
            }
        }

}

class Circle extends JPanel{
    int xCoord;
    int yCoord;
    String fillColor;
    Circle(int xCoord, int yCoord, String fillColor){
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.fillColor = fillColor;
    }
    public void paint(Graphics g){
        setSize(100, 600);

        if(fillColor.contains("Red"))
                g.setColor(Color.RED);
        if(fillColor.contains("Orange"))
                g.setColor(Color.ORANGE);
        if(fillColor.contains("Green"))
            g.setColor(Color.GREEN);
        if(fillColor.contains("Blue"))
            g.setColor(Color.BLUE);

        g.fillOval(xCoord, yCoord, 50, 50);

    }

}