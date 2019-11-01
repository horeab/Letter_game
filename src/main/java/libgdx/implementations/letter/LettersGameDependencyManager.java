package libgdx.implementations.letter;

import java.util.ArrayList;
import java.util.List;

import libgdx.campaign.CampaignGameDependencyManager;
import libgdx.campaign.LettersCampaignLevelEnum;
import libgdx.campaign.LettersQuestionCategoryEnum;
import libgdx.campaign.LettersQuestionDifficultyLevel;
import libgdx.campaign.StarsService;
import libgdx.resources.IncrementingRes;
import libgdx.utils.model.RGBColor;

public class LettersGameDependencyManager extends CampaignGameDependencyManager {

    @Override
    public List<? extends IncrementingRes> getIncrementResList() {
        List<IncrementingRes> list = new ArrayList<>();
        return list;
    }

    @Override
    protected String allQuestionText() {
        return "";
    }

    @Override
    public Class<LettersGameSpecificResource> getSpecificResourceTypeEnum() {
        return LettersGameSpecificResource.class;
    }

    @Override
    public Class<LettersCampaignLevelEnum> getCampaignLevelTypeEnum() {
        return LettersCampaignLevelEnum.class;
    }

    @Override
    public Class<LettersQuestionCategoryEnum> getQuestionCategoryTypeEnum() {
        return LettersQuestionCategoryEnum.class;
    }

    @Override
    public Class<LettersQuestionDifficultyLevel> getQuestionDifficultyTypeEnum() {
        return LettersQuestionDifficultyLevel.class;
    }

    public StarsService getStarsService() {
        return new StarsService();
    }
}
