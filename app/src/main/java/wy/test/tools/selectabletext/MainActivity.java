package wy.test.tools.selectabletext;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import wy.test.tools.selectabletext.selectText.CustomImageSpan;
import wy.test.tools.selectabletext.selectText.SelectableTextView;

public class MainActivity extends Activity {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    private PopupWindow popupWindow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final BitmapDrawable bitmapDrawable= (BitmapDrawable) getResources().getDrawable(R.mipmap.ic_launcher);
        bitmapDrawable.setBounds(0,0,40,40);
        recyclerView= (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter= new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                MyViewHolder viewHolder=new MyViewHolder(new SelectableTextView(MainActivity.this));
                return viewHolder;
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                SelectableTextView selectableTextView=((SelectableTextView)holder.itemView);
                SpannableStringBuilder spannableStringBuilder=new SpannableStringBuilder("金刚狼金刚狼即将拉开个垃圾金刚狼金刚狼即将拉开个垃圾高考啦感觉了高考啦感觉了金刚狼金刚狼即将拉开个垃圾高考啦感觉了金刚狼金刚狼即将拉开个垃圾高考啦感觉了");
                spannableStringBuilder.setSpan(new CustomImageSpan(bitmapDrawable),30,33, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                selectableTextView.setText(spannableStringBuilder);
            }

            @Override
            public int getItemCount() {
                return 1000;
            }
        };
        recyclerView.setAdapter(adapter);

        final TextView textView=(TextView)findViewById(R.id.textview);
//        textView.setTextIsSelectable(false);
        textView.setAutoLinkMask(Linkify.WEB_URLS);
        textView.setText("4.你好");
        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showOperationPopWindow(textView);
                return true;
            }
        });
    }

    private void showOperationPopWindow(View view) {
        popupWindow = new PopupWindow(this);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LinearLayout contentView = new LinearLayout(this);
        contentView.setOrientation(LinearLayout.VERTICAL);


        LinearLayout ll_list=new LinearLayout(this);
        ll_list.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ll_list.setOrientation(LinearLayout.HORIZONTAL);
        ll_list.setBackgroundResource(R.drawable.bg_operation);
        ll_list.setPadding(20, 10, 20, 10);
        TextView tv_copy = new TextView(this);
        tv_copy.setText("复制");
        tv_copy.setTextColor(Color.WHITE);
        tv_copy.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ll_list.addView(tv_copy);

        contentView.addView(ll_list);

        tv_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
//                onCopy();
            }
        });

        View arrow = new View(this);
        arrow.setBackgroundResource(R.drawable.triangle_down);
        LinearLayout.LayoutParams arrowLp = new LinearLayout.LayoutParams(17, 17);
        arrow.setLayoutParams(arrowLp);
        contentView.addView(arrow);

        contentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        popupWindow.setContentView(contentView);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);

        contentView.measure(0, 0);
        int height = popupWindow.getContentView().getMeasuredHeight();
        WindowManager manager = getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int screenWidth = outMetrics.widthPixels;
        int screenHeight = outMetrics.heightPixels;
        int[] loc = new int[2];
        view.getLocationInWindow(loc);
        int x = loc[0];
        if (x < 0)
            x = 20;
        int y = loc[1] - 20 - height;
        boolean isDown = true;
        if (y < screenHeight / 5) {
            y = loc[1] + 20 + view.getMeasuredHeight();
            isDown = false;
        }
        if (y > screenHeight / 5 * 4) {
            y = loc[1] - 20 - height;
            isDown = true;
        }
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(17, 17);
        lp.setMargins(20,0,0,0);
        if (isDown) {
            arrow.setBackgroundResource(R.drawable.triangle_down);
            contentView.removeView(arrow);
            contentView.addView(arrow,lp);
        } else {
            arrow.setBackgroundResource(R.drawable.triangle_up);
            contentView.removeView(arrow);
            contentView.addView(arrow, 0,lp);
        }

        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, x, y);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
