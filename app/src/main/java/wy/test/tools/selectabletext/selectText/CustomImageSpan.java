package wy.test.tools.selectabletext.selectText;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.style.ImageSpan;

import java.lang.ref.WeakReference;

/**
 * Created by wangyang53 on 2018/4/9.
 */

public class CustomImageSpan extends ImageSpan {
    private boolean blackLayer = false;
    private WeakReference<ColorDrawable> layerDrawableWeakReference =new WeakReference<ColorDrawable>(new ColorDrawable(Color.GRAY));

    public CustomImageSpan(Bitmap b) {
        super(b);
    }

    public CustomImageSpan(Bitmap b, int verticalAlignment) {
        super(b, verticalAlignment);
    }

    public CustomImageSpan(Context context, Bitmap b) {
        super(context, b);
    }

    public CustomImageSpan(Context context, Bitmap b, int verticalAlignment) {
        super(context, b, verticalAlignment);
    }

    public CustomImageSpan(Drawable d) {
        super(d);
    }

    public CustomImageSpan(Drawable d, int verticalAlignment) {
        super(d, verticalAlignment);
    }

    public CustomImageSpan(Drawable d, String source) {
        super(d, source);
    }

    public CustomImageSpan(Drawable d, String source, int verticalAlignment) {
        super(d, source, verticalAlignment);
    }

    public CustomImageSpan(Context context, Uri uri) {
        super(context, uri);
    }

    public CustomImageSpan(Context context, Uri uri, int verticalAlignment) {
        super(context, uri, verticalAlignment);
    }

    public CustomImageSpan(Context context, int resourceId) {
        super(context, resourceId);
    }

    public CustomImageSpan(Context context, int resourceId, int verticalAlignment) {
        super(context, resourceId, verticalAlignment);
    }

    public void setBlackLayer(boolean b) {
        blackLayer = b;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
//        super.draw(canvas, text, start, end, x, top, y, bottom, paint);
        Drawable b = getCachedDrawable();

        canvas.save();
        canvas.translate(x, 0);
        if (blackLayer) {
            Rect rect = new Rect(b.getBounds());
            rect.bottom = bottom;
            rect.top = top;
            ColorDrawable layerDrawable = layerDrawableWeakReference.get();
            if (layerDrawable != null) {
                layerDrawable.setBounds(rect);
                layerDrawable.draw(canvas);
            } else {
                ColorDrawable tempDrawable = new ColorDrawable(Color.GRAY);
                tempDrawable.setBounds(rect);
                tempDrawable.draw(canvas);
            }

        }
        canvas.restore();
        canvas.save();
        int transY = bottom - b.getBounds().bottom;
        if (mVerticalAlignment == ALIGN_BASELINE) {
            transY -= paint.getFontMetricsInt().descent;
        }
        canvas.translate(x, transY);
        b.draw(canvas);
        canvas.restore();
    }

    private Drawable getCachedDrawable() {
        WeakReference<Drawable> wr = mDrawableRef;
        Drawable d = null;

        if (wr != null)
            d = wr.get();

        if (d == null) {
            d = getDrawable();
            mDrawableRef = new WeakReference<Drawable>(d);
        }

        return d;
    }

    private WeakReference<Drawable> mDrawableRef;
}
