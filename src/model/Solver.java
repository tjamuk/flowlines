package model;

import java.util.*;

public class Solver extends Game {

    /**
     * The number of coloured goal pairs.
     */
    int colourCount;

    public int componentCount;
    public int[] nodeToComponent;
    public ArrayList<Set<Integer>> componentGoals;
    public ArrayList<Integer> componentSizes;
    public ArrayList<Set<Integer>> componentColours;

    boolean[] areColoursPossibleToConnect;
    boolean[] nodeToIsBottleneck;
    int[] goalColours;
    boolean[] visited;
    Set<Integer> starts;
    Set<Integer> ends;

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

                printGrid();
                System.out.println("------------------");

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
                    System.out.println(idToCell[next]);

                    if (findConnectedComponents(colour) && !checkForBottleneck(next) && solve())
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
        componentSizes = new ArrayList<>();

        for (int node = 0; node < Game.size; node++) {
            if (nodeToComponent[node] == NO_COLOUR_VALUE && colours[node] == NO_COLOUR_VALUE) {
                componentGoals.add(new HashSet<>());
                componentColours.add(new HashSet<>());
                componentSizes.add(0);
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
        nodeToComponent[node] = componentCount;
        componentSizes.set(componentCount, componentSizes.get(componentCount)+1);
//        System.out.println(idToCell[node] + " in component " + componentCount + ". Size now = " + componentSizes.get(componentCount));
        for (int neighbour : Game.getEdges(node))
        {
            if (colours[neighbour] == NO_COLOUR_VALUE)
            {
                if (nodeToComponent[neighbour] == NO_COLOUR_VALUE)
                {
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

    public boolean aFunc(int x, int y)
    {
        return checkForBottleneck(cellToId[x][y]);
    }

    //int x, int y, int component
    public boolean checkForBottleneck(int node)
    {
//        for (int i = 0; i < componentCount; i++)
//        {
//            System.out.print(i + " = ");
//            for (int goal : componentGoals.get(i))
//            {
//                System.out.print(idToCell[goal] + ", ");
//            }
//            System.out.println();
//        }

        int component = -999;
        for (int neighbour : Game.getEdges(node))
        {
            if (nodeToComponent[neighbour] != -1)
            {
                if (component == -999)
                {
                    component = nodeToComponent[neighbour];
                }
                else if (component != nodeToComponent[neighbour])
                {
                    System.out.println("not bottleneck");
                    return false;
                }
            }
        }
        if (component == -999) {System.out.println("not bottleneck");return false;}

        int count = 0;
        boolean isNodeOnlyInOneComponent;
        for (int colour = 0; colour < colourCount; colour++)
        {
            if (
                    !isColourDone(colour) &&
                    componentGoals.get(component).contains(startGoals[colour]) &&
                    componentGoals.get(component).contains(endGoals[colour])
            )
            {
                isNodeOnlyInOneComponent = true;
                for (int neighbour : Game.getEdges(paths.get(colour).getFirst()))
                {
                    if (!(nodeToComponent[neighbour] == component || nodeToComponent[neighbour] == -1))
                    {
                        isNodeOnlyInOneComponent = false;
                        break;
                    }
                }
                if (isNodeOnlyInOneComponent)
                {
                    count++;
                }
            }
        }
        if (count > 1)
        {
            return isBottleneck(node, component);
        }
        else
        {
            System.out.println("not bottleneck");
            return false;
        }
    }

    public boolean isBottleneck(int node, int component)
    {
        //smaller component must have 1 goal.
        int count;
        boolean a = false;
        for (int neighbour : Game.getEdges(node)) //for every neighbour of node
        {
            if (colours[neighbour] == NO_COLOUR_VALUE)
            {
                visited = new boolean[size];
                visited[neighbour] = true;
                for (int start : Game.getEdges(neighbour))
                {
                    if (colours[start] == NO_COLOUR_VALUE)
                    {
                        starts = new HashSet<>(); ends = new HashSet<>();
                        System.out.println("\n\n\nSearching from " + idToCell[start]);
                        count = simpleDfs(start);
//                        System.out.println(count + " != " + (componentSizes.get(component)-1));
                        if (count != componentSizes.get(component)-1)
                        {
                            System.out.println("bottleneck");
                            System.out.println(starts);
                            System.out.println(ends);
                            return isNewComponentGood(component); //true
                        }
                        break;
                    }
                }

            }
        }
        System.out.println("not bottleneck");
        return false;
    }

    public boolean isNewComponentGood(int originalComponent)
    {
        boolean isNodeOnlyInOneComponent;
        int count = 0;
        int component;

        System.out.print("Start = ");
        for (int startGoalColour : starts)
        {
            System.out.print(idToCell[paths.get(startGoalColour).getFirst()]);
            System.out.print(", ");
        }
        System.out.print(" ||| End  = ");
        for (int endGoal : ends)
        {
            System.out.print(idToCell[endGoal]);
            System.out.print(", ");
        }
        System.out.println();

        for (int startGoalColour : starts)
        {
            isNodeOnlyInOneComponent = true;
            component = -999;
            for (int neighbour : Game.getEdges(paths.get(startGoalColour).getFirst()))
            {
                if (nodeToComponent[neighbour] != -1)
                {
                    if (component == -999)
                    {
                        component = nodeToComponent[neighbour];
                    }
                    else if (component != nodeToComponent[neighbour])
                    {
                        isNodeOnlyInOneComponent = false;
                    }
                }
            }

            if (component != -999 && isNodeOnlyInOneComponent)
            {
                if (componentGoals.get(originalComponent).contains(endGoals[startGoalColour])) //paths.get(startGoalColour).getFirst()
                {
                    if (!ends.remove(startGoalColour))
                    {
                        count++;
                        System.out.println("count = " + count + ". | because for start " + idToCell[paths.get(startGoalColour).getFirst()] + ", end " + idToCell[endGoals[startGoalColour]] + " is not in this component");
                    }
                }
            }
        }
        for (int endGoalColour : ends)
        {
            isNodeOnlyInOneComponent = true;
            component = -999;
            for (int neighbour : Game.getEdges(endGoals[endGoalColour]))
            {
                if (nodeToComponent[neighbour] != -1)
                {
                    if (component == -999)
                    {
                        component = nodeToComponent[neighbour];
                    }
                    else if (component != nodeToComponent[neighbour])
                    {
                        isNodeOnlyInOneComponent = false;
                    }
                }
            }

            if (component != -999 && isNodeOnlyInOneComponent)
            {
                if (componentGoals.get(originalComponent).contains(paths.get(endGoalColour).getFirst()))
                {
                    if (!starts.remove(endGoalColour))
                    {
                        count++;
                        System.out.println("count = " + count + ". | because for end " + idToCell[endGoals[endGoalColour]] + ", start " + idToCell[paths.get(endGoalColour).getFirst()] + " is not in this component");
                    }
                }
            }
        }

        return (count>1);
    }

    public int simpleDfs(int node)
    {
        visited[node] = true;
        int count = 1;
        for (int neighbour : Game.getEdges(node))
        {
            if (colours[neighbour] == NO_COLOUR_VALUE)
            {
                if (!visited[neighbour])
                {
                    count += simpleDfs(neighbour);
                }
            }
            else
            {
                int colour = colours[neighbour];
                if (colour != 999)
                {

                    if (neighbour == paths.get(colour).getFirst())
                    {
                        starts.add(colour);
                        System.out.println("adding start " + idToCell[neighbour]);
                        System.out.println(starts);
                    }
                    else if (neighbour == endGoals[colour])
                    {
                        ends.add(colour);
                        System.out.println("adding end " + idToCell[neighbour]);
                        System.out.println(ends);
                    }
                }
            }
        }
        return count;
    }

//    public boolean findBottlenecks(int node, ArrayList<Integer> addedBottlenecks) //node = most recently added node.
//    {
//        int xNode = idToCell[node].getCol(), yNode = idToCell[node].getRow();
//        int xNeighbour; int yNeighbour; int neighbour;
//
//        for (int direction = 0; direction < ADDENDS_LENGTH; direction++) //in every direction (for every neighbour), checks for bottlenecks.
//        {
//            xNeighbour = xNode + ADDENDS_TO_FIND_NEIGHBOURS[direction].getCol();
//            yNeighbour = yNode + ADDENDS_TO_FIND_NEIGHBOURS[direction].getRow();
//
//            if (isNodeInGrid(xNeighbour, yNeighbour))
//            {
//                neighbour = cellToId[xNeighbour][yNeighbour];
//                System.out.println(idToCell[neighbour]); System.out.println(nodeToComponent[neighbour]); System.out.println();
//                isBottleneck(
//                        neighbour,
//                        componentGoals.get(nodeToComponent[neighbour]).size(),
//                        direction,
//                        0
//                );
//            }
//        }
//
//        if (nodeToIsBottleneck)
//    }

    public int getComponent(int node)
    {
        return 0;
    }

//    public boolean isBottleneck(int cell, int goalCount, int direction, int current)
//    {
//        int xCell = idToCell[cell].getCol();
//        int yCell = idToCell[cell].getRow();
//        int x1; int y1; int x2; int y2; int direction1; int direction2; int xOther; int yOther; //int other
//
//        boolean isBottleneck = false;
//
//        Set<Integer> b = new HashSet<>();
//
//        if (direction < 2)
//        {
//            direction1 = 2;
//            direction2 = 3;
////            other = (direction==0)? 1 : 0;
//        }
//        else
//        {
//            direction1 = 0;
//            direction2 = 1;
////            other = (direction==2)? 3 : 2;
//        }
//
//        while (true)
//        {
//            x1 = xCell + ADDENDS_TO_FIND_NEIGHBOURS[direction1].getCol();
//            y1 = yCell + ADDENDS_TO_FIND_NEIGHBOURS[direction1].getRow();
//
//            x2 = xCell + ADDENDS_TO_FIND_NEIGHBOURS[direction2].getCol();
//            y2 = yCell + ADDENDS_TO_FIND_NEIGHBOURS[direction2].getRow();
//
//            if (
//                    !isNodeInGrid(x1, y1) ||
//                            !isNodeInGrid(x2, y2) ||
//                            colours[cellToId[x1][y1]] != NO_COLOUR_VALUE ||
//                            colours[cellToId[x2][y2]] != NO_COLOUR_VALUE
//            )
//            {
//                break;
//            }
//
//            xOther = xCell + ADDENDS_TO_FIND_NEIGHBOURS[direction].getCol();
//            yOther = yCell + ADDENDS_TO_FIND_NEIGHBOURS[direction].getRow();
//
//            if (!isNodeInGrid(xOther, yOther) || colours[cellToId[xOther][yOther]] != NO_COLOUR_VALUE)
//            {
//                b.add(cell);
//                isBottleneck = true;
//                break;
//            }
//            else if (current != goalCount)
//            {
//                b.add(cell);
//                //continue
//                xCell = xOther;
//                yCell = yOther;
//                cell = cellToId[xOther][yOther];
//                current++;
//            }
//            else
//            {
//                break;
//            }
//        }
//
//        if (isBottleneck)
//        {
//            while (bottlenecks.size() < b.size() + 1)
//            {
//                bottlenecks.add(new HashSet<>());
//            }
//
//            bottlenecks.get(b.size()).addAll(b);
//            return true;
//        }
//        else
//        {
//            return false;
//        }
//
//    }

    public void addPath(Cell[] path)
    {
        int node; int x; int y;

//        bottlenecks = new ArrayList<>();
//        nodeToBottleneckSize = new int[size];
//        Arrays.fill(nodeToBottleneckSize, NO_COLOUR_VALUE);

        for (Cell cell : path)
        {
            x = cell.getCol();
            y = cell.getRow();
            node = cellToId[x][y];
            colours[node] = 999;
//            findConnectedComponents(node);
//            findBottlenecks(node, new ArrayList<>());
        }

//        for (int i = 1; i < bottlenecks.size(); i++)
//        {
//            System.out.print("bottlenecks of size " + i + " ==> ");
//            for (Integer bottleneck : bottlenecks.get(i))
//            {
//                System.out.print(idToCell[bottleneck] + ", ");
//            }
//            System.out.println();
//        }
    }

    public void printGrid()
    {
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

    public void printComponents()
    {
        int node;
        for (int row = 0; row < height; row++)
        {
            for (int col = 0; col < width; col++)
            {
                node = cellToId[col][row];
                System.out.println(idToCell[node] + " = " + nodeToComponent[node]);
            }
            System.out.println("\u001B[0m");
        }
    }
}
