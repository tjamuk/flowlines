package model;

import java.util.*;

public class Game {

    /**
     * Iterated over to find every potential neighbour of a node.
     * e.g. node = (5,5) ---> (5,5) + (-1,0) = (4,5) -> (5,5) + (1,0) = (6,5) -> ...
     */
    private static final Cell[] addendsToFindNeighbours = {
            new Cell(-1, 0),
            new Cell(1, 0),
            new Cell(0, 1),
            new Cell(0, -1)
    };

    /**
     * The number of columns.
     */
    protected static int width;

    /**
     * The number of rows.
     */
    protected static int height;
    protected static int size;

    /**
     * Each element at a node represents the equivalent cell of the node.
     */
    protected static Cell[] idToCell;

    /**
     * Each element at a cell represents the equivalent node of the cell.
     */
    protected static int[][] cellToId;

    /**
     * 2 Arraylists representing a 2D array.
     * Represents the edges with an adjacency list
     * edges.get(column).get(row) returns a list of the neighbours of the cell at (column,row).
     * (not an array due to Pair<A> being a generic and generic array creation.)
     */
    protected static ArrayList<Set<Integer>> edges;

    /**
     * Adds the edges.
     */
    protected void addEdges() {
        Game.edges = new ArrayList<>(size);
        Cell cell;
        int neighbourCol;
        int neighbourRow;

        for (int id = 0; id < size; id++) {
            Game.edges.add(new HashSet<>());
            cell = Game.idToCell[id];
            for (Cell addendPair : addendsToFindNeighbours) {
                neighbourCol = cell.getCol() + addendPair.getCol();
                neighbourRow = cell.getRow() + addendPair.getRow();

                if (isNodeInGrid(neighbourCol, neighbourRow)) {
                    Game.edges.get(id).add(Game.cellToId[neighbourCol][neighbourRow]); //if valid neighbour, add to hashset.
                }
            }
        }
    }

    /**
     * Constructor.
     * @param width the number of columns in the grid.
     * @param height the number of rows in the grid.
     * @param isShuffling - if idToCell will be shuffled (happens for puzzle generation)
     */
    public Game(int width, int height, boolean isShuffling)
    {
        Game.width = width;
        Game.height = height;
        Game.size = width*height;

        Game.idToCell = new Cell[Game.size];
        Game.cellToId = new int[width][height];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Game.idToCell[row*width+col] = new Cell(col, row);
            }
        }

        if (isShuffling)
        {
            Collections.shuffle(Arrays.asList(Game.idToCell));
        }

        Cell cell;
        for (int id = 0; id < Game.size; id++) {
            cell = Game.idToCell[id];
            Game.cellToId[cell.getCol()][cell.getRow()] = id;
        }

        addEdges();
    }

    /**
     * Given 2 adjacent cells, finds the next cell going in the same direction.
     * @param first - the front of a path
     * @param second - the 2nd front (behind first) of the path
     * @return the straight on node.
     */
    protected int getStraightOnNode(Cell first, Cell second)
    {
        int col = 2*first.getCol() - second.getCol();
        int row = 2*first.getRow() - second.getRow();

        return (isNodeInGrid(col, row))? Game.cellToId[col][row] : PuzzleGenerator.NULL_INT_VALUE;
    }

    /**
     * Checks whether a given cell is in the grid.
     * @param col - column of the node in the grid
     * @param row - row of the node in the grid
     * @return true = node is in the grid.
     */
    protected boolean isNodeInGrid(int col, int row) {
        return (
                col >= 0 &&
                        col < width &&
                        row >= 0 &&
                        row < height
        );
    }
}
