package model;

import java.util.*;

public class Solver extends Game {

    /**
     * The number of coloured goal pairs.
     */
    int colourCount;

    int componentCount;
    int[] nodeToComponent;
    ArrayList<Set<Integer>> componentGoals;
    ArrayList<Set<Integer>> componentColours;

    boolean[] areColoursPossibleToConnect;
    int[] goalColours;

    /**
     * The paths of the colours.
     */
    ArrayList<LinkedList<Integer>> paths;

    /**
     * The last cells of the paths.
     */
    int[] lastCells;

    /**
     * An array where each element represents the colour held by a node.
     */
//    Colour[] colours;
//    int[] colours;

    /**
     * An arraylist of goals
     * (not an array due to Pair<A> being a generic and generic array creation.)
     */
//    int[] startGoals;
//    int[] endGoals;

    static final int NO_COLOUR_VALUE = -1;

    static ArrayList<Set<Integer>> bottlenecks;

    /**
     * Only constructor for model.Game that initialises everything.
     *
     * @param width the number of columns in the grid.
     * @param height the number of rows in the grid.
//     * @param goals An array list of start,end goal node pairs. Where each node is a column,row pair.
     */
    public Solver(int width, int height, ArrayList<Cell> startGoalsList, ArrayList<Cell> endGoalsList)
    {
        super(width, height, false);

//        colours = new Colour[width*height];
        colours = new int[width*height];

        colourCount = startGoalsList.size();

        Cell cell;

        startGoals = new int[colourCount];
        endGoals = new int[colourCount];

        for (int colour = 0; colour < colourCount; colour++)
        {
            cell = startGoalsList.get(colour);
            startGoals[colour] = Game.cellToId[cell.getCol()][cell.getRow()];

            cell = endGoalsList.get(colour);
            endGoals[colour] = Game.cellToId[cell.getCol()][cell.getRow()];
        }

//        Arrays.fill(colours, Colour.NONE);
        Arrays.fill(colours, NO_COLOUR_VALUE);

        addGoals();
        initialisePaths();
    }

    public Solver()
    {
        colourCount = Game.startGoals.length;

//        for (int i = 0; i < colourCount; i++)
//        {
//            System.out.print( idToCell[Game.startGoals[i]] );
//            System.out.print( " --> ");
//            System.out.println( idToCell[Game.endGoals[i]]);
//        }

        colours = new int[width*height];
        Arrays.fill(colours, NO_COLOUR_VALUE);
        addGoals();
        initialisePaths();
    }

    /**
     * Adds the coloured goals (found in ArrayList<Pair<Pair<Integer>>> goals) to model.Colour[][] grid.
     */
    private void addGoals()
    {
        for (int goalPairIndex = 0; goalPairIndex < colourCount; goalPairIndex++) //for every pair of goals...
        {
//            colours[startGoals[goalPairIndex]] = Colour.getEnumFromOrdinal(goalPairIndex);
//            colours[endGoals[goalPairIndex]] = Colour.getEnumFromOrdinal(goalPairIndex);

            colours[startGoals[goalPairIndex]] = goalPairIndex;
            colours[endGoals[goalPairIndex]] = goalPairIndex;
        }
        goalColours = colours.clone();
    }

    /**
     * Adds the coloured goals (found in ArrayList<Pair<Pair<Integer>>> goals) to model.Colour[][] grid.
     */
    private void initialisePaths()
    {
        lastCells = new int[colourCount];
        Arrays.fill(lastCells, -1);

        paths = new ArrayList<>();

        for (int index = 0; index < colourCount; index++)
        {
//            lastNodes[index] = startGoals.get(index);
            int finalIndex = index;
            paths.add(new LinkedList<>() {{
                addFirst(startGoals[finalIndex]);
            }});
        }
    }

    /**
     * Returns whether there is a path connecting a colour's start and end goals.
     */
    private boolean isColourDone(int colour)
    {
//        System.out.println(colour);
//        System.out.println(endGoals[colour]);
        return paths.get(colour).getFirst().equals(endGoals[colour]);
    }

    /**
     * A recursive backtracking algorithm that solves the puzzle.
     * @return true = was able to find a solution; false = was not able.
     */
    public boolean solve()
    {
        int current;
        int prev;
        int next;
        Stack<Integer> neighbours;

        for (int colour = 0; colour < colourCount; colour++)
        {
            if (!isColourDone(colour))
            {
//                System.out.print(colour);
//                System.out.print("'s path = ");
//                for (int node : paths.get(colour))
//                {
//                    System.out.print(Game.idToCell[node]);
//                    System.out.print(" <-- ");
//                }
//                System.out.println();

//                printGrid();
//                System.out.println("------------------");

                current = paths.get(colour).getFirst();
                prev = lastCells[colour];
                next = (prev == -1)? -1 : getStraightOnNode(idToCell[current], idToCell[prev]);
                neighbours = new Stack<>();

//                System.out.println(current);

                //if the front of the path is next to the endGoal, can move onto the next colour.
                if (Game.edges.get(current).contains(endGoals[colour]))
                {
                    paths.get(colour).addFirst(endGoals[colour]);
                    lastCells[colour] = current;
                    if (solve())
                    {
                        return true;
                    }
                    else
                    {
                        paths.get(colour).removeFirst();
                        lastCells[colour] = prev;
                        return false;
                    }
                }

                //For every neighbour of the front of the path...
                //If it's not the straightOn node nor filled, add.
                for (int n : edges.get(current))
                {
                    if (colours[n] == NO_COLOUR_VALUE && n!=next)
//                        if (colours[n] == Colour.NONE && n!=next)
                    {
                        neighbours.add(n);
                    }
                }

                //After all other neighbours have been added, if straightOn node exists and is unfilled, add (to front of stack)
                if (next != -1)
                {
                    if (colours[next] == NO_COLOUR_VALUE)
//                        if (colours[next] == Colour.NONE)
                    {
                        neighbours.add(next);
                    }
                }

                int third = -1; //the third most recent in the path.

                if (paths.get(colour).size() > 2)
                {
                    third = paths.get(colour).get(2);
                }

                while (!neighbours.isEmpty()) //add every neighbour.
                {
                    next = neighbours.pop();

                    if (third != -1) //check to see if redundant node.
                    {
                        if (edges.get(third).contains(next))
                        {
                            third = -1;
                            continue;
                        }
                    }

                    //add to path.
                    paths.get(colour).addFirst(next);
//                    colours[next] = Colour.getEnumFromOrdinal(colour);
                    colours[next] = colour;
                    lastCells[colour] = current;

                    if (findConnectedComponents(colour) && solve())
                    {
                        return true;
                    }
                    else
                    {
                        paths.get(colour).removeFirst();
//                        colours[next] = Colour.NONE;
                        colours[next] = NO_COLOUR_VALUE;
                        lastCells[colour] = prev;
                    }
                }
                return false;
            }
        }
        return true;
    }

    public boolean findConnectedComponents(int latestNodeColour) {

        nodeToComponent = new int[Game.size];
        Arrays.fill(nodeToComponent, NO_COLOUR_VALUE);
        areColoursPossibleToConnect = new boolean[colourCount];
        componentCount = 0;
        componentGoals = new ArrayList<>();
        componentColours = new ArrayList<>();

        for (int node = 0; node < Game.size; node++) {
            if (nodeToComponent[node] == NO_COLOUR_VALUE && colours[node] == NO_COLOUR_VALUE) {
                System.out.println(node);
                componentGoals.add(new HashSet<>());
                componentColours.add(new HashSet<>());
                dfs(node);
                if (!isComponentValid(latestNodeColour, componentCount)) {
                    return false;
                }
                componentCount++;
            }
        }
        return areComponentsValid(latestNodeColour);
    }

    public void dfs(int node)
    {
        System.out.println(idToCell[node]);
        for (int neighbour : Game.getEdges(node))
        {
            if (idToCell[neighbour].getCol() == 3 && idToCell[neighbour].getRow() == 2)
            {
                System.out.println(componentCount);
            }
            if (colours[neighbour] == NO_COLOUR_VALUE)
            {
                if (nodeToComponent[neighbour] == NO_COLOUR_VALUE)
                {
                    if (idToCell[neighbour].getCol() == 3 && idToCell[neighbour].getRow() == 2)
                    {
                        System.out.println(componentCount);
                    }
                    nodeToComponent[neighbour] = componentCount;
                    dfs(neighbour);
                }
            }
            else
            {
                int goalColour = goalColours[neighbour];
                if (goalColour != NO_COLOUR_VALUE)
                {
                    componentColours.get(componentCount).add(goalColour);
                    componentGoals.get(componentCount).add(neighbour);
                }
            }
        }
    }

    public boolean isComponentValid(int latestNodeColour, int componentId)
    {
        if (componentColours.get(componentId).isEmpty())
        {
            return false;
        }

        boolean atLeastOneColourPossibleToConnect = false;

        for (int colour : componentColours.get(componentId))
        {
            if (
                    componentGoals.get(componentId).contains(startGoals[colour])
                    &&
                    componentGoals.get(componentId).contains(endGoals[colour])
            )
            {
                atLeastOneColourPossibleToConnect = true;
                areColoursPossibleToConnect[colour] = true;
            }
        }
        if (atLeastOneColourPossibleToConnect)
        {
            return true;
        }
        else
        {
            return componentColours.get(componentId).contains(latestNodeColour);
        }
    }

    public boolean areComponentsValid(int latestNodeColour)
    {
        for (int colour = 0; colour < colourCount; colour++)
        {
            if (!isColourDone(colour) && !areColoursPossibleToConnect[colour])
            {
                if (colour != latestNodeColour)
                {
                    return false;
                }
            }
        }
        return true;
    }

    public void findBottlenecks(int node)
    {
        int xNode = idToCell[node].getCol(), yNode = idToCell[node].getRow();
        int xNeighbour; int yNeighbour; int neighbour;

        for (int direction = 0; direction < ADDENDS_LENGTH; direction++)
        {
            xNeighbour = xNode + ADDENDS_TO_FIND_NEIGHBOURS[direction].getCol();
            yNeighbour = yNode + ADDENDS_TO_FIND_NEIGHBOURS[direction].getRow();

            if (isNodeInGrid(xNeighbour, yNeighbour) && colours[cellToId[xNeighbour][yNeighbour]] == NO_COLOUR_VALUE)
            {
                neighbour = cellToId[xNeighbour][yNeighbour];
                System.out.println(idToCell[neighbour]); System.out.println(nodeToComponent[neighbour]); System.out.println();
                isBottleneck(
                        neighbour,
                        componentGoals.get(nodeToComponent[neighbour]).size(),
                        direction,
                        0
                );
            }
        }
    }

    public int getComponent(int node)
    {
        return 0;
    }

    public boolean isBottleneck(int cell, int goalCount, int direction, int current)
    {
        int xCell = idToCell[cell].getCol();
        int yCell = idToCell[cell].getRow();
        int x1; int y1; int x2; int y2; int direction1; int direction2; int other; int xOther; int yOther;

        Set<Integer> b = new HashSet<>();

        if (direction < 2)
        {
            direction1 = 2;
            direction2 = 3;
            other = (direction==0)? 1 : 0;
        }
        else
        {
            direction1 = 0;
            direction2 = 1;
            other = (direction==2)? 3 : 2;
        }

        while (true)
        {
            x1 = xCell + ADDENDS_TO_FIND_NEIGHBOURS[direction1].getCol();
            y1 = yCell + ADDENDS_TO_FIND_NEIGHBOURS[direction1].getRow();

            x2 = xCell + ADDENDS_TO_FIND_NEIGHBOURS[direction2].getCol();
            y2 = yCell + ADDENDS_TO_FIND_NEIGHBOURS[direction2].getRow();

            if (
                    !isNodeInGrid(x1, y1) ||
                            !isNodeInGrid(x2, y2) ||
                            colours[cellToId[x1][y1]] != NO_COLOUR_VALUE ||
                            colours[cellToId[x2][y2]] != NO_COLOUR_VALUE
            )
            {
                return false;
            }

            xOther = xCell + ADDENDS_TO_FIND_NEIGHBOURS[direction].getCol();
            yOther = yCell + ADDENDS_TO_FIND_NEIGHBOURS[direction].getRow();

            if (!isNodeInGrid(xOther, yOther) || colours[cellToId[xOther][yOther]] != NO_COLOUR_VALUE)
            {
                b.add(cell);
                break;
            }
            else if (current != goalCount)
            {
                b.add(cell);
                //continue
                xCell = xOther;
                yCell = yOther;
                cell = cellToId[xOther][yOther];
                current++;
            }
            else
            {
                return false;
            }
        }

        while (bottlenecks.size() < b.size() + 1)
        {
            bottlenecks.add(new HashSet<>());
        }

        bottlenecks.get(b.size()).addAll(b);
        return true;
    }

    public void addPath(Cell[] path)
    {
        int node; int x; int y;

        bottlenecks = new ArrayList<>();

        for (Cell cell : path)
        {
            x = cell.getCol();
            y = cell.getRow();
            node = cellToId[x][y];
            colours[node] = 999;
            findConnectedComponents(node);
            findBottlenecks(node);
        }

//        x = path[path.length-1].getCol();
//        y = path[path.length-1].getRow();
//        node = cellToId[x][y];
//        System.out.println(idToCell[node]);

        for (int i = 1; i < bottlenecks.size(); i++)
        {
            System.out.print("bottlenecks of size " + i + " ==> ");
            for (Integer bottleneck : bottlenecks.get(i))
            {
                System.out.print(idToCell[bottleneck] + ", ");
            }
            System.out.println();
        }
    }

//    public void printGrid()
//    {
//        int node = 0;
//        for (int row = 0; row < height; row++)
//        {
//            for (int col = 0; col < width; col++)
//            {
////                System.out.print(Colour.getBackgroundFromOrdinal(colours[node].ordinal()) + "   ");
//
//                if (colours[node] == NO_COLOUR_VALUE)
//                {
//                    System.out.print("\u001B[0m   ");
//                }
//                else
//                {
//                    System.out.print(Colour.getBackgroundFromOrdinal(colours[node]) + "   ");
//                }
////                System.out.println("\u001B[0m");
//                node++;
//            }
//            System.out.println("\u001B[0m");
//        }
//    }

    public void printGrid()
    {
//        System.out.println(paths.size());
        int node;
        for (int row = 0; row < height; row++)
        {
            for (int col = 0; col < width; col++)
            {
                node = cellToId[col][row];
                if (colours[node] == NO_COLOUR_VALUE)
                {
                    System.out.print("\u001B[0m   ");
                }
                else
                {
                    System.out.print(Colour.getBackgroundFromOrdinal(colours[node]) + "   ");
                }
            }
            System.out.println("\u001B[0m");
        }
    }
}
