import model.Cell;
import model.Game;
import model.Solver;

import java.time.Duration;
import java.util.ArrayList;

public class Application {
//    public static void main(String[] args)
//    {
////        model.PuzzleGenerator pg = new model.PuzzleGenerator(10, 10);
////        pg.generatePuzzle();
////        //pg.outputPaths();
////        System.out.println(pg.postGeneration());
////
////        Solver solver = new Solver();
////        long startTime = System.nanoTime();
////        solver.solve();
////        System.out.println(Duration.ofNanos(System.nanoTime() - startTime).toNanos());
////        solver.printGrid();
//
//        ArrayList<Cell> startGoals = new ArrayList<>();
//        ArrayList<Cell> endGoals = new ArrayList<>();
//
////        startGoals.add(new Cell(0,0));
////        endGoals.add(new Cell(4,0));
////
////        startGoals.add(new Cell(1,0));
////        endGoals.add(new Cell(3,0));
////
////        startGoals.add(new Cell(0,7));
////        endGoals.add(new Cell(4,7));
//
//        Cell[] path = {
//                new Cell(2,2),
//                new Cell(3,2),
//                new Cell(4,2),
//                new Cell(4,5),
//                new Cell(3,5),
//                new Cell(2,5),
//                new Cell(1,5),
//        };
//
//        startGoals.add(new Cell(4,0));
//        endGoals.add(new Cell(0,2));
//
//        startGoals.add(new Cell(4,1));
//        endGoals.add(new Cell(1,2));
//
//        startGoals.add(new Cell(4,3));
//        endGoals.add(new Cell(4,7));
//
//        startGoals.add(new Cell(4,4));
//        endGoals.add(new Cell(4,6));
//
////        startGoals.add(new Cell(0,3));
////        endGoals.add(new Cell(0,0));
//
//        Solver solver = new Solver(5,8, startGoals, endGoals); //5,8
//        solver.addPath(path);
//        solver.findConnectedComponents(2);
//        System.out.println(solver.componentCount);
//        solver.printComponents();
//        System.out.println(solver.aFunc(1,5));
////        System.out.println(solver.isBottleneck(1,));
//
//
//    }

    public static void main(String[] args)
    {

        ArrayList<Cell> startGoals = new ArrayList<>();
        ArrayList<Cell> endGoals = new ArrayList<>();

        Cell[] path = {
                new Cell(0,1),
                new Cell(0,2),
                new Cell(1,2),
                new Cell(2,2),
                new Cell(2,1),
                new Cell(3,1),
                new Cell(4,1),
                new Cell(5,1),
                new Cell(6,1),
                new Cell(6,2),
                new Cell(6,3),
                new Cell(6,4),
                new Cell(5,4),
                new Cell(5,5),
                new Cell(5,6),
                new Cell(4,6),
                new Cell(3,6),

        };

        startGoals.add(new Cell(0,0));
        endGoals.add(new Cell(1,8));

        startGoals.add(new Cell(1,1));
        endGoals.add(new Cell(7,2));

        startGoals.add(new Cell(2,3));
        endGoals.add(new Cell(1,9));

        startGoals.add(new Cell(1,4));
        endGoals.add(new Cell(4,3));

        startGoals.add(new Cell(5,3));
        endGoals.add(new Cell(2,4));

        startGoals.add(new Cell(3,7));
        endGoals.add(new Cell(8,4));

        startGoals.add(new Cell(9,0));
        endGoals.add(new Cell(5,7));

        startGoals.add(new Cell(9,1));
        endGoals.add(new Cell(9,9));

        startGoals.add(new Cell(3,8));
        endGoals.add(new Cell(7,6));

        Solver solver = new Solver(10,10, startGoals, endGoals); //5,8
        solver.addPath(path);
        solver.findConnectedComponents(0);
        System.out.println(solver.componentCount);
        solver.printComponents();
        System.out.println(solver.aFunc(3,6));

    }
}
