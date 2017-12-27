package zhy.com.highlight.position;

import android.graphics.RectF;

import zhy.com.highlight.HighLight;

/**
 * Created by caizepeng on 16/8/20.
 */
public class OnRightLocalPosCallback extends OnBaseCallback {
    public OnRightLocalPosCallback() {
    }

    public OnRightLocalPosCallback(float offset) {
        super(offset);
    }

    @Override
    public void getPosition(float rightMargin, float bottomMargin, RectF rectF, HighLight.MarginInfo marginInfo) {
        marginInfo.leftMargin = rectF.right + offset;
       // marginInfo.topMargin = rectF.top;
        marginInfo.bottomMargin = bottomMargin+rectF.height()+offset;
    }
}
