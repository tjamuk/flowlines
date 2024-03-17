import model.PuzzleGenerator;

import java.util.LinkedList;

public class Application {
    public static void main(String[] args) {
        model.PuzzleGenerator pg = new model.PuzzleGenerator(6, 6);
        pg.generatePuzzle();
        pg.outputPaths();

//        LinkedList<LinkedList<Integer>> test = new LinkedList<>();
//        for (int x = 0; x < 3; x++) {
//            test.addFirst(new LinkedList<>());
//            for (int y = 1; y < 11; y++) {
//                test.getFirst().addFirst(y + x * 10);
//            }
//        }
//
//        for (LinkedList<Integer> list : test) {
//            System.out.println(list);
//        }
    }
}
