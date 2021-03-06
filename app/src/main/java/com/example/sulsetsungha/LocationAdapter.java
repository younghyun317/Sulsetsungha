package com.example.sulsetsungha;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {
  //지도를 리스트로 표현

    //adater에 들어갈 list
    private ArrayList<LocationItem> itemLocation = new ArrayList<>();

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

    public void setLocationList(ArrayList<LocationItem> list){

        this.itemLocation = list;
        notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return itemLocation.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {


        ImageView img_marker;
        TextView txt_Uname;
        TextView txt_distance;
        Button btn_chat;

        String text = "M 이내";

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img_marker = itemView.findViewById(R.id.img_marker);
            txt_Uname = itemView.findViewById(R.id.txt_Uname);
            txt_distance = itemView.findViewById(R.id.txt_distance);
            btn_chat = itemView.findViewById(R.id.btn_chat);

            btn_chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), ChattingActivity.class);
                    intent.putExtra("username", txt_Uname.getText().toString());

                    itemView.getContext().startActivity(intent);
                }
            });

        }

        void onBind(LocationItem item){
            txt_Uname.setText(item.getUname());
            txt_distance.setText(item.getDistance()+text);

        }
    }
}



