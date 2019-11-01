package libgdx.resources;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import libgdx.graphics.GraphicUtils;
import libgdx.utils.model.FontColor;

public enum LettersGameButtonSkin implements libgdx.controls.button.ButtonSkin {

    BACKSPACE_BTN(Resource.backspace_btn_up, Resource.backspace_btn_down, Resource.backspace_btn_up, Resource.backspace_btn_up, null),
    SHUFFLE_BTN(Resource.shuffle_btn_up, Resource.shuffle_btn_down, Resource.shuffle_btn_up, Resource.shuffle_btn_up, null),
    BACK(Resource.back_btn_up, Resource.back_btn_down, Resource.back_btn_up, Resource.back_btn_up, null),
    SUBMIT_BTN(Resource.submit_btn_up, Resource.submit_btn_down, Resource.submit_btn_up, Resource.submit_btn_up, null),
    DELETE_BTN(Resource.delete_btn_up, Resource.delete_btn_down, Resource.delete_btn_up, Resource.delete_btn_up, null),

    HINT_BTN(Resource.hint_btn_up, Resource.hint_btn_down, Resource.hint_btn_up, Resource.hint_btn_up, null),
    LETTER_BTN(Resource.letter_btn_up, Resource.letter_btn_down, Resource.letter_btn_up, Resource.letter_btn_up, null),
    TRANSPARENT_BTN(Resource.transparent_btn, Resource.transparent_btn, Resource.transparent_btn, Resource.transparent_btn, null),
    LETTER_BTN_HINT(Resource.letter_btn_hint_up, Resource.letter_btn_hint_down, Resource.letter_btn_hint_up, Resource.letter_btn_hint_up, null),

    BACKGROUND_CIRCLE(Resource.btn_background_circle_up, Resource.btn_background_circle_down, Resource.btn_background_circle_up, Resource.btn_background_circle_up, null),
    MAINMENU(Resource.btn_mainmenu_btn_up, Resource.btn_mainmenu_btn_down, Resource.btn_mainmenu_btn_up, Resource.btn_mainmenu_btn_up, null),

    FINAL_WORD_UNKNOWN_PRESS(Resource.final_word_unknown_press, Resource.final_word_unknown_press, Resource.final_word_unknown_press, Resource.final_word_unknown_press, null),
    FINAL_WORD_UNKNOWN(Resource.final_word_unknown, Resource.final_word_unknown, Resource.final_word_unknown, Resource.final_word_unknown, null),
    FINAL_WORD_KNOWN(Resource.final_word_known, Resource.final_word_known, Resource.final_word_known, Resource.final_word_known, null),

    CAMPAIGN_CURRENT_LEVEL(Resource.btn_current_level_up, Resource.btn_current_level_down, Resource.btn_current_level_up, Resource.btn_campaign_disabled, null),
    CAMPAIGN_LEVEL_0(Resource.btn_campaign_0, Resource.btn_campaign_0, Resource.btn_campaign_0, Resource.btn_campaign_0, null),
    CAMPAIGN_LEVEL_1(Resource.btn_campaign_1, Resource.btn_campaign_1, Resource.btn_campaign_1, Resource.btn_campaign_1, null),
    CAMPAIGN_LEVEL_2(Resource.btn_campaign_2, Resource.btn_campaign_2, Resource.btn_campaign_2, Resource.btn_campaign_2, null),
    CAMPAIGN_LEVEL_3(Resource.btn_campaign_3, Resource.btn_campaign_3, Resource.btn_campaign_3, Resource.btn_campaign_3, null),
    CAMPAIGN_LEVEL_4(Resource.btn_campaign_4, Resource.btn_campaign_4, Resource.btn_campaign_4, Resource.btn_campaign_4, null),
    CAMPAIGN_LEVEL_5(Resource.btn_campaign_5, Resource.btn_campaign_5, Resource.btn_campaign_5, Resource.btn_campaign_5, null),

    CAMPAIGN_LOCKED_LEVEL(Resource.btn_campaign_disabled, Resource.btn_campaign_disabled, Resource.btn_campaign_disabled, Resource.btn_campaign_disabled, null),
    ;

    LettersGameButtonSkin(Res imgUp, Res imgDown, Res imgChecked, Res imgDisabled, FontColor buttonDisabledFontColor) {
        this.imgUp = imgUp;
        this.imgDown = imgDown;
        this.imgChecked = imgChecked;
        this.imgDisabled = imgDisabled;
        this.buttonDisabledFontColor = buttonDisabledFontColor;
    }

    private Res imgUp;
    private Res imgDown;
    private Res imgChecked;
    private Res imgDisabled;
    private FontColor buttonDisabledFontColor;

    @Override
    public Drawable getImgUp() {
        return GraphicUtils.getImage(imgUp).getDrawable();
    }

    @Override
    public Drawable getImgDown() {
        return GraphicUtils.getImage(imgDown).getDrawable();
    }

    @Override
    public Drawable getImgChecked() {
        return GraphicUtils.getImage(imgChecked).getDrawable();
    }

    @Override
    public Drawable getImgDisabled() {
        return GraphicUtils.getImage(imgDisabled).getDrawable();
    }

    @Override
    public FontColor getButtonDisabledFontColor() {
        return buttonDisabledFontColor;
    }
}
