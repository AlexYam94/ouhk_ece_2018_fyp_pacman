package entrants.pacman.Alex;

import pacman.controllers.PacmanController;
import pacman.game.Constants;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.GameView;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by ricky on 4/16/17.
 *
 * final test
 *
 * feature:
 * los searching method
 * shortest path prediction
 * real time left-right-turning strategy
 * final move optimization
 * ghost array list
 *
 */
public class POPacMan extends PacmanController {
    RandomMove randomMove = new RandomMove();
    Random random = new Random();
    private ArrayList<MyGhost> ghostArrayList = new ArrayList<MyGhost>();

    private int[][] editableTimeRecord = new int[4][2];
    private int numberOfEditableTime = 0;
    private final int editableStartTime = 0, edibleEndTime = 1;
    private final int editablePeriod = 200;
    private boolean isEditableTime = false;
    private final int MIN_DISTANCE = 50;
    private ArrayList<Integer> targetNodeIndex = null;
    private int numberOfPrediction = 0;
    private boolean isInitialization = true, isInitializationTime = true;
    private final int redIndex = 0, pinkIndex = 1, cyanIndex = 2, orangeIndex = 3;
    private final int redDelay = 40, pinkDelay = 60, cyanDelay = 80, orangeDelay = 100;
    private int initializationTime;
    private final int lairTimePeriod = 40;

    @Override
    public MOVE getMove(Game game, long timeDue) {

        //--------------------------------------------------------------------------------------------------------------

        GameView gameView = new GameView(game);
        int currentNodeIndex = game.getPacmanCurrentNodeIndex();
        int currentLevelTime = game.getCurrentLevelTime();
        LOSLineSearching losLineSearching = new LOSLineSearching(game);
        losLineSearching.findPaths(game, currentNodeIndex);
        int[][] pacmanLOSPaths = losLineSearching.getLosPaths();
        ShortestPathPrediction shortestPathPrediction = new ShortestPathPrediction();
        ArrayList<MOVE> realtimeMoveToBeBlocked = new ArrayList<>();
        ArrayList<MOVE> predictionMovesToBeBlocked = new ArrayList<>();


        if (ghostArrayList.size() == 0) {
            for (int i = 0; i < 4; i++) {
                ghostArrayList.add(new MyGhost(-1, MOVE.NEUTRAL, 0, false, true, -1));

            }
        }

        if (game.wasPacManEaten()) {
            for (MyGhost ghost : ghostArrayList) {
                ghost.setNodeIndex(-1);
                ghost.setObservedTime(0);
                ghost.setLastMoveMade(MOVE.NEUTRAL);
                ghost.setEditable(false);
                ghost.setLair(true);
                ghost.setLairEndTime(-1);
            }
            isInitialization = true;
            isInitializationTime = true;
        }

        if (isInitializationTime) {
            initializationTime = currentLevelTime;
            isInitializationTime = false;
        }

        if (isInitialization) {
            if ((currentLevelTime > initializationTime + redDelay) && ghostArrayList.get(redIndex).isLair()) {
                //System.out.println("red out.");
                ghostArrayList.get(redIndex).setLair(false);
            } else if ((currentLevelTime > initializationTime + pinkDelay) && ghostArrayList.get(pinkIndex).isLair()) {
                //System.out.println("pink out.");
                ghostArrayList.get(pinkIndex).setLair(false);
            } else if ((currentLevelTime > initializationTime + cyanDelay) && ghostArrayList.get(cyanIndex).isLair()) {
                //System.out.println("cyan out.");
                ghostArrayList.get(cyanIndex).setLair(false);
            } else if ((currentLevelTime > initializationTime + orangeDelay) && ghostArrayList.get(orangeIndex).isLair()) {
                //System.out.println("orange out.");
                ghostArrayList.get(orangeIndex).setLair(false);
                isInitialization = false;
            }
        }


        //--------------------------------------------------------------------------------------------------------------
        //record the ghost info

        for (GHOST ghost : GHOST.values()) {
            int ghostColorIndex = shortestPathPrediction.getGhostColorIndex(ghost);
            int ghostCurrentNodeIndex = game.getGhostCurrentNodeIndex(ghost);
            if (game.wasGhostEaten(ghost)) {
                //reset the ghost info when ghost was being eaten
                ghostArrayList.get(ghostColorIndex).setNodeIndex(-1);
                ghostArrayList.get(ghostColorIndex).setLastMoveMade(MOVE.NEUTRAL);
                ghostArrayList.get(ghostColorIndex).setObservedTime(currentLevelTime + lairTimePeriod);
                ghostArrayList.get(ghostColorIndex).setEditable(false);
                ghostArrayList.get(ghostColorIndex).setLair(true);
                ghostArrayList.get(ghostColorIndex).setLairEndTime(currentLevelTime + lairTimePeriod);
                //System.out.println("Eaten.");
            }
            if (ghostCurrentNodeIndex != -1) {
                ghostArrayList.get(ghostColorIndex).setNodeIndex(ghostCurrentNodeIndex);
                ghostArrayList.get(ghostColorIndex).setLastMoveMade(game.getGhostLastMoveMade(ghost));
                ghostArrayList.get(ghostColorIndex).setObservedTime(currentLevelTime);

                //get the real time escape moves
                //if ghost is not edible and is not in lair
                if (game.getGhostEdibleTime(ghost) == 0 && game.getGhostLairTime(ghost) == 0) {
                    //if distance between pac-man and ghost is less than 50
                    if (game.getShortestPathDistance(currentNodeIndex, ghostCurrentNodeIndex) < MIN_DISTANCE) {
                        //add the move to the to-be blocked moves list
                        realtimeMoveToBeBlocked.add(game.getNextMoveTowardsTarget(currentNodeIndex, ghostCurrentNodeIndex, Constants.DM.PATH));
                    }
                }
            }
        }

        //
        for (MyGhost ghost : ghostArrayList) {
            int lairEndTime = ghost.getLairEndTime();
            boolean isLair = ghost.isLair();
            if (lairEndTime > 0 && isLair) {
                if (currentLevelTime >= lairEndTime) {
                    ghost.setLair(false);
                    ghost.setLairEndTime(-1);
                    ghost.setNodeIndex(game.getGhostInitialNodeIndex());
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------
        //record the editable time
        if (game.wasPowerPillEaten()) {
            // every call + 1 ?
            if (numberOfEditableTime > 4) {
                numberOfEditableTime = 0;
            }
            editableTimeRecord[numberOfEditableTime][editableStartTime] = currentLevelTime;
            editableTimeRecord[numberOfEditableTime][edibleEndTime] = currentLevelTime + editablePeriod;
            numberOfEditableTime++;
            isEditableTime = true;
            //System.out.println("Editable time started.");
            for (MyGhost ghost : ghostArrayList) {
                if (!ghost.isLair()) {
                    ghost.setEditable(true);
                }
            }
        }

        if (isEditableTime && currentLevelTime == editableTimeRecord[numberOfEditableTime - 1][edibleEndTime]) {
            isEditableTime = false;
            for (MyGhost ghost : ghostArrayList) {
                ghost.setEditable(false);

            }
            //System.out.println("End of editable time.");
        }
        //--------------------------------------------------------------------------------------------------------------
        //Shortest Path Prediction
        int row = shortestPathPrediction.getDirectionIndex(game.getPacmanLastMoveMade());
        shortestPathPrediction.execute(game, currentLevelTime, ghostArrayList, row, editableTimeRecord, pacmanLOSPaths);
        predictionMovesToBeBlocked = shortestPathPrediction.getPredictionMovesToBeBlocked();

        //--------------------------------------------------------------------------------------------------------------
        //Moves Optimization
        //return the optimized move

        //reduce the real time duplicate move
        if (realtimeMoveToBeBlocked.size() > 0) {
            realtimeMoveToBeBlocked = shortestPathPrediction.realTimeBlockedDuplicatesRemove(realtimeMoveToBeBlocked);
        }

        ArrayList<MOVE> possibleMove = new ArrayList<>();
        MOVE[] moves = game.getPossibleMoves(currentNodeIndex);
        Collections.addAll(possibleMove, moves);


        if (realtimeMoveToBeBlocked.size() > 0) {

            if (predictionMovesToBeBlocked.size() == 0) {
                //real time escape only
                possibleMove.removeAll(realtimeMoveToBeBlocked);
                if (possibleMove.size() > 0) {
                    //return the remaining escape movement

                    for (int i = 0; i < possibleMove.size(); i++) {
                        if (possibleMove.get(i) == game.getPacmanLastMoveMade()) {
                            possibleMove.remove(i);
                        }
                    }
                    if (possibleMove.size() > 0) {
                        return possibleMove.get(random.nextInt(possibleMove.size()));
                    } else {
                        return game.getPacmanLastMoveMade();
                    }


                } else {
                    //the ghost is around pacman, move to the nearest junction
                    /*if (game.isJunction(currentNodeIndex)){
                        int[] junctions = game.getJunctionIndices();
                        int junction = game.getClosestNodeIndexFromNodeIndex(currentNodeIndex, junctions, Constants.DM.PATH);
                        int[] tempTargetNodeIndex = game.getShortestPath(currentNodeIndex, junction);
                        for (int nodeIndex : tempTargetNodeIndex) {
                            targetNodeIndex.add(nodeIndex);
                        }
                    }*/
                    //System.out.println("No road.");

                    //No action can be made
                    return game.getPacmanLastMoveMade();
                }
            } else {
                //real time and prediction
                possibleMove.removeAll(realtimeMoveToBeBlocked);
                possibleMove.removeAll(predictionMovesToBeBlocked);
                if (possibleMove.size() > 0) {
                    numberOfPrediction += 1;
                    System.out.println("Prediction : " + numberOfPrediction + " times.");
                    gameView.addPoints(game, Color.yellow, shortestPathPrediction.getPathDisplay());
                    return possibleMove.get(random.nextInt(possibleMove.size()));
                } else {
                    //System.out.println("00");
                    numberOfPrediction += 1;
                    System.out.println("Prediction : " + numberOfPrediction + " times.");
                    gameView.addPoints(game, Color.yellow, shortestPathPrediction.getPathDisplay());
                    return game.getPacmanLastMoveMade();
                }
            }


        } else {
            if (predictionMovesToBeBlocked.size() > 0) {
                //prediction only
                //System.out.println("Prediction only");
                numberOfPrediction += 1;
                System.out.println("Prediction : " + numberOfPrediction + " times.");
                gameView.addPoints(game, Color.yellow, shortestPathPrediction.getPathDisplay());
                return predictionMovesToBeBlocked.get(0).opposite();
            } else {
                //System.out.println("Random Move");
                return randomMove.getMove(game, currentNodeIndex);
            }
        }


        //--------------------------------------------------------------------------------------------------------------


        //return randomMove.getMove(game, currentNodeIndex);
    }


}
