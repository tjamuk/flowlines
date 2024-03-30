package model;

import java.util.*;

public class PuzzleGenerator extends Game
{
    /**
     * The probability that a path will turn.
     */
    private final static double TURN_PROBABILITY = 0.1;

    private final static Random RANDOM_GENERATOR = new Random();

    /**
     * For the primitive int data type there is no null value, this is the assigned value to represent it.
     */
    public final static int NULL_INT_VALUE = -1;

    /**
     * Represents an element in int[] colours having no colour.
     */
    public final static int NO_COLOUR = 0;

    /**
     * A set representing all unvisited nodes in the graph.
     */
    Set<Integer> unvisited;

    /**
     * Represents all the paths.
     */
    Stack<Path> paths;

    /**
     * Simply a debug value to check which iteration of model.PuzzleGenerator the code is in.
     */
    int counter;

    /**
     * Used for finding connected components where each element represents whether a node has been visited by the dfs.
     */
    int[] visited;

    int[] idToComponentId;
    int[] idToComponentCause;

    /**
     * An ArrayList where each Set<Integer> is a connected component and each Integer of that set is a node.
     */
    ArrayList<Set<Integer>> components;

    /**
     * To be implemented.
     *
     * It may not be necessary to iteratively start at all unvisited nodes.
     */
    ArrayList<ArrayList<Integer>> startPoints;

    private boolean foundGap;
    private final static int[][] addendToAddends = {
            {2,3},
            {2,3},
            {0,1},
            {0,1}
    };
    private final static int directionCount = 4;
    private static Cell[] cells;

    /**
     * The constructor for model.PuzzleGenerator.
     * @param width - the number of columns.
     * @param height - the number of rows.
     */
    public PuzzleGenerator(int width, int height)
    {
        super(width, height, true);

        colours = new int[Game.size];

        counter = 0;

        Path.upperBound = width * 2 + height * 2; //Not currently used, but the longest possible path is equal to basically going all around the outside of the grid.

        paths = new Stack<>();
        unvisited = new HashSet<>(Game.size);

        for (int id = 0; id < Game.size; id++)
        {
            unvisited.add(id);
        }

        cells = new Cell[3];
    }

    /**
     * A recursive backtracking algorithm that builds a random solvable puzzle path by path.
     * @return true = was able to generate a puzzle; false = was not able.
     */
    public boolean generatePuzzle()
    {
        counter++;
        Path path;
        int second;
        int first;

        if (!unvisited.isEmpty()) //if there are some uncoloured nodes...
        {
            if (paths.isEmpty())
            {
//                calculateConnectedComponents(0);
                paths.push(new Path());
            }
            path = paths.peek();
            if (path.isFull() || path.isAtDeadend) //basically if the most recent path is finished, create a new path.
            {
                paths.push(new Path());
                path = paths.peek();
            }
            if (path.isEmpty()) //if path is empty, try initialising the path with all unvisited nodes.
            {
                //need to calculate components

                calculateConnectedComponents(0);
                for (int node : components.get(getSmallestComponent()).parallelStream().toList()) //start on the smallest component
                {
                    addNode(node, path.id, false); //isTurn meaningless here.
                    if (checkForSquares(idToCell[node]) && calculateConnectedComponents(node))
                    {
                        if (generatePuzzle())
                        {
                            return true;
                        }
                    }
                    removeNode(node);
                }
                paths.pop();
                return false;
            }
            else
            {
                System.out.print("path ");
                System.out.print(path.id);
                System.out.print(" = ");
                for (int node : path.getSequence())
                {
                    System.out.print(idToCell[node]);
                    System.out.print(", ");
                }
                System.out.println();

                first = path.peek(); //the front of the path.
                second = path.peekSecond(); //2nd front of the path (behind first)
                ArrayList<Integer> neighbours = new ArrayList<>(); //an ordered sequence of neighbours to visit

                int straightOnNode = getValidNeighbours(neighbours, first, second);

                for (int neighbour : neighbours)
                {
                    addNode(neighbour, path.id, straightOnNode!=neighbour);
//                    System.out.print("adding ");
//                    System.out.println(idToCell[neighbour]);

                    //if the size of any connected components is less than 3...
                    //  (But if one is less than 3 need to check whether it's adjacent to the front of the most recent path )
                    if (path.areDirectionsValid() && checkForSquares(idToCell[neighbour]) && calculateConnectedComponents(neighbour))
                    {
                        if (generatePuzzle())
                        {
                            return true;
                        }
//                        System.out.print("removing ");
//                        System.out.println(idToCell[neighbour]);
                        removeNode(neighbour);
                        path.isAtDeadend = false;
                    }
                    else
                    {
//                        System.out.print("removing ");
//                        System.out.println(idToCell[neighbour]);
                        removeNode(neighbour);
                    }
                }

                //no more neighbours to add (at a deadend) so...
                if (path.isTooSmall()) //if path is too small backtrack.
                {
                    return false;
                }
                else //if the path is of sufficient size, will call generatePuzzle again where it will create a new path.
                {
                    path.isAtDeadend = true;
                    return generatePuzzle();
                }
            }
        }
        else
        {
            if (!paths.isEmpty())
            {
                //                    calculateConnectedComponents(0);
                return !paths.peek().isTooSmall();
            }
            else
            {
                return false;
            }
        }
    }

    /**
     * In order, gets the valid neighbours of the front of the most recent path.
     * @param visitOrder - the order in which to visit the valid neighbours.
     * @param first - front of the most recent path.
     * @param second - 2nd to the front of the most recent path/
     */
    public int getValidNeighbours(ArrayList<Integer> visitOrder, int first, int second)
    {
//        System.out.print("    in getValidNeighbours( first = ");
//        System.out.print(first);
//        if (first != Path.NULL_VALUE)
//        {
//            System.out.print(" - ");
//            System.out.print(idToCell[first]);
//        }
//        System.out.print(", second = ");
//        System.out.print(second);
//        if (second != Path.NULL_VALUE)
//        {
//            System.out.print(" - ");
//            System.out.print(idToCell[second]);
//        }
//        System.out.println(" )");

//        Set<Integer> neighbours = Game.edges.get(first);
        Set<Integer> neighbours = new HashSet<>(Game.getEdges(first));

        if (second != Path.NULL_VALUE)
        {
            neighbours.remove(second); //if there is a second, it will be a neighbour to the first so remove (as it's already been visited)
        }

        boolean hasFoundRedundantNode;

        for (int neighbour : neighbours)
        {
//            System.out.print("      neighbour ");
//            System.out.print(idToCell[neighbour]);
            if (unvisited.contains(neighbour))
            {
                hasFoundRedundantNode = false;

                //finds redundant nodes where for each neighbour, need to check if it could've been visited earlier in the path.
//                System.out.print("            has neighbours ");
//                System.out.println(Game.getEdges(neighbour));
                for (int nn : Game.getEdges(neighbour)) //Game.edges.get(neighbour).parallelStream().toList()
                {
//                    System.out.print("\n            neighbour to neighbour ");
//                    System.out.print(idToCell[nn]);
//                    System.out.print(" - nn != first = ");
//                    System.out.print(nn != first);
//                    System.out.print("; redundant = ");
//                    System.out.print(paths.peek().contains(nn));
                    if (nn != first && paths.peek().contains(nn))
                    {
//                        System.out.println("\n            -> is redundant - INVALID");
                        hasFoundRedundantNode = true;
                        break;
                    }
                }
                if (!hasFoundRedundantNode)
                {
//                    System.out.println(" -> VALID");
                    visitOrder.add(neighbour);
                }
            }
            else
            {
//                System.out.println(" -> has been visited before - INVALID");
            }
        }

        return reorderVisitOrder(visitOrder, first, second);
    }

    /**
     * Reorders the visit order of nodes.
     *
     * Decides if the path will be going straight or not and then reorders it accordingly.
     * @param visitOrder - the order in which to visit the valid neighbours.
     * @param first - front of the most recent path.
     * @param second - 2nd to the front of the most recent path.
     */
    private int reorderVisitOrder(ArrayList<Integer> visitOrder, int first, int second)
    {
        if (first != Path.NULL_VALUE && second != Path.NULL_VALUE)
        {
            boolean isGoingStraight = isGoingStraight();
            int straightOnNode = getStraightOnNode(Game.idToCell[first], Game.idToCell[second]);
            int lastIndex = visitOrder.size()-1;

            if (lastIndex > -1 && straightOnNode != NULL_INT_VALUE)
            {
                if (visitOrder.get(0) == straightOnNode)
                {
                    if (!isGoingStraight)
                    {
                        if (lastIndex > 0) //in case it's just the straightOnNode in the neighbours.
                        {
                            visitOrder.set(0, visitOrder.get(lastIndex));
                            visitOrder.set(lastIndex, straightOnNode);
                        }
                    }
                    //else do nothing
                }
                else
                {
                    if (isGoingStraight)
                    {
                        //swap to front
                        for (int index = 1; index < lastIndex; index++)
                        {
                            if (visitOrder.get(index) == straightOnNode)
                            {
                                visitOrder.set(index, visitOrder.get(0));
                                visitOrder.set(0, straightOnNode);
                            }
                        }
                    }
                    //else do nothing
                }
            }
            return straightOnNode;
        }
        return NULL_INT_VALUE;
    }

    /**
     * Removes a node.
     * @param node = the node to be removed.
     */
    private void removeNode(int node)
    {
        System.out.print("removing ");
        System.out.print(idToCell[node]);
        System.out.print(" from path ");
        System.out.println(paths.peek().id);
        paths.peek().remove();
        unvisited.add(node);
        colours[node] = NO_COLOUR;
    }

    /**
     * Add a node.
     * @param node = the node to be added.
     */
    private void addNode(int node, int pathId, boolean isTurn)
    {
        System.out.print("adding ");
        System.out.print(idToCell[node]);
        System.out.print(" to path ");
        System.out.println(paths.peek().id);
        paths.peek().add(node, isTurn);
        unvisited.remove(node);
        colours[node] = pathId;
    }

    /**
     * Decides randomly if the path is going straight or not.
     * @return true = path going straight.
     */
    public boolean isGoingStraight()
    {
        return RANDOM_GENERATOR.nextDouble() > TURN_PROBABILITY;
    }

    public void outputPaths()
    {
//        System.out.println(paths.size());

        int[][] grid = new int[width][height];
        Cell cell;
        int colour = 0;

        int pathSize = paths.size();

        while (!paths.isEmpty())
        {
            System.out.print("[");
            for (int node : paths.pop().getSequence())
            {
                cell = Game.idToCell[node];
                grid[cell.getCol()][cell.getRow()] = colour;
                colours[node] = colour;
                System.out.print(Game.idToCell[node]);
                System.out.print(", ");
            }
            colour++;
            System.out.println("]");
        }

        if (pathSize <= Colour.getColourCount())
        {
            for (int row = 0; row < height; row++)
            {
                for (int col = 0; col < width; col++)
                {
                    System.out.print(Colour.getBackgroundFromOrdinal(grid[col][row]) + "   ");
                }
                System.out.println("\u001B[0m");
            }
        }
    }

    protected int getSmallestComponent()
    {
        if (components.isEmpty())
        {
            return 0;
        }
        else
        {
            int smallestIndex = 0;
            int smallestSize = components.get(0).size();

            for (int componentIndex = 1; componentIndex < components.size(); componentIndex++)
            {
                if (components.get(componentIndex).size() < smallestSize)
                {
                    smallestIndex = componentIndex;
                    smallestSize = components.get(componentIndex).size();
                }
            }

            return smallestIndex;
        }
    }

    public void outputConnectedComponents()
    {
        for (Set<Integer> component : components)
        {
            System.out.print("CC = ");
            for (Integer node : component)
            {
                System.out.print(node);
                System.out.print(" - ");
                System.out.print(Game.idToCell[node]);
                System.out.print(", ");

            }
            System.out.println();
        }

        for (ArrayList<Integer> component : startPoints)
        {
            System.out.print("StartPoints = ");
            for (Integer node : component)
            {
                System.out.print(node);
                System.out.print(" - ");
                System.out.print(Game.idToCell[node]);
                System.out.print(", ");

            }
            System.out.println();
        }

    }

    /**
     * Finds the connected components.
     * @param latestNode - the most recently added node.
     * @return true = All components are of sufficient size OR they're adjacent to the most recent node.
     */
    public boolean calculateConnectedComponents(int latestNode)
    {
        components = new ArrayList<>();
        visited = new int[Game.size];

        if (paths.isEmpty())
        {
            components.add(new HashSet<>());
            for (int node = 0; node < Game.size; node++)
            {
                components.get(0).add(node);
            }
            Arrays.fill(visited, 0);
        }
        else
        {
            Set<Integer> latestNodeNeighbours = new HashSet<>(Game.getEdges(latestNode));
            int lastComponentIndex = 0;
            boolean isGood;

            Arrays.fill(visited, NULL_INT_VALUE);
            startPoints = new ArrayList<>();

            for (int vertex = 0; vertex < size; vertex++)
            {
                if (visited[vertex] == NULL_INT_VALUE && colours[vertex] == NO_COLOUR)
                {
                    isGood = false;
                    startPoints.add(new ArrayList<>());
                    components.add(new HashSet<>());
                    dfs(vertex, lastComponentIndex);

                    if (components.get(lastComponentIndex).size() < 3) //size
                    {
                        for (int node : components.get(lastComponentIndex)) //if component too small, check if its adjacent to the latest node.
                        {
                            if (latestNodeNeighbours.contains(node))
                            {
                                isGood = true;
                                break;
                            }
                        }
                        if (!isGood) {return false;}
                    }
                    lastComponentIndex++;
                }
            }
        }
        return true;
    }

    public void newCalcConnectedComponents(int latestNode)
    {
//        Set<Integer> latestNodeNeighbours = Game.edges.get(latestNode);
//        int lastComponentIndex = 0;
//        boolean isGood;
//
//        idToComponentCause = new int[Game.size];
//        idToComponentId = new int[Game.size];
//
//        Arrays.fill(idToComponentCause, NULL_INT_VALUE);
//
//
//        components = new ArrayList<>();
//
//        if (paths.isEmpty())
//        {
//            components.add(new HashSet<>());
//
//            for (int node = 0; node < Game.size; node++)
//            {
//                components.get(0).add(node);
//            }
//            Arrays.fill(idToComponentId, 0);
//        }
//        else
//        {
//            Arrays.fill(idToComponentId, NULL_INT_VALUE);
//
//            for (Path path : paths)
//            {
//                for (int node : path.sequence)
//                {
//                    if (visited[node] == NULL_INT_VALUE)
//                    {
//                        for (int neighbour : Game.edges.get(0))
//                        {
//                            if (visited[neighbour] == NULL_INT_VALUE && colours[neighbour] == NO_COLOUR)
//                            {
//                                isGood = false;
//                                components.add(new HashSet<>());
//                                dfsNew(neighbour, lastComponentIndex, path.id); //adjust
//
//                                if (components.get(lastComponentIndex).size() < 3)
//                                {
//                                    for (int vertex : components.get(lastComponentIndex)) //if component too small, check if its adjacent to the latest node.
//                                    {
//                                        if (latestNodeNeighbours.contains(vertex))
//                                        {
//                                            isGood = true;
//                                            break;
//                                        }
//                                    }
//                                    if (!isGood) {return false;}
//                                }
//                                lastComponentIndex++;
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }

    private void dfsNew(int vertexId, int componentIndex, int pathId)
    {

//        visited[vertexId] = pathId;
//        components.get(componentIndex).add(vertexId);
//
//        for (int neighbour : Game.edges.get(vertexId))
//        {
//            if (colours[neighbour] == NO_COLOUR)
//            {
//                if (visited[neighbour] == NULL_INT_VALUE)
//                {
//                    dfsNew(neighbour, componentIndex, pathId);
//                }
//            }
//            else if (colours[neighbour] == pathId)
//            {
//                visited[vertexId] = pathId;
//            }
//        }
    }

    /**
     * Depth first search for finding connected components.
     * @param vertexId = the node.
     * @param componentIndex = the component index.
     */
    private void dfs(int vertexId, int componentIndex)
    {
        boolean isStartingVertex;
        if (Game.edges.get(vertexId).size() < 4)
        {
            isStartingVertex = true;
            startPoints.get(componentIndex).add(vertexId);
        }
        else
        {
            isStartingVertex = false;
        }

        visited[vertexId] = componentIndex;
        components.get(componentIndex).add(vertexId);

        for (int neighbour : Game.getEdges(vertexId))
        {
            if (colours[neighbour] == NO_COLOUR)
            {
                if (visited[neighbour] == NULL_INT_VALUE)
                {
                    dfs(neighbour, componentIndex);
                }
            }
            else
            {
                if (!isStartingVertex)
                {
                    isStartingVertex = true;
                    startPoints.get(componentIndex).add(vertexId);
                }
            }
        }
    }

    private boolean checkForSquares(Cell newNode)
    {
        cells[0] = newNode;

        for (int direction = 0; direction < directionCount; direction++)
        {
            cells[1] = addCells(ADDENDS_TO_FIND_NEIGHBOURS[direction], newNode);

            if (!isNodeInGrid(cells[1].getCol(), cells[1].getRow()) || colours[cellToId[cells[1].getCol()][cells[1].getRow()]] != NO_COLOUR)
            {
                for (int addend : addendToAddends[direction])
                {
                    cells[2] = addCells(ADDENDS_TO_FIND_NEIGHBOURS[addend], cells[1]);
                    if (!isNodeInGrid(cells[2].getCol(), cells[2].getRow()) || colours[cellToId[cells[2].getCol()][cells[2].getRow()]] != NO_COLOUR)
                    {
                        if (!checkSquare())
                        {
                            System.out.println("Square found");
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean checkSquare()
    {
//        System.out.print("Entering checkSquare()\n      ");
//        System.out.print(cells[0]); System.out.print(" <-- "); System.out.print(cells[1]); System.out.print(" <-- "); System.out.print(cells[2]);
//        System.out.println();

        Cell closestCorner = addCells(subtractCells(cells[0], cells[1]), cells[2]);
        int x;
        int y;

        if (!Game.isNodeInGrid(closestCorner.getCol(), closestCorner.getRow()) || colours[cellToId[closestCorner.getCol()][closestCorner.getRow()]] != NO_COLOUR)
        {
            return true;
        }

        Set<Integer> corners = new HashSet<>(4);
        corners.add(cellToId[closestCorner.getCol()][closestCorner.getRow()]);

        for (Cell cell : cells)
        {
            x = 2 * closestCorner.getCol() - cell.getCol();
            y = 2 * closestCorner.getRow() - cell.getRow();
            // 1,4 --> !true || true --> false || true --> true
            if (!Game.isNodeInGrid(x,y) || colours[cellToId[x][y]] != NO_COLOUR)
            {
                return true;
            }

            corners.add(cellToId[x][y]);
        }

        foundGap = false;

        for (int corner : corners)
        {
            if (checkCorner(corner, corners))
            {
                if (foundGap)
                {
                    return true;
                }
                else
                {
                    foundGap = true;
                }
            }
        }
        return false;
    }

    private Cell addCells(Cell a, Cell b)
    {
        return new Cell(
                a.getCol() + b.getCol(),
                a.getRow() + b.getRow()
        );
    }

    private Cell subtractCells(Cell a, Cell b)
    {
        return new Cell(
                a.getCol() - b.getCol(),
                a.getRow() - b.getRow()
        );
    }

    private boolean checkCorner(int corner, Set<Integer> corners)
    {
//        System.out.print("      Entering checkCorner( corner = ");
//        System.out.print(idToCell[corner]);
//        System.out.print(", corners = ");
//        for (int c : corners)
//        {
//            System.out.print(idToCell[c]);
//            System.out.print(", ");
//        }
//        System.out.println(") ");

        boolean found = false;
        ArrayList<Integer> neighbours = getFreeNeighbours(corner);

//        System.out.print("            ");
//        for (int n : neighbours)
//        {
//            System.out.print(idToCell[n]);
//            System.out.print(",  ");
//        }
//        System.out.println();

        for (int neighbour : neighbours)
        {
            if (!corners.contains(neighbour))
            {
                if (getFreeNeighbours(neighbour).size() > 1)
                {
                    if (found || foundGap)
                    {
//                        System.out.print("            ");
//                        System.out.print(idToCell[neighbour]);
//                        System.out.println(" has more than 1 free neighbour - already have 1 found");
                        foundGap = true;
                        return true;
                    }
                    else
                    {
//                        System.out.print("            ");
//                        System.out.print(idToCell[neighbour]);
//                        System.out.println(" has more than 1 cell - first found");
                        found = true;
                    }
                }
            }
        }
//        System.out.print("            returning, found = ");
//        System.out.println(found);
        return found;
    }

    public ArrayList<Integer> getFreeNeighbours(int id)
    {
        ArrayList<Integer> neighbours = new ArrayList<>();
        int neighbourCol;
        int neighbourRow;
        Cell cell = idToCell[id];

        for (Cell addendPair : ADDENDS_TO_FIND_NEIGHBOURS)
        {
            neighbourCol = cell.getCol() + addendPair.getCol();
            neighbourRow = cell.getRow() + addendPair.getRow();

            if (isNodeInGrid(neighbourCol, neighbourRow) && colours[Game.cellToId[neighbourCol][neighbourRow]] == NO_COLOUR)
            {
                neighbours.add(Game.cellToId[neighbourCol][neighbourRow]); //if valid neighbour, add to hashset.
            }
        }
        return neighbours;
    }

    public float postGeneration()
    {
        float turnCount = 0;
        Path p;
        final int pathCount = paths.size();
        Game.startGoals = new int[pathCount];
        Game.endGoals = new int[pathCount];

        for (int i = 0; i < pathCount; i++)
        {
            p = paths.pop();
            Game.startGoals[i] = p.peek();
            Game.endGoals[i] = p.peekLast();

            System.out.print( idToCell[Game.startGoals[i]] );
            System.out.print( " --> ");
            System.out.println( idToCell[Game.endGoals[i]]);

            turnCount += p.getTurnCount();
        }

        return turnCount/pathCount;

//        if (turnsPerPath <= 1.5)
//        {
//            return 1;
//        }
//        else if (turnsPerPath <= 2.5)
//        {
//            return 2;
//        }
//        else
//        {
//            return 3;
//        }

    }
}
