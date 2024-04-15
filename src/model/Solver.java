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
    int[] goalColours;
    boolean[] visited;
    ArrayList<Set<Integer>> starts;
    ArrayList<Set<Integer>> ends;
    ArrayList<Integer> sizes;
    int newComponentsSize;

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

    static final int NULL_INT_VALUE = -1;

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

        colours = new int[width*height];

        colourCount = startGoalsList.size(); //the number of coloured pairs of goals.

        Cell cell;

        startGoals = new int[colourCount];
        endGoals = new int[colourCount];

        for (int colour = 0; colour < colourCount; colour++) //setups the start and end goals for all colours.
        {
            cell = startGoalsList.get(colour);
            startGoals[colour] = Game.cellToId[cell.getCol()][cell.getRow()];

            cell = endGoalsList.get(colour);
            endGoals[colour] = Game.cellToId[cell.getCol()][cell.getRow()];
        }

        Arrays.fill(colours, NULL_INT_VALUE); //all cells in the grid are unfilled.

        addGoalsToGrid(); //adds goals to the grid (fills in the cells with the colours of the goals)
        initialisePaths(); //adds the start goal to the paths.
    }

    /**
     * A constructor for when there are already goals, width and height set.
     */
    public Solver()
    {
        colourCount = Game.startGoals.length;

        colours = new int[width*height];
        Arrays.fill(colours, NULL_INT_VALUE);
        addGoalsToGrid();
        initialisePaths();
    }

    /**
     * Adds the coloured goals (found in int[] startGoals, endGoals) to Colour[] colours.
     */
    private void addGoalsToGrid()
    {
        for (int goalPairIndex = 0; goalPairIndex < colourCount; goalPairIndex++) //for every pair of goals...
        {
            colours[startGoals[goalPairIndex]] = goalPairIndex;
            colours[endGoals[goalPairIndex]] = goalPairIndex;
        }
        goalColours = colours.clone();
    }

    /**
     * Adds the start goals to the paths.
     *
     * For each colour's path, add the colour's start goal to it.
     */
    private void initialisePaths()
    {
        lastCells = new int[colourCount]; //basically
        Arrays.fill(lastCells, -1);

        paths = new ArrayList<>();

        for (int index = 0; index < colourCount; index++) //for each colour's path, add the colour's start goal to it.
        {
            paths.add(new LinkedList<>());
            paths.get(index).addFirst(startGoals[index]);
        }
    }

    /**
     * Returns whether there is a path connecting a colour's start and end goals.
     */
    private boolean isColourNotDone(int colour)
    {
        return !paths.get(colour).getFirst().equals(endGoals[colour]);
    }

    /**
     * A recursive backtracking algorithm that solves the puzzle.
     * @return true = was able to find a solution; false = was not able.
     */
    public boolean solve()
    {
        int current; //the head/front of the most recent path.
        int prev; //the 2nd front of the most recent path.
        int next; //the next node to add to the path.
        Stack<Integer> neighbours;

        for (int colour = 0; colour < colourCount; colour++) //for every colour, check if it's path is done.
        {
            if (isColourNotDone(colour)) //if colour's path not completed.
            {

                printGrid();
                System.out.println("------------------");

                current = paths.get(colour).getFirst(); //1st front of colour's path.
                prev = getInPath(colour, 1); //2nd front of colour's #####path.lastCells[colour]####
                next = (prev == NULL_INT_VALUE)? NULL_INT_VALUE : getStraightOnNode(idToCell[current], idToCell[prev]); //if there is a previous front in path, assign 'next' to the straight on node. ELSE set to null.
                neighbours = new Stack<>();

                //if the new front of the path is next to the endGoal, can move onto the next colour.
                if (Game.edges.get(current).contains(endGoals[colour]))
                {
                    paths.get(colour).addFirst(endGoals[colour]); //add end goal onto path (completing it)
                    if (solve()) //move onto next colour. IF solution from this point -> return true. ELSE (no solution), remove end goal and return false (backtrack)
                    {
                        return true;
                    }
                    else
                    {
                        paths.get(colour).removeFirst();
                        return false;
                    }
                }

                getVisitOrder(neighbours, current, next, colour); //get an ordered stack of neighbours to visit.

                while (!neighbours.isEmpty()) //add every neighbour.
                {
                    next = neighbours.pop();

                    paths.get(colour).addFirst(next); //add to path.
                    colours[next] = colour;

                    if (findConnectedComponents(colour) && !checkForBottleneck(next) && solve())
                    {
                        return true;
                    }
                    else
                    {
                        paths.get(colour).removeFirst();
                        colours[next] = NULL_INT_VALUE;
                    }
                }
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the order in which to visit the neighbours of the front of the current.
     * @param neighbours - the visit order stack.
     * @param frontNodeOfPath - the front/1st of the current path.
     * @param straightOnNode - the next node when going straight from front of path.
     * @param colour - the current colour path being worked on.
     */
    public void getVisitOrder(Stack<Integer> neighbours, int frontNodeOfPath, int straightOnNode, int colour)
    {
        int third = getInPath(colour, 2);

        //For every neighbour of the front of the path...
        for (int node : edges.get(frontNodeOfPath))
        {
            if (colours[node] == NULL_INT_VALUE && node != straightOnNode) //If the node is not filled NOR the straightOn node -> add.
            {
                if (third != NULL_INT_VALUE) //check to see if redundant node.
                {
                    if (edges.get(third).contains(node)) //check to see that node is redundant.
                    {
                        third = NULL_INT_VALUE;
                        continue;
                    }
                }
                neighbours.add(node);
            }
        }

        //After all other neighbours have been added, if straightOn node exists and is unfilled, add (to front of stack)
        if (straightOnNode != NULL_INT_VALUE)
        {
            if (colours[straightOnNode] == NULL_INT_VALUE)
            {
                neighbours.add(straightOnNode);
            }
        }
    }

    /**
     *
     * @param latestNodeColour - the colour of the node most recently added.
     * @return
     */
    public boolean findConnectedComponents(int latestNodeColour) {

        nodeToComponent = new int[Game.size];
        Arrays.fill(nodeToComponent, NULL_INT_VALUE);
        areColoursPossibleToConnect = new boolean[colourCount];
        componentCount = 0;
        componentGoals = new ArrayList<>();
        componentColours = new ArrayList<>();
        componentSizes = new ArrayList<>();

        for (int node = 0; node < Game.size; node++) {
            if (nodeToComponent[node] == NULL_INT_VALUE && colours[node] == NULL_INT_VALUE) {
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
            if (colours[neighbour] == NULL_INT_VALUE)
            {
                if (nodeToComponent[neighbour] == NULL_INT_VALUE)
                {
                    dfs(neighbour);
                }
            }
            else
            {
                int goalColour = goalColours[neighbour];
                if (goalColour != NULL_INT_VALUE)
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
            if (isColourNotDone(colour) && !areColoursPossibleToConnect[colour])
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
                    isColourNotDone(colour) &&
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
        starts = new ArrayList<>();
        ends = new ArrayList<>();
        sizes = new ArrayList<>();
        int count;
        newComponentsSize = 0;
        for (int neighbour : Game.getEdges(node)) //for every neighbour (possible bottleneck) of newly node
        {
            if (colours[neighbour] == NULL_INT_VALUE)
            {
                visited = new boolean[size];
                visited[neighbour] = true;
                for (int start : Game.getEdges(neighbour))
                {
                    if (colours[start] == NULL_INT_VALUE)
                    {
                        starts.add(new HashSet<>());
                        ends.add(new HashSet<>());
//                        System.out.println("\n\n\nSearching from " + idToCell[start]);
                        count = simpleDfs(start);
//                        System.out.println(count + " != " + (componentSizes.get(component)-1));
                        if (count != componentSizes.get(component)-1)
                        {
                            System.out.println("bottleneck");
//                            System.out.println(starts);
//                            System.out.println(ends);
                            sizes.add(count);
                            newComponentsSize++;
                            return isNewComponentGood(component, neighbour, start); //true
                        }
                        else
                        {
                            starts.remove(newComponentsSize);
                            ends.remove(newComponentsSize);
                        }
                        break;
                    }
                }

            }
        }
        System.out.println("not bottleneck");
        return false;
    }

    public boolean isNewComponentGood(int originalComponent, int bottleneckNode, int startNode)
    {
        int chosenComponent = 0;
        int chosenComponentSize = starts.get(0).size() + ends.get(0).size();
        int temp;
        for (int node : Game.getEdges(bottleneckNode))
        {
            if (colours[node] == NULL_INT_VALUE && node != startNode)
            {
                starts.add(new HashSet<>());
                ends.add(new HashSet<>());
                sizes.add(simpleDfs(node));
                temp = starts.get(newComponentsSize).size() + ends.get(newComponentsSize).size();
                if (temp < chosenComponentSize)
                {
                    chosenComponentSize = temp;
                    chosenComponent = starts.size()-1;
                }
                newComponentsSize++;
            }
        }

        boolean good;
        boolean isNodeOnlyInOneComponent;
        int count = 0;
        int component;
//
//        System.out.print("Start = ");
//        for (int startGoalColour : starts.get(chosenComponent))
//        {
//            System.out.print(idToCell[paths.get(startGoalColour).getFirst()]);
//            System.out.print(", ");
//        }
//        System.out.print(" ||| End  = ");
//        for (int endGoal : ends.get(chosenComponent))
//        {
//            System.out.print(idToCell[endGoal]);
//            System.out.print(", ");
//        }
//        System.out.println();

        for (int startGoalColour : starts.get(chosenComponent))
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
                    if (!ends.get(chosenComponent).remove(startGoalColour))
                    {
                        good = true;
                        for (int i = 0; i < starts.size(); i++)
                        {
                            if (i != chosenComponent && starts.get(i).contains(startGoalColour))
                            {
                                good = false;
                                break;
                            }
                        }
                        if (good)
                        {
                            count++;
                        }
//                        count++;
//                        System.out.println("count = " + count + ". | because for start " + idToCell[paths.get(startGoalColour).getFirst()] + ", end " + idToCell[endGoals[startGoalColour]] + " is not in this component");
                    }
                }
            }
        }
        for (int endGoalColour : ends.get(chosenComponent))
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
                    if (!starts.get(chosenComponent).remove(endGoalColour))
                    {
                        good = true;
                        for (int i = 0; i < ends.size(); i++)
                        {
                            if (i != chosenComponent && ends.get(i).contains(endGoalColour))
                            {
                                good = false;
                                break;
                            }
                        }
                        if (good)
                        {
                            count++;
                        }
//                        count++;
//                        System.out.println("count = " + count + ". | because for end " + idToCell[endGoals[endGoalColour]] + ", start " + idToCell[paths.get(endGoalColour).getFirst()] + " is not in this component");
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
            if (colours[neighbour] == NULL_INT_VALUE)
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
                        starts.get(newComponentsSize).add(colour);
//                        System.out.println("adding start " + idToCell[neighbour]);
//                        System.out.println(starts);
                    }
                    else if (neighbour == endGoals[colour])
                    {
                        ends.get(newComponentsSize).add(colour);
//                        System.out.println("adding end " + idToCell[neighbour]);
//                        System.out.println(ends);
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
                if (colours[node] == NULL_INT_VALUE)
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

    public int getInPath(int colour, int index)
    {
        return (paths.get(colour).size() > index) ? paths.get(colour).get(index) : NULL_INT_VALUE;
    }
}
