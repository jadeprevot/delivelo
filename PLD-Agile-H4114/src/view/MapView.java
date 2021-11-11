/**
 * MapView
 * @author 4IF-4114
 */
package view;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JPanel;

import model.*;

import observer.Observable;
import observer.Observer;

/**
 * The map view of the window, where the imported map is shown
 */
public class MapView extends JPanel implements Observer {
    private Graphics g;
    private Graphics2D g2 ;
    private CityMap cityMap;
    private double scaleWidth;
    private double scaleHeight;
    private double originLat;
    private double originLong;
    private double originLatClicked;
    private double originLongClicked;
    private final int VIEW_HEIGHT = 800;
    private final int VIEW_WIDTH = 800;
    private final int POINT_SIZE = 15;
    private final int HIGHLIGHT_POINT_SIZE = 20;
    private final int SECONDARY_POINT_SIZE = 17;
    private double scaleZoom = 1;
    private double mapWidth;
    private double mapHeight;
    private double smallRoadThickness;
    private double greatRoadThickness;
    private int mouseClickedX;
    private int mouseClickedY;
    private int counterInter;

    /**
     * Constructor of MapView
     * @param cityMap the map and its element(points) to be shown
     * @param window the current application window
     */
    public MapView(CityMap cityMap, Window window) {
        super();
        this.cityMap = cityMap;
        cityMap.addObserver(this); // this observes cityMap
        cityMap.getDistribution().addObserver(this); // this observes distribution
        cityMap.getTour().addObserver(this);
        mapWidth = cityMap.getWidth();
        mapHeight = cityMap.getHeight();
        scaleWidth = 1;
        scaleHeight = 1;
        originLong = 0;
        originLat = 0;
        smallRoadThickness = 1;
        greatRoadThickness = 3;
        counterInter = 0;
        setLayout(null);
        setBackground(new Color(180,180,180));
        setSize(VIEW_WIDTH,VIEW_HEIGHT);
        window.getContentPane().add(this);
    }

    /**
     * Manage the map during a zoom in or zoom out
     * @param zoom the value of the zoom
     * @param centerX the X position of the map's center
     * @param centerY the Y position of the map's center
     */
    public void modifyZoom(double zoom, int centerX, int centerY){
        if (zoom == 1 || (scaleZoom * zoom < 0.95)){
            scaleZoom = 1;
            smallRoadThickness = 1;
            greatRoadThickness = 3;
            mapWidth = cityMap.getWidth();
            mapHeight = cityMap.getHeight();
            originLong = cityMap.getWestPoint();
            originLat = cityMap.getNordPoint();
        }
        else if (!(zoom < 1 && scaleZoom <= 1) && !(zoom > 1 && scaleZoom >= 16)){
            scaleZoom = scaleZoom * zoom;
            smallRoadThickness = smallRoadThickness*zoom;
            greatRoadThickness = greatRoadThickness*zoom;
            mapWidth = mapWidth/zoom;
            mapHeight = mapHeight/zoom;
            if (originLong - mapWidth*((double)centerX/VIEW_WIDTH) + centerX/scaleWidth < cityMap.getWestPoint()){
                originLong = cityMap.getWestPoint();
            }else if(originLong - mapWidth*((double)centerX/VIEW_WIDTH) + centerX/scaleWidth + mapWidth> cityMap.getWestPoint() + cityMap.getWidth()){
                originLong = cityMap.getWestPoint() + cityMap.getWidth() - mapWidth;
            }else{
                originLong = originLong - mapWidth*((double)centerX/VIEW_WIDTH) +
                        centerX/scaleWidth;

            }
            if (originLat + mapHeight*((double)centerY/VIEW_HEIGHT) - centerY/scaleHeight > cityMap.getNordPoint()){
                originLat = cityMap.getNordPoint();
            }else if (originLat + mapHeight*((double)centerY/VIEW_HEIGHT) - centerY/scaleHeight - mapHeight < cityMap.getNordPoint() - cityMap.getHeight()){
                originLat = cityMap.getNordPoint() - cityMap.getHeight() + mapHeight;
            }else{
                originLat = originLat + mapHeight*((double)centerY/VIEW_HEIGHT) - centerY/scaleHeight;
            }

        }
        scaleWidth = VIEW_WIDTH/cityMap.getWidth()*scaleZoom;
        scaleHeight = VIEW_HEIGHT/cityMap.getHeight()*scaleZoom;

        repaint();

    }

    /**
     * Move the map when the mouse is dragged ( when zoom-in)
     * @param mouseX the end position X of the mouse
     * @param mouseY the end position Y of the mouse
     */
    public void dragMap(int mouseX, int mouseY){
        if  ((cityMap.getWestPoint() <= originLongClicked - (mouseX - mouseClickedX)/scaleWidth) &&
                (cityMap.getWestPoint() + cityMap.getWidth()>= originLongClicked - (mouseX - mouseClickedX)/scaleWidth  + mapWidth ))  {
            originLong = originLongClicked - (mouseX - mouseClickedX)/scaleWidth;
        }
        if  ((cityMap.getNordPoint() >= originLatClicked + (mouseY - mouseClickedY)/scaleHeight) &&
                (cityMap.getNordPoint() - cityMap.getHeight()<= originLatClicked + (mouseY - mouseClickedY)/scaleHeight - mapHeight )) {
            originLat = originLatClicked + (mouseY - mouseClickedY) / scaleHeight;
        }
        repaint();
    }

    /**
     * Method called when the users uses directional keys to move in a zoomed map
     * @param keyCode the code of the pressed key
     */
    public void moveMapView(int keyCode){
        int horizontal = 0;
        int vertical = 0;
        if (keyCode == 37){
            horizontal = -1;
        }
        if (keyCode == 38){
            vertical = -1;
        }
        if (keyCode == 39){
            horizontal = 1;
        }
        if (keyCode == 40){
            vertical = 1;
        }
        int speed = 3;
        originLong = originLong + horizontal*speed/scaleWidth;
        if (originLong < cityMap.getWestPoint()){
            originLong = cityMap.getWestPoint();
        } else if( originLong + mapWidth > cityMap.getWestPoint() + cityMap.getWidth()){
            originLong = cityMap.getWestPoint() + cityMap.getWidth() - mapWidth;
        }
        originLat = originLat - vertical*speed/ scaleHeight;
        if (originLat > cityMap.getNordPoint()) { //TODO change "Nord" to "North"
            originLat = cityMap.getNordPoint();
        } else if(originLat - mapHeight < cityMap.getNordPoint() - cityMap.getHeight()){
            originLat = cityMap.getNordPoint() - cityMap.getHeight() + mapHeight;
        }
        repaint();
    }

    /**
     * Updates the map view (refresh the UI)
     * @param o the Observable to check if there's changes about the map/requests and update the related data
     * @param arg the modified object
     */
    @Override
    public void update(Observable o, Object arg) {
        repaint();
    }

    public void resetZoom(){
        mapWidth = cityMap.getWidth();
        mapHeight = cityMap.getHeight();
        scaleZoom = 1;
        smallRoadThickness = 1;
        greatRoadThickness = 3;
        scaleWidth = VIEW_WIDTH/mapWidth*scaleZoom;
        scaleHeight = VIEW_HEIGHT/mapHeight*scaleZoom;
        originLong = cityMap.getWestPoint();
        originLat = cityMap.getNordPoint();
    }

    public int getViewHeight() {
        return VIEW_HEIGHT;
    }

    public int getViewWidth() {
        return VIEW_WIDTH;
    }

    public void setMouseClickedX(int mouseClickedX) {
        this.mouseClickedX = mouseClickedX;
    }

    public void setMouseClickedY(int mouseClickedY) {
        this.mouseClickedY = mouseClickedY;
    }

    public void fixOrigin(){
        this.originLatClicked = originLat;
        this.originLongClicked = originLong;
    }

    /**
     * Draw the map on the view
     * @param g Graphics to paint elements on the map
     */
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        counterInter = 0;
        this.g = g;
        this.g2 = (Graphics2D) g;
        for(Map.Entry<AbstractMap.SimpleEntry<String,String>,Road> road : cityMap.getRoads().entrySet()){
            displayRoad(road.getValue(),Color.white, 1,false);
        }
        Color outline = Color.BLACK;
        Tour t = cityMap.getTour();
        if (t!=null) {
            if (t.getPaths().size() != 0){
                outline = new Color(200,0,0);
            }
            for (Path p : t.getPaths()){
                displayPath(p,outline);
            }

        }
        Distribution d = cityMap.getDistribution();
        displayDepot();

        if (d.getRequests().size()!=0) {

            for (Request q : d.getRequests()){
                outline = q.color.darker().darker();
                displayRequest(q,outline);
            }
            if(cityMap.secondaryHighlight!=null & cityMap.primaryHighlight!=null){
                displayHighlights(cityMap.primaryHighlight,cityMap.secondaryHighlight);

            }


        }

        GradientPaint grad = new GradientPaint(0,0,new Color(0,0,0,50),40,0,new Color(0,0,0,0),false);
        g2.setPaint(grad);
        g2.fillRect(0,0,50,800);
        grad = new GradientPaint( VIEW_WIDTH,0,new Color(0,0,0,50),VIEW_WIDTH-40,0,new Color(0,0,0,0),false);
        g2.setPaint(grad);
        g2.fillRect(760,0,50,800);
        grad = new GradientPaint(0,0,new Color(0,0,0,50),0,40,new Color(0,0,0,0),false);
        g2.setPaint(grad);
        g2.fillRect(0,0,800,50);
        grad = new GradientPaint( 0,VIEW_HEIGHT,new Color(0,0,0,50),0,VIEW_HEIGHT-40,new Color(0,0,0,0),false);
        g2.setPaint(grad);
        g2.fillRect(0,760,800,50);
        displaySelected(cityMap.i1Selected,cityMap.i2Selected);

        if (d.getRequests().size()!=0) {
            grad = new GradientPaint(0,30,new Color(0,0,0,50),0,60,new Color(0,0,0,0),false);
            g2.setPaint(grad);
            g2.fillRect(250,30,300,30);

            g.setColor(Color.WHITE);
            g2.fillRoundRect(250,-20,300,60,20,20);
            int x = 260;
            int y = 10;

            g2.setStroke(new BasicStroke(3));
            g.setColor(Color.BLACK);
            g.drawOval(x,y , POINT_SIZE, POINT_SIZE);
            g.drawString("Pickup point",x+25,y+10);
            g.drawPolygon(new int[]{x+100, x+100+POINT_SIZE, x+100+POINT_SIZE/2},
                    new int[]{y, y, y+POINT_SIZE}, 3);
            g.drawString("Delivery point",x+125,y+10);
            g.fillRect(x+200, y-2, POINT_SIZE+1, POINT_SIZE);
            g.drawString("Depot point",x+225,y+10);
            g2.draw(new Line2D.Float(x+202, y+10, x+202, y+20));
        }
    }

    /**
     * Display the points the user just added ( new request ) on the map
     * @param i1Selected the intersection of the new pickup
     * @param i2Selected the intersection of the new delivery
     */
    private void displaySelected(Intersection i1Selected, Intersection i2Selected) {
        if(i1Selected!=null){
            int x1 = convertLongitudeToPixel(i1Selected.getLongitude());
            int y1 = convertLatitudeToPixel(i1Selected.getLatitude());
            g.setColor(Color.black);
            g.drawLine(x1-5,y1,x1+5,y1);
            g.drawLine(x1,y1-5,x1,y1+5);

        } if(i2Selected!=null){
            int x2 = convertLongitudeToPixel(i2Selected.getLongitude());
            int y2 = convertLatitudeToPixel(i2Selected.getLatitude());
            g.setColor(Color.black);
            g.drawLine(x2-5,y2,x2+5,y2);
            g.drawLine(x2,y2-5,x2,y2+5);


        }

        displayPoiToAdd();
    }

    /**
     * Highlight the pickup and delivery points of a request on the map
     * @param i1Selected the intersection of the pickup
     * @param i2Selected the intersection of the delivery
     */
    private void displayHighlights(PointOfInterest p1, PointOfInterest p2) {

        int x1 = convertLongitudeToPixel(p1.getIntersection().getLongitude());
        int y1 = convertLatitudeToPixel(p1.getIntersection().getLatitude());
        Color Color1 = p1.getColor();
        int x2 = convertLongitudeToPixel(p2.getIntersection().getLongitude());
        int y2 = convertLatitudeToPixel(p2.getIntersection().getLatitude());
        Color Color2 = p2.getColor();
        if (p1 instanceof PickupAddress) {
            g.setColor(Color1);
            g.fillOval(x1 - HIGHLIGHT_POINT_SIZE/2, y1 - HIGHLIGHT_POINT_SIZE/2, HIGHLIGHT_POINT_SIZE , HIGHLIGHT_POINT_SIZE );
            g.setColor(Color.yellow);
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(x1 - HIGHLIGHT_POINT_SIZE/2, y1 - HIGHLIGHT_POINT_SIZE/2, HIGHLIGHT_POINT_SIZE , HIGHLIGHT_POINT_SIZE);
            g.setColor(Color2);
            g.fillPolygon(new int[]{x2-SECONDARY_POINT_SIZE/2, x2 + SECONDARY_POINT_SIZE/2, x2 }, new int[]{y2-SECONDARY_POINT_SIZE/2, y2-SECONDARY_POINT_SIZE/2, y2 + SECONDARY_POINT_SIZE/2}, 3);
            g.setColor(Color.yellow);
            g2.setStroke(new BasicStroke(2));
            g.drawPolygon(new int[]{x2-SECONDARY_POINT_SIZE/2, x2 + SECONDARY_POINT_SIZE/2, x2 }, new int[]{y2-SECONDARY_POINT_SIZE/2, y2-SECONDARY_POINT_SIZE/2, y2 + SECONDARY_POINT_SIZE/2}, 3);
        } else {
            g.setColor(Color2);
            g.fillOval(x2 - SECONDARY_POINT_SIZE/2, y2 - SECONDARY_POINT_SIZE/2, SECONDARY_POINT_SIZE , SECONDARY_POINT_SIZE );
            g.setColor(Color.yellow);
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(x2 - SECONDARY_POINT_SIZE/2, y2 - SECONDARY_POINT_SIZE/2, SECONDARY_POINT_SIZE , SECONDARY_POINT_SIZE );
            g.setColor(Color1);
            g.fillPolygon(new int[]{x1-HIGHLIGHT_POINT_SIZE/2, x1 + HIGHLIGHT_POINT_SIZE/2, x1}, new int[]{y1-HIGHLIGHT_POINT_SIZE/2, y1-HIGHLIGHT_POINT_SIZE/2, y1 + HIGHLIGHT_POINT_SIZE/2}, 3);
            g.setColor(Color.yellow);
            g2.setStroke(new BasicStroke(2));
            g.drawPolygon(new int[]{x1-HIGHLIGHT_POINT_SIZE/2, x1 + HIGHLIGHT_POINT_SIZE/2, x1}, new int[]{y1-HIGHLIGHT_POINT_SIZE/2, y1-HIGHLIGHT_POINT_SIZE/2, y1 + HIGHLIGHT_POINT_SIZE/2}, 3);
        }

    }

    public int convertLongitudeToPixel(double longitude) {
        return (int)((longitude - originLong) * scaleWidth);
    }

    public int convertLatitudeToPixel(double latitude) {
        return - (int) ((latitude - originLat) * scaleHeight);
    }

    /**
     * Draw the roads on the map
     * @param r the road to be drawn
     * @param c the color of the road
     * @param thickness the thickness of the road
     * @param displayTour boolean if the tour is computed or not
     */
    public void displayRoad(Road r, Color c , double thickness, boolean displayTour){
        int x1 = convertLongitudeToPixel(r.getOrigin().getLongitude());
        int y1 = convertLatitudeToPixel(r.getOrigin().getLatitude()); /* the latitude is reversed*/
        int x2 = convertLongitudeToPixel(r.getDestination().getLongitude());
        int y2 = convertLatitudeToPixel(r.getDestination().getLatitude());
        if (!displayTour){
            if (r.getName().contains("Boulevard") || r.getName().contains("Avenue") || r.getName().contains("Cours") ){
                thickness=greatRoadThickness;
                c=Color.decode("#ffd800");
            } else if (r.getName().contains("Impasse") ){
                thickness=smallRoadThickness;
                c=Color.WHITE;
            }else {
                thickness=smallRoadThickness;
                c=Color.WHITE;
            }
        }else if (counterInter++ == 10){
            counterInter = 0;
            double theta = Math.atan2(x1-x2,y1-y2);
            int middleRoadX = (int)((x1 + x2)/2);
            int middleRoadY = (int)((y1 + y2)/2);
            int fSize = 8;
            g2.fillPolygon(new int[] {middleRoadX,
                    (int)(middleRoadX+fSize*(Math.cos(-theta)-Math.sin(-theta))),
                    (int)(middleRoadX-fSize*(Math.cos(theta)-Math.sin(theta)))}, new int[] {middleRoadY,
                    (int)(middleRoadY+fSize*(Math.sin(-theta)+Math.cos(-theta))),
                    (int)(middleRoadY+fSize*(Math.sin(theta)+Math.cos(theta)))}, 3);
        }
        g.setColor(c);
        g2.setStroke(new BasicStroke((float)thickness));
        g2.draw(new Line2D.Float(x1, y1, x2, y2));

    }

    /**
     * Change the color of the roads from the computed path
     * @param p the path is be colored
     * @param c the color of the path's roads
     */
    public void displayPath(Path p,Color c){
        for (Road r : p.getRoads()){
            displayRoad(r,c,3,true);
        }
    }

    /**
     * Add requests on the map : draw forms depending on the type of the point
     * @param q the request to be added on the map
     * @param outline the color of the pair of points (a request)
     */
    public void displayRequest(Request q,Color outline){
        int x1 = convertLongitudeToPixel(q.getPickup().getIntersection().getLongitude());
        int y1 = convertLatitudeToPixel(q.getPickup().getIntersection().getLatitude());
        int x2 = convertLongitudeToPixel(q.getDelivery().getIntersection().getLongitude());
        int y2 = convertLatitudeToPixel(q.getDelivery().getIntersection().getLatitude());

        g.setColor(q.color);
        g.fillOval(x1-POINT_SIZE/2, y1-POINT_SIZE/2, POINT_SIZE, POINT_SIZE);
        g.setColor(outline);
        g2.setStroke(new BasicStroke(3));
        g2.drawOval(x1-POINT_SIZE/2, y1-POINT_SIZE/2, POINT_SIZE, POINT_SIZE);
        g.setColor(q.color);
        g.fillPolygon(new int[] {x2-POINT_SIZE/2, x2+POINT_SIZE/2, x2}, new int[] {y2-POINT_SIZE/2, y2-POINT_SIZE/2, y2+POINT_SIZE/2}, 3);
        g.setColor(outline);
        g2.setStroke(new BasicStroke(3));
        g.drawPolygon(new int[] {x2-POINT_SIZE/2, x2+POINT_SIZE/2, x2}, new int[] {y2-POINT_SIZE/2, y2-POINT_SIZE/2, y2+POINT_SIZE/2}, 3);


    }

    /**
     * Add depot point on the map : draw a rectangle
     */
    public void displayDepot(){
        if (cityMap.getDistribution().getDepot().getIntersection() != null) {
            int x = convertLongitudeToPixel(cityMap.getDistribution().getDepot().getIntersection().getLongitude());
            int y = convertLatitudeToPixel(cityMap.getDistribution().getDepot().getIntersection().getLatitude());
            g.setColor(Color.black);
            g.fillRect(x-2, y-25, POINT_SIZE+1, POINT_SIZE);
            g2.setStroke(new BasicStroke(3));
            g2.draw(new Line2D.Float(x, y-15, x, y));
        }
    }

    /**
     * Search the closest PointOfInterest to the clicked position on the map
     * @param x the x position of our point
     * @param y the y position of our point
     * @return the closest PointOfInterest to our point
     */
    public PointOfInterest getClosestPointOfInterest(int x, int y) {
        for (PointOfInterest poi : this.cityMap.getTour().getPointOfInterests()) {
            int xPoi = convertLongitudeToPixel(poi.getIntersection().getLongitude());
            int yPoi = convertLatitudeToPixel(poi.getIntersection().getLatitude());
            if (x <= xPoi + POINT_SIZE && x >= xPoi - POINT_SIZE && // check if point is inside the shape of the specific poi
                    y <= yPoi + POINT_SIZE && y >= yPoi - POINT_SIZE) { // for now, we just check if inside a square, to be modified...
                return poi;
            }
        }
        return null;
    }

    /**
     * Search the closest Intersection to the clicked position on the map
     * @param x the x position of our point
     * @param y the y position of our point
     * @return the closest Intersection to our point
     */
    public Intersection getClosestIntersection(int x, int y) {
        for (Intersection i : this.cityMap.getIntersections().values()) {
            int xPoi = convertLongitudeToPixel(i.getLongitude());
            int yPoi = convertLatitudeToPixel(i.getLatitude());
            if (x <= xPoi + POINT_SIZE/2 && x >= xPoi - POINT_SIZE/2 &&
                    y <= yPoi + POINT_SIZE/2 && y >= yPoi - POINT_SIZE/2) {
                return i;
            }
        }
        return null;
    }

    /**
     * Display a red cross following the mouse while the user try to add a new point
     */
    public void displayPoiToAdd() {
        if (cityMap.getPoiToAdd() != null) {
            int x = convertLongitudeToPixel(cityMap.getPoiToAdd().getLongitude());
            int y = convertLatitudeToPixel(cityMap.getPoiToAdd().getLatitude());
            g.setColor(Color.RED);
            g.drawLine(x-5, y, x+5, y);
            g.drawLine(x, y-5, x, y+5);
        }
    }
}