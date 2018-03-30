package wy.test.tools.selectabletext.selectText;

import android.text.Spannable;

/**
 * Created by wangyang53 on 2018/3/27.
 */

public class SelectedTextInfo {
    public int start;
    public int end;
    public Spannable spannable;
    public int[] startPosition = new int[2];
    public int[] endPosition = new int[2];
    public int startLineTop;
    public int endLineTop;
}
