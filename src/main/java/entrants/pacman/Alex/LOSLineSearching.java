package entrants.pacman.Alex;

import pacman.game.Constants.MOVE;
import pacman.game.Game;

import java.util.Random;

/**
 * Created by ricky on 4/2/17.
 * <p>
 * aim : get the all the possible line of sight node indexes
 * and store it into an array.
 */
public class LOSLineSearching {
    Random random = new Random();
    private final int sightDistance = 3;
    private final int row = 4, col = sightDistance;
    private final int upIndex = 0, downIndex = 1, leftIndex = 2, rightIndex = 3;
    public int[][] losPaths = new int[row][col];
    public boolean[] isLastMoveMadeUpdated = new boolean[4];
    private int maxNumberOfNodes, maxX, maxY, minX, minY;


    public LOSLineSearching(Game game) {
        maxNumberOfNodes = game.getNumberOfNodes() - 2;
        maxX = game.getNodeXCood(maxNumberOfNodes);
        maxY = game.getNodeYCood(maxNumberOfNodes);
        minX = game.getNodeXCood(0);
        minY = game.getNodeYCood(0);
    }

    public void findPaths(Game game, int currentNodeIndex) {
        //int[] neighbouringNodes = game.getNeighbouringNodes(currentNodeIndex);
        MOVE[] moves = game.getPossibleMoves(currentNodeIndex);
        for (MOVE move : moves) {
            losPathUpdate(game, currentNodeIndex, move);
        }

        //return losPaths;
    }

    private void losPathUpdate(Game game, int currentNodeIndex, MOVE lastMoveMade) {
        //int[] losPath = new int[10];
        //MOVE tempDirection = game.getMoveToMakeToReachDirectNeighbour(currentNodeIndex, neighbouringNode);
        int neighbouringNode = game.getNeighbour(currentNodeIndex, lastMoveMade);

        switch (lastMoveMade) {
            case UP://up
                int colOfUp = 0;
                //3 nodes of direction UP
                for (int y = game.getNodeYCood(neighbouringNode); y > game.getNodeYCood(neighbouringNode) - sightDistance; y--) {
                    //Get index of the next y node
                    int nextNode = xyToNodeIndex(game, game.getNodeXCood(neighbouringNode), y);
                    //if y within the maze? and node index exist?
                    if (y > minY && y < maxY && nextNode > 0) {
                        //put the index to the 2d array, record what is the next y node index if they exist
                        losPaths[upIndex][colOfUp] = nextNode;
                        colOfUp++;
                    } else {
                        losPaths[upIndex][colOfUp] = -2;
                        colOfUp++;
                    }
                }
                break;
            case DOWN:
                int colOfDown = 0;
                //System.out.println("DOWN! See here.");
                for (int y = game.getNodeYCood(neighbouringNode); y < game.getNodeYCood(neighbouringNode) + sightDistance; y++) {
                    int nextNode = xyToNodeIndex(game, game.getNodeXCood(neighbouringNode), y);
                    if (y > minY && y < maxY && nextNode > 0) {
                        losPaths[downIndex][colOfDown] = nextNode;
                        colOfDown++;
                    } else {
                        losPaths[downIndex][colOfDown] = -2;
                        colOfDown++;
                    }
                }
                break;
            case LEFT://left
                int colOfLeft = 0;
                //System.out.println("LEFT! See here.");
                for (int x = game.getNodeXCood(neighbouringNode); x > game.getNodeXCood(neighbouringNode) - sightDistance; x--) {
                    int nextNode = xyToNodeIndex(game, x, game.getNodeYCood(neighbouringNode));
                    if (x > minX && x < maxX && nextNode > 0) {
                        losPaths[leftIndex][colOfLeft] = nextNode;
                        colOfLeft++;
                    } else {
                        losPaths[leftIndex][colOfLeft] = -2;
                        colOfLeft++;
                    }
                }
                break;
            case RIGHT://right
                int colOfRight = 0;
                //System.out.println("RIGHT! See here.");
                for (int x = game.getNodeXCood(neighbouringNode); x < game.getNodeXCood(neighbouringNode) + sightDistance; x++) {
                    int nextNode = xyToNodeIndex(game, x, game.getNodeYCood(neighbouringNode));
                    if (x > minX && x < maxX && nextNode > 0) {
                        losPaths[rightIndex][colOfRight] = nextNode;
                        colOfRight++;
                    } else {
                        losPaths[rightIndex][colOfRight] = -2;
                        colOfRight++;
                    }
                }
                break;
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
                    int nextMove = game.getNeighbour(endNode, tempLastMoveMade);
                    endNode = nextMove;
                    numberOfTempNode++;
                }
            }
        }

        return endNode;
    }


    public int[][] getLosPaths() {
        return losPaths;
    }


    public boolean[] getIsLastMoveMadeUpdated() {
        return isLastMoveMadeUpdated;
    }

}
