package libgdx.screens.game.service.submitanddelete;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;

import libgdx.game.LettersGame;
import libgdx.model.CrossWordWithPosition;

import libgdx.utils.model.FontColor;
import org.apache.commons.lang3.StringUtils;

import libgdx.game.Game;
import libgdx.model.LetterButton;
import libgdx.resources.LettersGameLabel;
import libgdx.resources.ResourcesManager;
import libgdx.screen.AbstractScreen;
import libgdx.screens.game.GameScreen;
import libgdx.screens.game.service.Utils;
import libgdx.screens.game.service.crossworddisplay.CrossWordDisplayService;
import libgdx.services.CrossWordContext;

public abstract class CrosswordSubmitAndDeleteService extends SubmitAndDeleteService {

    private CrossWordContext crossWordContext;

    CrosswordSubmitAndDeleteService(CrossWordContext crossWordContext) {
        super(crossWordContext.getLettersToPressService());
        this.crossWordContext = crossWordContext;
    }

    abstract CrossWordWithPosition isWordCorrect(String pressedWord);

    private boolean isWordAlreadyFound(String pressedWord) {
        return crossWordContext.getCrossWordDisplayService().wordAlreadyFound(pressedWord);
    }


    private void processWordWasFound(CrossWordWithPosition wordCorrect) {
        crossWordContext.getCrossWordDisplayService().processWordWasFound(wordCorrect);
    }

    @Override
    String getPressedWord() {
        return StringUtils.join(crossWordContext.getLettersToPressService().getPressedLetters().toArray());
    }

    @Override
    public void onClickSubmitBtn() {
        String pressedWord = getPressedWord();
        if (isSubmitBtnVisible()) {
            if (isWordAlreadyFound(pressedWord)) {
                wordAnimationService.createWordInfoLabelAnimation(LettersGameLabel.ALREADY_ADDED.getText(), FontColor.GREEN);
            } else {
                CrossWordWithPosition wordCorrect = isWordCorrect(pressedWord);
                processGameFinished();
                if (wordCorrect != null) {
                    lettersToPressService.resetButtonsSkin();
                    wordAnimationService.addCorrectWordAnimation();
                    wordCorrect.setFoundWord(pressedWord);
                    processWordWasFound(wordCorrect);
                    processGameFinished();
                }
                if (wordCorrect == null && StringUtils.isNotBlank(pressedWord)) {
                    Utils.animateIncorrectWord();
                }
            }
            lettersToPressService.movePressedLetterButtonsToOriginal();
        }
    }

    @Override
    public boolean processGameFinished() {
      final   AbstractScreen abstractScreen = Game.getInstance().getAbstractScreen();
        if (crossWordContext.getCrossWordDisplayService().areAllCellsDiscovered()) {
            for (LetterButton letterButton : crossWordContext.getLettersToPressService().getLetterButtons().values()) {
                CrossWordDisplayService.scaleDownActor(letterButton.getMyButton());
            }
            crossWordContext.getCrossWordDisplayService().scaleDownAllCells();
            RunnableAction createNewMatrixAction = new RunnableAction();
            createNewMatrixAction.setRunnable(new Runnable() {
                @Override
                public void run() {
                    ((GameScreen) abstractScreen).goToNextStage();
                }

            });
            abstractScreen.addAction(Actions.sequence(Actions.delay(CrossWordDisplayService.scaleDownDuration()), createNewMatrixAction));
            return true;
        }
        return false;
    }
}
