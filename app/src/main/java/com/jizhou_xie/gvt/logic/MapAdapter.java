package com.jizhou_xie.gvt.logic;

import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * Created by Thomas Hsieh on 2016/10/13.
 */

public class MapAdapter<T> extends ArrayAdapter<String> {
    private T[] keys;

    public MapAdapter(Context context, int resource, T[] keys, String[] text) {
        super(context,resource,text);
        this.keys = keys;
    }

    public T getKey(int position) {
        return this.keys[position];
    }
}
