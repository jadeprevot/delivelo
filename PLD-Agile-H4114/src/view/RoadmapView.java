package view;

import model.*;

import observer.Observable;
import observer.Observer;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import  java.util.*;
import java.util.List;

/**
 * @author 4IF-4114
 */
public class RoadmapView extends JPanel implements Observer {

    private Tour tour;
    private Distribution distribution;
    private final int VIEW_HEIGHT = 700;
    private final int VIEW_WIDTH = 300;
    private JButton addButton;
    private JButton delButton;
    private final int BUTTON_HEIGHT = 30;
    private final int BUTTON_WIDTH = 100;
    private JPanel roadmap;
    private boolean start = true;
    private int arrivalTime;
    private GridBagConstraints gc;

    /**
     * Default constructor
     * @param tour
     * @param window
     */
    public RoadmapView(CityMap citymap, Window window) {
        super();

        this.tour = citymap.getTour();
        this.tour.addObserver(this); // this observes tour
        this.distribution = citymap.getDistribution();
        this.distribution.addObserver(this); // this observes distribution

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(Color.WHITE);
        this.setSize(VIEW_WIDTH, VIEW_HEIGHT);

        GridBagLayout grid = new GridBagLayout();
        gc = new GridBagConstraints();
        gc.anchor = GridBagConstraints.NORTH;

        this.roadmap = new JPanel(grid);
        this.roadmap.setBackground(Color.WHITE);
        JScrollPane scrollPanel = new JScrollPane(this.roadmap,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPanel.getVerticalScrollBar().setUnitIncrement(30);
        this.add(scrollPanel);
        scrollPanel.setBackground(Color.BLACK);
        scrollPanel.setSize(0, VIEW_HEIGHT - this.BUTTON_HEIGHT);

        this.add(scrollPanel);


        window.getContentPane().add(this);
    }


    public void update(Observable observed, Object object) {
        System.out.println("Roadmap/update");
        System.out.println(this.tour.getPointOfInterests().size());
        System.out.println(this.tour.getPaths().size());
        System.out.println(this.distribution.getRequests().size());

        this.start = true;
        int i = 0;
        this.arrivalTime = this.distribution.getDepot().getDepartureTime().toSecondOfDay();

        this.roadmap.removeAll();

        if (this.tour.getPointOfInterests().size() == 0) { // Load distribution

            Set<Request> requestList = this.distribution.getRequests();
            if(requestList.size() != 0 ) {
                addRequestToRoadmap(requestList);
            }

        }
        else { //Compute Tour

            List<PointOfInterest> pointList = this.tour.getPointOfInterests();
            addPointOfInterestToRoadMap(pointList);

        }

        this.revalidate();
        this.repaint();
    }

    public int getViewHeight() {
        return VIEW_HEIGHT;
    }

    public int getViewWidth() {
        return VIEW_WIDTH;
    }

    public void addRequestToRoadmap(Set<Request> requestList){ //Add Request to roadmap in order
        int number = 0;

        JPanel firstPanel = new JPanel();
        firstPanel.setLayout(null);
        firstPanel.setPreferredSize(new Dimension(250,80));
        firstPanel.setBackground(new Color(196,215,254));

        int hours = arrivalTime / 3600;
        int minutes = (arrivalTime % 3600) / 60;
        int seconds = arrivalTime % 60;

        JLabel title = new JLabel("Starting Point");
        title.setBounds(25,15,150,20);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JLabel depTime = new JLabel("    Departure Time: " + String.format("%02d:%02d:%02d", hours, minutes, seconds));
        depTime.setBounds(25,35,200,20);
        depTime.setFont(new Font("Segoe UI", Font.BOLD, 13));

        firstPanel.add(title);
        firstPanel.add(depTime);

        gc.gridx = 0;
        gc.weighty = 0;
        int posCard = 0;
        gc.gridy = posCard++;
        gc.insets = new Insets(5,0,5,0);

        roadmap.add(firstPanel,gc);

        for (Request request : requestList) {
            number++;
            System.out.println(request);

            JPanel subPanel1 = new JPanel();
            subPanel1.setLayout(null);
            subPanel1.setPreferredSize(new Dimension(250,80));
            subPanel1.setBackground(new Color(196,215,254));

            JLabel title1 = new JLabel("Pickup Point "+ number );
            title1.setBounds(25,15,150,20);
            title1.setFont(new Font("Segoe UI", Font.BOLD, 16));

            JLabel title2 = new JLabel("Delivery Point "+ number );
            title2.setBounds(25,45,150,20);
            title2.setFont(new Font("Segoe UI", Font.BOLD, 16));
            subPanel1.add(title2);
            subPanel1.add(title1);

            gc.gridx = 0;
            gc.weighty = 0;
            if (number == requestList.size()){
                gc.weighty = 1;
            }
            gc.gridy = posCard++;
            roadmap.add(subPanel1,gc);

        }
    }

    public void addPointOfInterestToRoadMap(List<PointOfInterest> pointList) {

        List<Path> pathList = this.tour.getPaths();
        gc.gridy = 0;
        gc.gridx = 0;
        gc.insets = new Insets(5,0,5,0);
        for (int poiNum = 0; poiNum < pointList.size(); poiNum++) {
            PointOfInterest poi = pointList.get(poiNum);

            JPanel subPanel = new JPanel();
            subPanel.setLayout(null);
            subPanel.setPreferredSize(new Dimension(260,100));
            subPanel.setBackground(new Color(196,215,254));
            JLabel titleLabel = new JLabel();
            titleLabel.setBounds(20,20,250,20);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            if (poi.getIdPointOfInterest() == 0) { // Depot
                if (this.start) {
                    titleLabel.setText("START");

                } else {
                    titleLabel.setText("END");
                }
            } else {
                if (poi instanceof DeliveryAddress) {
                    titleLabel.setText("Delivery Point " + ((poi.getIdPointOfInterest()-2)/2+1));

                } else if (poi instanceof PickupAddress) {
                    titleLabel.setText("Pickup Point " + ((poi.getIdPointOfInterest()-1)/2+1));
                }
            }
            
            subPanel.add(titleLabel);
            int durationRoad=0;

            if(poiNum<pointList.size() && !start) {
                Path path = (Path) (pathList.get(poiNum-1));
                arrivalTime += (int)(path.getLength() / 15000. * 3600.);

            }

            int hours = arrivalTime / 3600;
            int minutes = (arrivalTime % 3600) / 60;
            int seconds = arrivalTime % 60;

            int departureTime = arrivalTime+poi.getDuration();

            if(this.start){
                JLabel departureTimeLabel = new JLabel("Departure Time: " + String.format("%02d:%02d:%02d", hours, minutes, seconds));
                departureTimeLabel.setBounds(20,40,250,20);
                departureTimeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                subPanel.add(departureTimeLabel);
            }else if(poiNum!=pointList.size()-1) {
                JLabel arrivalTimeLabel = new JLabel("Arrival Time: " + String.format("%02d:%02d:%02d", hours, minutes, seconds));
                arrivalTimeLabel.setBounds(20,40,250,20);
                arrivalTimeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                subPanel.add(arrivalTimeLabel);
              
                JLabel durLabel = new JLabel("Duration: " + String.format("%02dmin%02dsec", (poi.getDuration() % 3600) / 60, poi.getDuration() % 60)));
                durLabel.setBounds(20,60,200,20);
                durLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                subPanel.add(durLabel);
              
                JLabel departureTimeLabel = new JLabel("Departure Time: " + String.format("%02d:%02d:%02d", departureTime / 3600, (departureTime % 3600)/60, departureTime % 60)));
                departureTimeLabel.setBounds(20,80,250,20);
                departureTimeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                subPanel.add(departureTimeLabel);
            }else{
                JLabel arrivalTimeLabel = new JLabel("Arrival Time: " + String.format("%02d:%02d:%02d", hours, minutes, seconds));
                arrivalTimeLabel.setBounds(20,40,250,20);
                arrivalTimeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                subPanel.add(arrivalTimeLabel);

            }

            arrivalTime += poi.getDuration();

            if(this.start){
                this.start = false;
            }

            arrivalTime +=durationRoad;
            roadmap.add(subPanel,gc);
            gc.gridy ++;
        }
    }


}
