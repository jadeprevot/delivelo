package view;

import model.*;

import observer.Observable;
import observer.Observer;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import  java.util.*;

/**
 * @author 4IF-4114
 */
public class RoadmapView extends JPanel implements Observer {

    private Tour tour;
    private final int VIEW_HEIGHT = 770;
    private final int VIEW_WIDTH = 400;
    private final JButton addButton = new JButton("Add");
    private final JButton delButton = new JButton("Remove");
    private final int buttonHeight = 30;
    private final int buttonWidth = 100;
    private JPanel roadmap;

    /**
     * Default constructor
     * @param tour
     * @param window
     */
    public RoadmapView(Tour tour, Window window) {
        super();

        this.tour = tour;
        this.tour.addObserver(this); // this observes tour

        this.setBorder(BorderFactory.createTitledBorder("Roadmap"));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(Color.GRAY);
        this.setSize(VIEW_WIDTH, VIEW_HEIGHT);

        this.roadmap = new JPanel();
        this.roadmap.setLayout(new BoxLayout(this.roadmap, BoxLayout.Y_AXIS));
        this.roadmap.setBackground(Color.LIGHT_GRAY);
        JScrollPane scrollPanel = new JScrollPane(this.roadmap,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(scrollPanel);
        scrollPanel.setBackground(Color.BLACK);
        scrollPanel.setSize(0, VIEW_HEIGHT - this.buttonHeight);

        JPanel buttonPanel = new JPanel();
        this.add(buttonPanel);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBackground(Color.BLUE);
        buttonPanel.add(this.addButton);
        buttonPanel.add(this.delButton);

        this.add(scrollPanel);
        this.add(buttonPanel);

        window.getContentPane().add(this);
    }

    /**
     * @param observed 
     * @param object 
     * @return
     */
    public void update(Observable observed, Object object) {
        System.out.println("Roadmap/update");
        System.out.println(this.tour.getPointOfInterests().size());
        System.out.println(this.tour.getPaths().size());

        int i = 0;

        this.roadmap.removeAll();

        for (PointOfInterest poi : this.tour.getPointOfInterests()) {
            System.out.println(poi.toString());

            JPanel subPanel = new JPanel();
            subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.Y_AXIS));
            subPanel.setBackground(Color.YELLOW);
            if (poi.getIdPointOfInterest() == 0) { // Depot
                subPanel.setBorder(BorderFactory.createTitledBorder("START"));
            } else {
                subPanel.setBorder(BorderFactory.createTitledBorder("Point of Interest #" + poi.getIdPointOfInterest()));
            }

            subPanel.add(new JLabel("    Latitude: " + poi.getIntersection().getLatitude()));
            subPanel.add(new JLabel("    Longitude: " + poi.getIntersection().getLongitude()));
            subPanel.add(new JLabel("    Duration: " + poi.getDuration() + " .s"));

            this.roadmap.add(subPanel);

            Path path = this.tour.getPaths().get(i);
            int duration = 0;
            double length = 0;
            String name;
            int nbIntersection = 0;

//            for (Road road : path.getRoads()) {
            for (int j = 0; j < path.getRoads().size(); ++j) {
                duration += (int) (path.getRoads().get(j).getLength() / 15000. * 3600.);
                length += path.getRoads().get(j).getLength();
                name = path.getRoads().get(j).getName();
                nbIntersection += 1;

                if (j+1 < path.getRoads().size() && name.equals(path.getRoads().get(j+1).getName())) {
                    continue;
                }

                JPanel subPanel2 = new JPanel();
                subPanel2.setLayout(new BoxLayout(subPanel2, BoxLayout.Y_AXIS));
                subPanel2.setBackground(Color.PINK);

                subPanel2.add(new JLabel(" via " + name));
                int minutes = (duration / 60);
                int seconds = (duration % 60);
                if (minutes > 0) {
                    subPanel2.add(new JLabel(" for " + minutes + "min" + seconds + "s (" + String.format("%,.0f", length)+ " m) " + nbIntersection + " intersections"));
                }
                else {
                    subPanel2.add(new JLabel(" for " + seconds + "s (" + String.format("%,.0f", length)+ " m) " + nbIntersection + " intersections"));
                }

                this.roadmap.add(subPanel2);

                duration = 0;
                length = 0;
                nbIntersection = 0;
            }

            i += 1;
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

}