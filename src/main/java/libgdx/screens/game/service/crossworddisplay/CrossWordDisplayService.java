package libgdx.screens.game.service.crossworddisplay;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;

import libgdx.model.CrossWordWithPosition;

import libgdx.utils.model.FontColor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import libgdx.controls.label.MyWrappedLabel;
import libgdx.game.Game;
import libgdx.graphics.GraphicUtils;
import libgdx.model.CrossWordCell;
import libgdx.resources.Resource;
import libgdx.resources.ResourcesManager;
import libgdx.resources.dimen.MainDimen;
import libgdx.screen.AbstractScreen;
import libgdx.screens.game.service.letters.LettersToPressService;
import libgdx.services.CrossWordContext;
import libgdx.services.CrossWordMatrixEncoder;
import libgdx.services.CrossWordService;
import libgdx.services.Utils;
import libgdx.utils.ScreenDimensionsManager;

public abstract class CrossWordDisplayService {

    List<CrossWordWithPosition> correctWords;
    Set<CrossWordWithPosition> alreadyPressedCorrectWords = new HashSet<>();
    private List<CrossWordCell> cells = new ArrayList<>();

    private AbstractScreen abstractScreen;
    private String[][] crossWordMatrix;
    private float cellSideDimen;
    private static final float POPIN_DURATION = 0.1f;

    CrossWordDisplayService(CrossWordContext crossWordContext) {
        this.correctWords = crossWordContext.getAllCrossWords();
        this.crossWordMatrix = crossWordContext.getCrossWordMatrix();
        this.abstractScreen = Game.getInstance().getAbstractScreen();
        this.cellSideDimen = calculateCellSideDimen();
    }

    abstract float getMaxScreenHeight();

    public Set<String> getAlreadyPressedCorrectWords() {
        Set<String> alreadyPressedCorrectWords = new HashSet<>();
        for (CrossWordWithPosition word : this.alreadyPressedCorrectWords) {
            alreadyPressedCorrectWords.add(word.getFoundWord());
        }
        return alreadyPressedCorrectWords;
    }

    private float getMaxScreenWidth() {
        return ScreenDimensionsManager.getScreenWidth();
    }

    private Resource getCellBackgroundResource(CrossWordCell crossWordCell, List<String> remainingPosInCrossWordCell) {
        Resource resource;
        if (remainingPosInCrossWordCell.size() > 1) {
            resource = Resource.remaining_more;
        } else if (remainingPosInCrossWordCell.size() == 0) {
            resource = Resource.found_all;
            crossWordCell.getLetter().setTextColor(FontColor.GRAY);
        } else {
            resource = getHorizontalOrVerticalCellBackground(Integer.valueOf(remainingPosInCrossWordCell.get(0)));
        }
        return resource;
    }

    Resource getHorizontalOrVerticalCellBackground(Integer positionInCrossWord) {
        return CrossWordService.isHorizontal(positionInCrossWord)
                ? Resource.remaining_horizontal : Resource.remaining_vertical;
    }

    private void animateLetterPopIn(MyWrappedLabel letter) {
        letter.setVisible(true);
        letter.setScale(0, 0);
        animatePopIn(letter, getAmountToIncreaseDiscoveredLetter());
    }

    private float getAmountToIncreaseDiscoveredLetter() {
        return 1f;
    }

    private Table createEmptyCell(int xPosInMatrix, int yPosInMatrix, String matrixVal) {
        float amountToIncrease = cellSideDimen / 400;
        Table emptyCell = new Table();
        emptyCell.addAction(Actions.scaleBy(-amountToIncrease, -amountToIncrease));
        emptyCell.setBackground(getCellEmptyDefaultBackground(matrixVal));
        emptyCell.setHeight(cellSideDimen);
        emptyCell.setWidth(cellSideDimen);
        emptyCell.setY(getCellY(yPosInMatrix) - getVerticalMargin());
        emptyCell.setX(getHorizontalMargin() + getCellX(xPosInMatrix));
        animatePopIn(emptyCell, amountToIncrease);
        CrossWordCell crossWordCell = new CrossWordCell(emptyCell, matrixVal);
        MyWrappedLabel letter = crossWordCell.getLetter();
        float multiplier = getMatrixCols() <= 3 && getMatrixRows() <= 3 ? 2.5f : 1.5f;
        letter.setFontScale(letter.getLabels().get(0).getFontScaleX() * multiplier);
        cells.add(crossWordCell);
        return emptyCell;
    }

    NinePatchDrawable getCellEmptyDefaultBackground(String matrixVal) {
        return GraphicUtils.getNinePatch(new CrossWordMatrixEncoder().getCellBackgroundResToDisplay(matrixVal));
    }

    private static void animatePopIn(Table table, float amountToIncrease) {
        table.addAction(Actions.sequence(Actions.fadeIn(POPIN_DURATION)));
        table.setTransform(true);
        table.addAction(Actions.scaleBy(amountToIncrease, amountToIncrease, POPIN_DURATION));
    }


    private float getVerticalMargin() {
        float lastCellX = getCellY(getMatrixRows()) - cellSideDimen;
        float firstCellX = getCellY(0);
        float diff = firstCellX - lastCellX;
        return (Math.abs(diff - getMaxScreenHeight())) / 2;
    }

    private float getHorizontalMargin() {
        float lastCellX = getCellX(getMatrixCols());
        return (getMaxScreenWidth() - lastCellX) / 2;
    }

    private float getCellY(int yPosInMatrix) {
        float verticalMargin = MainDimen.vertical_general_margin.getDimen() / 2.5f;
        float firstRowY = ScreenDimensionsManager.getScreenHeightValue(91) - cellSideDimen;
        return firstRowY - yPosInMatrix * cellSideDimen - yPosInMatrix * verticalMargin;
    }

    private float getCellX(int xPosInMatrix) {
        float horizontalGeneralMarginDimen = MainDimen.horizontal_general_margin.getDimen();
        float horizontalMargin = horizontalGeneralMarginDimen / 1.5f;
        return horizontalGeneralMarginDimen + xPosInMatrix * cellSideDimen + xPosInMatrix * horizontalMargin;
    }

    public void createCrossWordMatrix() {
        final MutableBoolean isHorizontal = new MutableBoolean(true);
        RunnableAction[] createMatrixAnimation = new RunnableAction[getMatrixRows()];
        for (int i = 0; i < getMatrixRows(); i++) {
            final int iValue = i;
            RunnableAction action = new RunnableAction();
            action.setRunnable(new Runnable() {
                @Override
                public void run() {
                    createMatrixRow(crossWordMatrix[iValue], iValue);
                    isHorizontal.setValue(!isHorizontal.getValue());
                }
            });
            createMatrixAnimation[i] = action;
        }
        RunnableAction resolveAction = new RunnableAction();
        resolveAction.setRunnable(new Runnable() {
            @Override
            public void run() {
//                resolveRandomCells(cells.size() / 5);
                verifyIfWordWasRandomFound();
            }
        });
        RunnableAction[] animationAndResolveArray = Arrays.copyOf(createMatrixAnimation, createMatrixAnimation.length + 1);
        animationAndResolveArray[animationAndResolveArray.length - 1] = resolveAction;
        abstractScreen.addAction(Actions.sequence(animationAndResolveArray));
    }

    public void processWordWasFound(CrossWordWithPosition crossWord) {
        alreadyPressedCorrectWords.add(crossWord);
        refreshCrosswordCells(alreadyPressedCorrectWords);
        verifyIfWordWasRandomFound();
        int index = 0;
        for (CrossWordCell cell : cells) {
            if (Arrays.asList(cell.getPositionsInCrossWord()).contains(String.valueOf(crossWord.getPositionInCrossWord()))) {
                MyWrappedLabel letter = cell.getLetter();
                letter.setVisible(true);
                letter.setText(String.valueOf(crossWord.getFoundWord().toCharArray()[index]));
                index++;
            }
        }
    }

    public boolean areAllCellsDiscovered() {
        for (CrossWordCell cell : cells) {
            if (!cell.getLetter().isVisible()) {
                return false;
            }
        }
        return true;
    }

    public static void scaleDownActor(Actor actor) {
        actor.setOrigin(Align.center);
        actor.addAction(Actions.sequence(Actions.scaleBy(-1, -1, scaleDownDuration()), Utils.createRemoveActorAction(actor)));
    }

    public void scaleDownAllCells() {
        for (final CrossWordCell crossWordCell : getCells()) {
            CrossWordDisplayService.scaleDownActor(crossWordCell.getCell());
        }
    }

    public static float scaleDownDuration() {
        return POPIN_DURATION * 3;
    }

    public boolean wordAlreadyFound(String pressedWord) {
        for (CrossWordWithPosition pos : alreadyPressedCorrectWords) {
            if (pressedWord.equals(pos.getFoundWord())) {
                return true;
            }
        }
        return false;
    }

    private void resolveRandomCells(int nrToResolve) {
        int resolved = 0;
        while (resolved < nrToResolve) {
            CrossWordCell crossWordCell = cells.get(new Random().nextInt(cells.size() - 1));
            MyWrappedLabel letter = crossWordCell.getLetter();
            crossWordCell.getCell().setBackground(GraphicUtils.getNinePatch(getCellBackgroundResource(crossWordCell, Arrays.asList(crossWordCell.getPositionsInCrossWord()))));
            if (!letter.isVisible()) {
                animateLetterPopIn(letter);
                resolved++;
            }
        }
    }

    void verifyIfWordWasRandomFound() {
        Map<Integer, Integer> positionsInCrossWordWithLettersFound = new HashMap<>();
        for (CrossWordCell cell : cells) {
            for (String positionInCrossWord : cell.getPositionsInCrossWord()) {
                Integer key = Integer.valueOf(positionInCrossWord);
                Integer value = positionsInCrossWordWithLettersFound.get(key);
                if (cell.getLetter().isVisible()) {
                    value = value != null ? value + 1 : 1;
                }
                positionsInCrossWordWithLettersFound.put(key, value);
            }
        }
        for (CrossWordWithPosition crossWord : correctWords) {
            Integer lettersFound = positionsInCrossWordWithLettersFound.get(crossWord.getPositionInCrossWord());
            if (lettersFound != null && lettersFound == crossWord.getWordLength()) {
                alreadyPressedCorrectWords.add(crossWord);
            }
        }
        refreshCrosswordCells(alreadyPressedCorrectWords);
    }

    private void refreshCrosswordCells(Set<CrossWordWithPosition> alreadyPressedCorrectWords) {
        for (CrossWordCell crossWordCell : getCells()) {
            List<String> remainingPositionInCell = getRemainingPositionInCell(crossWordCell, alreadyPressedCorrectWords);
            if (crossWordCell.getPositionsInCrossWord().length > remainingPositionInCell.size()) {
                Resource cellBackgroundResource = getCellBackgroundResource(crossWordCell, remainingPositionInCell);
                if (!crossWordCell.getLetter().isVisible()) {
                    animateLetterPopIn(crossWordCell.getLetter());
                }
                crossWordCell.getCell().setBackground(GraphicUtils.getNinePatch(cellBackgroundResource));
            }
        }
    }

    private List<String> getRemainingPositionInCell(CrossWordCell crossWordCell, Set<CrossWordWithPosition> alreadyPressedCorrectWords) {
        List<String> remainingPosInCrossWord = new ArrayList<>(Arrays.asList(crossWordCell.getPositionsInCrossWord()));
        for (CrossWordWithPosition crossWord : alreadyPressedCorrectWords) {
            remainingPosInCrossWord.remove(String.valueOf(crossWord.getPositionInCrossWord()));
        }
        return remainingPosInCrossWord;
    }

    private void createMatrixRow(String[] crossWordMatrix, int i) {
        for (int j = 0; j < crossWordMatrix.length; j++) {
            if (StringUtils.isNotBlank(crossWordMatrix[j])) {
                abstractScreen.addActor(createEmptyCell(j, i, crossWordMatrix[j]));
            }
        }
    }

    public float getCellSideDimen() {
        return cellSideDimen;
    }

    private float calculateCellSideDimen() {
        float horizontalGeneralMarginDimen = MainDimen.horizontal_general_margin.getDimen();
        float maxScreenWidth = getMaxScreenWidth() - horizontalGeneralMarginDimen * 2;
        float dimen = maxScreenWidth / 2;
        float valToModify = horizontalGeneralMarginDimen / 2;
        int matrixCols = getMatrixCols();
        while ((dimen * matrixCols) > maxScreenWidth) {
            dimen = dimen - valToModify;
        }
        dimen = dimen - valToModify;
        int matrixRows = getMatrixRows();
        float maxScreenHeight = getMaxScreenHeight();
        while ((dimen * matrixRows) > maxScreenHeight) {
            dimen = dimen - valToModify;
        }
        return dimen;
    }

    private int getMatrixCols() {
        return crossWordMatrix[0].length;
    }

    private int getMatrixRows() {
        return crossWordMatrix.length;
    }

    public List<CrossWordCell> getCells() {
        return cells;
    }
}
