import model.Cell;
import model.Solver;

import java.time.Duration;
import java.util.ArrayList;

public class Application {
    public static void main(String[] args)
    {
//        model.PuzzleGenerator pg = new model.PuzzleGenerator(10, 10);
//        pg.generatePuzzle();
//        //pg.outputPaths();
//        System.out.println(pg.postGeneration());
//
//        Solver solver = new Solver();
//        long startTime = System.nanoTime();
//        solver.solve();
//        System.out.println(Duration.ofNanos(System.nanoTime() - startTime).toNanos());
//        solver.printGrid();

        ArrayList<Cell> startGoals = new ArrayList<>();
        ArrayList<Cell> endGoals = new ArrayList<>();

        startGoals.add(new Cell(0,0));
        endGoals.add(new Cell(4,0));

        startGoals.add(new Cell(1,0));
        endGoals.add(new Cell(3,0));

        startGoals.add(new Cell(0,7));
        endGoals.add(new Cell(4,7));

        Solver solver = new Solver(5,8, startGoals, endGoals);

        Cell[] path = {
                new Cell(2,4),
                new Cell(2,5),
                new Cell(2,6),
        };

        solver.addPath(path);
    }
}
