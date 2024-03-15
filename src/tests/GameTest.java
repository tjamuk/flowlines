package tests;

import com.sun.source.tree.UsesTree;
import model.Cell;
import model.Game;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

public class GameTest {

    @Test
    public void testCase_GameClass_TestingEdges()
    {
        Game game = new Game(6,6,true);

        Set<Integer> expected = new HashSet<>(Set.of(
                Game.cellToId[2][3],
                Game.cellToId[2][5],
                Game.cellToId[1][4],
                Game.cellToId[3][4]
        ));

        Assertions.assertEquals(expected, game.getNeighbours(Game.cellToId[2][4]));
    }

}
