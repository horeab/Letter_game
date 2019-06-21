package libgdx.screens.game.service.finalword;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import libgdx.controls.animations.ActorAnimation;
import libgdx.controls.button.ButtonSize;
import libgdx.game.Game;
import libgdx.graphics.GraphicUtils;
import libgdx.model.FinalWordCell;
import libgdx.resources.LettersGameButtonSize;
import libgdx.resources.Resource;
import libgdx.resources.dimen.MainDimen;
import libgdx.screen.AbstractScreen;
import libgdx.screens.game.service.letters.LetterPositionService;
import libgdx.screens.game.service.letters.LettersToPressService;
import libgdx.services.Utils;
import libgdx.utils.ScreenDimensionsManager;

public class FinalWordService {

    public static final int TOTAL_STARS = 3;

    private static final float CONTROLPANEL_POPIN_DURATION = 0.1f;
    private List<FinalWordCell> finalWordCells;
    private List<Table> tries;
    private Table controlPanel;
    private Table starsBackgroundTable;
    private Table informationTable;
    private String finalWord;
    private int wrongPressedAnswers = 0;

    public FinalWordService(String finalWord) {
        finalWordCells = createFinalWordCell(
                Utils.textToStringChar(finalWord),
                new LetterPositionService(
                        LettersGameButtonSize.FINAL_WORD_BUTTON,
                        finalWord.length(),
                        finalWord.length(),
                        getLettersYPos()).getLettersCoordList());
        this.tries = createStarsTable();
        this.finalWord = finalWord;
    }

    private float getLettersYPos() {
        return ScreenDimensionsManager.getScreenHeightValue(93f);
    }

    private List<Table> createStarsTable() {
        List<Table> stars = new ArrayList<>();
        float imgDim = getStarSideDimen();
        for (int i = TOTAL_STARS - 1; i >= 0; i--) {
            Table table = new Table();
            table.setY(getStartYPos());
            table.setX(getStarPos(i));
            table.add(GraphicUtils.getImage(Resource.star_enabled)).padTop(getStarsTablePadTop()).width(imgDim).height(imgDim);
            table.addAction(Actions.fadeOut(0));
            stars.add(table);
        }
        return stars;
    }

    private float getStartYPos() {
        return getLettersYPos() - MainDimen.vertical_general_margin.getDimen() * 3;
    }

    private float getStarsTablePadTop() {
        return MainDimen.vertical_general_margin.getDimen() * 6;
    }

    private float getStarSideDimen() {
        return MainDimen.horizontal_general_margin.getDimen() * 8f;
    }

    public String getFinalWord() {
        return finalWord;
    }

    private float getStarPos(int i) {
        int screenWidth = ScreenDimensionsManager.getScreenWidth();
        float section = screenWidth / TOTAL_STARS;
        float pos = section * i;
        float result = section / 2 + pos;
        if (i == 0) {
            result = result + section / 2;
        }
        if (i == 2) {
            result = result - section / 2;
        }
        return result;
    }

    private void showTries() {
        starsBackgroundTable.addAction(Actions.fadeIn(CONTROLPANEL_POPIN_DURATION));
        starsBackgroundTable.toFront();
        informationTable.addAction(Actions.fadeIn(CONTROLPANEL_POPIN_DURATION));
        informationTable.toFront();
        for (Table table : tries) {
            table.addAction(Actions.fadeIn(CONTROLPANEL_POPIN_DURATION));
            table.toFront();
        }
    }

    private void hideTries() {
        starsBackgroundTable.addAction(Actions.fadeOut(CONTROLPANEL_POPIN_DURATION));
        informationTable.addAction(Actions.fadeOut(CONTROLPANEL_POPIN_DURATION));
        for (Table table : tries) {
            table.addAction(Actions.fadeOut(CONTROLPANEL_POPIN_DURATION));
        }
    }

    public int getActiveStars() {
        int result = TOTAL_STARS - wrongPressedAnswers;
        return result < 0 ? 0 : result;
    }

    public void wrongPressedAnswers() {
        wrongPressedAnswers++;
        float zoomAmount = 2f;
        float duration = 0.3f;
        for (int i = 0; i < wrongPressedAnswers; i++) {
            tries.get(i).clearChildren();
            Image image = GraphicUtils.getImage(Resource.star_disabled);
            if (i == wrongPressedAnswers - 1) {
                image.addAction(
                        Actions.sequence(
                                Actions.scaleBy(zoomAmount, zoomAmount, duration),
                                Actions.scaleBy(-zoomAmount, -zoomAmount, duration / 2f),
                                Actions.delay(0.3f)
                        ));
            }
            tries.get(i).add(image).padTop(getStarsTablePadTop()).width(getStarSideDimen()).height(getStarSideDimen());
        }
    }

    private List<FinalWordCell> createFinalWordCell(List<String> letters, List<Pair<Float, Float>> lettersCoordList) {
        List<FinalWordCell> finalWordCells = new ArrayList<>();
        AbstractScreen abstractScreen = Game.getInstance().getAbstractScreen();
        int i = 0;
        for (final String letter : letters) {
            Table table = new Table();
            Float x = lettersCoordList.get(i).getKey();
            Float y = lettersCoordList.get(i).getValue();
            table.setX(x);
            table.setY(y);
            table.setWidth(LettersGameButtonSize.FINAL_WORD_BUTTON.getWidth());
            table.setHeight(LettersGameButtonSize.FINAL_WORD_BUTTON.getHeight());
            table.setTransform(true);
            new ActorAnimation(table, abstractScreen).animateZoomInZoomOut(0.08f);
            FinalWordCell cell = new FinalWordCell(table, letter);
            finalWordCells.add(cell);
            i++;
        }
        return finalWordCells;
    }

    public void addListenersFinalWordCells(final Runnable onCreateProcessControlsVisibility, final Runnable onDestroyProcessControlsVisibility) {
        for (FinalWordCell finalWordCell : finalWordCells) {
            finalWordCell.getCell().getListeners().clear();
            finalWordCell.getCell().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (!Game.getInstance().getAbstractScreen().isLoading()) {
                        if (controlPanel == null) {
                            createControlPanel(onCreateProcessControlsVisibility);
                        } else {
                            destroyControlPanel(onDestroyProcessControlsVisibility);
                        }
                    }
                }
            });
        }
    }

    public void displayRandomLetter() {
        FinalWordCell finalWordCell = finalWordCells.get(new Random().nextInt(finalWordCells.size()));
        while (finalWordCell.isTextTableVisible()) {
            finalWordCell = finalWordCells.get(new Random().nextInt(finalWordCells.size()));
        }
        float zoomAmount = 2f;
        float duration = 0.3f;
        finalWordCell.getCell().addAction(
                Actions.sequence(
                        Actions.scaleBy(zoomAmount, zoomAmount, duration),
                        Actions.scaleBy(-zoomAmount, -zoomAmount, duration / 2f),
                        Actions.delay(0.3f)
                ));
        finalWordCell.setVisibleTextTable(true);
        int numberOfDisplayedLetters = 0;
        for (FinalWordCell cell : finalWordCells) {
            if (cell.isTextTableVisible()) {
                numberOfDisplayedLetters++;
            }
            if (numberOfDisplayedLetters > 1) {
                break;
            }
        }
        if (numberOfDisplayedLetters == 1) {
            float zoomAmount2 = 2f;
            float duration2 = 0.8f;
            Image pressFinger = GraphicUtils.getImage(Resource.press_finger);
            float imgDim = MainDimen.horizontal_general_margin.getDimen() * 10;
            pressFinger.setWidth(imgDim);
            pressFinger.setHeight(imgDim);
            pressFinger.addAction(
                    Actions.sequence(
                            Actions.scaleBy(zoomAmount2, zoomAmount2, duration2),
                            Actions.scaleBy(-zoomAmount2, -zoomAmount2, duration2 / 2f),
                            Actions.delay(0.3f),
                            Actions.fadeOut(0.5f),
                            Actions.removeActor(pressFinger)
                    ));
            pressFinger.setY(ScreenDimensionsManager.getScreenHeight() - imgDim);
            pressFinger.setX(ScreenDimensionsManager.getScreenWidthValue(50) - imgDim / 2);
            Game.getInstance().getAbstractScreen().addActor(pressFinger);
        }
    }

    private void bringFinalWordLettersToFront() {
        for (FinalWordCell cell : finalWordCells) {
            cell.getCell().toFront();
        }
    }

    public List<FinalWordCell> getFinalWordCells() {
        return finalWordCells;
    }

    public void addTries() {
        informationTable = new Table();

        starsBackgroundTable = new Table();
        starsBackgroundTable.setBackground(GraphicUtils.getNinePatch(Resource.final_word_stars_background));
        starsBackgroundTable.setY(getLettersYPos() - MainDimen.vertical_general_margin.getDimen() * 8.7f);
        starsBackgroundTable.setX(getStarPos(0) - MainDimen.horizontal_general_margin.getDimen() * 5.5f);
        starsBackgroundTable.setHeight(getStarSideDimen() + getStarSideDimen() / 4);
        starsBackgroundTable.setWidth(getStarSideDimen() * TOTAL_STARS + getStarSideDimen() / 2);
        starsBackgroundTable.addAction(Actions.fadeOut(0));
        Game.getInstance().getAbstractScreen().addActor(starsBackgroundTable);
        for (Table table : tries) {
            Game.getInstance().getAbstractScreen().addActor(table);
        }
    }

    public void addFinalWordLettersToScreen() {
        for (FinalWordCell cell : finalWordCells) {
            Game.getInstance().getAbstractScreen().addActor(cell.getCell());
        }
    }

    private void createControlPanel(Runnable processControlsVisibility) {
        Table controlPanel = new Table();
        AbstractScreen abstractScreen = Game.getInstance().getAbstractScreen();
        abstractScreen.addActor(controlPanel);
        controlPanel.addAction(Actions.fadeOut(0));
        controlPanel.setWidth(ScreenDimensionsManager.getExternalDeviceWidth());
        controlPanel.setHeight(ScreenDimensionsManager.getExternalDeviceHeight());
        controlPanel.setOrigin(Align.center);
        controlPanel.setBackground(GraphicUtils.getNinePatch(Resource.final_word_background));
        bringFinalWordLettersToFront();
        for (FinalWordCell cell : finalWordCells) {
            cell.getCell().setTransform(false);
        }
        controlPanel.addAction(Actions.sequence(
                Utils.createRunnableAction(processControlsVisibility),
                Actions.fadeIn(CONTROLPANEL_POPIN_DURATION),
                Utils.createRunnableAction(new Runnable() {
                    @Override
                    public void run() {
                        showTries();
                    }
                })
        ));

        this.controlPanel = controlPanel;
    }

    public void destroyControlPanel(Runnable processControlsVisibility) {
        hideTries();
        for (FinalWordCell cell : finalWordCells) {
            cell.getCell().setTransform(true);
        }
        controlPanel.addAction(Actions.sequence(
                Actions.fadeOut(CONTROLPANEL_POPIN_DURATION),
                Utils.createRunnableAction(processControlsVisibility),
                Utils.createRunnableAction(new Runnable() {
                    @Override
                    public void run() {
                        controlPanel.remove();
                        controlPanel = null;
                    }
                })));
    }

    public boolean isFinalWordContextActive() {
        return controlPanel != null;
    }

}
