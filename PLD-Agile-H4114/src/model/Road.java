package model;

import java.util.*;

/**
 * @author 4IF-4114
 */
public class Road {

    private Double length;
    private String name;
    private Intersection origin;
    private Intersection destination;

    /**
     * Default constructor
     */
    public Road() {


    }

    public Road(String myName, double myLength) {
        this.length = myLength;
        this.name = myName;
    }


    public void addRoads(Intersection myOrigin,Intersection myDestination){
        this.origin=myOrigin;
        this.destination=myDestination;
    }

    /**
     * 
     */


}