package entrants.pacman.Alex;


import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 * Created by ricky on 4/16/17.
 */
public class ShortestPathPrediction {
    private ArrayList<MOVE> predictionMovesToBeBlocked = new ArrayList<>();
    private int numberOfPredictionNode = 3;
    private int[] pathDisplay;
    Random random = new Random();
    private final int upIndex = 0, downIndex = 1, leftIndex = 2, rightIndex = 3;
    private final int redIndex = 0, pinkIndex = 1, cyanIndex = 2, orangeIndex = 3;
    //private int numberOfPrediction;


    public void execute(Game game, int currentLevelTime, ArrayList<MyGhost> ghostArrayList, int row, int[][] editableTimeRecord, int[][] pacmanLOSPaths) {
        for (MyGhost aGhostArrayList : ghostArrayList) {
            int[] nextNodes = new int[3];
            int ghostLocation = aGhostArrayList.getNodeIndex();
            boolean isEditable = aGhostArrayList.isEditable();
            if (ghostLocation >= 0 && !isEditable) {
                //how many game state has passed?
                int steps = currentLevelTime - aGhostArrayList.getObservedTime();
                //int[][] paths = losLineSearching.findPaths(game, current);
                if (row > 0) {
                    //row = which direction Ms.Pac-Man facing
                    //pacmanLOSPaths is a 2d array, which stores 3 node indices of direction row indicates
                    System.arraycopy(pacmanLOSPaths[row], 0, nextNodes, 0, numberOfPredictionNode);
                    //nextNodes length = 3
                    //check next 3 node of pacman of the direction passing in
                    for (int i = 0; i < nextNodes.length; i++) {
                        //if next i node index exist
                        if (nextNodes[i] > 0) {
                            ArrayList<Integer> shortestPathList = new ArrayList<Integer>();
                            //indices for the corresponding ghost to go to the next i node of Ms.Pacman
                            int[] tempShortestPath = game.getShortestPath(ghostLocation, nextNodes[i]);
                            for (int node : tempShortestPath) {
                                //Add indices of predicted ghost path to a list
                                shortestPathList.add(node);
                            }

                            //shortest path adjustment according to editable time
                            int time = aGhostArrayList.getObservedTime();
                            //4 ghost
                            for (int m = 0; m < 4; m++) {
                                for (int j = 0; j < shortestPathList.size(); j++) {
                                    //if within edible time
                                    if (time + j <= editableTimeRecord[m][1] & time + j >= editableTimeRecord[m][0]) {
                                        //?
                                        if (j % 2 == 0) {
                                            shortestPathList.remove(j);
                                        }
                                    }
                                }
                            }

                            int[] shortestPath = new int[shortestPathList.size()];
                            for (int k = 0; k < shortestPathList.size(); k++) {
                                shortestPath[k] = shortestPathList.get(k);
                            }

                            //i affecte by next y node index
                            // 0 <= i <= 3
                            //Each element of shotestPath equals to one game state
                            //steps + i is for finding out within next 3 node of direction ROW, which one will Ms.Pacn-Man encounter a ghost
                            if (steps + i == shortestPath.length) {
                                //prediction say that ghost will go to your prediction nodes
                                pathDisplay = shortestPath;
                                predictionMovesToBeBlocked.add(game.getPacmanLastMoveMade());
                                //numberOfPrediction++;
                                //System.out.println("Prediction : " + numberOfPrediction + " times.");

                            }
                        }
                    }
                }
            }
        }
    }

    public int getDirectionIndex(MOVE lastMoveMade) {
        switch (lastMoveMade) {
            case UP:
                return upIndex;
            case DOWN:
                return downIndex;
            case LEFT:
                return leftIndex;
            case RIGHT:
                return rightIndex;
            default:
                return -2;
        }
    }


    private int xyToNodeIndex(Game game, int x, int y) {
        //translate x and y index to node index
        for (int i = 0; i < game.getNumberOfNodes() - 2; i++) {
            if (game.getNodeXCood(i) == x && game.getNodeYCood(i) == y) {
                return i;
            }
        }
        return -2;
    }

    public int getEndNodeIndexWithSteps(Game game, int currentNode, int steps) {
        int endNode = currentNode;
        int numberOfTempNode = 0;
        MOVE[] tempLastMoveMades = game.getPossibleMoves(currentNode);
        MOVE tempLastMoveMade = tempLastMoveMades[random.nextInt(tempLastMoveMades.length)];

        while (numberOfTempNode == steps) {
            if (endNode >= 0) {
                if (game.isJunction(endNode) || game.getNeighbour(endNode, tempLastMoveMade) == -1) {
                    //the node index is in junction
                    int[] move = game.getNeighbouringNodes(endNode, tempLastMoveMade);
                    if (move != null) {
                        int nextMove = move[random.nextInt(move.length)];
                        tempLastMoveMade = game.getMoveToMakeToReachDirectNeighbour(endNode, nextMove);
                        endNode = nextMove;
                        numberOfTempNode++;
                    }
                } else {
                    //the node index is 2-way road
                    endNode = game.getNeighbour(endNode, tempLastMoveMade);
                    numberOfTempNode++;
                }
            }
        }

        return endNode;
    }

    public int getGhostColorIndex(GHOST ghost) {
        switch (ghost) {
            case BLINKY:
                return redIndex;
            case INKY:
                return cyanIndex;
            case PINKY:
                return pinkIndex;
            case SUE:
                return orangeIndex;
        }
        return -1;
    }

    public ArrayList<MOVE> realTimeBlockedDuplicatesRemove(ArrayList<MOVE> arrayList) {
        ArrayList<MOVE> result = new ArrayList<>();
        HashSet<MOVE> set = new HashSet<MOVE>();
        //Collections.reverse(arrayList);

        for (MOVE move : arrayList) {
            if (!set.contains(move)) {
                set.add(move);
                result.add(move);
            }
        }
        return result;
    }

    public ShortestPathPrediction() {
    }

    public ArrayList<MOVE> getPredictionMovesToBeBlocked() {
        HashSet<MOVE> set = new HashSet<MOVE>(predictionMovesToBeBlocked);
        ArrayList<MOVE> moves = new ArrayList<>();
        moves.addAll(set);
        return moves;
    }

    public int[] getPathDisplay() {
        return pathDisplay;
    }
}


