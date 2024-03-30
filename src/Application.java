import model.Solver;

import java.time.Duration;

public class Application {
    public static void main(String[] args)
    {
        model.PuzzleGenerator pg = new model.PuzzleGenerator(10, 10);
        pg.generatePuzzle();
        //pg.outputPaths();
        System.out.println(pg.postGeneration());

        Solver solver = new Solver();
        long startTime = System.nanoTime();
        solver.solve();
        System.out.println(Duration.ofNanos(System.nanoTime() - startTime).toNanos());
        solver.printGrid();
    }
}
