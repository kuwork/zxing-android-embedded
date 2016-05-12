package com.ajb.merchants.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.ajb.merchants.R;
import com.ajb.merchants.model.MerchantsAccoutInfo;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenming on 2016/5/11.
 */
public class AccoutManagementAdapter extends BaseAdapter{
    private LayoutInflater inflater;
    private Context context;
    private List<MerchantsAccoutInfo> list;
    public View.OnClickListener onClickListener;


    public AccoutManagementAdapter( Context context, List<MerchantsAccoutInfo> list,View.OnClickListener onClickListener) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        if (list != null) {
            this.list = list;
        } else {
            this.list = new ArrayList<MerchantsAccoutInfo>();
        }
        this.onClickListener = onClickListener;

    }
    public void changeData(List<MerchantsAccoutInfo> list) {
        if (list != null) {
            this.list = list;
        } else {
            this.list = new ArrayList<MerchantsAccoutInfo>();
        }
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

         ViewHodler holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_item_account, parent, false);
            holder = new ViewHodler();
            ViewUtils.inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHodler) convertView.getTag();
        }
        holder.tv_name.setText(list.get(position).getMa_accoutname()+"");
        holder.tv_address.setText(list.get(position).getMa_address()+"");
        holder.tv_remark.setText(list.get(position).getMa_remark()+"");
        String [] from ={"competence"};
        int [] to = {R.id.tv_competence};
        SimpleAdapter  sim_adapter = new SimpleAdapter(context, getData(list.get(position).getMa_listCompetence()), R.layout.accout_competence_list, from, to);
        holder.gd_competence.setAdapter(sim_adapter);
        holder.gd_competence.setFocusable(false);
        holder.gd_competence.setFocusableInTouchMode(false);
        holder.btn_delete.setOnClickListener(AccoutManagementAdapter.this.onClickListener);
        holder.btn_delete.setTag("delete");
        holder.btn_edit.setOnClickListener(AccoutManagementAdapter.this.onClickListener);
        holder.btn_edit.setTag("edit");
        return convertView;
    }

    public class ViewHodler {
        @ViewInject(R.id.ly_content)
        LinearLayout ly_content;
        @ViewInject(R.id.im_iocn)
        ImageView im_iocn;
        @ViewInject(R.id.tv_name)//标题
        TextView tv_name;
        @ViewInject(R.id.btn_edit)
        ImageView btn_edit;
        @ViewInject(R.id.btn_delete)
        ImageView btn_delete;
        @ViewInject(R.id.gd_competence)
        GridView gd_competence;//权限列表
        @ViewInject(R.id.lv_detail)
        LinearLayout lv_detail;//账户详情
        @ViewInject(R.id.tv_address)
        TextView tv_address;//账户地址
        @ViewInject(R.id.tv_remark)
        TextView  tv_remark;//备注
    }
    public List<Map<String, Object>> getData(List<String> strings){
        //cion和iconName的长度是相同的，这里任选其一都可以
        List<Map<String, Object>> listObject=new ArrayList<Map<String, Object>>();
        for(int i=0;i<strings.size();i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("competence", strings.get(i));
            listObject.add(map);
        }

        return listObject;
    }
}
