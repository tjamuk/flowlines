package tests;

import model.Cell;
import model.Solver;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SolverTest {

    @Disabled
    public void testCase_SolverClass_SolvingExamplePuzzle()
    {
        boolean isSolvable;

        ArrayList<Cell> startGoals = new ArrayList<>();
        ArrayList<Cell> endGoals = new ArrayList<>();

        //red
        startGoals.add(new Cell(1,0)); //5,5
        endGoals.add(new Cell(1,2)); //2,5

        //blue
        startGoals.add(new Cell(1,1)); //1,0
        endGoals.add(new Cell(0,0)); //3,1

        //green
        startGoals.add(new Cell(0,5)); //0,5
        endGoals.add(new Cell(1,3)); //1,1

        //yellow
        startGoals.add(new Cell(4,1)); //4,1
        endGoals.add(new Cell(0,2)); //0,4

        Solver game = new Solver(
                6,
                6,
                startGoals,
                endGoals
        );

        game.printGrid();
        System.out.println();

        isSolvable = game.solve();

        if (isSolvable)
        {
            game.printGrid();
        }
        else
        {
            System.out.println("No solution");
        }

        assertTrue(isSolvable);
    }

    @Disabled
    public void testCase_SolverClass_SolvingExamplePuzzle_2()
    {
        boolean isSolvable;

        ArrayList<Cell> startGoals = new ArrayList<>();
        ArrayList<Cell> endGoals = new ArrayList<>();

        int width = 30;
        int height = 30;

        for (int col = 0; col < width; col++)
        {
            startGoals.add(new Cell(col,0));
            endGoals.add(new Cell(col,height-1));
        }

        Solver game = new Solver(
                width,
                height,
                startGoals,
                endGoals
        );

        isSolvable = game.solve();

        if (isSolvable)
        {
            System.out.println("Solution found.");
        }
        else
        {
            System.out.println("No solution");
        }

        assertTrue(isSolvable);
    }

    @Test
    public void testCase_SolverClass_SolvingExamplePuzzle_3()
    {
        boolean isSolvable;

        ArrayList<Cell> startGoals = new ArrayList<>();
        ArrayList<Cell> endGoals = new ArrayList<>();

        int width = 10;
        int height = 10;

        startGoals.add(new Cell(0,0));
        endGoals.add(new Cell(1,8));

        startGoals.add(new Cell(1,1));
        endGoals.add(new Cell(7,2));

        startGoals.add(new Cell(2,3));
        endGoals.add(new Cell(1,9));

        startGoals.add(new Cell(1,4));
        endGoals.add(new Cell(4,3));

        startGoals.add(new Cell(2,4));
        endGoals.add(new Cell(5,3));

        startGoals.add(new Cell(3,7));
        endGoals.add(new Cell(8,4));

        startGoals.add(new Cell(3,8));
        endGoals.add(new Cell(7,6));

        startGoals.add(new Cell(4,7));
        endGoals.add(new Cell(9,0));

        startGoals.add(new Cell(9,1));
        endGoals.add(new Cell(9,9));

        Solver game = new Solver(
                width,
                height,
                startGoals,
                endGoals
        );

        isSolvable = game.solve();

        if (isSolvable)
        {
            System.out.println("Solution found.");
        }
        else
        {
            System.out.println("No solution");
        }

        assertTrue(isSolvable);
    }
}
