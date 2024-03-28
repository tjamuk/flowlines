public class Application {
    public static void main(String[] args)
    {
        model.PuzzleGenerator pg = new model.PuzzleGenerator(10, 10);
        pg.generatePuzzle();
        pg.outputPaths();
    }
}
