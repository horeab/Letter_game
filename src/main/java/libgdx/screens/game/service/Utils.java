package libgdx.screens.game.service;

import libgdx.utils.model.FontColor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import libgdx.model.TextCell;
import libgdx.resources.LettersGameLabel;
import libgdx.resources.ResourcesManager;
import libgdx.resources.dimen.MainDimen;
import libgdx.utils.ScreenDimensionsManager;

public class Utils {

    public static float getLettersToPressButtonsY() {
        return ScreenDimensionsManager.getScreenHeightValue(13);
    }

    private static float getSubmitDeleteShuffleBackspaceToPressButtonsY(float multiplier) {
        return getLettersToPressButtonsY() + MainDimen.vertical_general_margin.getDimen() * multiplier;
    }

    public static float getBoardSubmitDeleteShuffleBackspaceToPressButtonsY() {
        return getSubmitDeleteShuffleBackspaceToPressButtonsY(11);
    }

    public static float getHighlightSubmitDeleteShuffleBackspaceToPressButtonsY() {
        return getSubmitDeleteShuffleBackspaceToPressButtonsY(6);
    }

    public static void animateIncorrectWord() {
        new WordAnimationService().createWordInfoLabelAnimation(LettersGameLabel.INCORRECT_WORD.getText(), FontColor.RED);
    }

    public static String getPressedWordForAlreadyDisplayedLetters(List<String> pressedLetters, List<? extends TextCell> cells) {
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        for (TextCell textCell : cells) {
            String text = "";
            String textDisplayedForCell = textCell.getTextDisplayedForCell();
            if (StringUtils.isNotBlank(textDisplayedForCell)) {
                text = textDisplayedForCell;
            } else if (i < pressedLetters.size()) {
                text = pressedLetters.get(i);
                i++;
            }
            stringBuilder.append(text);
        }
        return stringBuilder.toString();
    }
}
