package com.ubt.alpha1e.maincourse.courselayout;

import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import zhy.com.highlight.HighLight;
import zhy.com.highlight.shape.BaseLightShape;

/**
 * @author：liuhai
 * @date：2017/12/13 10:35
 * @modifier：ubt
 * @modify_date：2017/12/13 10:35
 * [A brief description]
 * version
 */

public class RectLocalLightShap extends BaseLightShape {

    private float rx = 6; //The x-radius of the oval used to round the corners
    private float ry = 6; //The y-radius of the oval used to round the corners

    public RectLocalLightShap() {
        super();
    }

    /**
     * @param dx         水平方向偏移
     * @param dy         垂直方向偏移
     * @param blurRadius 模糊半径 默认15px 0不模糊
     * @param rx         The x-radius of the oval used to round the corners,default 6px.
     * @param ry         The y-radius of the oval used to round the corners,default 6px.
     */
    public RectLocalLightShap(float dx, float dy, float blurRadius, float rx, float ry) {
        super(dx, dy, blurRadius);
        this.rx = rx;
        this.ry = ry;
    }

    public RectLocalLightShap(float dx, float dy, float blurRadius) {
        super(dx, dy, blurRadius);
    }

    public RectLocalLightShap(float dx, float dy) {
        super(dx, dy);
    }

    @Override
    protected void drawShape(Bitmap bitmap, HighLight.ViewPosInfo viewPosInfo) {
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);
        paint.setAntiAlias(true);
        if (blurRadius > 0) {
            paint.setMaskFilter(new BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.SOLID));
        }
        canvas.drawRoundRect(viewPosInfo.rectF, rx, ry, paint);
        RectF rectF = new RectF();
        rectF.left = viewPosInfo.rectF.left - 15;
        rectF.right = viewPosInfo.rectF.right + 15;
        rectF.top = viewPosInfo.rectF.top - 15;
        rectF.bottom = viewPosInfo.rectF.bottom + 15;
        paint.setColor(Color.WHITE);
        canvas.drawRoundRect(rectF, rx, ry, paint);
    }

    @Override
    protected void resetRectF4Shape(RectF viewPosInfoRectF, float dx, float dy) {
        viewPosInfoRectF.inset(dx, dy);
    }
}
