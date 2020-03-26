package entrants.pacman.Alex;

import pacman.game.Constants.MOVE;

/**
 * Created by ricky on 3/5/17.
 */
public class VirtualGhost {
    private int nodeIndex;
    private MOVE lastMoveMade;
    private int lastObservedTime;
    private boolean isInitialized;
    private boolean isEditable;
    private int editableEndTime;
    private boolean isLairGhost;
    private int lairEndTime;

    public VirtualGhost() {
    }


    public VirtualGhost(int nodeIndex, MOVE lastMoveMade, int lastObservedTime, boolean isInitialized, boolean isEditable, int editableEndTime, boolean isLairGhost, int lairEndTime) {
        this.nodeIndex = nodeIndex;
        this.lastMoveMade = lastMoveMade;
        this.lastObservedTime = lastObservedTime;
        this.isInitialized = isInitialized;
        this.isEditable = isEditable;
        this.editableEndTime = editableEndTime;
        this.isLairGhost = isLairGhost;
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

    public int getLastObservedTime() {
        return lastObservedTime;
    }

    public void setLastObservedTime(int lastObservedTime) {
        this.lastObservedTime = lastObservedTime;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public void setInitialized(boolean initialized) {
        isInitialized = initialized;
    }

    public boolean isLairGhost() {
        return isLairGhost;
    }

    public void setLairGhost(boolean lairGhost) {
        isLairGhost = lairGhost;
    }

    public int getLairEndTime() {
        return lairEndTime;
    }

    public void setLairEndTime(int lairEndTime) {
        this.lairEndTime = lairEndTime;
    }


    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    public int getEditableEndTime() {
        return editableEndTime;
    }

    public void setEditableEndTime(int editableEndTime) {
        this.editableEndTime = editableEndTime;
    }
}
