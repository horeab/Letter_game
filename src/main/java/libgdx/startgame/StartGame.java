package libgdx.startgame;

import libgdx.constants.GameIdEnum;
import libgdx.constants.Language;
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
                        return "Jocul Cuvintelor";
                    }

                    @Override
                    public String getLanguage() {
                        return Language.en.name();
                    }
                });
        libgdx.utils.startgame.StartGame.main(game, args);
    }
}
