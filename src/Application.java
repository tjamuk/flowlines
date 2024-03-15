import model.PuzzleGenerator;

public class Application {
    public static void main(String[] args)
    {
        model.PuzzleGenerator pg = new model.PuzzleGenerator(6, 6);
        pg.generatePuzzle();
        pg.outputPaths();
    }
}
