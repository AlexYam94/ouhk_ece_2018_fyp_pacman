
import entrants.pacman.Alex.POPacMan;
import entrants.pacman.Alex.Test;

//import entrants.ghosts.username.Blinky;
//import entrants.ghosts.username.Inky;
//import entrants.ghosts.username.Pinky;
//import entrants.ghosts.username.Sue;

import examples.StarterGhostComm.Blinky;
import examples.StarterGhostComm.Inky;
import examples.StarterGhostComm.Pinky;
import examples.StarterGhostComm.Sue;

import examples.StarterPacManOneJunction.MyPacMan;
import examples.StarterISMCTS.InformationSetMCTSPacMan;
import entrants.pacman.Alex.fyp;
import examples.StarterISMCTS.InformationSetMCTSPacMan;
import org.jopendocument.dom.spreadsheet.SpreadSheet;
import pacman.Executor;
import pacman.controllers.IndividualGhostController;
import pacman.controllers.MASController;
import pacman.game.Constants.*;
import pacman.game.util.Stats;

import java.io.File;
import java.util.EnumMap;


/**
 * Created by pwillic on 06/05/2016.
 */
public class Main {

    private static String file_Path = "Test.ods";
    private static int numOfRow = 2;

    public static void main(String[] args) {

        Executor executor = new Executor(true, true);

        EnumMap<GHOST, IndividualGhostController> controllers = new EnumMap<>(GHOST.class);

        controllers.put(GHOST.INKY, new Inky());
        controllers.put(GHOST.BLINKY, new Blinky());
        controllers.put(GHOST.PINKY, new Pinky());
        controllers.put(GHOST.SUE, new Sue());

        //executor.runGameTimed(new Test(), new MASController(controllers),true);
        //executor.runGameTimed(new InformationSetMCTSPacMan(), new MASController(controllers),true);
        //executor.runGameTimed(new MyPacMan(), new MASController(controllers), true);
        //executor.runGameTimed(new InformationSetMCTSPacMan(), new MASController(controllers), true);
        //executor.runGameTimed(new POPacMan(), new MASController(controllers), true);
        //String duration;
        int i = 0;
        while(i++<10) {
            //executor.runGameTimed(new fyp(), new MASController(controllers), true);
            executor.runGameTimed(new Test(), new MASController(controllers),true);
//            Stats[] result = executor.runExperiment(new Test(), new MASController(controllers),1000,
//                    "Basic + go to farthest ghost + save powerpill d=50 dT=20");
//            WriteToExcel(Math.ceil(result[1].getAverage()),"A");
//            WriteToExcel(Math.ceil(result[1].getMax()),"B");
//            WriteToExcel(Math.ceil(result[1].getMin()),"C");
//            numOfRow++;
            //duration = String.valueOf((System.currentTimeMillis()-Test.startPlayTime)/1000);
           // WriteToExcel(duration);
        }
    }

     public static void WriteToExcel(double data,String column){
//        XSSFWorkbook workbook = new XSSFWorkbook();
//        XSSFSheet sheet = workbook.createSheet("Test Result");
        File file = new File(file_Path);
        //data.add(dataToAdd);
        //System.out.println(new File(".").getAbsolutePath());

        try {
            final org.jopendocument.dom.spreadsheet.Sheet sheet = SpreadSheet.createFromFile(file).getSheet(0);
            sheet.getCellAt(column+ numOfRow).setValue(data);
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
