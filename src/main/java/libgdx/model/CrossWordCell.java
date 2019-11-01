package libgdx.model;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

import libgdx.utils.model.FontColor;
import org.apache.commons.lang3.StringUtils;

import libgdx.controls.label.MyWrappedLabel;
import libgdx.controls.label.MyWrappedLabelConfigBuilder;
import libgdx.resources.ResourcesManager;
import libgdx.services.CrossWordMatrixEncoder;

public class CrossWordCell implements TextCell {

    private Table cell;
    private String[] positionsInCrossWord;
    private MyWrappedLabel letter;

    public CrossWordCell(Table cell, String matrixVal) {
        this.cell = cell;
        CrossWordMatrixEncoder crossWordMatrixEncoder = new CrossWordMatrixEncoder();
        String text = crossWordMatrixEncoder.decodeLetterValue(matrixVal);
        MyWrappedLabel letterLabel = new MyWrappedLabel(new MyWrappedLabelConfigBuilder()
                .setText(text)
                .setSingleLineLabel()
                .setTextColor(FontColor.BLACK).build());
        letterLabel.setVisible(StringUtils.isNotBlank(text));
        cell.add(letterLabel);
        this.letter = letterLabel;
        this.positionsInCrossWord = crossWordMatrixEncoder.getPositionsInCrossWord(matrixVal);
    }

    @Override
    public String getTextDisplayedForCell() {
        String text = "";
        if (letter.isVisible()) {
            text = letter.getLabels().get(0).getText().toString();
        }
        return text;
    }

    public MyWrappedLabel getLetter() {
        return letter;
    }

    public String[] getPositionsInCrossWord() {
        return positionsInCrossWord;
    }

    public Table getCell() {
        return cell;
    }

}