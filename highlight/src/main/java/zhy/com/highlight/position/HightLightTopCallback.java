package zhy.com.highlight.position;

import android.graphics.RectF;

import zhy.com.highlight.HighLight;

/**
 * @author：liuhai
 * @date：2017/11/29 15:27
 * @modifier：ubt
 * @modify_date：2017/11/29 15:27
 * [A brief description]
 * version
 */

public class HightLightTopCallback extends OnBaseCallback {
    public HightLightTopCallback() {
    }

    public HightLightTopCallback(float offset) {
        super(offset);
    }

    @Override
    public void getPosition(float rightMargin, float bottomMargin, RectF rectF, HighLight.MarginInfo marginInfo) {
        marginInfo.leftMargin = rectF.right - rectF.width() * 3 / 4;
        marginInfo.bottomMargin = bottomMargin + rectF.height() + offset;
    }
}