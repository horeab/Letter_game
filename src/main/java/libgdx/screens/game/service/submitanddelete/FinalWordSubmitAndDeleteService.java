package libgdx.screens.game.service.submitanddelete;

import libgdx.campaign.CampaignLevel;
import libgdx.campaign.CampaignService;
import libgdx.game.LettersGame;
import libgdx.screens.game.service.Utils;
import libgdx.screens.game.service.WordAnimationService;
import libgdx.screens.game.service.finalword.FinalWordService;
import libgdx.screens.game.service.letters.LettersToPressService;

public class FinalWordSubmitAndDeleteService extends SubmitAndDeleteService {

    private float buttonsY;
    private String finalWord;
    private FinalWordService finalWordService;
    private CampaignLevel campaignLevel;

    public FinalWordSubmitAndDeleteService(LettersToPressService lettersToPressService, FinalWordService finalWordService, CampaignLevel campaignLevel, float buttonsY) {
        super(lettersToPressService);
        this.buttonsY = buttonsY;
        this.finalWord = finalWordService.getFinalWord();
        this.finalWordService = finalWordService;
        this.campaignLevel = campaignLevel;
    }

    @Override
    String getPressedWord() {
        return Utils.getPressedWordForAlreadyDisplayedLetters(lettersToPressService.getPressedLetters(), finalWordService.getFinalWordCells());
    }

    @Override
    public boolean processGameFinished() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                LettersGame.getInstance().getScreenManager().showCampaignScreen();
            }
        };
        if (finalWord.equals(getPressedWord())) {
            LettersGame.getInstance().getAppInfoService().showPopupAd(new Runnable() {
                @Override
                public void run() {
                }
            });
            new CampaignService().levelFinished(finalWordService.getActiveStars(), campaignLevel);
            new WordAnimationService().animateGameWin(runnable, finalWordService.getActiveStars());
            return true;
        }
        return false;
    }

    @Override
    boolean isSubmitBtnVisible() {
        return getPressedWord().length() == finalWord.length();
    }

    @Override
    public float getButtonsY() {
        return buttonsY;
    }

    @Override
    public void onClickSubmitBtn() {
        String pressedWord = getPressedWord();
        if (isSubmitBtnVisible()) {
            if (!finalWord.equals(pressedWord)) {
                finalWordService.wrongPressedAnswers();
                Utils.animateIncorrectWord();
                lettersToPressService.movePressedLetterButtonsToOriginal();
            }
            processGameFinished();
        }
    }
}
