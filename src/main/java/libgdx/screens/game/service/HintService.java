package libgdx.screens.game.service;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import libgdx.screens.game.service.letters.HighlightCrosswordLettersToPressService;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import libgdx.controls.button.ButtonBuilder;
import libgdx.controls.button.MyButton;
import libgdx.game.Game;
import libgdx.model.CrossWordWithPosition;
import libgdx.resources.LettersGameButtonSize;
import libgdx.resources.LettersGameButtonSkin;
import libgdx.screens.game.service.letters.LettersToPressService;
import libgdx.services.CrossWordContext;
import libgdx.services.CrossWordService;
import libgdx.services.LettersGameService;
import libgdx.services.Utils;
import libgdx.utils.ScreenDimensionsManager;

public class HintService {

    private final static int TOTAL_HINTS = 8;
        public static final float FADE_OUT_DURATION = 15000;
//    public static final float FADE_OUT_DURATION = 2000;

    private LettersToPressService lettersToPressService;
    private WordAnimationService wordAnimationService;
    private List<String> allPossibleCorrectWords;
    private List<CrossWordWithPosition> correctWords;
    private int pressedHintButtons = 0;
    private MyButton displayedHintButton;
    private CrossWordContext crossWordContext;
    private Map<Integer, String> proposedHintWords = new HashMap<>();

    public HintService(CrossWordContext crossWordContext) {
        this.crossWordContext = crossWordContext;
        this.allPossibleCorrectWords = new ArrayList<>(crossWordContext.getAllPossibleCorrectWords());
        this.correctWords = crossWordContext.getAllCrossWords();
        this.lettersToPressService = crossWordContext.getLettersToPressService();
        this.wordAnimationService = new WordAnimationService();
    }

    private boolean nextCrosswordsHasSameLengthAsLetters() {
        for (CrossWordWithPosition word : crossWordContext.getAllCrossWords()) {
            if (StringUtils.isBlank(word.getFoundWord()) && word.getWordLength() != crossWordContext.getTotalNrOfLetters()) {
                return false;
            }
        }
        return true;
    }

    public void displayHint(boolean isFinalWordContextActive) {
        if (displayedHintButton == null
                && pressedHintButtons < TOTAL_HINTS
                && !isFinalWordContextActive) {
            MyButton button = createHintBtn();
            float margin = getButtonSize().getWidth();
            int screenWidth = ScreenDimensionsManager.getScreenWidth();
            int randomX = new Random().nextInt(screenWidth);
            while (randomX > screenWidth - margin || randomX < margin) {
                randomX = new Random().nextInt(screenWidth);
            }
            button.setX(randomX);
            button.setY(ScreenDimensionsManager.getScreenHeight());
            float duration = FADE_OUT_DURATION / 1000;
            button.addAction(Actions.sequence(Actions.moveTo(button.getX(), 0, duration), setHintToNullRunnableAction()));
            button.addAction(Actions.sequence(Actions.fadeOut(duration), setHintToNullRunnableAction()));
            button.toFront();
            displayedHintButton = button;
            Game.getInstance().getAbstractScreen().addActor(button);
        }
    }

    private RunnableAction setHintToNullRunnableAction() {
        return Utils.createRunnableAction(new Runnable() {
            @Override
            public void run() {
                if (displayedHintButton != null) {
                    displayedHintButton.setVisible(false);
                    displayedHintButton.remove();
                }
                displayedHintButton = null;
            }
        });
    }

    public MyButton getDisplayedHintButton() {
        return displayedHintButton;
    }

    public void fadeOutDisplayedButton() {
        if (displayedHintButton != null) {
            displayedHintButton.addAction(Actions.sequence(Actions.fadeOut(0.1f), Utils.createRunnableAction(new Runnable() {
                @Override
                public void run() {
                    if (displayedHintButton != null) {
                        displayedHintButton.setVisible(false);
                        displayedHintButton = null;
                    }
                }
            })));
        }
    }

    private MyButton createHintBtn() {
        final MyButton button = new ButtonBuilder().setButtonSkin(LettersGameButtonSkin.HINT_BTN).setFixedButtonSize(getButtonSize()).build();
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                displayedHintButton = null;
                HighlightCrosswordLettersToPressService lettersToPressService = (HighlightCrosswordLettersToPressService) crossWordContext.getLettersToPressService();
                String alreadyPressedWord = lettersToPressService.getAlreadyPressedWord();
                if (!getHintWord().substring(0, alreadyPressedWord.length()).equals(alreadyPressedWord)) {
                    crossWordContext.getLettersToPressService().movePressedLetterButtonsToOriginal();
                }
                int firstEmptyCellIndex = lettersToPressService.getEmptyCellIndexes().get(crossWordContext.getLettersToPressService().getPressedLetters().size());
                List<String> wordLetters = new LettersGameService(crossWordContext.getTotalCrossWords(), crossWordContext.getTotalNrOfLetters()).getWordLetters(getHintWord());
                if (firstEmptyCellIndex != -1) {
                    HintService.this.lettersToPressService.setHintButtonsSkin(Collections.singletonList(wordLetters.get(firstEmptyCellIndex)));
                    button.setTouchable(Touchable.disabled);
                    button.addAction(Actions.sequence(Actions.fadeOut(0.5f), Utils.createRunnableAction(new Runnable() {
                        @Override
                        public void run() {
                            button.setVisible(false);
                            button.remove();
                        }
                    })));
                    pressedHintButtons++;
                }
            }
        });
        button.setTransform(true);
        wordAnimationService.animateHintBtnZoomInZoomOut(button);
        return button;
    }

    private LettersGameButtonSize getButtonSize() {
        return LettersGameButtonSize.HINT_BUTTON;
    }

    private String getHintWord() {
        int foundWords = crossWordContext.getCrossWordDisplayService().getAlreadyPressedCorrectWords().size();
        String proposedHintWord = proposedHintWords.get(foundWords);
        if (proposedHintWord == null) {
            proposedHintWord = "";
            for (CrossWordWithPosition word : correctWords) {
                if (StringUtils.isBlank(word.getFoundWord())) {
                    String firstMatchingWord = CrossWordService.getFirstMatchingWord(allPossibleCorrectWords, word);
                    while (crossWordContext.getCrossWordDisplayService().getAlreadyPressedCorrectWords().contains(firstMatchingWord)) {
                        firstMatchingWord = CrossWordService.getFirstMatchingWord(allPossibleCorrectWords, word);
                    }
                    proposedHintWord = firstMatchingWord;
                    break;
                }
            }
            proposedHintWords.put(foundWords, proposedHintWord);
        }
        return proposedHintWord;
    }
}
