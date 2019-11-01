package libgdx.screens.game.service.crossworddisplay;

import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import libgdx.graphics.GraphicUtils;
import libgdx.model.CrossWordCell;
import libgdx.model.CrossWordWithPosition;
import libgdx.resources.Resource;
import libgdx.resources.ResourcesManager;
import libgdx.services.CrossWordContext;
import libgdx.services.CrossWordService;
import libgdx.utils.ScreenDimensionsManager;
import libgdx.utils.model.FontColor;

public class HighlightCrossWordDisplayService extends CrossWordDisplayService {

    public HighlightCrossWordDisplayService(CrossWordContext crossWordContext) {
        super(crossWordContext);
    }

    @Override
    float getMaxScreenHeight() {
        return ScreenDimensionsManager.getScreenHeightValue(43);
    }


    private void highlightAllCrosswords() {
        CrossWordWithPosition activeHighlightedCrossword = getActiveHighlightedCrossword();
        if (activeHighlightedCrossword != null) {
            highlightCrossword(activeHighlightedCrossword.getPositionInCrossWord());
        }
    }

    public CrossWordWithPosition getActiveHighlightedCrossword() {
        for (CrossWordWithPosition crossWord : correctWords) {
            if (!alreadyPressedCorrectWords.contains(crossWord)) {
                return crossWord;
            }
        }
        return null;
    }

    public List<CrossWordCell> getCellsForPositionInCrossword(int positionInCrossWord) {
        List<CrossWordCell> result = new ArrayList<>();
        for (CrossWordCell crossWordCell : getCells()) {
            if (Arrays.asList(crossWordCell.getPositionsInCrossWord()).contains(String.valueOf(positionInCrossWord))) {
                result.add(crossWordCell);
            }
        }
        return result;
    }

    private void highlightCrossword(int positionInCrossWord) {
        for (CrossWordCell crossWordCell : getCells()) {
            crossWordCell.getLetter().setTextColor(FontColor.GRAY);
            crossWordCell.getCell().setBackground(GraphicUtils.getNinePatch(Resource.found_all));
        }
        for (CrossWordCell crossWordCell : getCellsForPositionInCrossword(positionInCrossWord)) {
            crossWordCell.getCell().setBackground(GraphicUtils.getNinePatch(crossWordCell.getLetter().isVisible() ? getHorizontalOrVerticalCellBackground(positionInCrossWord) : CrossWordService.isHorizontal(positionInCrossWord) ? Resource.crossword_horizontal : Resource.crossword_vertical));
            crossWordCell.getLetter().setTextColor(FontColor.BLACK);
        }
    }

    @Override
    void verifyIfWordWasRandomFound() {
        super.verifyIfWordWasRandomFound();
        highlightAllCrosswords();
    }

    @Override
    NinePatchDrawable getCellEmptyDefaultBackground(String matrixVal) {
        return null;
    }
}
