package controller;

import model.CityMap;
import org.xml.sax.SAXException;
import view.Window;
import xml.XMLException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * @author 4IF-4114
 */
public class Controller {
    // Instances associated with each possible state of the controller
    protected final InitialState initialState = new InitialState();
    protected final CityMapState citymapState = new CityMapState();
    protected final DistributionState distributionState = new DistributionState();
    protected final TourState tourState = new TourState();

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
        this.window = new Window(cityMap,this);
    }

    protected void setCurrentState(State state){
        this.currentState = state;
    }

    /**
     * @return
     */
    public void undo() {
        // TODO implement here
    }

    /**
     * @return
     */
    public void redo() {
        // TODO implement here
    }

    /**
     * @param command 
     * @return
     */
    public void add(Command command) {
        // TODO implement here
    }

    public void loadCityMap()  {
        try{
            this.currentState.loadMap(this, window);
        }catch(XMLException e){
            cityMap.reset();
            this.currentState=this.initialState;
            window.parsingError(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void loadDistribution(){
        try{
            this.currentState.loadDistribution(this,window);
        }catch(XMLException e){
            cityMap.getDistribution().reset();
            cityMap.getTour().resetTour();
            this.currentState=this.citymapState;
            window.parsingError(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void keystroke(int keyCode) {

    }

    public CityMap getCitymap() {
        return cityMap;
    }

    public void computeTour() {
        try{
            this.currentState.computeTour(this, window);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}