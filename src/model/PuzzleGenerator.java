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
     * An array where each element represents the colour held by a node.
     */
    int[] colours;

    /**
     * Used for finding connected components where each element represents whether a node has been visited by the dfs.
     */
    int[] visited;

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
                for (int node : unvisited.parallelStream().toList())
                {
                    addNode(node, path.id);
                    if (calculateConnectedComponents(node))
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
                first = path.peek(); //the front of the path.
                second = path.peekSecond(); //2nd front of the path (behind first)
                ArrayList<Integer> neighbours = new ArrayList<>(); //an ordered sequence of neighbours to visit

                getValidNeighbours(neighbours, first, second);

                for (int neighbour : neighbours)
                {
                    addNode(neighbour, path.id);

                    //if the size of any connected components is less than 3...
                    //  (But if one is less than 3 need to check whether it's adjacent to the front of the most recent path )
                    if (calculateConnectedComponents(neighbour))
                    {
                        if (generatePuzzle())
                        {
                            return true;
                        }
                        removeNode(neighbour);
                        path.isAtDeadend = false;
                    }
                    else
                    {
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
        return true;
    }

    /**
     * In order, gets the valid neighbours of the front of the most recent path.
     * @param visitOrder - the order in which to visit the valid neighbours.
     * @param first - front of the most recent path.
     * @param second - 2nd to the front of the most recent path/
     */
    public void getValidNeighbours(ArrayList<Integer> visitOrder, int first, int second)
    {
        Set<Integer> neighbours = Game.edges.get(first);

        if (second != Path.NULL_VALUE)
        {
            neighbours.remove(second); //if there is a second, it will be a neighbour to the first so remove (as it's already been visited)
        }

        boolean hasFoundRedundantNode;

        for (int neighbour : neighbours)
        {
            if (unvisited.contains(neighbour))
            {
                hasFoundRedundantNode = false;

                //finds redundant nodes where for each neighbour, need to check if it could've been visited earlier in the path.
                for (int nn : Game.edges.get(neighbour))
                {
                    if (nn != first && paths.peek().contains(nn))
                    {
                            hasFoundRedundantNode = true;
                            break;
                    }
                }
                if (!hasFoundRedundantNode)
                {
                    visitOrder.add(neighbour);
                }
            }
        }

        reorderVisitOrder(visitOrder, first, second);
    }

    /**
     * Reorders the visit order of nodes.
     *
     * Decides if the path will be going straight or not and then reorders it accordingly.
     * @param visitOrder - the order in which to visit the valid neighbours.
     * @param first - front of the most recent path.
     * @param second - 2nd to the front of the most recent path/
     */
    private void reorderVisitOrder(ArrayList<Integer> visitOrder, int first, int second)
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
        }
    }

    /**
     * Removes a node.
     * @param node = the node to be removed.
     */
    private void removeNode(int node)
    {
        paths.peek().remove();
        unvisited.add(node);
        colours[node] = NO_COLOUR;
    }

    /**
     * Add a node.
     * @param node = the node to be added.
     */
    private void addNode(int node, int pathId)
    {
        paths.peek().add(node);
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
        System.out.println(paths.size());

        Colour[][] grid = new Colour[width][height];
        Cell cell;
        int colour = 0;

        while (!paths.isEmpty())
        {
            System.out.print("[");
            for (int node : paths.pop().getSequence())
            {
                cell = Game.idToCell[node];
                grid[cell.getCol()][cell.getRow()] = Colour.getEnumFromOrdinal(colour);
                System.out.print(Game.idToCell[node]);
                System.out.print(", ");
            }
            colour++;
            System.out.println("]");
        }

        for (int row = 0; row < height; row++)
        {
            for (int col = 0; col < width; col++)
            {
                System.out.print(Colour.getBackgroundFromOrdinal(grid[col][row].ordinal()) + "   ");
            }
            System.out.println("\u001B[0m");
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
        Set<Integer> latestNodeNeighbours = Game.edges.get(latestNode);
        int lastComponentIndex = 0;
        boolean isGood;

        visited = new int[Game.size];
        Arrays.fill(visited, NULL_INT_VALUE);
        components = new ArrayList<>();
        startPoints = new ArrayList<>();

        for (int vertex = 0; vertex < size; vertex++)
        {
            if (visited[vertex] == NULL_INT_VALUE && colours[vertex] == NO_COLOUR)
            {
                isGood = false;
                startPoints.add(new ArrayList<>());
                components.add(new HashSet<>());
                dfs(vertex, lastComponentIndex);

                if (components.get(lastComponentIndex).size() < 3)
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
        return true;
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

        for (int neighbour : Game.edges.get(vertexId))
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
}