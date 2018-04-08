package wy.test.tools.selectabletext.selectText;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by wangyang53 on 2018/3/26.
 */

public class SelectableTextView extends TextView implements PromptPopWindow.CursorListener, OperationView.OperationItemClickListener {
    private final String TAG = SelectableTextView.class.getSimpleName();
    private Context mContext;
    private PromptPopWindow promptPopWindow;
    private SelectedTextInfo mSelectedTextInfo;
    private BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(Color.GRAY);
    private int downX, downY;

    public SelectableTextView(Context context) {
        super(context);
        init(context);
    }

    public SelectableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SelectableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        setTextIsSelectable(false);
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                updateCursorInWindow();
                post(new Runnable() {
                    @Override
                    public void run() {
                        if (promptPopWindow != null) {
                            promptPopWindow.showOperation();
                        }
                    }
                });

                return true;
            }
        });
    }

    private void updateCursorInWindow() {
        if (getText() == null) {
            return;
        }
        Spannable spannableText = getSpannableText();
        if (mSelectedTextInfo == null) {
            if (spannableText != null) {
                mSelectedTextInfo = new SelectedTextInfo();
                mSelectedTextInfo.start = 0;
                mSelectedTextInfo.end = spannableText.length();
                mSelectedTextInfo.spannable = spannableText;

                Layout layout = getLayout();
                mSelectedTextInfo.startPosition[0] = (int) layout.getPrimaryHorizontal(mSelectedTextInfo.start);
                int startLine = layout.getLineForOffset(mSelectedTextInfo.start);
                mSelectedTextInfo.startPosition[1] = (int) layout.getLineBottom(startLine);
                mSelectedTextInfo.startLineTop = layout.getLineTop(startLine);

                int endLine = layout.getLineForOffset(mSelectedTextInfo.end);
                mSelectedTextInfo.endPosition[0] = (int) layout.getSecondaryHorizontal(mSelectedTextInfo.end);
                mSelectedTextInfo.endPosition[1] = (int) layout.getLineBottom(endLine);
                mSelectedTextInfo.endLineTop = layout.getLineTop(endLine);

            }
        }

        if (mSelectedTextInfo != null && spannableText != null) {
            showCursor();
            spannableText.removeSpan(backgroundColorSpan);
            spannableText.setSpan(backgroundColorSpan, mSelectedTextInfo.start, mSelectedTextInfo.end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            setText(spannableText);
        }
    }

    private void showCursor() {
        Log.d(TAG, "showCursor");
        int x = 0, y = 0;
        int[] coors = getLocation();
        if (promptPopWindow == null) {
            promptPopWindow = new PromptPopWindow(getContext());
            promptPopWindow.setCursorTouchListener(this);
            promptPopWindow.setOperationClickListener(this);
        }
        x = (int) (coors[0] + mSelectedTextInfo.startPosition[0] + getPaddingLeft());
        y = coors[1] + mSelectedTextInfo.startPosition[1] + getPaddingTop();
        Point left = new Point(x, y);

        x = (int) (coors[0] + mSelectedTextInfo.endPosition[0] + getPaddingLeft());
        y = coors[1] + mSelectedTextInfo.endPosition[1] + getPaddingTop();
        Point right = new Point(x, y);

        Rect hitRect = new Rect();
        getGlobalVisibleRect(hitRect);

        hitRect.left = hitRect.left - CursorView.getFixWidth();
        hitRect.right += 1;
        hitRect.bottom += 1;
        promptPopWindow.setCursorVisible(true, !hitRect.isEmpty() && hitRect.contains(left.x, left.y));
        promptPopWindow.setCursorVisible(false, !hitRect.isEmpty() && hitRect.contains(right.x, right.y));
        promptPopWindow.showCursor(this, left, right, mSelectedTextInfo.startLineTop + coors[1] + getPaddingTop());

    }

    private Spannable getSpannableText() {
        Spannable spannableText = null;
        if (!(getText() instanceof Spannable)) {
            spannableText = new SpannableString(getText());
        } else spannableText = (Spannable) getText();

        return spannableText;
    }

    private int[] getLocation() {
        int[] location = new int[2];
        getLocationInWindow(location);
//        location[0]+=getTranslationX();
//        location[1]+=getTranslationY();
        return location;
    }

    @Override
    public boolean OnCursorTouch(boolean isLeft, View view, MotionEvent event) {
        Log.d(TAG, "OnCursorTouch:" + event);
        if (mSelectedTextInfo == null)
            return false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getX();
                downY = (int) event.getY();
                if (promptPopWindow != null) {
                    promptPopWindow.hideOperation();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "OnCursorTouch setOperationVisible  visible");
                post(new Runnable() {
                    @Override
                    public void run() {
                        if (promptPopWindow != null) {
                            promptPopWindow.showOperation();
                        }
                    }
                });
                break;
            case MotionEvent.ACTION_MOVE:
//                mOperateWindow.dismiss();
                int newX = (int) event.getX();
                int newY = (int) event.getY();
                int verticalOffset;
                int horizontalOffset;

                //设置抖动阈值
                int lineHeight = getLineHeight();
                if (lineHeight > 0) {
                    if (lineHeight > Math.abs(newX - downX) && lineHeight > Math.abs(newY - downY)) {
                        return true;
                    }
                }

                if (isLeft) {
                    verticalOffset = mSelectedTextInfo.startPosition[1] + (newY - downY);
                    horizontalOffset = mSelectedTextInfo.startPosition[0] + (newX - downX);
                } else {
                    verticalOffset = mSelectedTextInfo.endPosition[1] + (newY - downY);
                    horizontalOffset = mSelectedTextInfo.endPosition[0] + (newX - downX);
                }
                Log.d(TAG, "OnCursorTouch verticalOffset:" + verticalOffset + "  horizontalOffset:" + horizontalOffset);
                Layout layout = getLayout();
                int line = layout.getLineForVertical(verticalOffset);
                int index = layout.getOffsetForHorizontal(line, horizontalOffset);
                Log.d(TAG, "OnCursorTouch line:" + line + "  index:" + index);

                if (isLeft) {
                    if (index == mSelectedTextInfo.start)
                        return true;
                    if (index <= mSelectedTextInfo.end) {
                        mSelectedTextInfo.start = index;
                        mSelectedTextInfo.startPosition[0] = (int) (layout.getPrimaryHorizontal(index));
                        mSelectedTextInfo.startPosition[1] = layout.getLineBottom(line);
                        mSelectedTextInfo.spannable = (Spannable) getSpannableText().subSequence(index, mSelectedTextInfo.end);
                        mSelectedTextInfo.startLineTop = layout.getLineTop(line);
                    }

                } else {
                    if (index == mSelectedTextInfo.end)
                        return true;
                    if (index >= mSelectedTextInfo.start) {
                        mSelectedTextInfo.end = index;
                        mSelectedTextInfo.endPosition[0] = (int) (layout.getSecondaryHorizontal(index));
                        mSelectedTextInfo.endPosition[1] = layout.getLineBottom(line);
                        mSelectedTextInfo.spannable = (Spannable) getSpannableText().subSequence(mSelectedTextInfo.start, index);
                        mSelectedTextInfo.endLineTop = layout.getLineTop(line);
                    }

                }

                updateCursorInWindow();
                break;
        }
        return true;
    }

    @Override
    public boolean onPopLayoutTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            int[] location = getLocation();
            int x = (int) motionEvent.getX();
            int y = (int) motionEvent.getY();
            int verticalOffset = y - getPaddingTop() - location[1];
            int horizontalOffset = x - getPaddingLeft() - location[0];
            if (verticalOffset < 0 || horizontalOffset < 0 || verticalOffset > getHeight() || horizontalOffset > getWidth()) {
                promptPopWindow.dismiss();
                return true;
            }

            Log.d(TAG, "onPopLayoutTouch verticalOffset:" + verticalOffset + "   horizontalOffset:" + horizontalOffset);
            Layout layout = getLayout();
            int line = layout.getLineForVertical(verticalOffset);
            int index = layout.getOffsetForHorizontal(line, horizontalOffset);
            Log.d(TAG, "onPopLayoutTouch line:" + line + "  index:" + index);
            if (index <= mSelectedTextInfo.start || index >= mSelectedTextInfo.end)
                promptPopWindow.dismiss();
        }
        return true;
    }

    @Override
    public void onCursorDismiss() {
        reset();
    }

    public void reset() {
        mSelectedTextInfo = null;
        Spannable spannable = getSpannableText();
        spannable.removeSpan(backgroundColorSpan);
        setText(spannable);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        reset();
    }

    @Override
    public void onOperationClick(OperationItem item) {
        Log.d(TAG, "onOperationClick item:" + item);
        if (item.action == OperationItem.ACTION_SELECT_ALL) {
            mSelectedTextInfo = null;
            updateCursorInWindow();
            post(new Runnable() {
                @Override
                public void run() {
                    if (promptPopWindow != null) {
                        promptPopWindow.showOperation();
                    }
                }
            });
        } else if (item.action == OperationItem.ACTION_COPY) {
            Toast.makeText(getContext(), mSelectedTextInfo.spannable, Toast.LENGTH_SHORT).show();
            promptPopWindow.dismiss();
            reset();
        } else {
            promptPopWindow.dismiss();
            reset();
        }


    }
}
