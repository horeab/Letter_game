package service;

import com.badlogic.gdx.Gdx;

import libgdx.game.Game;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import libgdx.constants.GameIdEnum;
import libgdx.services.LettersGameService;
import libgdx.services.matrix.MatrixFileWriter;

public class MatrixFileWriterTest extends TestMain {


    @Test
    public void writeToFile() {
        int totalCrossWords = 2;
        int totalNrOfLetters = 4;
//        totalCrossWords = 7;
//        totalNrOfLetters = 6;
        new MatrixFileWriter().writeAllMatrixToFile(totalCrossWords, totalNrOfLetters);
    }


    @Test
    public void parseWordList() {
        Scanner scanner = getFileContents("allWords");
        List<String> result = new ArrayList<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            List<String> words = new ArrayList<>(Arrays.asList(line.split(" @@ ")));
            for (String word : words) {
                if (isAlpha(word) && word.length() >= 3 && word.length() <= 10 && !Character.isUpperCase(word.toCharArray()[0])) {
                    result.add(word);
                    System.out.println(word);
                }
            }
        }
    }


    /**
     * This method verifies allWords that they are common words.
     * Attthe end of the method a printLn is made with all the valid words that need to be put in allWords.txt
     */
    @Test
    public void parseLongText() {
        Set<String> allWords = new LettersGameService(2, 2).getAllWords(false);
        Scanner scanner = getFileContents("verifyText");
        Set<String> result = new HashSet<>();

        List<String> OMMIT = new ArrayList<>();
        //ENGLISH
//        List<String> OMMIT = Arrays.asList("abbrev", "adj", "adv", "conj", "phr", "prep", "eng", "pron", "ing");

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            line = line.replace("!", "");
            line = line.replace("?", "");
            line = line.replace(",", "");
            line = line.replace(";", "");
            line = line.replace("(", "");
            line = line.replace(")", "");
            line = line.replace("[", "");
            line = line.replace("]", "");
            if (line.contains("/<")) {
                line = line.substring(0, line.indexOf("/<"));
            }
            List<String> words = new ArrayList<>(Arrays.asList(line.split(" ")));
            for (String word : words) {
                String s = word.toLowerCase();
//                if (allWords.contains(s.trim()) && isStringUpperCase(word)) {
                if (allWords.contains(s.trim()) && !OMMIT.contains(s.trim())) {
                    result.add(s);
//                    if (s.length() == 3) {
//                        System.out.println(s);
//                    }
                }
            }
        }
        for (String word : result) {
            System.out.println(word);
        }
    }

    private static boolean isStringUpperCase(String str) {
        char[] charArray = str.toCharArray();
        for (char aCharArray : charArray) {
            if (!Character.isUpperCase(aCharArray))
                return false;
        }
        return true;
    }


    private boolean isAlpha(String name) {
        char[] chars = name.toCharArray();

        for (char c : chars) {
            if (!Character.isLetter(c)) {
                return false;
            }
        }

        return true;
    }

    private Scanner getFileContents(final String fileName) {
        String gameId = GameIdEnum.lettersgame.name();
        String lang = Game.getInstance().getAppInfoService().getLanguage();
        return new Scanner(getFileText("tournament_resources/implementations/" + gameId + "/words/" + lang + "/" + fileName + ".txt"));
    }

    private String getFileText(String path) {
        return Gdx.files.internal(path).readString();
    }
}
