package com.gy.mywifi.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gy.mywifi.R;
import com.gy.mywifi.bean.ConnectHotClient;
import com.gy.mywifi.bean.WifiApClient;
import com.gy.mywifi.untils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

public class MyConnectAdapter extends RecyclerView.Adapter<MyConnectAdapter.MyViewHolder> {

    private List<WifiApClient> datas=new ArrayList<>();


    private Context context;
    public MyConnectAdapter(Context context){
        this.context=context;
    }

    public void setDatas(List<WifiApClient> datas){
        this.datas=datas;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=View.inflate(parent.getContext(), R.layout.item_connect,null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ViewGroup.LayoutParams nameLayoutParams=holder.tv_name.getLayoutParams();
        nameLayoutParams.width= ScreenUtils.getScreenWidth(context);
        holder.tv_name.setText(datas.get(position).getClientName());
        ViewGroup.LayoutParams ipLayoutParams=holder.tv_ip.getLayoutParams();
        ipLayoutParams.width= ScreenUtils.getScreenWidth(context);
        holder.tv_ip.setText(datas.get(position).getClientIp());
        ViewGroup.LayoutParams macLayoutParams=holder.tv_mac.getLayoutParams();
        macLayoutParams.width= ScreenUtils.getScreenWidth(context);
        holder.tv_mac.setText(datas.get(position).getClientMac());
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_ip;
        TextView tv_mac;
        TextView tv_name;
        public MyViewHolder(View itemView) {
            super(itemView);
            tv_ip=itemView.findViewById(R.id.tv_ip);
            tv_mac=itemView.findViewById(R.id.tv_mac);
            tv_name=itemView.findViewById(R.id.device_name);
        }
    }
}
