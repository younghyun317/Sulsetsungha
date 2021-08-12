package com.example.sulsetsungha;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {
  //지도를 리스트로 표현

    //adater에 들어갈 list
    private ArrayList<Data> itemLocation = new ArrayList<>();

    @NonNull
    @Override
    public LocationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationAdapter.ViewHolder holder, int position) {
        holder.onBind(itemLocation.get(position));
    }

    public void setLocationList(ArrayList<Data> list){

        this.itemLocation = list;
        notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return itemLocation.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {


        ImageView img_marker;
        TextView txt_state;
//        TextView txtview;
        TextView txt_distance;
        ImageButton btn_chat;

        String text = "M 이내";

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img_marker = itemView.findViewById(R.id.img_marker);
            txt_state = itemView.findViewById(R.id.txt_state);
//            txtview = itemView.findViewById(R.id.txtview);
            txt_distance = itemView.findViewById(R.id.txt_distance);
            btn_chat = itemView.findViewById(R.id.btn_chat);

        }

        void onBind(Data item){
            txt_state.setText(item.getState());
            txt_distance.setText(item.getDistance()+text);
//            txtview.setText("이내");



        }
    }
}



