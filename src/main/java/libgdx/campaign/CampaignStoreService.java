package libgdx.campaign;

import java.util.ArrayList;
import java.util.List;

import libgdx.preferences.PreferencesService;

class CampaignStoreService {

    private static final String CAMPAIGN_LEVEL = "CampaignLevel";
    private static final String PREF_NAME = "campaignStoreService";

    private PreferencesService preferencesService = new PreferencesService(PREF_NAME);

    CampaignStoreService() {
//        this.preferencesService.clear();
    }

    void createCampaignLevel(CampaignLevel campaignLevelEnum) {
        preferencesService.putInteger(formCampaignLevelKey(campaignLevelEnum.getIndex()), -1);
    }

    private String formCampaignLevelKey(int campaignLevelEnumIndex) {
        return CAMPAIGN_LEVEL + campaignLevelEnumIndex;
    }

    List<CampaignStoreLevel> getAllCampaignLevels() {
        ArrayList<CampaignStoreLevel> levels = new ArrayList<>();
        for (LettersCampaignLevelEnum levelEnum : LettersCampaignLevelEnum.values()) {
            int val = preferencesService.getPreferences().getInteger(formCampaignLevelKey(levelEnum.getIndex()), -1);
            if (val != -1) {
                CampaignStoreLevel level = new CampaignStoreLevel(levelEnum);
                level.setCrosswordLevel(val);
                level.setStarsWon(getStarsWon(levelEnum.getIndex()));
                level.setStatus(preferencesService.getPreferences().getInteger(formCampaignLevelStatusKey(levelEnum)));
                levels.add(level);
            }
        }
        return levels;
    }

    int getStarsWon(int campaignLevelEnumIndex) {
        return preferencesService.getPreferences().getInteger(formCampaignLevelStarsWonKey(campaignLevelEnumIndex));
    }

    Integer getCrosswordLevel(int campaignLevelEnumIndex) {
        return preferencesService.getPreferences().getInteger(formCampaignLevelKey(campaignLevelEnumIndex), -1);
    }

    void updateCrosswordLevel(CampaignLevel campaignLevelEnum) {
        preferencesService.putInteger(formCampaignLevelKey(campaignLevelEnum.getIndex()), 1);
    }

    void updateStatus(CampaignLevel campaignLevelEnum, CampaignLevelStatusEnum campaignLevelStatusEnum) {
        preferencesService.putInteger(formCampaignLevelStatusKey(campaignLevelEnum), campaignLevelStatusEnum.getStatus());
    }

    void updateStarsWon(CampaignLevel campaignLevelEnum, int starsWon) {
        preferencesService.putInteger(formCampaignLevelStarsWonKey(campaignLevelEnum.getIndex()), starsWon);
    }

    private String formCampaignLevelStarsWonKey(int campaignLevelEnumIndex) {
        return formCampaignLevelKey(campaignLevelEnumIndex) + "StarsWon";
    }

    private String formCampaignLevelStatusKey(CampaignLevel campaignLevelEnum) {
        return formCampaignLevelKey(campaignLevelEnum.getIndex()) + "CampaignLevelStatusEnum";
    }

}
