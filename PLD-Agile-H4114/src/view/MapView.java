package view;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.*;
import javax.swing.JPanel;
import model.CityMap;
import model.Road;

import model.CityMap;
import observer.Observable;
import observer.Observer;

import javax.swing.*;

/**
 * @author 4IF-4114
 */
public class MapView extends JPanel implements Observer {

    private int scale;
    private int viewHeight = 50;
    private int viewWidth = 50;
    private CityMap cityMap;

    /**
     * Default constructor
     */
    public MapView(CityMap cityMap, int s, Window w) {
        super();
        cityMap.addObserver(this); // this observes plan
        this.scale = s;
//        viewHeight = cityMap.getHeight()*s;
//        viewWidth = cityMap.getWidth()*s;
        setLayout(null);
        setBackground(Color.white);
        setSize(viewWidth, viewHeight);
        w.getContentPane().add(this);
        this.cityMap = cityMap;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg != null){ // arg is a shape that has been added to plan
            Shape s = (Shape)arg;
//            s.addObserver(this);  // this observes s
        }
        repaint();
    }

    public int getViewHeight() {
        return viewHeight;
    }

    public int getViewWidth() {
        return viewWidth;
    }
    private Graphics g;
    private Graphics2D g2 ;
    private CityMap cityMap;
    private double scaleWidth;
    private double scaleHeight;
    private double originLat;
    private double originLong;
    private final int VIEW_HEIGHT = 800;
    private final int VIEW_WIDTH = 800;
    /**
     * Default constructor
     */
    public MapView(CityMap cityMap, Window window) {
        super();
        this.cityMap = cityMap;
        scaleWidth = VIEW_WIDTH/cityMap.getWidth();
        scaleHeight = VIEW_HEIGHT/cityMap.getHeight();
        originLong = cityMap.getWestPoint();
        originLat = cityMap.getNordPoint();
        setLayout(null);
        setBackground(Color.GRAY);
        setSize(VIEW_WIDTH,VIEW_HEIGHT);
        window.getContentPane().add(this);
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        this.g = g;
        this.g2 = (Graphics2D) g;
        g.setColor(Color.BLACK);
        for (Road r : cityMap.getRoads()){
            displayRoad(r);
        }
    }

    /**
     * @param observed
     * @param object
     * @return
     */
    public void update(Observable observed, Object object) {
        // TODO implement here
    }

    /**
     *  Method called by
     * @param r
     */
    public void displayRoad(Road r){
        int x1 = (int)((r.getOrigin().getLongitude()- originLong) * scaleWidth);
        int y1 = -(int)((r.getOrigin().getLatitude()- originLat) * scaleHeight); /* Le repère de latitude est inversé */
        int x2 = (int)((r.getDestination().getLongitude()- originLong) * scaleWidth);
        int y2 = -(int)((r.getDestination().getLatitude()- originLat) * scaleHeight);
        g.setColor(Color.RED);

        g2.setStroke(new BasicStroke(1));
        g2.draw(new Line2D.Float(x1, y1, x2, y2));
//        g.drawLine(x1, y1, x2, y2);

    }

}