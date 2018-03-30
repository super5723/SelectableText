package wy.test.tools.selectabletext;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import wy.test.tools.selectabletext.selectText.SelectableTextView;

public class MainActivity extends Activity {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                selectableTextView.setText("金刚狼金刚狼即将拉开个垃圾金刚狼金刚狼即将拉开个垃圾高考啦感觉了高考啦感觉了金刚狼金刚狼即将拉开个垃圾高考啦感觉了金刚狼金刚狼即将拉开个垃圾高考啦感觉了");
            }

            @Override
            public int getItemCount() {
                return 1000;
            }
        };
        recyclerView.setAdapter(adapter);
    }
    class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
