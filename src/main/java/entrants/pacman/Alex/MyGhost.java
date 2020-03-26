package entrants.pacman.Alex;

import pacman.game.Constants;
import pacman.game.Constants.MOVE;

import java.util.BitSet;
import java.util.EnumMap;

/**
 * Created by ricky on 4/4/17.
 */
public class MyGhost {
    private int nodeIndex;
    private MOVE lastMoveMade;
    private int observedTime;
    private boolean isEditable;
    private boolean isLair;
    private int lairEndTime;

    public MyGhost(int nodeIndex, MOVE lastMoveMade, int observedTime, boolean isEditable, boolean isLair, int lairEndTime) {
        this.nodeIndex = nodeIndex;
        this.lastMoveMade = lastMoveMade;
        this.observedTime = observedTime;
        this.isEditable = isEditable;
        this.isLair = isLair;
        this.lairEndTime = lairEndTime;
    }

    public int getNodeIndex() {
        return nodeIndex;
    }

    public void setNodeIndex(int nodeIndex) {
        this.nodeIndex = nodeIndex;
    }

    public MOVE getLastMoveMade() {
        return lastMoveMade;
    }

    public void setLastMoveMade(MOVE lastMoveMade) {
        this.lastMoveMade = lastMoveMade;
    }

    public int getObservedTime() {
        return observedTime;
    }

    public void setObservedTime(int observedTime) {
        this.observedTime = observedTime;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    public boolean isLair() {
        return isLair;
    }

    public void setLair(boolean lair) {
        isLair = lair;
    }

    public int getLairEndTime() {
        return lairEndTime;
    }

    public void setLairEndTime(int lairEndTime) {
        this.lairEndTime = lairEndTime;
    }
}
