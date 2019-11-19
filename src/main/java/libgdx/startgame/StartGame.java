package libgdx.startgame;

import libgdx.constants.GameIdEnum;
import libgdx.constants.Language;
import libgdx.game.Game;
import libgdx.game.LettersGame;
import libgdx.utils.startgame.test.DefaultAppInfoService;
import libgdx.utils.startgame.test.DefaultBillingService;
import libgdx.utils.startgame.test.DefaultFacebookService;
import libgdx.utils.startgame.test.DefaultLoginService;

public class StartGame {

    public static void main(String[] args) {
        LettersGame game = new LettersGame(
                new DefaultFacebookService(),
                new DefaultBillingService(),
                new DefaultAppInfoService() {
                    @Override
                    public String getGameIdPrefix() {
                        return GameIdEnum.lettersgame.name();
                    }

                    @Override
                    public float gameScreenTopMargin() {
                        return super.gameScreenTopMargin();
                    }

                    @Override
                    public String getAppName() {
                        return getTitle();
                    }

                    @Override
                    public String getLanguage() {
                        return Language.pt.name();
                    }
                });
        libgdx.utils.startgame.StartGame.main(game, args);
    }

    public static String getTitle() {
        switch (Language.valueOf(Game.getInstance().getAppInfoService().getLanguage())) {
            case cs:
                return "Světová geografie";
            case da:
                return "Verdensgeografi";
            case de:
                return "Weltgeografie";
            case en:
                return "World Geography";
            case es:
                return "Geografia mundial";
            case fr:
                return "Géographie du monde";
            case hr:
                return "Svjetska geografija";
            case hu:
                return "Világföldrajz";
            case id:
                return "Geografi dunia";
            case it:
                return "Geografia mondiale";
            case nl:
                return "Wereld Aardrijkskunde";
            case no:
                return "Verdensgeografi";
            case pl:
                return "Geografia świata";
            case pt:
                return "Geografia mundial";
            case ro:
                return "Geografia lumii";
            case ru:
                return "Мировая география";
            case sk:
                return "Svetová geografia";
            case sv:
                return "Världsgeografi";
            case tr:
                return "Dünya coğrafyası";
            case uk:
                return "Світова географія";
        }
        return null;
    }
}
