package entrants.pacman.Alex;

import pacman.game.Constants;
import pacman.game.internal.Ghost;


public class GhostInfo {
    public Constants.MOVE ghostsMove ;
    public   int ghostsIndex ;
    public boolean insightGhosts ;
    public int timeObserved;
    public int deltaTime;

    public GhostInfo(){
        this.insightGhosts = false;
        ghostsIndex = -1;
        ghostsMove = null;
        timeObserved = 0;
        deltaTime = Integer.MAX_VALUE/2;
    }

    public void SetGhostMove(Constants.MOVE move){ this.ghostsMove=move; }
}
