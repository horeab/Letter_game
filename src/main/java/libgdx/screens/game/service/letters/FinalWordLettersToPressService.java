package libgdx.screens.game.service.letters;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.ArrayList;
import java.util.List;

import libgdx.controls.button.MyButton;
import libgdx.game.Game;
import libgdx.model.FinalWordCell;
import libgdx.model.LetterButton;
import libgdx.resources.FontManager;
import libgdx.resources.LettersGameButtonSize;
import libgdx.resources.LettersGameButtonSkin;
import libgdx.resources.dimen.MainDimen;
import libgdx.screen.AbstractScreen;
import libgdx.screens.game.service.Utils;
import libgdx.utils.ScreenDimensionsManager;

public class FinalWordLettersToPressService extends LettersToPressService {

    private List<FinalWordCell> finalWordCells;

    public FinalWordLettersToPressService(int totalNrOfLetters, List<String> gameLetters, List<FinalWordCell> finalWordCells) {
        super(totalNrOfLetters, gameLetters);
        this.finalWordCells = finalWordCells;
        addButtonsToPanel();
    }

    @Override
    float getAmountToChangeBtnSize() {
        return 0f;
    }

    @Override
    void onClickLetterButton(LetterButton letterButton) {
        super.onClickLetterButton(letterButton);
        letterButton.getMyButton().getCenterRow().padBottom(FinalWordCell.LETTER_PAD_BOTTOM);
        letterButton.getMyButton().setButtonSkin(LettersGameButtonSkin.FINAL_WORD_UNKNOWN_PRESS);
    }

    private void addButtonsToPanel() {
        AbstractScreen abstractScreen = Game.getInstance().getAbstractScreen();
        for (LetterButton letterButton : getLetterButtons().values()) {
            MyButton myButton = letterButton.getMyButton();
            myButton.setScale(0);
            abstractScreen.addActor(myButton);
        }
    }

    private List<FinalWordCell> getEmptyCells() {
        List<FinalWordCell> result = new ArrayList<>();
        for (FinalWordCell cell : finalWordCells) {
            if (!cell.isTextTableVisible()) {
                result.add(cell);
            }
        }
        return result;
    }

    private Table getUnpressedFinalWordCell() {
        return getEmptyCells().get(pressedLetterButtons.size() - 1).getCell();
    }

    @Override
    public float buttonMoveToX(MyButton myButton) {
        return getUnpressedFinalWordCell().getX() - ScreenDimensionsManager.getScreenWidthValue(2);
    }

    @Override
    public float buttonMoveToY(MyButton myButton) {
        return getUnpressedFinalWordCell().getY() - ScreenDimensionsManager.getScreenHeightValue(2);
    }

    @Override
    public float getInitialButtonsY() {
        return Utils.getLettersToPressButtonsY();
    }

    @Override
    public boolean executeLetterButtonClick() {
        return pressedLetterButtons.size() < getEmptyCells().size();
    }
}
