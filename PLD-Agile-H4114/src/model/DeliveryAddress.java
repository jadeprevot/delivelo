package model;

import java.util.*;

/**
 * @author 4IF-4114
 */
public class DeliveryAddress extends PointOfInterest {

    /**
     * Default constructor
     */
    public DeliveryAddress() {
    }

    public DeliveryAddress(Intersection dIntersection, Integer deliveryDuration) {
        super(dIntersection);
        this.duration=deliveryDuration;
    }

    @Override
    public boolean equals(Object obj) {
        if(!super.equals(obj)) {
            return false;
        }
        if(Integer.compare(this.duration,((DeliveryAddress) obj).duration)!=0){
            return false;
        }
        return true;
    }
}