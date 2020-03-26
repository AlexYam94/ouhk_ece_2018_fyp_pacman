package entrants.pacman.Alex;

import pacman.game.Constants.MOVE;
import pacman.game.Game;

import java.util.Random;

/**
 * Created by ricky on 2/20/17.
 * random getMove
 */
public class RandomMove {

    private Random random = new Random();

    public MOVE getMove(Game game, int currentNodeIndex){

        MOVE[] moves = game.getPossibleMoves(currentNodeIndex, game.getPacmanLastMoveMade());
        if (moves.length > 0) {

            return moves[random.nextInt(moves.length)];
        }

        // Must be possible to turn around
        return game.getPacmanLastMoveMade().opposite();

    }

}
