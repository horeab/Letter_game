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
                        return Language.uk.name();
                    }
                });
        libgdx.utils.startgame.StartGame.main(game, args);
    }

    public static String getTitle() {
        switch (Language.valueOf(Game.getInstance().getAppInfoService().getLanguage())) {
            case cs:
                return "Slovní hra";
            case da:
                return "Ordspelet";
            case de:
                return "Wortspiel";
            case en:
                return "Letters Game";
            case es:
                return "El juego de palabras";
            case fr:
                return "Le jeu de mots";
            case hr:
                return "Igra riječi";
            case hu:
                return "Szó Játék";
            case id:
                return "Game Kata";
            case it:
                return "Il gioco di parole";
            case nl:
                return "Woordspel";
            case no:
                return "Ordspillet";
            case pl:
                return "Gra słowna";
            case pt:
                return "O jogo de palavras";
            case ro:
                return "Jocul Cuvintelor";
            case ru:
                return "Письма игры";
            case sk:
                return "Slovná Hra";
            case sv:
                return "Ordet Spel";
            case tr:
                return "Harfler Oyunu";
            case uk:
                return "Гра в слова";
        }
        return null;
    }
}
