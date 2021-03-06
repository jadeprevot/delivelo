/**
 * AddState1
 *
 * @author 4IF-4114
 */
package controller;

import model.CityMap;
import model.Intersection;
import model.PointOfInterest;
import view.Window;
/**
 * AddState1, a state used when the user want to add a pickup point
 */

public class AddState1 implements State {
    private Integer d1;

    @Override
    public void leftClick(Controller controller, Window window, CityMap map, ListOfCommands listOfCommands, Intersection i, PointOfInterest poi) {

        if (i != null) {
            String strDuration = window.getDuration();

            try {
                this.d1 = Integer.parseInt(strDuration);
                if (d1 < 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                window.parsingError("Incorrect value\nPlease enter a positive number\nand place the point.");
                return;
            }
            controller.ADD_STATE_2.entryAction(i, this.d1);
            controller.setCurrentState(controller.ADD_STATE_2);
            map.setSelected1(i);

            map.setPOIToAdd(null);
            window.displayMessage("After which point ?\nSelect a point of interest on the map.");
            window.enableJtextField(false);

        } else {
            window.parsingError("Misplaced point error: Click on a valid intersection.");


        }
    }


    @Override
    public void rightClick(Controller c) {
        c.getCityMap().resetSelected();
        c.getCityMap().setPOIToAdd(null);
        c.getWindow().displayMessage("");
        c.getWindow().enableJtextField(false);
        c.setCurrentState(c.TOUR_STATE);
    }

    protected void entryAction(Window w) {
        this.d1 = 300;

        w.displayMessage("Choose the pickup duration in sec and\nchoose the pickup point position on the map.");
        w.enableJtextField(true);

        w.resetDurationInserted();
    }


    public void enableButtons(Window window, ListOfCommands listOfCommands) {
        window.enableButton("Load a city map", true);
        window.enableButton("Load a distribution", true);
        window.enableButton("Compute a tour", false);
        window.enableButton("Add request", false);
        window.enableButton("Remove", false);
        window.enableButton("Redo", false);
        window.enableButton("Undo", false);
        window.enableButton("Generate roadmap", false);

    }

    public void mouseMoved(Controller controller, Intersection intersection) {
        controller.getCityMap().setPOIToAdd(intersection);
    }
}
