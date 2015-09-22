package com.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 项目名称:Project
 * 类描述：
 * 创建人：超
 * 创建时间: 2015/9/21 10:57
 * 修改人：
 * 修改时间：
 */
public class MenuAdapter extends BaseAdapter implements DragGridView.OnExchangeListener {

    private final Context context;

    public MenuAdapter(Context context) {
        this.context = context;

        for (int i = 0; i < 16; i++) {
            data.add("选项 " + i);
        }
    }


    List<String> data = new ArrayList<>(16);


    @Override
    public int getCount() {
        return 16;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = new Button(context);
            ((Button) convertView).setText(data.get(position));

            convertView.setBackgroundColor(Color.rgb(new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255)));
        } else {
            ((Button) convertView).setText(data.get(position));
            convertView.setBackgroundColor(Color.rgb(new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255)));
        }


        return convertView;
    }

    @Override
    public void onExchange(int oldIndex, int newIndex) {

        Log.e("-------------------", "oldIndex == " + oldIndex + "    -------------------  newIndex == " + newIndex);
        Collections.swap(data, oldIndex, newIndex);

        if (oldIndex < newIndex) {
            for (int i = oldIndex; i < newIndex; i++) {
                Collections.swap(data, i, i);
            }
        } else if (oldIndex > newIndex) {
            for (int i = oldIndex; i > newIndex; i--) {
                Collections.swap(data, i, i);
            }
        }
        notifyDataSetChanged();
    }
}
