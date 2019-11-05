package libgdx.screens.game.service.letters;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.ArrayList;
import java.util.List;

import libgdx.controls.button.MyButton;
import libgdx.game.LettersGame;
import libgdx.model.CrossWordCell;
import libgdx.model.LetterButton;
import libgdx.resources.LettersGameButtonSkin;
import libgdx.screens.game.service.Utils;
import libgdx.screens.game.service.crossworddisplay.HighlightCrossWordDisplayService;
import libgdx.services.CrossWordContext;
import libgdx.utils.ScreenDimensionsManager;
import org.apache.commons.lang3.StringUtils;

public class HighlightCrosswordLettersToPressService extends LettersToPressService {

    private CrossWordContext crossWordContext;
    private HighlightCrossWordDisplayService crossWordDisplayService;

    public HighlightCrosswordLettersToPressService(CrossWordContext crossWordContext) {
        super(crossWordContext.getTotalNrOfLetters(), crossWordContext.getGameLetters());
        this.crossWordDisplayService = (HighlightCrossWordDisplayService) crossWordContext.getCrossWordDisplayService();
        this.crossWordContext = crossWordContext;
    }

    @Override
    public float buttonMoveToX(MyButton myButton) {
        return getHighlightedCell().getX() + getMoveMargin();
    }

    @Override
    public float buttonMoveToY(MyButton myButton) {
        return getHighlightedCell().getY() + getMoveMargin();
    }

    private float getMoveMargin() {
        return -(getLetterButtonSize().getWidth() - crossWordDisplayService.getCellSideDimen()) / 2;
    }

    private Table getHighlightedCell() {
        return getHighlightedCrossWordCell().getCell();
    }

    private CrossWordCell getHighlightedCrossWordCell() {
        return getEmptyCellsForHighlightedCrossword().get(pressedLetterButtons.size() - 1);
    }

    public List<CrossWordCell> getEmptyCellsForHighlightedCrossword() {
        List<CrossWordCell> cellsForPositionInCrossword = crossWordDisplayService.getCellsForPositionInCrossword(crossWordDisplayService.getActiveHighlightedCrossword().getPositionInCrossWord());
        List<CrossWordCell> result = new ArrayList<>();
        for (CrossWordCell crossWordCell : cellsForPositionInCrossword) {
            if (!crossWordCell.getLetter().isVisible()) {
                result.add(crossWordCell);
            }
        }
        return result;
    }

    public String getAlreadyPressedWord() {
        List<CrossWordCell> cellsForPositionInCrossword = crossWordDisplayService.getCellsForPositionInCrossword(crossWordDisplayService.getActiveHighlightedCrossword().getPositionInCrossWord());
        StringBuilder result = new StringBuilder();
        for (int i = 0, j = 0; i < cellsForPositionInCrossword.size(); i++) {
            String textDisplayedForCell = cellsForPositionInCrossword.get(i).getTextDisplayedForCell();
            if (StringUtils.isNotBlank(textDisplayedForCell)) {
                result.append(textDisplayedForCell);
            } else {
                if (j < getPressedLetters().size()) {
                    result.append(getPressedLetters().get(j));
                    j++;
                } else {
                    break;
                }
            }
        }
        return result.toString();
    }

    public List<Integer> getEmptyCellIndexes() {
        List<CrossWordCell> cellsForPositionInCrossword = crossWordDisplayService.getCellsForPositionInCrossword(crossWordDisplayService.getActiveHighlightedCrossword().getPositionInCrossWord());
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < cellsForPositionInCrossword.size(); i++) {
            if (!cellsForPositionInCrossword.get(i).getLetter().isVisible()) {
                result.add(i);
            }
        }
        return result;
    }


    @Override
    public boolean executeLetterButtonClick() {
        return !crossWordContext.getCrossWordDisplayService().getCells().isEmpty() && pressedLetterButtons.size() < getEmptyCellsForHighlightedCrossword().size();
    }

    @Override
    float getAmountToChangeBtnSize() {
        return (getLetterButtonSize().getWidth() - crossWordDisplayService.getCellSideDimen()) / 200;
    }

    @Override
    void onClickLetterButton(LetterButton letterButton) {
        super.onClickLetterButton(letterButton);
        letterButton.getMyButton().setButtonSkin(LettersGameButtonSkin.TRANSPARENT_BTN);
        if (allEmptyCellsFilled()) {
            pressSubmitButtonDelayed();
        }
    }

    private void pressSubmitButtonDelayed() {
        LettersGame.getInstance().getAbstractScreen().addAction(Actions.sequence(
                Actions.delay(MOVE_BUTTON_DURATION),
                libgdx.services.Utils.createRunnableAction(new Runnable() {
                    @Override
                    public void run() {
                        crossWordContext.getSubmitAndDeleteService().onClickSubmitBtn();
                    }
                })));
    }

    @Override
    public float getInitialButtonsY() {
        return Utils.getLettersToPressButtonsY();
    }

    private boolean allEmptyCellsFilled() {
        return pressedLetterButtons.size() == getEmptyCellsForHighlightedCrossword().size();
    }
}
