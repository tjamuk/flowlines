package tests;

import model.Cell;
import model.Solver;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SolverTest {

    @Test
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
}
