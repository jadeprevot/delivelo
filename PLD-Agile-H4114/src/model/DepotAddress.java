package model;

import java.time.LocalTime;

/**
 * @author 4IF-4114
 */
public class DepotAddress extends PointOfInterest {


    private LocalTime  departureTime;

    /**
     * Default constructor
     */
    public DepotAddress() {
        super();
        departureTime = LocalTime.parse("00:00:00");
    }


    public DepotAddress(Intersection i, String departureTime) {
        super(i,0);
        String[] fractureddepartureTime= departureTime.split(":");
        for(int j=0;j<3;j++){
            if (Integer.parseInt(fractureddepartureTime[j])<10){
                fractureddepartureTime[j]="0"+fractureddepartureTime[j];
            }
        }
        this.departureTime = LocalTime.parse(fractureddepartureTime[0]+":"+fractureddepartureTime[1]+":"+fractureddepartureTime[2]);
        this.duration=0;
    }

    @Override
    public boolean equals(Object obj) {
        if(!super.equals(obj)) {
            return false;
        }
        if(this.departureTime!=((DepotAddress) obj).departureTime){
            return false;
        }
        return true;
    }
}