package controller;

import model.CityMap;
import model.Intersection;
import model.PointOfInterest;
import view.Window;
import xml.XMLException;

/**
 * @author 4IF-4114
 */
public class Controller {
    // Instances associated with each possible state of the controller
    protected final InitialState initialState = new InitialState();
    protected final CityMapState citymapState = new CityMapState();
    protected final DistributionState distributionState = new DistributionState();
    protected final TourState tourState = new TourState();
    protected final AddState1 addState1 = new AddState1();
    protected final AddState2 addState2 = new AddState2();
    protected final AddState3 addState3 = new AddState3();
    protected final AddState4 addState4 = new AddState4();
    protected final HighlightState highlightState = new HighlightState();


    private CityMap cityMap;
    private Window window;
    private ListOfCommands listOfCommands;
    private State currentState;

    /**
     * Default constructor
     */

    public Controller(CityMap city) {
        this.cityMap = city;
        this.listOfCommands = new ListOfCommands();
        this.currentState = initialState;
        this.window = new Window(cityMap, this);
    }

    protected void setCurrentState(State state) {
        this.currentState = state;
    }

    /**
     * Method called by window after a click on the button "Undo"
     */
    public void undo() {
        currentState.undo(listOfCommands);
    }

    /**
     * Method called by window after a click on the button "Redo"
     */
    public void redo() {
        currentState.redo(listOfCommands);
    }


    public void loadCityMap() {
        try {
            this.currentState.loadMap(this, window);
        } catch (XMLException e) {
            cityMap.reset();
            this.currentState = this.initialState;
            window.parsingError(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void loadDistribution() {
        try {
            this.currentState.loadDistribution(this, window);
        } catch (XMLException e) {
            cityMap.getDistribution().reset();
            cityMap.getTour().resetTour();
            this.currentState = this.citymapState;
            window.parsingError(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void keystroke(int keyCode) {
        currentState.keyStroke(window.getMapView(), keyCode);
    }

    public CityMap getCitymap() {
        return cityMap;
    }

    public void computeTour() {
        try {
            this.currentState.computeTour(this, window);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void modifyDistribution() {
        try {
            this.currentState.modifyDistribution(this);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void removePointOfInterest() {
        try {
            this.currentState.removePointOfInterest(this, this.window, this.cityMap, this.listOfCommands);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void zoomIn() {
        try {
            window.getMapView().modifyZoom(1.5, window.getMapView().getViewWidth() / 2,
                    window.getMapView().getViewHeight() / 2);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void zoomOut() {
        try {
            window.getMapView().modifyZoom(1 / 1.5, window.getMapView().getViewWidth() / 2,
                    window.getMapView().getViewHeight() / 2);
        } catch (Exception e) {
            System.out.println(e.getMessage());

        }
    }

    public void recenter() {
        try {
            window.getMapView().modifyZoom(1, window.getMapView().getViewWidth() / 2,
                    window.getMapView().getViewHeight() / 2);
        } catch (Exception e) {
            System.out.println(e.getMessage());

        }
    }

    /**
     * Method called by window after a left click on a point of the graphical view
     * Precondition : p != null
     *
     * @param p = coordinates of the click in the citymap
     */
    public void leftClick(Intersection intersection, PointOfInterest pointOfInterest) {
        System.out.println(this.currentState);
        currentState.leftClick(this, window, cityMap, listOfCommands, intersection, pointOfInterest);
    }

    public Window getWindow() {
        return window;
    }
}