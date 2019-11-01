package libgdx.services;

import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import libgdx.controls.button.ButtonBuilder;
import libgdx.controls.button.MyButton;
import libgdx.controls.labelimage.LabelImage;
import libgdx.graphics.GraphicUtils;
import libgdx.resources.FontManager;
import libgdx.resources.LettersGameButtonSize;
import libgdx.resources.LettersGameButtonSkin;
import libgdx.resources.LettersGameLabel;
import libgdx.resources.Res;
import libgdx.resources.Resource;
import libgdx.resources.dimen.MainDimen;
import libgdx.resources.gamelabel.GameLabel;

public class MainMenuButtonBuilder extends ButtonBuilder {

    private Res icon;
    private GameLabel text;
    private float fontDimen = FontManager.getNormalFontDim();

    public ButtonBuilder setFontDimen(float fontDimen) {
        this.fontDimen = fontDimen;
        return this;
    }

    public MainMenuButtonBuilder setStartGameButton() {
        this.icon = Resource.flower;
        setup(LettersGameLabel.start_game);
        return this;
    }

    private void setup(GameLabel text) {
        this.text = text;
        setButtonSkin(LettersGameButtonSkin.MAINMENU);
        setFixedButtonSize(LettersGameButtonSize.MAINMENU);
    }

    @Override
    public MyButton build() {
        Table table = new Table();
        float horizontalGeneralMarginDimen = MainDimen.horizontal_general_margin.getDimen();
        float iconDimen = horizontalGeneralMarginDimen * 5;
        Stack iconWithSparkle = new Stack();
        iconWithSparkle.addActor(GraphicUtils.getImage(icon));
        Table iconTable = new Table();
        iconTable.add(iconWithSparkle)
                .width(iconDimen)
                .height(iconDimen);
        LabelImage textTable = createTextTable(text.getText(), horizontalGeneralMarginDimen * 17, fontDimen);
        table.add(iconTable).padRight(horizontalGeneralMarginDimen);
        table.add(textTable).padTop(MainDimen.vertical_general_margin.getDimen() / 2);
        addCenterTextImageColumn(table);
        return super.build();
    }

}
