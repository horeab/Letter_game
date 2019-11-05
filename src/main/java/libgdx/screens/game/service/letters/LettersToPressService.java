package libgdx.screens.game.service.letters;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import libgdx.controls.button.MyButton;
import libgdx.model.LetterButton;
import libgdx.resources.LettersGameButtonSize;
import libgdx.resources.LettersGameButtonSkin;
import libgdx.services.Utils;

public abstract class LettersToPressService {

    public static final float MOVE_BUTTON_DURATION = 0.4f;
    static final int MAX_LETTERS_TO_PRESS_ON_ROW = 9;

    LetterPositionService letterPositionService;

    List<LetterButton> pressedLetterButtons = new ArrayList<>();
    private List<MyButton> buttonsWithHintSkin = new ArrayList<>();
    private List<String> gameLetters;
    private Map<String, LetterButton> letterButtons;

    public LettersToPressService(int totalNrOfLetters, List<String> gameLetters) {
        this.gameLetters = gameLetters;
        this.letterPositionService = new LetterPositionService(
                getLetterButtonSize(),
                getNrOfButtonsOnRow(),
                totalNrOfLetters,
                getInitialButtonsY());
        this.letterButtons = new LetterButtonCreator(getLetterButtonSize(), getLetterBtnSkin(), getLettersCoordList()).createLetterButtons(gameLetters);
        prepareLetterButtons(letterButtons);
    }

    public abstract float getInitialButtonsY();

    public abstract float buttonMoveToX(MyButton myButton);

    public abstract float buttonMoveToY(MyButton myButton);

    public abstract boolean executeLetterButtonClick();

    private void prepareLetterButtons(Map<String, LetterButton> letterButtons) {
        for (final LetterButton letterButton : letterButtons.values()) {
            letterButton.getMyButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (executeLetterButtonClick()) {
                        onClickLetterButton(letterButton);
                    }
                }
            });
        }
    }

    public List<Pair<Float, Float>> getLettersCoordList() {
        return letterPositionService.getLettersCoordList();
    }

    float getAmountToChangeBtnSize() {
        return 0.10f;
    }

    void onClickLetterButton(LetterButton letterButton) {
        pressedLetterButtons.add(letterButton);
        MyButton myButton = letterButton.getMyButton();
        myButton.setOrigin(Align.center);
        myButton.addAction(Actions.moveTo(buttonMoveToX(myButton), buttonMoveToY(myButton), MOVE_BUTTON_DURATION));
        float diff = getAmountToChangeBtnSize();
        myButton.addAction(Actions.scaleBy(-diff, -diff, MOVE_BUTTON_DURATION));
        myButton.setTouchable(Touchable.disabled);
        myButton.toFront();
    }

//    private void processButtonAvailability(LetterButton button) {
//        List<String> lettersToPressFromBoard = new ArrayList<>();
//        List<CrossWord> crossWordsNotFound = new ArrayList<>(crossWordContext.getAllCrossWords());
//        crossWordsNotFound.removeAll(crossWordContext.getCrossWordDisplayService().getAlreadyPressedCorrectWords());
//        for (CrossWord crossWord : crossWordsNotFound) {
//            lettersToPressFromBoard.addAll(new LettersGameService().getWordLetters(crossWord.getWord()));
//        }
//        if (!lettersToPressFromBoard.contains(button.getLetterBtn())) {
//            CrossWordDisplayService.scaleDownActor(button.getMyButton());
//            letterButtons.remove(button.getKey());
//        }
//    }

    public boolean areAnyButtonsPressed() {
        return !pressedLetterButtons.isEmpty();
    }

    private int getNrOfButtonsOnRow() {
        return gameLetters.size() / 2;
    }

    public void movePressedLetterButtonsToOriginal() {
        while (!pressedLetterButtons.isEmpty()) {
            moveLastPressedLetterButtonBackToOriginal();
        }
    }

    public void moveLastPressedLetterButtonBackToOriginal() {
        if (pressedLetterButtons.size() > 0) {
            final LetterButton lastPressedItem = pressedLetterButtons.get(pressedLetterButtons.size() - 1);
            MyButton myButton = lastPressedItem.getMyButton();
            myButton.getCenterRow().padBottom(0);
            myButton.addAction(Actions.moveTo(lastPressedItem.getOriginalX(), lastPressedItem.getOriginalY(), 0.5f));
            float diff = getAmountToChangeBtnSize();
            myButton.setButtonSkin(buttonsWithHintSkin.contains(myButton) ? LettersGameButtonSkin.LETTER_BTN_HINT : LettersGameButtonSkin.LETTER_BTN);
            myButton.addAction(Actions.sequence(
                    Actions.scaleBy(diff, diff, LettersToPressService.MOVE_BUTTON_DURATION),
                    Utils.createRunnableAction(new Runnable() {
                        @Override
                        public void run() {
//                            processButtonAvailability(lastPressedItem);
                        }
                    })));
            pressedLetterButtons.remove(pressedLetterButtons.size() - 1);
            myButton.setTouchable(Touchable.enabled);
        }
    }

    public List<String> getPressedLetters() {
        List<String> pressedLetters = new ArrayList<>();
        for (LetterButton button : pressedLetterButtons) {
            pressedLetters.add(button.getLetter());
        }
        return pressedLetters;
    }

    LettersGameButtonSize getLetterButtonSize() {
        return LettersGameButtonSize.LETTER_BUTTON;
    }

    public Map<String, LetterButton> getLetterButtons() {
        return letterButtons;
    }

    public void resetButtonsSkin() {
        buttonsWithHintSkin.clear();
        for (LetterButton button : letterButtons.values()) {
            button.getMyButton().setButtonSkin(getLetterBtnSkin());
        }
    }

    public void hideLetterButtons() {
        movePressedLetterButtonsToOriginal();
        for (LetterButton button : letterButtons.values()) {
            Utils.scaleDownButton(button.getMyButton());
        }
    }

    public void showLetterButtons() {
        movePressedLetterButtonsToOriginal();
        for (LetterButton button : letterButtons.values()) {
            Utils.scaleUpButton(button.getMyButton());
        }
    }

    public void bringButtonsToFront() {
        movePressedLetterButtonsToOriginal();
        for (LetterButton button : letterButtons.values()) {
            button.getMyButton().toFront();
        }
    }

    public void setHintButtonsSkin(List<String> buttonKeys) {
        List<MyButton> buttonsToChangeSkin = new ArrayList<>();
        for (String key : buttonKeys) {
            int i = 0;
            LetterButton button = getLetterButtons().get(key + i);
            while (button != null && (button.getMyButton().getButtonSkin() == LettersGameButtonSkin.LETTER_BTN_HINT
                    || buttonsToChangeSkin.contains(button.getMyButton()))) {
                i++;
                button = getLetterButtons().get(key + i);
            }
            if (button != null) {
                buttonsToChangeSkin.add(button.getMyButton());
            }
        }
        for (MyButton button : buttonsToChangeSkin) {
            button.setButtonSkin(LettersGameButtonSkin.LETTER_BTN_HINT);
            buttonsWithHintSkin.add(button);
        }
    }

    private LettersGameButtonSkin getLetterBtnSkin() {
        return LettersGameButtonSkin.LETTER_BTN;
    }
}
