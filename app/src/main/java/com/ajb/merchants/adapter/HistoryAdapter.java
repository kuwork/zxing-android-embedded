package com.ajb.merchants.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ajb.merchants.R;
import com.ajb.merchants.util.SharedFileUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.util.ObjectUtil;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter<T> extends BaseAdapter {

    public final static String CARNO_HISTORY = "carno_history";
    public static final String ADDRESS_HISTORY = "address_history";
    private final OnClickListener onClickListener;
    private String type;
    private LayoutInflater inflater;
    private Context context;
    private List<T> list;
    private SharedFileUtils sharedFileUtils;

    public HistoryAdapter(Context context, List<T> list, String type) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        sharedFileUtils = new SharedFileUtils(context);
        if (list != null) {
            this.list = list;
        } else {
            this.list = new ArrayList<T>();
        }
        this.type = type;
        ;
        this.onClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                delete((T) v.getTag());
            }
        };
    }

    public void changeData(List<T> list) {
        if (list != null) {
            this.list = list;
        } else {
            this.list = new ArrayList<T>();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        return list.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int arg0, View convertView, ViewGroup parent) {
        ViewHodler holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.history_item, parent, false);
            holder = new ViewHodler();
            ViewUtils.inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHodler) convertView.getTag();
        }
        holder.init(context, list.get(arg0), arg0);


        return convertView;
    }

    public class ViewHodler {
        @ViewInject(R.id.title)
        TextView title;
        @ViewInject(R.id.delete_history)
        View delete;

        public void init(Context context, T info, int position) {
            if (info instanceof String) {
                if (CARNO_HISTORY.equals(type) || ADDRESS_HISTORY.equals(type)) {
                    if (title != null) {
                        title.setText((String) info);
                    }
                }
            }
            if (delete != null) {
                delete.setTag(info);
                delete.setOnClickListener(onClickListener);
            }
        }
    }


    public void delete(T info) {
        if (info == null || list == null) {
            return;
        }
        if (info instanceof String) {
            if (CARNO_HISTORY.equals(type)) {
                if (list.remove(info)) {
                    notifyDataSetChanged();
                    sharedFileUtils.putString(SharedFileUtils.LAST_CARNO_HISTORY, ObjectUtil.getBASE64String(list));
                }
            } else if (ADDRESS_HISTORY.equals(type)) {
                if (list.remove(info)) {
                    notifyDataSetChanged();
                    sharedFileUtils.putString(SharedFileUtils.LAST_ADDRESS_HISTORY, ObjectUtil.getBASE64String(list));
                }
            }
        }
    }

    public void save(T info) {
        if (info instanceof String) {
            if (CARNO_HISTORY.equals(type)) {
                String carNo = (String) info;
                sharedFileUtils.putString(SharedFileUtils.LAST_CARNO_AREA, carNo.substring(0, 1));
                sharedFileUtils.putString(SharedFileUtils.LAST_CARNO, carNo.substring(1).trim());
                //只保存5条历史查询记录
                if (!list.contains(info)) {
                    list.add(0, (T) info);
                    if (list.size() > 5) {
                        for (int i = 5; i < list.size(); i++) {
                            list.remove(i);

                        }

                    }
                } else {
                    list.remove(info);
                    list.add(0, (T) info);
                }
                sharedFileUtils.putString(SharedFileUtils.LAST_CARNO_HISTORY, ObjectUtil.getBASE64String(list));
                notifyDataSetChanged();
            } else if (ADDRESS_HISTORY.equals(type)) {
                //只保存5条历史查询记录
                if (!list.contains(info)) {
                    list.add(0, (T) info);
                    if (list.size() > 5) {
                        for (int i = 5; i < list.size(); i++) {
                            list.remove(i);

                        }

                    }
                } else {
                    list.remove(info);
                    list.add(0, (T) info);
                }
                sharedFileUtils.putString(SharedFileUtils.LAST_ADDRESS_HISTORY, ObjectUtil.getBASE64String(list));
                notifyDataSetChanged();
            }
        }
    }

}
