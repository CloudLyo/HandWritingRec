package com.cloudnew.poweroff.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cloudnew.poweroff.R;
import com.cloudnew.poweroff.model.Result;

import java.util.ArrayList;

/**
 * Created by CloudNew on 2018/4/18
 * cloudnew@foxmail.com.
 */

public class CharAdapter extends RecyclerView.Adapter<CharAdapter.ViewHolder> {

    private ArrayList<Result> mCharList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onItemClick(View view);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView char_item;
        public ViewHolder(View itemView) {
               super(itemView);
            char_item = (TextView) itemView.findViewById(R.id.char_);
        }
    }

    public CharAdapter(ArrayList<Result> mCharList) {
        this.mCharList = mCharList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.char_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Character character = mCharList.get(position).str.charAt(0);
        holder.char_item.setText(character+"");
        holder.char_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCharList.size();
    }
}
