package libgdx.services;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import libgdx.controls.ScreenRunnable;
import libgdx.controls.label.MyWrappedLabel;
import libgdx.graphics.GraphicUtils;
import libgdx.resources.FontManager;
import libgdx.resources.LettersGameLabel;
import libgdx.resources.Res;
import libgdx.resources.Resource;
import libgdx.resources.ResourcesManager;
import libgdx.resources.dimen.MainDimen;
import libgdx.utils.ActorPositionManager;
import libgdx.utils.ScreenDimensionsManager;
import libgdx.utils.model.FontColor;

public class ImageAnimation {

    private libgdx.screen.AbstractScreen screen;

    public ImageAnimation(libgdx.screen.AbstractScreen screen) {
        this.screen = screen;
    }

    public Table animateWinGame(Runnable runnable, Table topTable) {
        return createTable(Resource.submit_btn_up, LettersGameLabel.LEVEL_FINISHED.getText(), topTable, runnable);
    }

    private Table createTable(Res resource, String text, Table topTable, Runnable runnable) {
        Table table = new Table();
        table.setWidth(ScreenDimensionsManager.getExternalDeviceWidth());
        table.setHeight(ScreenDimensionsManager.getExternalDeviceHeight());
        table.add(topTable).padBottom(MainDimen.horizontal_general_margin.getDimen() * 2).row();
        table.add(GraphicUtils.getImage(resource)).row();
        table.setBackground(GraphicUtils.getNinePatch(Resource.gray_background));
        MyWrappedLabel myLabel = new MyWrappedLabel(text);
        myLabel.setFontScale(FontManager.getBigFontDim());
        myLabel.setTextColor(FontColor.RED);
        table.add(myLabel);
        table.setTransform(true);
        ActorPositionManager.setActorCenterScreen(table);
        animateZoomInZoomOut(table, runnable);
        return table;
    }

    private void animateZoomInZoomOut(Actor actor, Runnable runnable) {
        float duration = 0.6f;
        float zoomAmount = 1.5f;
        actor.setOrigin(Align.center);
        actor.addAction(Actions.sequence(
                Actions.scaleBy(zoomAmount, zoomAmount, duration),
                Actions.scaleBy(-zoomAmount, -zoomAmount, duration / 2f),
                createSoundRunnableAction(),
                Actions.delay(0.3f),
                Actions.fadeOut(0.5f),
                Utils.createRunnableAction(runnable)
        ));
    }

    private RunnableAction createSoundRunnableAction() {
        RunnableAction run = new RunnableAction();
        run.setRunnable(new ScreenRunnable(screen) {
            @Override
            public void executeOperations() {
            }
        });
        return run;
    }

}
