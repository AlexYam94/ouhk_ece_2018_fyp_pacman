package entrants.pacman.Alex;

import com.sun.corba.se.impl.orbutil.closure.Constant;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader;
import examples.StarterISMCTS.InformationSetMCTSPacMan;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Sheet;
import org.jenetics.internal.math.random;
import org.jopendocument.dom.OOUtils;
import org.jopendocument.dom.spreadsheet.SpreadSheet;
import org.jopendocument.model.OpenDocument;
import pacman.controllers.MASController;
import pacman.controllers.PacmanController;
import pacman.controllers.examples.po.POCommGhosts;
import pacman.game.Constants;
import pacman.game.Game;
import pacman.game.GameView;
import pacman.game.info.GameInfo;
import pacman.game.internal.Ghost;
import pacman.game.internal.Maze;
import entrants.pacman.Alex.GhostInfo;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.io.*;
import java.sql.Array;
import java.util.*;
import java.util.concurrent.ConcurrentNavigableMap;



public class Test extends PacmanController {
    /**  Ghost information **/
    private Map<Constants.GHOST, GhostInfo> ghostInfo = new HashMap<>();
    //private ArrayList<GhostInfo> ghostInfo = new ArrayList<>();
    /** Ghost information **/
    private int insightGhostNumber;
    private Map<Constants.GHOST, Constants.MOVE> movesTowardGhosts = new HashMap<>();
    private Game coGame;
    private static String NearestPowerPillIndex = "NearestPowerPillIndex";;
    private static String NearestPowerPillDistance = "NearestPowerPillDistance";
    private boolean isInit = true;
    private boolean shouldApplyFarthestGhost = false;
    private ArrayList<Integer> powerPillsIndices = new ArrayList<>();

    /** Write to excel **/
    public static final long startPlayTime = System.currentTimeMillis();
    private String file_Path = "Test.ods";
    private static int numOfRow = 2;
    // ArrayList<String> data;
    /** **/
    @Override
    public Constants.MOVE getMove(Game game, long timeDue) {

//        if(game.gameOver()){
//            if(numOfRow<500)
//                WriteToExcel(""+game.getCurrentLevelTime());
//        }

        if(game.wasPacManEaten()){
            shouldApplyFarthestGhost = false;
        }

        long startTime = System.nanoTime();
        Random random = new Random();
        int pacmanCurrentIndex;
        Constants.MOVE[] possibleMoves;
        Constants.MOVE[] randomMoves;
        /** Ghost information **/
        Map<Constants.MOVE, Constants.MOVE> ghostInsightMoves = new HashMap<>();
        Map<Constants.GHOST, Double> insightGhostDistance = new HashMap<>();
        boolean anyGhostInsight = false;
        /** Ghost information **/
        Constants.MOVE bestMove = Constants.MOVE.NEUTRAL;
        Maze maze;

        //initialization




        coGame = game.copy();
        maze= coGame.getCurrentMaze();



        pacmanCurrentIndex = coGame.getPacmanCurrentNodeIndex();

        possibleMoves = coGame.getPossibleMoves(pacmanCurrentIndex);

        if(coGame.wasPowerPillEaten()){
            for(int i =0;i<powerPillsIndices.size();i++) {
                if(powerPillsIndices.get(i)==pacmanCurrentIndex)
                    powerPillsIndices.remove(i);
            }
        }

        // Make some ghosts
        MASController ghosts = new POCommGhosts(50);



        if(isInit){
            isInit = false;
            for(Constants.GHOST ghost : Constants.GHOST.values()) {
                ghostInfo.put(ghost, new GhostInfo());
                insightGhostDistance.put(ghost,Double.MAX_VALUE);
            }
            int[] temp = coGame.getPowerPillIndices();
            for(int i=0; i<temp.length;i++){
                powerPillsIndices.add(temp[i]);
            }
        }

        Map<String,Integer> NearestPowerPill = new HashMap<>();
        if(powerPillsIndices.size()>0) {
            NearestPowerPill = GetNearestPowerpillIndex(coGame.getPacmanCurrentNodeIndex());
        }
        //initialization end

        //check if ghost insight
        int insightGhostIndex ;
        insightGhostNumber = 0;
        for(Constants.GHOST ghost : Constants.GHOST.values()) {
            insightGhostIndex  = coGame.getGhostCurrentNodeIndex(ghost);
            if (insightGhostIndex !=-1){
                ghostInfo.get(ghost).ghostsMove = coGame.getGhostLastMoveMade(ghost);
                ghostInfo.get(ghost).ghostsIndex=insightGhostIndex;
                ghostInfo.get(ghost).insightGhosts = true ;
                ghostInfo.get(ghost).timeObserved = game.getCurrentLevelTime();
                movesTowardGhosts.put(ghost,coGame.getNextMoveTowardsTarget(pacmanCurrentIndex,insightGhostIndex,Constants.DM.PATH));
                //Calculate distance for later use(all directions are blocked by ghost)
                insightGhostDistance.put(ghost,GetDistanceToGhost(ghost));
                anyGhostInsight = true;
                insightGhostNumber++;
//                if(numOfRow <= 700){
//                if(ghost == Constants.GHOST.BLINKY){
//                    WriteToExcel("Reset","A");
//                }else if(ghost == Constants.GHOST.INKY){
//                    WriteToExcel("Reset","B");
//                }else if(ghost == Constants.GHOST.SUE){
//                    WriteToExcel("Reset","C");
//                }else if(ghost == Constants.GHOST.PINKY){
//                    WriteToExcel("Reset","D");
//                }
//                }
            }else{
                if (ghostInfo.get(ghost).timeObserved > 0)
                ghostInfo.get(ghost).deltaTime = game.getCurrentLevelTime() - ghostInfo.get(ghost).timeObserved;
//                if(numOfRow<=700) {
//                    if (ghost == Constants.GHOST.BLINKY) {
//                        WriteToExcel(ghostInfo.get(ghost).toString(), "A");
//                    } else if (ghost == Constants.GHOST.INKY) {
//                        WriteToExcel(ghostInfo.get(ghost).toString(), "B");
//                    } else if (ghost == Constants.GHOST.SUE) {
//                        WriteToExcel(ghostInfo.get(ghost).toString(), "C");
//                    } else if (ghost == Constants.GHOST.PINKY) {
//                        WriteToExcel(ghostInfo.get(ghost).toString(), "D");
//                    }
//                }
                movesTowardGhosts.remove(ghost);
                ghostInfo.get(ghost).insightGhosts = false;
            }
        }


        //change direction when on junction, will not reverse only when no ghost insight(or base on prediction?)
        //No ghost insight, move randomly(for now, if not left behind schedule will implement path finding algorithm of getting high score.
        if(!anyGhostInsight) {
            Constants.MOVE dontmove = Constants.MOVE.NEUTRAL;
            if(powerPillsIndices.size()>0)
                dontmove = coGame.getNextMoveTowardsTarget(pacmanCurrentIndex,NearestPowerPill.get(NearestPowerPillIndex),Constants.DM.PATH);

            if (coGame.isJunction(coGame.getPacmanCurrentNodeIndex())) {
                    randomMoves = coGame.getPossibleMoves(pacmanCurrentIndex, coGame.getPacmanLastMoveMade());
                    bestMove = randomMoves[random.nextInt(randomMoves.length)];

            } else {
                    randomMoves = coGame.getPossibleMoves(pacmanCurrentIndex, coGame.getPacmanLastMoveMade());
                    bestMove = randomMoves[0];
            }
            //1 steps
            if(!NearestPowerPill.isEmpty()&&NearestPowerPill.get(NearestPowerPillDistance)<5){
                    if(bestMove == dontmove){
                        int dT = 20;
                            if(ghostInfo.get(Constants.GHOST.BLINKY).deltaTime<=dT
                                    ||ghostInfo.get(Constants.GHOST.INKY).deltaTime<=dT
                                    ||ghostInfo.get(Constants.GHOST.PINKY).deltaTime<=dT
                                    ||ghostInfo.get(Constants.GHOST.SUE).deltaTime<=dT) {
                                bestMove = dontmove;
                            } else {
                                do {
                                    randomMoves = coGame.getPossibleMoves(pacmanCurrentIndex);
                                    bestMove = randomMoves[random.nextInt(randomMoves.length)];
                                } while (bestMove == dontmove);
                            }
                    }
            }
        }/*ghost insight*/else{
            double farthestGhostDistance = Double.MIN_VALUE;
            Constants.MOVE moveTowardFarestGhost = null;


            for(Constants.MOVE move : possibleMoves)
                ghostInsightMoves.put(move,move);
            Constants.MOVE moveTowardGhost = null;
            for(Constants.GHOST ghost : Constants.GHOST.values()){
                //if ghost is not insight, jump to next ghost
                if(!ghostInfo.get(ghost).insightGhosts)
                    continue;
                //Get the move that will go toward the ghost
                moveTowardGhost=movesTowardGhosts.get(ghost);
                //ghostInsightMoves is copy from possibleMoves
                //if possible moves of pac-man contain move that will make the pac-man go toward ghost, eliminate it
                if(ghostInsightMoves.containsKey(moveTowardGhost)){
                    ghostInsightMoves.remove(moveTowardGhost);

                }
                //Get farthest insight ghost distance and move
                if(farthestGhostDistance < insightGhostDistance.get(ghost)){
                    farthestGhostDistance = insightGhostDistance.get(ghost);
                    moveTowardFarestGhost = movesTowardGhosts.get(ghost);
                }
            }

            //if all directions have ghost, go to the one with longest distance to maximize survival time
            if(ghostInsightMoves.isEmpty()) {
                if(!shouldApplyFarthestGhost) {
                    bestMove = moveTowardFarestGhost;
                    shouldApplyFarthestGhost=true;
                }else{
                    bestMove=coGame.getPacmanLastMoveMade();
                }
//                bestMove = possibleMoves[random.nextInt(possibleMoves.length)];
            }else{
                shouldApplyFarthestGhost=false;
                //Get how many indices(i.e. how many steps) needed to go to the nearest powerpill
                int[] movesToNearestPowerpill;
                Constants.MOVE nextMoveTowardPowerpill = null;
                int distanceBetweenPacManAndPowerpill = Integer.MAX_VALUE;
                if(powerPillsIndices.size()>0) {
                    movesToNearestPowerpill = game.getShortestPath(pacmanCurrentIndex, NearestPowerPill.get(NearestPowerPillIndex));
                    nextMoveTowardPowerpill = game.getNextMoveTowardsTarget(pacmanCurrentIndex, movesToNearestPowerpill[0], Constants.DM.PATH);
                    distanceBetweenPacManAndPowerpill = NearestPowerPill.get(NearestPowerPillDistance);
                }

                //if it takes less than x steps to nearest powerpill and ghost insight
                if(distanceBetweenPacManAndPowerpill <= 50 && ghostInsightMoves.containsKey(nextMoveTowardPowerpill)){
                        bestMove = nextMoveTowardPowerpill;
                } else  if(coGame.isJunction(coGame.getPacmanCurrentNodeIndex())){
                    /**When ghost insight and pacman in junction part starting line**/
                    //if impossible to move toward to powerpill(i.e the direction is blocked by wall or ghost) in current index, apply left-right turning strategy.
                        bestMove = LeftRightTurning(ghostInsightMoves);
                    /**When ghost insight and pacman in junction part finishing line**/
                }else{
                    //keep going to oppsite direction of ghost until reach junction
                    Object[] moves = ghostInsightMoves.values().toArray();
                    bestMove = (Constants.MOVE)moves[random.nextInt(moves.length)];

                }
            }
        }
//        if(numOfRow<=500) {
//            long duration = (System.nanoTime() - startTime) / 100000;
//            WriteToExcel("" + duration);
//        }
        return bestMove;
    }

    private Constants.MOVE LeftRightTurning(Map<Constants.MOVE, Constants.MOVE> ghostsInsightMoves){
        ArrayList<Constants.MOVE> leftRight = new ArrayList<>();
        Map<Constants.MOVE, Constants.MOVE> ghostsInsightMovesCopy = new HashMap<>(ghostsInsightMoves);
        Random random = new Random();
        for (Constants.GHOST ghost : Constants.GHOST.values()){
            //not this ghost, jump to check next one
            if(!ghostInfo.get(ghost).insightGhosts)
                continue;
            //Need to know insight ghosts move, then return moves according to it
            //Only chased by one ghost
            if(insightGhostNumber==1){
                //remove the direction of turning the back to ghost
                ghostsInsightMovesCopy.remove(movesTowardGhosts.get(ghost).opposite());
                //put the remaining moves to the arraylist
                for(Map.Entry<Constants.MOVE,Constants.MOVE> entry : ghostsInsightMovesCopy.entrySet()){
                    leftRight.add(entry.getValue());
                }
                //since only one ghost insight
                break;
            }/* Chased by multiple ghosts */else if(insightGhostNumber>1){
                ghostsInsightMovesCopy.remove(movesTowardGhosts.get(ghost).opposite());
                //Divide this into two situation
                //1:Only one way to escape
                //2: More than one way to escape
                //ghostsInsightMovesCopy will not equal to 0 since if it does, this function will not be called

                //Situation 1
                if(ghostsInsightMovesCopy.size()==1){
                    for(Map.Entry<Constants.MOVE,Constants.MOVE> entry : ghostsInsightMovesCopy.entrySet()){
                        leftRight.add(entry.getValue());
                    }
                }/*Situation 2 */else{
                    //Further divide into two situation
                    //2.1 all ghosts in same direction
                    //2.2 ghosts in different direction

                    //check if all ghosts directions are the same
                    ArrayList<Constants.MOVE> checkGhostDirection = new ArrayList<>();
                    for(Map.Entry<Constants.MOVE,Constants.MOVE> entry : ghostsInsightMovesCopy.entrySet()){
                        checkGhostDirection.add(entry.getValue());
                    }
                    Set<Constants.MOVE> set = new HashSet<>(checkGhostDirection);
                    //2.1
                    //if all ghosts are chasing the pacman in same direction, their move should be the same at that time(i.e moving toward to pacman).
                    if(set.size()==1) {
                        ghostsInsightMovesCopy.remove(checkGhostDirection.get(0).opposite());
                        for(Map.Entry<Constants.MOVE,Constants.MOVE> entry : ghostsInsightMovesCopy.entrySet()){
                            leftRight.add(entry.getValue());
                        }
                    }else{
                        //2.2
                        //If prediction system is finished
                        //Can decide which direction based on prediction
                        for(Map.Entry<Constants.MOVE,Constants.MOVE> entry : ghostsInsightMovesCopy.entrySet()){
                            leftRight.add(entry.getValue());
                        }
                    }
                }
            }
        }
        //Actually can do more on choosing left or right, but that needs prediction system.
        //So now the direction will be randomly chosen
        if(leftRight.size()!=0)
            return leftRight.get(random.nextInt(leftRight.size()));
        ArrayList<Constants.MOVE> ran = new ArrayList<>();
        for(Map.Entry<Constants.MOVE,Constants.MOVE> entry : ghostsInsightMoves.entrySet()){
            ran.add(entry.getValue());
        }
        return ran.get(random.nextInt(ran.size()));
    }

    private double GetDistanceToGhost(Constants.GHOST ghost){
        double distance;
        distance = coGame.getDistance(coGame.getPacmanCurrentNodeIndex(),coGame.getGhostCurrentNodeIndex(ghost),Constants.DM.PATH);
        return distance;
    }

    private Map<String, Integer> GetNearestPowerpillIndex(int currentIndex){
        double nearestPowerPillDistance = coGame.getDistance(currentIndex,powerPillsIndices.get(0),Constants.DM.PATH);
        int nearestPowerPillIndex = powerPillsIndices.get(0);
        Map<String,Integer> result = new HashMap<>();

        for(int i = 1;i<powerPillsIndices.size();i++){
            double temp = coGame.getDistance(currentIndex,powerPillsIndices.get(i),Constants.DM.PATH);
            if(nearestPowerPillDistance > temp){
                nearestPowerPillDistance = temp;
                nearestPowerPillIndex = powerPillsIndices.get(i);
            }
        }
        result.put(NearestPowerPillIndex,nearestPowerPillIndex);
        result.put(NearestPowerPillDistance,(int)nearestPowerPillDistance);
        return result;
    }

    private void WriteToExcel(String data, String column){
//        XSSFWorkbook workbook = new XSSFWorkbook();
//        XSSFSheet sheet = workbook.createSheet("Test Result");
        File file = new File(file_Path);
        //data.add(dataToAdd);
        //System.out.println(new File(".").getAbsolutePath());

        try {
            final org.jopendocument.dom.spreadsheet.Sheet sheet = SpreadSheet.createFromFile(file).getSheet(0);
            sheet.getCellAt(column + numOfRow++).setValue(data);
            sheet.getSpreadSheet().saveAs(file);
//            if(coGame.gameOver()){
//                String[] columns = new String[] {"HashMap"};
//                Object[] data = this.data.toArray();
//                TableModel model = new DefaultTableModel(data, columns);
//
//            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

