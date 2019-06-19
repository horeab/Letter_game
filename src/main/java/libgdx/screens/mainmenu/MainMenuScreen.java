package libgdx.screens.mainmenu;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import libgdx.campaign.CampaignService;
import libgdx.campaign.LettersCampaignLevelEnum;
import libgdx.controls.animations.ActorAnimation;
import libgdx.controls.button.MyButton;
import libgdx.controls.label.MyWrappedLabel;
import libgdx.controls.label.MyWrappedLabelConfigBuilder;
import libgdx.game.Game;
import libgdx.graphics.GraphicUtils;
import libgdx.resources.FontManager;
import libgdx.resources.Resource;
import libgdx.resources.dimen.MainDimen;
import libgdx.screens.AbstractScreen;
import libgdx.services.BackButtonBuilder;
import libgdx.services.MainMenuButtonBuilder;
import libgdx.utils.ScreenDimensionsManager;

public class MainMenuScreen extends AbstractScreen {

    @Override
    public void buildStage() {
        addButtons();
    }

    private void addButtons() {
        Table table = new Table();
        float verticalGeneralMarginDimen = MainDimen.vertical_general_margin.getDimen();
        if (Gdx.app.getType() == Application.ApplicationType.iOS) {
            MyButton backBtn = new BackButtonBuilder().build();
            table.add(backBtn).padLeft(-MainDimen.horizontal_general_margin.getDimen() * 40).padTop(-verticalGeneralMarginDimen * 3).width(backBtn.getWidth()).height(backBtn.getHeight()).row();
        }
        table.setFillParent(true);
        addTitle(table);
        MyButton startGameBtn = createStartGameBtn();
        table.add(startGameBtn).height(ScreenDimensionsManager.getScreenHeightValue(13)).width(ScreenDimensionsManager.getScreenWidthValue(70)).padTop(verticalGeneralMarginDimen * 4).row();
        table.add().padTop(verticalGeneralMarginDimen * 17);
        addActor(table);
    }

    private void addTitle(Table table) {
        Image titleRaysImage = GraphicUtils.getImage(Resource.title_rays);
        new ActorAnimation(titleRaysImage, this).animateFastFadeInFadeOut();
        float titleWidth = ScreenDimensionsManager.getScreenWidth();
        float titleHeight = ScreenDimensionsManager.getNewHeightForNewWidth(titleWidth, titleRaysImage.getWidth(), titleRaysImage.getHeight());
        titleRaysImage.setWidth(titleWidth);
        titleRaysImage.setHeight(titleHeight);
        titleRaysImage.setY(ScreenDimensionsManager.getScreenHeightValue(49));
        addActor(titleRaysImage);
        Stack titleLabel = createTitleLabel();
        table.add(titleLabel)
                .width(titleWidth)
                .height(titleHeight)
                .padBottom(MainDimen.vertical_general_margin.getDimen() * 1)
                .row();
    }

    private Stack createTitleLabel() {
        String appName = Game.getInstance().getAppInfoService().getAppName();
        MyWrappedLabel titleLabel = new MyWrappedLabel(new MyWrappedLabelConfigBuilder().setText(appName).build());
        titleLabel.setFontScale(FontManager.calculateMultiplierStandardFontSize(appName.length() > 14 ? 1.5f : 2f));
        titleLabel.setAlignment(Align.center);
        return createTitleStack(titleLabel);
    }

    protected Stack createTitleStack(MyWrappedLabel titleLabel) {
        Stack stack = new Stack();
        Image image = GraphicUtils.getImage(Resource.title_background);
        stack.addActor(image);
        stack.addActor(titleLabel);
        return stack;
    }

    private MyButton createStartGameBtn() {
        MyButton button = new MainMenuButtonBuilder().setStartGameButton().setFontDimen(FontManager.calculateMultiplierStandardFontSize(1.2f)).build();
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (new CampaignService().getCrosswordLevel(LettersCampaignLevelEnum.LEVEL_0_0.getIndex()) == -1) {
                    screenManager.showGameScreen(LettersCampaignLevelEnum.LEVEL_0_0);
                } else {
                    screenManager.showCampaignScreen();
                }
            }
        });
        return button;
    }

    @Override
    public void onBackKeyPress() {
        Gdx.app.exit();
    }

}
