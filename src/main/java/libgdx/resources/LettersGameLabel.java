package libgdx.resources;

import libgdx.game.Game;
import libgdx.resources.gamelabel.GameLabelUtils;
import libgdx.resources.gamelabel.SpecificPropertiesUtils;

public enum LettersGameLabel implements libgdx.resources.gamelabel.GameLabel {

    START_GAME,
    LEVEL_FINISHED,
    ALREADY_ADDED,
    ;

    @Override
    public String getText(Object... params) {
        String language = Game.getInstance().getAppInfoService().getLanguage();
        return GameLabelUtils.getText(getKey(), language, SpecificPropertiesUtils.getLabelFilePath(), params);
    }

    @Override
    public String getKey() {
        return name().toLowerCase();
    }
}
