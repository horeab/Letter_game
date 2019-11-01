package libgdx.game;

import libgdx.constants.GameIdEnum;
import libgdx.controls.labelimage.InventoryTableBuilderCreator;
import libgdx.controls.popup.RatingService;
import libgdx.resources.LettersGameLabel;
import libgdx.resources.Resource;
import libgdx.resources.ResourceService;
import libgdx.screens.AbstractScreen;
import libgdx.services.LettersGameRatingService;
import libgdx.services.LettersGameResourceService;
import libgdx.services.ScreenManager;
import libgdx.transactions.TransactionsService;

public class LettersGameMainDependencyManager extends MainDependencyManager<ScreenManager, AbstractScreen, LettersGameLabel, Resource, GameIdEnum> {

    @Override
    public Class<Resource> getMainResourcesClass() {
        return Resource.class;
    }

    @Override
    public Class<GameIdEnum> getGameIdClass() {
        return GameIdEnum.class;
    }

    @Override
    public ResourceService createResourceService() {
        return new LettersGameResourceService();
    }

    @Override
    public RatingService createRatingService(AbstractScreen abstractScreen) {
        return new LettersGameRatingService(abstractScreen);
    }

    @Override
    public Class<LettersGameLabel> getGameLabelClass() {
        return LettersGameLabel.class;
    }

    @Override
    public ScreenManager createScreenManager() {
        return new ScreenManager();
    }

    @Override
    public InventoryTableBuilderCreator createInventoryTableBuilderCreator() {
        throw new RuntimeException("Transactions not implemented for Game");
    }

    @Override
    public TransactionsService getTransactionsService() {
        throw new RuntimeException("Transactions not implemented for Game");
    }
}
