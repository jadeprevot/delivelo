package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.SwingUtilities;

import controller.Controller;
import model.Intersection;
import model.PointOfInterest;

/**
 * @author 4IF-4114
 */
public class MouseListener extends MouseAdapter {

    private Controller controller;
    private MapView mapView;
    private RoadmapView roadMapView;
    private Window window;

    /**
     * Default constructor
     */
    public MouseListener(Controller c, MapView mapView, Window w){
        this.controller = c;
        this.mapView = mapView;
        this.window = w;
    }

    @Override
    public void mouseClicked(MouseEvent evt){
        MouseEvent e = SwingUtilities.convertMouseEvent(window, evt, mapView);
        switch (e.getButton()) {
            case MouseEvent.BUTTON1:
                Intersection intersection = mapView.getClosestIntersection(e.getX(), e.getY());
                PointOfInterest pointOfInterest = mapView.getClosestPointOfInterest(e.getX(),e.getY());
                System.out.println(intersection);
                System.out.println(pointOfInterest);

                controller.leftClick(intersection,pointOfInterest);
                break;
            case MouseEvent.BUTTON3:
                controller.rightClick();
                break;
            default:
        }
    }

    @Override
    public void mousePressed(MouseEvent evt) {
        MouseEvent e = SwingUtilities.convertMouseEvent(window, evt, mapView);
        if (e.getX() >= 0){
            mapView.setMouseClickedX(e.getX());
            mapView.setMouseClickedY(e.getY());
            mapView.fixOrigin();
        }
    }

    @Override
    public void mouseDragged(MouseEvent evt){
        MouseEvent e = SwingUtilities.convertMouseEvent(window, evt, mapView);
        if (e.getX() >= 0){
            mapView.dragMap(e.getX(),e.getY());
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e){
        int scrollDirection = e.getWheelRotation();
        MouseEvent ev = SwingUtilities.convertMouseEvent(window, e, mapView);
        int mouseX = ev.getX();
        int mouseY = ev.getY();
        if (scrollDirection > 0){
            mapView.modifyZoom(1/1.2,mouseX, mouseY);
        } else if (scrollDirection < 0){
            mapView.modifyZoom(1.2, mouseX, mouseY);
        }

    }

    @Override
    public void mouseMoved(MouseEvent evt) {
        MouseEvent e = SwingUtilities.convertMouseEvent(window, evt, mapView);
        Intersection intersection = mapView.getClosestIntersection(e.getX(), e.getY());
        controller.mouseMoved(intersection);
    }
}