package libgdx.services;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import libgdx.model.CrossWordWithPosition;

public class CrossWordService {

    private static final String DELIM = ":";
    public static final String HORIZ = "HH";
    public static final String VERT = "VV";

    private int TRIES_DIV = 10;

    public List<CrossWordWithPosition> createAllCrossWords(Set<String> allWords, int totalCrossWords) {
        System.out.println(allWords);
        List<CrossWordWithPosition> list = new ArrayList<>();
        ////////////////TRIES//////////////
        int tries = 120;
        ////////////////TRIES//////////////
        while (list.size() < totalCrossWords && tries > 0) {
            List<String> allWordsToProcessToShuffle = new ArrayList<>(allWords);
            Collections.shuffle(allWordsToProcessToShuffle);
            Set<String> allWordsToProcess = new HashSet<>(allWordsToProcessToShuffle);
//            System.out.println(allWordsToProcess);
            list = new ArrayList<>();
            String startingWord = randomSetItem(allWordsToProcess);
            CrossWordWithPosition startingCrossWord = getCrossWordWithPosition(allWordsToProcess, list, totalCrossWords, startingWord, 0);
            if (startingCrossWord != null) {
                list.add(startingCrossWord);
                for (int i = 1; i < totalCrossWords; i++) {
                    Set<String> commonLetterWords = findCommonLetterWords(allWordsToProcess, startingWord);
                    if (commonLetterWords.isEmpty()) {
                        break;
                    }
                    CrossWordWithPosition crossWordToProcess = getCrossWordWithPosition(allWordsToProcess, list, totalCrossWords, randomSetItem(commonLetterWords), i);
                    if (crossWordToProcess != null) {
                        list.add(crossWordToProcess);
                    } else {
                        break;
                    }
                }
            }
            tries--;
            if (tries == 0) {
                list.clear();
            }
        }
        return list;
    }

    public boolean isRemoveLetterValidForWord(Set<String> allWords, String baseWord, int baseWordCrossPos, int letterPosToRemove) {
        return baseWordCrossPos != letterPosToRemove && allWords.contains(removeLetterFromWord(baseWord, letterPosToRemove));
    }

    private String removeLetterFromWord(String baseWord, int letterPosToRemove) {
        return baseWord.substring(0, letterPosToRemove) + baseWord.substring(letterPosToRemove + 1);
    }

    private String randomSetItem(Set<String> set) {
        int item = new Random().nextInt(set.size());
        int i = 0;
        for (String obj : set) {
            if (i == item)
                return obj;
            i++;
        }
        return null;
    }


    public Map<CrossWordWithPosition, Pair<Integer, Integer>> getPosition(Collection<CrossWordWithPosition> crossWords) {
        Map<CrossWordWithPosition, Pair<Integer, Integer>> pos = new LinkedHashMap<>();
        boolean isHorizontal = true;
        for (CrossWordWithPosition crossWordWithPosition : crossWords) {
            int horizontalStartPos = getHorizontalStartPos(crossWordWithPosition.getPositionInCrossWord(), pos, crossWords);
            int verticalStartPos = getVerticalStartPos(crossWordWithPosition.getPositionInCrossWord(), pos, crossWords);

            int left = !isHorizontal ? horizontalStartPos : getHorizontalValue(horizontalStartPos, crossWordWithPosition.getPositionInCrossWord(), pos);
            int right = isHorizontal ? verticalStartPos : getVerticalValue(verticalStartPos, crossWordWithPosition.getPositionInCrossWord(), pos);

            if (right < 0) {
                incrementRightValue(pos, Math.abs(right));
                right = 0;
            }
            if (left < 0) {
                incrementLeftValue(pos, Math.abs(left));
                left = 0;
            }

            pos.put(crossWordWithPosition, new MutablePair<>(left, right));
            isHorizontal = !isHorizontal;
        }
        return pos;
    }

    public static boolean isHorizontal(int positionInCrossWord) {
        return positionInCrossWord % 2 == 0;
    }

    private int getVerticalStartPos(int positionInCrossWord, Map<CrossWordWithPosition, Pair<Integer, Integer>> addedPos, Collection<CrossWordWithPosition> allCrossWords) {
        boolean isVertical = !isHorizontal(positionInCrossWord);
        CrossWordWithPosition prevParent = getCrosswordForIndex(allCrossWords, positionInCrossWord - 1);
        CrossWordWithPosition sameDimensionParent = getSameDimensionParentCrossword(allCrossWords, positionInCrossWord);
        CrossWordWithPosition verticalParentCrossWord = isVertical ? sameDimensionParent : prevParent;
        CrossWordWithPosition crosswordForIndex = getCrosswordForIndex(allCrossWords, positionInCrossWord);
        int toReturn = isVertical ? -crosswordForIndex.getBaseWordIndexToCross() : 0;
        if (verticalParentCrossWord != null) {
            if (isVertical) {
                toReturn = verticalParentCrossWord.getChildIndexToCross() - crosswordForIndex.getBaseWordIndexToCross();
            } else {
                toReturn = addedPos.get(verticalParentCrossWord).getRight() + verticalParentCrossWord.getChildIndexToCross();
            }
        }
        return toReturn;
    }

    private int getHorizontalStartPos(int positionInCrossWord, Map<CrossWordWithPosition, Pair<Integer, Integer>> addedPos, Collection<CrossWordWithPosition> allCrossWords) {
        boolean isHorizontal = isHorizontal(positionInCrossWord);
        CrossWordWithPosition prevParent = getCrosswordForIndex(allCrossWords, positionInCrossWord - 1);
        CrossWordWithPosition sameDimensionParent = getSameDimensionParentCrossword(allCrossWords, positionInCrossWord);
        CrossWordWithPosition horizontalParentCrossWord = isHorizontal ? sameDimensionParent : prevParent;
        CrossWordWithPosition crosswordForIndex = getCrosswordForIndex(allCrossWords, positionInCrossWord);
        int toReturn = 0;
        if (horizontalParentCrossWord != null) {
            if (isHorizontal) {
                toReturn = horizontalParentCrossWord.getChildIndexToCross() - crosswordForIndex.getBaseWordIndexToCross();
            } else {
                toReturn = addedPos.get(horizontalParentCrossWord).getLeft() + horizontalParentCrossWord.getChildIndexToCross();
            }
        }
        return toReturn;
    }

    private int getVerticalValue(int toCompare, int positionInCrossWord, Map<CrossWordWithPosition, Pair<Integer, Integer>> pos) {
        int toReturn = toCompare;
        CrossWordWithPosition parentCrossword = getSameDimensionParentCrossword(pos.keySet(), positionInCrossWord);
        if (parentCrossword != null) {
            Integer right = pos.get(parentCrossword).getRight();
            toReturn = toCompare + right;
            while (parentCrossword != null && right != 0) {
                parentCrossword = getSameDimensionParentCrossword(pos.keySet(), parentCrossword.getPositionInCrossWord());
                if (parentCrossword != null) {
                    right = pos.get(parentCrossword).getRight();
                    if (right == 0) {
                        break;
                    }
                    if (toReturn < 0) {
                        toReturn = toReturn + right;
                    }
                }
            }
        }
        return toReturn;
    }

    public int getHorizontalValue(int toCompare, int positionInCrossWord, Map<CrossWordWithPosition, Pair<Integer, Integer>> pos) {
        int toReturn = toCompare;
        CrossWordWithPosition parentCrossword = getSameDimensionParentCrossword(pos.keySet(), positionInCrossWord);
        if (parentCrossword != null) {
            Integer left = pos.get(parentCrossword).getLeft();
            toReturn = toCompare + left;
            while (parentCrossword != null && left != 0) {
                parentCrossword = getSameDimensionParentCrossword(pos.keySet(), parentCrossword.getPositionInCrossWord());
                if (parentCrossword != null) {
                    left = pos.get(parentCrossword).getLeft();
                    if (left == 0) {
                        break;
                    }
                    if (toReturn < 0) {
                        toReturn = toReturn + left;
                    }
                }
            }
        }
        return toReturn;
    }

    public static Pair<Integer, Integer> getMaxWidthAndMaxHeight(Map<CrossWordWithPosition, Pair<Integer, Integer>> map) {
        boolean isHorizontal = true;
        int maxWidth = 0;
        int maxHeight = 0;
        for (Map.Entry<CrossWordWithPosition, Pair<Integer, Integer>> entry : map.entrySet()) {
            int horizontalLength = isHorizontal ? entry.getKey().getWordLength() + entry.getValue().getLeft() : 0;
            maxWidth = horizontalLength > maxWidth ? horizontalLength : maxWidth;

            int verticalLength = isHorizontal ? 1 : entry.getKey().getWordLength() + entry.getValue().getRight();
            maxHeight = verticalLength > maxHeight ? verticalLength : maxHeight;

            isHorizontal = !isHorizontal;
        }
        return new MutablePair<>(maxWidth, maxHeight);
    }

    private void incrementLeftValue(Map<CrossWordWithPosition, Pair<Integer, Integer>> map, int valueToIncrement) {
        for (Map.Entry<CrossWordWithPosition, Pair<Integer, Integer>> entry : map.entrySet()) {
            entry.setValue(new MutablePair<>(entry.getValue().getLeft() + valueToIncrement, entry.getValue().getRight()));
        }
    }

    private void incrementRightValue(Map<CrossWordWithPosition, Pair<Integer, Integer>> map, int valueToIncrement) {
        for (Map.Entry<CrossWordWithPosition, Pair<Integer, Integer>> entry : map.entrySet()) {
            entry.setValue(new MutablePair<>(entry.getValue().getLeft(), entry.getValue().getRight() + valueToIncrement));
        }
    }

    private String getLetterFromEncodedVal(String val) {
        return StringUtils.isNotBlank(val) ? val.split(DELIM)[0] : null;
    }

    private boolean verifyOverlappingCharactersAreSame(int iIndex, int jIndex, Collection<String> allWords, Map<CrossWordWithPosition, Pair<Integer, Integer>> wordsWithPositions, int characterIndexToTest, CrossWordWithPosition toTest) {
        String[][] matrix = buildWordMatrix(allWords, wordsWithPositions.keySet());
        String matrixLetter = getLetterFromEncodedVal(matrix[iIndex][jIndex]);
        String firstMatchingWord = getFirstMatchingWord(new ArrayList<>(allWords), toTest);
        String characterToTest = Character.toString(firstMatchingWord.toCharArray()[characterIndexToTest]);

        //verify that the overlapping characters are same
        return StringUtils.isNotBlank(matrixLetter) && matrixLetter.equals(characterToTest);
    }

    public boolean isGeneratedMatrixValid(Collection<String> allWords, Collection<CrossWordWithPosition> alreadyAddedCrossWords, int totalCrosswords, CrossWordWithPosition toTest) {

        List<CrossWordWithPosition> listToTest = new ArrayList<>(alreadyAddedCrossWords);
        listToTest.add(toTest);

        Map<CrossWordWithPosition, Pair<Integer, Integer>> wordsWithPositions = getPosition(listToTest);

        String[][] matrix = createEmptyCrosswordMatrix(wordsWithPositions);

        boolean isGeneratedMatrixValid = matrixIsRelSquare(matrix, totalCrosswords);
        if (!isGeneratedMatrixValid) {
            return false;
        }
        boolean isHorizontal = true;

        Set<String> availableWords = new HashSet<>(allWords);
        //verify that all the crosswords have at least one corresponding word
        for (CrossWordWithPosition crossWordWithPosition : listToTest) {
            String matchingWord = getFirstMatchingWord(new ArrayList<>(availableWords), crossWordWithPosition);
            if (StringUtils.isNotBlank(matchingWord)) {
                availableWords.remove(matchingWord);
            } else {
                isGeneratedMatrixValid = false;
                break;
            }
        }

        for (Map.Entry<CrossWordWithPosition, Pair<Integer, Integer>> entry : wordsWithPositions.entrySet()) {
            for (int j = 0; j < entry.getKey().getWordLength(); j++) {
                int iIndex = entry.getValue().getRight() + (isHorizontal ? 0 : j);
                int jIndex = entry.getValue().getLeft() + (isHorizontal ? j : 0);
                ////////////////TRIES//////////////
                int tries = 500 / TRIES_DIV;
                ////////////////TRIES//////////////
                isGeneratedMatrixValid = verifyOverlappingCharactersAreSame(iIndex, jIndex, allWords, wordsWithPositions, j, entry.getKey());
                while (!isGeneratedMatrixValid && tries > 0) {
                    isGeneratedMatrixValid = verifyOverlappingCharactersAreSame(iIndex, jIndex, allWords, wordsWithPositions, j, entry.getKey());
                    tries--;
                }

                if (!isGeneratedMatrixValid) {
                    break;
                }

                //verify that there are no neighbouring characters left or right
                if (isHorizontal) {
                    if (j == 0 && jIndex > 0 && matrix[iIndex][jIndex - 1] != null) {
                        isGeneratedMatrixValid = false;
                        break;
                    }
                    if (iIndex > 0) {
                        String valToTest = matrix[iIndex - 1][jIndex];
                        if (valToTest != null && StringUtils.isBlank(matrix[iIndex][jIndex])) {
                            isGeneratedMatrixValid = false;
                            break;
                        }
                    }
                    if (iIndex < matrix.length - 1) {
                        String valToTest = matrix[iIndex + 1][jIndex];
                        if (valToTest != null && StringUtils.isBlank(matrix[iIndex][jIndex])) {
                            isGeneratedMatrixValid = false;
                            break;
                        }
                    }
                } else {
                    if (j == 0 && iIndex > 0 && matrix[iIndex - 1][jIndex] != null) {
                        isGeneratedMatrixValid = false;
                        break;
                    }
                    if (jIndex > 0) {
                        String valToTest = matrix[iIndex][jIndex - 1];
                        if (valToTest != null && StringUtils.isBlank(matrix[iIndex][jIndex])) {
                            isGeneratedMatrixValid = false;
                            break;
                        }
                    }
                    if (jIndex < matrix[0].length - 1) {
                        String valToTest = matrix[iIndex][jIndex + 1];
                        if (valToTest != null && StringUtils.isBlank(matrix[iIndex][jIndex])) {
                            isGeneratedMatrixValid = false;
                            break;
                        }
                    }
                }

                matrix[iIndex][jIndex] = StringUtils.isBlank(matrix[iIndex][jIndex]) ? appendHorizVertical("", isHorizontal) : appendHorizVertical(matrix[iIndex][jIndex], isHorizontal);
            }
            isHorizontal = !isHorizontal;
        }

        ////////////////TRIES//////////////
        int tries = 300 / TRIES_DIV;
        ////////////////TRIES//////////////
        for (int t = 0; t < tries; t++) {
            matrix = new CrossWordService().buildWordMatrix(allWords, listToTest);
            Map<Integer, String> wordsForPosition = getWordsForPosition(totalCrosswords, matrix);
            for (String word : wordsForPosition.values()) {
                if (!allWords.contains(word)) {
                    isGeneratedMatrixValid = false;
                    break;
                }
            }
            if (!isGeneratedMatrixValid) {
                break;
            }
        }
        return isGeneratedMatrixValid;
    }

    private boolean matrixIsRelSquare(String[][] matrix, int totalCrosswords) {
        //matrix should be as square as possible
        return Math.abs(matrix.length - matrix[0].length) <= 3;
    }


    public Map<Integer, String> getWordsForPosition(int totalCrossWords, String[][] matrix) {
        CrossWordMatrixEncoder encoder = new CrossWordMatrixEncoder();
        Map<Integer, String> matrixWords = new HashMap<>();
        for (int position = 0; position < totalCrossWords; position++) {
            for (int i = 0; i < matrix.length; i++) {
                List<String> word = new ArrayList<>();
                for (int j = 0; j < matrix[i].length; j++) {
                    String value = matrix[i][j];
                    if (value != null && Arrays.asList(encoder.getPositionsInCrossWord(value)).contains(String.valueOf(position))) {
                        word.add(encoder.decodeLetterValue(value));
                    }
                }
                String wordJoined = StringUtils.join(word, "");
                if (wordJoined.length() > 1) {
                    matrixWords.put(position, wordJoined);
                }
            }
            for (int i = 0; i < matrix[0].length; i++) {
                List<String> word = new ArrayList<>();
                for (int j = 0; j < matrix.length; j++) {
                    String value = matrix[j][i];
                    if (value != null && Arrays.asList(encoder.getPositionsInCrossWord(value)).contains(String.valueOf(position))) {
                        word.add(encoder.decodeLetterValue(value));
                    }
                }
                String wordJoined = StringUtils.join(word, "");
                if (wordJoined.length() > 1) {
                    matrixWords.put(position, wordJoined);
                }
            }
        }
        return matrixWords;
    }

    public String[][] buildWordMatrix(Collection<String> allWords, Collection<CrossWordWithPosition> crossWords) {
        boolean isHorizontal = true;
        List<String> wordList = new ArrayList<>(allWords);
        Map<CrossWordWithPosition, Pair<Integer, Integer>> wordsWithPositions = getPosition(crossWords);
        String[][] matrix = createEmptyCrosswordMatrix(wordsWithPositions);
        for (Map.Entry<CrossWordWithPosition, Pair<Integer, Integer>> entry : wordsWithPositions.entrySet()) {
            CrossWordWithPosition key = entry.getKey();
            String firstMatchingWord = getFirstMatchingWord(wordList, key);
            wordList.remove(firstMatchingWord);
            char[] word = firstMatchingWord.toCharArray();
            for (int j = 0; j < key.getWordLength(); j++) {
                int iIndex = entry.getValue().getRight() + (isHorizontal ? 0 : j);
                int jIndex = entry.getValue().getLeft() + (isHorizontal ? j : 0);
                matrix[iIndex][jIndex] = new CrossWordMatrixEncoder().encodeValue(String.valueOf(word[j]), matrix[iIndex][jIndex], key.getPositionInCrossWord(), isHorizontal);
            }
            isHorizontal = !isHorizontal;
        }
        return matrix;
    }

    private String appendHorizVertical(String val, boolean isHorizontal) {
        return val + DELIM + (isHorizontal ? HORIZ : VERT);
    }

    public static String[][] createEmptyCrosswordMatrix
            (Map<CrossWordWithPosition, Pair<Integer, Integer>> wordsWithPositions) {
        Pair<Integer, Integer> maxWidthAndMaxHeight = getMaxWidthAndMaxHeight(wordsWithPositions);
        return new String[maxWidthAndMaxHeight.getRight()][maxWidthAndMaxHeight.getLeft()];
    }

    private CrossWordWithPosition getCrossWordWithPosition
            (Collection<String> allWords, List<CrossWordWithPosition> alreadyAddedCrosswords,
             int totalCrosswords, String baseWord, int positionInCrossWord) {
        CrossWordWithPosition crossWord = null;
        ////////////////TRIES//////////////
        int tries = 500;
        ////////////////TRIES//////////////
        ArrayList<String> allWordsList = new ArrayList<>(allWords);
        while (crossWord == null && tries > 0) {
            CrossWordWithPosition parentCrossword = getParentCrossword(alreadyAddedCrosswords, positionInCrossWord);
            String firstMatchingWord = parentCrossword == null ? null : getFirstMatchingWord(allWordsList, parentCrossword);
            MutablePair<MutablePair<Integer, String>, MutablePair<Integer, String>> randomMatchingLetterPosition = parentCrossword != null && StringUtils.isNotBlank(firstMatchingWord)
                    ? (MutablePair<MutablePair<Integer, String>, MutablePair<Integer, String>>) getRandomMatchingLetterPositionChildForParentAndBaseForChild(baseWord, firstMatchingWord)
                    : new MutablePair<>(new MutablePair<>(0, String.valueOf(baseWord.toCharArray()[0])), new MutablePair<>(0, String.valueOf(baseWord.toCharArray()[0])));
            Pair<Integer, String> baseWordInfo = randomMatchingLetterPosition.getRight();
            Pair<Integer, String> parentWordInfo = randomMatchingLetterPosition.getLeft();
            CrossWordWithPosition crossWordToTest = new CrossWordWithPosition(positionInCrossWord, baseWord.length(), baseWordInfo.getLeft(), baseWordInfo.getRight());
            if (parentCrossword != null) {
                parentCrossword.setChildInfo(parentWordInfo.getLeft(), parentWordInfo.getRight());
            }
            if (isGeneratedMatrixValid(alreadyAddedCrosswords, totalCrosswords, allWordsList, crossWordToTest)) {
                crossWord = new CrossWordWithPosition(positionInCrossWord, baseWord.length(), baseWordInfo.getLeft(), baseWordInfo.getRight());
            }
            allWordsList.remove(firstMatchingWord);
            tries--;
        }
        return crossWord;
    }

    private boolean isGeneratedMatrixValid(List<CrossWordWithPosition> alreadyAddedCrosswords, int totalCrosswords, ArrayList<String> allWordsList, CrossWordWithPosition crossWordToTest) {
        boolean generatedMatrixValid;
        try {
            generatedMatrixValid = isGeneratedMatrixValid(allWordsList, alreadyAddedCrosswords, totalCrosswords, crossWordToTest);
        } catch (Exception e) {
            generatedMatrixValid = false;
        }
        return generatedMatrixValid;
    }

    private static List<String> getMatchingWords(List<String> allWords, CrossWordWithPosition crossWordWithPosition) {
        List<String> result = new ArrayList<>();
        ArrayList<String> allWordsList = new ArrayList<>(allWords);
        Collections.shuffle(allWordsList);
        for (String word : allWordsList) {
            char[] wordCharArray = word.toCharArray();
            if (crossWordWithPosition.getBaseWordIndexToCross() != -1 && crossWordWithPosition.getBaseWordIndexToCross() < wordCharArray.length
                    && String.valueOf(wordCharArray[crossWordWithPosition.getBaseWordIndexToCross()]).equals(crossWordWithPosition.getBaseWordLetterToCross())
                    && word.length() == crossWordWithPosition.getWordLength()) {
                if (crossWordWithPosition.getChildIndexToCross() == null) {
                    result.add(word);
                } else if (crossWordWithPosition.getChildIndexToCross() != -1 && crossWordWithPosition.getChildIndexToCross() < wordCharArray.length
                        && String.valueOf(wordCharArray[crossWordWithPosition.getChildIndexToCross()]).equals(crossWordWithPosition.getChildLetterToCross())) {
                    result.add(word);
                }
            }
        }
        return result;
    }

    public static String getFirstMatchingWord(List<String> allWords, CrossWordWithPosition
            crossWordWithPosition) {
        List<String> matchingWords = getMatchingWords(allWords, crossWordWithPosition);
        Collections.shuffle(matchingWords);
        return matchingWords.size() > 0 ? matchingWords.get(0) : null;
    }

    private CrossWordWithPosition getParentCrossword
            (Collection<CrossWordWithPosition> addedCrosswords, int positionInCrossWord) {
        return getCrosswordForIndex(addedCrosswords, positionInCrossWord - 1);
    }

    private CrossWordWithPosition getSameDimensionParentCrossword
            (Collection<CrossWordWithPosition> addedCrosswords, int positionInCrossWord) {
        return getCrosswordForIndex(addedCrosswords, positionInCrossWord - 2);
    }


    public CrossWordWithPosition getCrosswordForIndex
            (Collection<CrossWordWithPosition> addedCrosswords, int index) {
        for (CrossWordWithPosition CrossWordWithPosition : addedCrosswords) {
            if (CrossWordWithPosition.getPositionInCrossWord() == index) {
                return CrossWordWithPosition;
            }
        }
        return null;
    }

    public Set<String> findCommonLetterWords(Set<String> allWords, String baseWord) {
        Set<String> set = new HashSet<>();
        for (String word : allWords) {
            if (childIsValid(baseWord, word)) {
                set.add(word);
            }
        }
        return set;
    }

    private boolean childIsValid(String word1, String word2) {
        for (char w1c : word1.toCharArray()) {
            int i = 0;
            for (char w2c : word2.toCharArray()) {
                if (w1c == w2c && i < word2.length() - 2) {
                    return true;
                }
                i++;
            }
        }
        return false;
    }

    //LEFT - Parent info
    //RIGHT - Child Info
    public MutablePair<MutablePair<Integer, String>, MutablePair<Integer, String>> getRandomMatchingLetterPositionChildForParentAndBaseForChild
    (String baseWord, String parentWord) {
        MutablePair<MutablePair<Integer, String>, MutablePair<Integer, String>> pos = new MutablePair<MutablePair<Integer, String>, MutablePair<Integer, String>>(new MutablePair<Integer, String>(-1, null), new MutablePair<Integer, String>(-1, null));
        ////////////////TRIES//////////////
        int nrOfTries = 1000;
        ////////////////TRIES//////////////
        for (int i = 0; i < nrOfTries; i++) {
            int baseWordPos = new Random().nextInt(baseWord.length() - 2);
            int parentPos = new Random().nextInt(parentWord.length());
            if (baseWord.toCharArray()[baseWordPos] == parentWord.toCharArray()[parentPos]) {
                pos = new MutablePair<>(
                        new MutablePair<>(parentPos, String.valueOf(parentWord.toCharArray()[parentPos])),
                        new MutablePair<>(baseWordPos, String.valueOf(baseWord.toCharArray()[baseWordPos])));
                break;
            }
        }
        return pos;
    }
}
