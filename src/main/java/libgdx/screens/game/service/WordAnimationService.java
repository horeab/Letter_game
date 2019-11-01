package libgdx.screens.game.service;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import libgdx.campaign.StarsBarCreator;
import libgdx.campaign.StarsService;
import libgdx.controls.animations.ActorAnimation;
import libgdx.controls.button.MyButton;
import libgdx.controls.label.MyWrappedLabel;
import libgdx.controls.label.MyWrappedLabelConfigBuilder;
import libgdx.game.Game;
import libgdx.graphics.GraphicUtils;
import libgdx.resources.FontManager;
import libgdx.resources.Resource;
import libgdx.resources.dimen.MainDimen;
import libgdx.screen.AbstractScreen;
import libgdx.services.ImageAnimation;
import libgdx.services.Utils;
import libgdx.utils.ScreenDimensionsManager;
import libgdx.utils.model.FontColor;

public class WordAnimationService {

    private AbstractScreen abstractScreen;

    public WordAnimationService() {
        this.abstractScreen = Game.getInstance().getAbstractScreen();
    }

    void animateHintBtnZoomInZoomOut(MyButton hintBtn) {
        new ActorAnimation(hintBtn, abstractScreen).animateZoomInZoomOut();
    }

    public void createWordInfoLabelAnimation(String text, FontColor fontColor) {
        MyWrappedLabel infoLabel = new MyWrappedLabel(new MyWrappedLabelConfigBuilder().setFontScale(FontManager.getBigFontDim()).setText(text).setTextColor(fontColor).build());
        addWordInfoAnimation(infoLabel);
        infoLabel.setX(ScreenDimensionsManager.getScreenWidthValue(50));
        infoLabel.setY(ScreenDimensionsManager.getScreenHeightValue(60));
        abstractScreen.addActor(infoLabel);
    }

    private void addWordInfoAnimation(Table actor) {
        actor.setTransform(true);
        actor.setOrigin(Align.center);
        float duration = 1f;
        actor.addAction(
                Actions.sequence(
                        Actions.fadeOut(duration),
                        Utils.createRemoveActorAction(actor)
                ));
        actor.addAction(Actions.scaleBy(-0.1f, -0.1f, duration));
    }


    public void addCorrectWordAnimation() {
        Image correctImg = GraphicUtils.getImage(Resource.submit_btn_up);
        float sideDimen = MainDimen.horizontal_general_margin.getDimen();
        correctImg.setHeight(sideDimen * 3);
        correctImg.setWidth(sideDimen * 3);
        abstractScreen.addActor(correctImg);
        correctImg.setY(ScreenDimensionsManager.getScreenHeightValue(47));
        float duration = 0.4f;
        correctImg.addAction(
                Actions.sequence(
                        Actions.moveBy(
                                ScreenDimensionsManager.getScreenWidthValue(5),
                                ScreenDimensionsManager.getScreenHeightValue(50),
                                duration),
                        createSoundAction()
                ));
        correctImg.addAction(Actions.scaleBy(2f, 2f, duration));
        correctImg.addAction(Actions.fadeOut(duration));
    }

    private RunnableAction createSoundAction() {
        RunnableAction playSoundAction = new RunnableAction();
        playSoundAction.setRunnable(new Runnable() {
            @Override
            public void run() {
            }
        });
        return playSoundAction;
    }

    public void animateGameWin(Runnable runnable, int starsWon) {
        Table horizontalStarsBar = new StarsBarCreator(starsWon).createHorizontalStarsBar(MainDimen.vertical_general_margin.getDimen() * 3f);
        horizontalStarsBar.setBackground(GraphicUtils.getNinePatch(Resource.final_word_stars_background));
        abstractScreen.addActor(new ImageAnimation(abstractScreen).animateWinGame(runnable, horizontalStarsBar));
    }

}
