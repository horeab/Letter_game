package libgdx.services;

import com.badlogic.gdx.Gdx;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import libgdx.game.Game;

public class LettersGameService {

    private int startingTotalWords;
    private int startingTotalLetters;
    private Set<String> allWords = new HashSet<>();

    public LettersGameService(int startingTotalWords, int startingTotalLetters) {
        this.startingTotalWords = startingTotalWords;
        this.startingTotalLetters = startingTotalLetters;
    }

    private static final List<String> CHARS_TO_BE_IGNORED = Arrays.asList(" ", "-", "'");

    public List<String> getCommonLettersWords(List<String> gameLetters) {
        Set<String> result = new HashSet<>();
        for (String word : getAllWords(true)) {
            if (wordContainsAllLetters(word.toLowerCase(), gameLetters)) {
                result.add(word.toLowerCase());
            }
        }
        return new ArrayList<>(result);
    }

    private boolean wordContainsAllLetters(String word, List<String> gameLetters) {
        List<String> wordLetters = getWordLetters(word);
        for (String letter : wordLetters) {
            int letterAmount = word.length() - word.replace(letter, "").length();
            if (letterAmount > getAmountOfElemInList(gameLetters, letter) || !gameLetters.contains(letter)) {
                return false;
            }
        }
        return true;
    }

    public int getAmountOfElemInList(List<String> list, String elem) {
        List<String> newList = new ArrayList<>(list);
        while (newList.remove(elem)) {
        }
        return list.size() - newList.size();
    }

    public List<String> getGameLetters(int nrOfCommonLetters) {
        List<String> allWords = new ArrayList<>(getAllWords(true));
        Collections.shuffle(allWords);
        List<String> letters = new ArrayList<>();
        outerLoop:
        for (String word : allWords) {
            for (char c : word.toCharArray()) {
                if (letters.size() < nrOfCommonLetters) {
                    letters.add(Character.toString(c).toLowerCase());
                } else {
                    break outerLoop;
                }
            }
        }
        return new ArrayList<>(letters);
    }

    public List<String> getWordLetters(String word) {
        word = word.toLowerCase();
        if (StringUtils.indexOfAny(word, (String[]) CHARS_TO_BE_IGNORED.toArray()) != -1) {
            for (String charToBeIgnored : CHARS_TO_BE_IGNORED) {
                word = word.replace(charToBeIgnored, "");
            }
        }
        List<String> result = new ArrayList<>();
        for (char c : word.toCharArray()) {
            result.add(Character.toString(c).toLowerCase());
        }
        return result;
    }

    public Set<String> getAllWords(boolean withFilter) {
        if (withFilter) {
            if (allWords.isEmpty()) {
                allWords = allWordLines(withFilter, startingTotalLetters, startingTotalWords);
            }
            return allWords;
        } else {
            return allWordLines(withFilter, startingTotalLetters, startingTotalWords);
        }
    }

    public static Set<String> allWordLines(boolean withDifficulty, int startingTotalLetters, int startingTotalWords) {
        Set<String> set = new HashSet<>();
        Scanner scanner = new Scanner(Gdx.files.internal(Game.getInstance().getAppInfoService().getImplementationGameResourcesFolder() + "words/" + Game.getInstance().getAppInfoService().getLanguage() + "/allWords.txt").readString());
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            //last part of the line is the correct word in this case
//            if (line.contains(" ")) {
//                line = line.split(" ")[1];
//            }
            //first part of the line is the correct word in this case
            if (line.contains(" ")) {
                line = line.split(" ")[0];
            }
            if (line.length() >= getMinWordLength()
                    && line.length() <= getMaxWordLength(withDifficulty, startingTotalLetters, startingTotalWords)
                    && isAlpha(line)) {
                set.add(line.toLowerCase());
            }
        }
        scanner.close();
        return set;
    }

    private static int getMaxWordLength(boolean withDifficulty, int startingTotalLetters, int startingTotalWords) {
        int wordLength;
        if (startingTotalLetters == 4) {
            wordLength = 4;
        } else if (startingTotalLetters == 6) {
            wordLength = 4;
        } else if (startingTotalLetters == 8) {
            wordLength = 5;
        } else if (startingTotalLetters == 10) {
            wordLength = 6;
        } else if (startingTotalLetters == 12) {
            wordLength = 7;
        } else {
            wordLength = 8;
        }
        wordLength = wordLength + (startingTotalWords - 4);

        int MAX_WORD_LENGTH = 8;
        int MIN_WORD_LENGTH = 4;
        wordLength = wordLength > MAX_WORD_LENGTH ? MAX_WORD_LENGTH : wordLength;
        wordLength = wordLength < MIN_WORD_LENGTH ? MIN_WORD_LENGTH : wordLength;
        return withDifficulty ? wordLength : MAX_WORD_LENGTH;
    }

    private static int getMinWordLength() {
        return 4;
    }

    private static boolean isAlpha(String name) {
        char[] chars = name.toCharArray();

        for (char c : chars) {
            if (!Character.isLetter(c)) {
                return false;
            }
        }

        return true;
    }
}
