package libgdx.services.matrix;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import libgdx.game.Game;
import libgdx.model.CrossWordWithPosition;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class MatrixFileWriter {

    static final String ALL_SPLIT = "XKX";

    private static final int MAX_WORDS_IN_CROSSWORD = 10;
    private static final int MAX_LETTERS_IN_CROSSWORD = 10;

    public void writeAllMatrixToFile(int startingTotalWords, int startingTotalLetters) {
        for (int i = startingTotalWords; i <= MAX_WORDS_IN_CROSSWORD; i++) {
            for (int j = startingTotalLetters; j <= MAX_LETTERS_IN_CROSSWORD; j = j + 2) {
                Set<String> matrix = new HashSet<>();
                MatrixCreatorService matrixCreatorService1 = new MatrixCreatorService(i, j);
                Map<CrossWordWithPosition, Pair<Integer, Integer>> crossword = matrixCreatorService1.createCrossword();
                String encodedCrossword = null;
                if (crossword != null && !crossword.isEmpty()) {
                    encodedCrossword = encodeMatrix(crossword, matrixCreatorService1.getGameLetters());
                }
                //*******************************int tries = 200;
                int tries = 20;
                int matrixRepeated = 0;
                while (tries > 0 && matrix.size() < tries) {
                    if (matrixRepeated > tries) {
                        break;
                    }
                    if (matrix.contains(encodedCrossword) || encodedCrossword == null) {
                        matrixRepeated++;
                    }
                    if (StringUtils.isNotBlank(encodedCrossword)) {
                        matrix.add(encodedCrossword);
                    }
                    MatrixCreatorService matrixCreatorService2 = new MatrixCreatorService(i, j);
                    crossword = matrixCreatorService2.createCrossword();
                    if (crossword != null && !crossword.isEmpty()) {
                        encodedCrossword = encodeMatrix(crossword, matrixCreatorService2.getGameLetters());
                    }
                    tries--;
                    System.out.println("STARTINGTOTALWORDS: " + i + ", STARTINGTOTALLETTERS: " + j + ", TRIES: " + tries);
//                    System.out.println(matrixRepeated);
                }
                if (!matrix.isEmpty()) {
                    System.out.println("write: " + matrix);
                    writeMatrixToFile(matrix, i, j);
                }
            }
        }
    }

    private String encodeMatrix(Map<CrossWordWithPosition, Pair<Integer, Integer>> allCrossWordsWithPosition, List<String> gameLetters) {
        return new Gson().toJson(gameLetters) + ALL_SPLIT + new Gson().toJson(allCrossWordsWithPosition.keySet());
    }

    private void writeMatrixToFile(Set<String> matrix, int totalWords, int totalLetters) {
        String path = "C:\\workspace\\Letter_game\\src\\main\\resources\\tournament_resources\\implementations\\lettersgame\\matrix\\" + Game.getInstance().getAppInfoService().getLanguage() + "\\";
//        String path = "D:\\workspace\\Lett\\src\\main\\resources\\tournament_resources\\implementations\\lettersgame\\matrix\\" + Game.getInstance().getAppInfoService().getLanguage() + "\\";
        String fileName = path + totalWords + "_" + totalLetters + ".txt";
        FileWriter fr = null;
        try {
            File file = new File(fileName);
            file.createNewFile();
            fr = new FileWriter(file);
            fr.write(matrix.size() + "\n");
            for (String elem : matrix) {
                fr.write(elem + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException();
        } finally {
            try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int getMatrixCols(String[][] matrix) {
        return matrix[0].length;
    }

    private int getMatrixRows(String[][] matrix) {
        return matrix.length;
    }
}
