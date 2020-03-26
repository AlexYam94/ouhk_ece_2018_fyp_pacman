package entrants.pacman.Alex;

import pacman.controllers.MASController;
import pacman.controllers.PacmanController;
import pacman.controllers.examples.po.POCommGhosts;
import pacman.game.Constants;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.info.GameInfo;
import pacman.game.internal.Ghost;

import java.util.concurrent.ThreadLocalRandom;

import java.util.ArrayList;

/**
 * Start by Alex 02/9/2017
 */

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getMove() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., entrants.pacman.Alex).
 */

public class fyp extends PacmanController {
    private MOVE myMove = MOVE.NEUTRAL;
    private MOVE nextMove;
    private int randomNum = 0;
    public ArrayList<MOVE> availableMoves;

    /*RandomMove randomMove = new RandomMove();
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
    private final int lairTimePeriod = 40;*/

    @Override
    public MOVE getMove(Game game, long timeDue) {
        //Place your game logic here to play the game as Ms Pac-Man

        Game coGame;
        Game forwardGame;
        GameInfo info = game.getPopulatedGameInfo();
        info.fixGhosts((ghost) -> new Ghost(
                ghost,
                game.getCurrentMaze().lairNodeIndex,
                -1,
                -1,
                Constants.MOVE.NEUTRAL
        ));
        coGame = game.getGameFromInfo(info);
        forwardGame = coGame.copy();

        /*GameView gameView = new GameView(game);
        int currentNodeIndex = game.getPacmanCurrentNodeIndex();
        int currentLevelTime = game.getCurrentLevelTime();
        LOSLineSearching losLineSearching = new LOSLineSearching(game);
        losLineSearching.findPaths(game, currentNodeIndex);
        int[][] pacmanLOSPaths = losLineSearching.getLosPaths();
        ShortestPathPrediction shortestPathPrediction = new ShortestPathPrediction();
        ArrayList<MOVE> realtimeMoveToBeBlocked = new ArrayList<>();
        ArrayList<MOVE> predictionMovesToBeBlocked = new ArrayList<>();*/

        // Make some ghosts
        MASController ghosts = new POCommGhosts(50);

        forwardGame.advanceGame(nextMove,ghosts.getMove(forwardGame.copy(), 40));


        if (coGame.isJunction(coGame.getPacmanCurrentNodeIndex())) {
            nextMove = getRandomMove();
        }else if(forwardGame.getPacmanCurrentNodeIndex()==coGame.getPacmanCurrentNodeIndex()) {
            for (Constants.MOVE move : Constants.MOVE.values()) {
                Game dontStuckWall = coGame.copy();
                for(int i =0;i<10;i++){
                    dontStuckWall.advanceGame(move,ghosts.getMove(dontStuckWall.copy(),40));
                    if(dontStuckWall.isJunction(dontStuckWall.getPacmanCurrentNodeIndex())){
                        availableMoves.add(move);
                    }
                }
            }

            randomNum = ThreadLocalRandom.current().nextInt(0, availableMoves.size());
            nextMove =  availableMoves.get(randomNum);
            /*do {
                nextMove = getRandomMove();
            }while(nextMove==coGame.getPacmanLastMoveMade());*/
        }

        //Last year's code
        /*if (ghostArrayList.size() == 0) {
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

        *//*forwardDown.advanceGame(MOVE.DOWN,ghosts.getMove(forwardDown.copy(),40));
        forwardUp.advanceGame(MOVE.UP,ghosts.getMove(forwardUp.copy(),40));
        forwardRight.advanceGame(MOVE.RIGHT,ghosts.getMove(forwardRight.copy(),40));
        forwardLeft.advanceGame(MOVE.LEFT,ghosts.getMove(forwardLeft.copy(),40));
        forwardNetural.advanceGame(MOVE.NEUTRAL,ghosts.getMove(forwardNetural.copy(),40))*//*;
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
                    *//*if (game.isJunction(currentNodeIndex)){
                        int[] junctions = game.getJunctionIndices();
                        int junction = game.getClosestNodeIndexFromNodeIndex(currentNodeIndex, junctions, Constants.DM.PATH);
                        int[] tempTargetNodeIndex = game.getShortestPath(currentNodeIndex, junction);
                        for (int nodeIndex : tempTargetNodeIndex) {
                            targetNodeIndex.add(nodeIndex);
                        }
                    }*//*
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
        }*/


        //--------------------------------------------------------------------------------------------------------------


        //return randomMove.getMove(game, currentNodeIndex);

        return nextMove;
    }

    private Constants.MOVE getRandomMove(){
        randomNum = ThreadLocalRandom.current().nextInt(0, 5);
        MOVE move = null;
        switch (randomNum) {
            case 0:
                move = MOVE.NEUTRAL;
                break;
            case 1:
                move = MOVE.RIGHT;
                break;
            case 2:
                move = MOVE.LEFT;
                break;
            case 3:
                move = MOVE.DOWN;
                break;
            case 4:
                move = MOVE.UP;
                break;
        }
        return move;
    }

}

