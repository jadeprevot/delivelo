package tsp;

import model.GraphPointToPoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public abstract class TemplateTSP implements TSP {
    protected Integer[] bestSolution;  //de base que g en protected, le reste en private
    protected GraphPointToPoint g;
    protected Double bestSolutionCost;
    private int timeLimit;
    private long startTime;

    public void searchSolution(int timeLimit, GraphPointToPoint g){
        if (timeLimit <= 0) return;
        startTime = System.currentTimeMillis();
        this.timeLimit = timeLimit;
        this.g = g;
        bestSolution = new Integer[g.getNbVertices()];
        Collection<Integer> unvisited = new ArrayList<Integer>(g.getPickupSet());
        Collection<Integer> visited = new ArrayList<Integer>(g.getNbVertices());
        visited.add(0); // The first visited vertex is 0
        bestSolutionCost = Double.MAX_VALUE;
        branchAndBound(0, unvisited, visited, 0.0);
    }

    /**
     * Method that must be defined in TemplateTSP subclasses
     * @param currentVertex
     * @param unvisited
     * @return a lower bound of the cost of paths in <code>g</code> starting from <code>currentVertex</code>, visiting
     * every vertex in <code>unvisited</code> exactly once, and returning back to vertex <code>0</code>.
     */
    protected abstract int bound(Integer currentVertex, Collection<Integer> unvisited);

    /**
     * Template method of a branch and bound algorithm for solving the TSP in <code>g</code>.
     * @param currentVertex the last visited vertex
     * @param unvisited the set of vertex that have not yet been visited
     * @param visited the sequence of vertices that have been already visited (including currentVertex)
     * @param currentCost the cost of the path corresponding to <code>visited</code>
     */
    private void branchAndBound(int currentVertex, Collection<Integer> unvisited,
                                Collection<Integer> visited, Double currentCost){
        if (System.currentTimeMillis() - startTime > timeLimit) return;
        if (unvisited.size() == 0){

            if (currentCost+g.getCost(currentVertex,0) < bestSolutionCost){
                visited.toArray(bestSolution);
                bestSolutionCost = currentCost+g.getCost(currentVertex,0);

            }
        } else if (currentCost+bound(currentVertex,unvisited) < bestSolutionCost){
            Iterator<Integer> it = iterator(currentVertex, unvisited, g);
            while (it.hasNext()){
                Integer nextVertex = it.next();
                visited.add(nextVertex);
                unvisited.remove(nextVertex);
                if(g.getDelivery(nextVertex)!=null){
                    unvisited.add(g.getDelivery(nextVertex));
                }
                branchAndBound(nextVertex, unvisited, visited,
                        currentCost+g.getCost(currentVertex, nextVertex));
                if(g.getDelivery(nextVertex)!=null){
                    unvisited.remove(g.getDelivery(nextVertex));
                }
                visited.remove(nextVertex);
                unvisited.add(nextVertex);
            }
        }
    }

    /**
     * Method that must be defined in TemplateTSP subclasses
     * @param currentVertex
     * @param unvisited
     * @param g
     * @return an iterator for visiting all vertices in <code>unvisited</code> which are successors of <code>currentVertex</code>
     */
    protected abstract Iterator<Integer> iterator(Integer currentVertex, Collection<Integer> unvisited, GraphPointToPoint g);

    public Integer getSolution(int i){
        if (g != null && i>=0 && i<g.getNbVertices()+1)
            return bestSolution[i];
        return -1;
    }

    public Double getSolutionCost(){
        if (g != null)
            return bestSolutionCost;
        return -1.0;
    }
}
