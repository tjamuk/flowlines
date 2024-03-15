package model;

import java.util.*;

public class Solver extends Game {

    /**
     * The number of coloured goal pairs.
     */
    int colourCount;

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
    Colour[] colours;

    /**
     * An arraylist of goals
     * (not an array due to Pair<A> being a generic and generic array creation.)
     */
    int[] startGoals;
    int[] endGoals;

    /**
     * Only constructor for model.Game that initialises everything.
     *
     * @param width the number of columns in the grid.
     * @param height the number of rows in the grid.
//     * @param goals An array list of start,end goal node pairs. Where each node is a column,row pair.
     */
    public Solver(int width, int height, ArrayList<Cell> startGoals, ArrayList<Cell> endGoals)
    {
        super(width, height, false);

        colours = new Colour[width*height];

        colourCount = startGoals.size();

        Cell cell;

        this.startGoals = new int[colourCount];
        this.endGoals = new int[colourCount];

        for (int colour = 0; colour < colourCount; colour++)
        {
            cell = startGoals.get(colour);
            this.startGoals[colour] = Game.cellToId[cell.getCol()][cell.getRow()];

            cell = endGoals.get(colour);
            this.endGoals[colour] = Game.cellToId[cell.getCol()][cell.getRow()];
        }

        Arrays.fill(colours, Colour.NONE);

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
            colours[startGoals[goalPairIndex]] = Colour.getEnumFromOrdinal(goalPairIndex);
            colours[endGoals[goalPairIndex]] = Colour.getEnumFromOrdinal(goalPairIndex);
        }
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
                    }
                }

                //For every neighbour of the front of the path...
                //If it's not the straightOn node nor filled, add.
                for (int n : edges.get(current))
                {
                    if (colours[n] == Colour.NONE && n!=next)
                    {
                        neighbours.add(n);
                    }
                }

                //After all other neighbours have been added, if straightOn node exists and is unfilled, add (to front of stack)
                if (next != -1)
                {
                    if (colours[next] == Colour.NONE)
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
                    colours[next] = Colour.getEnumFromOrdinal(colour);
                    lastCells[colour] = current;

                    if (solve())
                    {
                        return true;
                    }
                    else
                    {
                        paths.get(colour).removeFirst();
                        colours[next] = Colour.NONE;
                        lastCells[colour] = prev;
                    }
                }
                return false;
            }
        }
        return true;
    }

    public void printGrid()
    {
        int node = 0;
        for (int row = 0; row < height; row++)
        {
            for (int col = 0; col < width; col++)
            {
                System.out.print(Colour.getBackgroundFromOrdinal(colours[node].ordinal()) + "   ");
                node++;
            }
            System.out.println("\u001B[0m");
        }
    }
}
