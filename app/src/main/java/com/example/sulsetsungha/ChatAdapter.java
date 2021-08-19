package com.example.sulsetsungha;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<ChatItem> myChatList = new ArrayList<>();

    TextView content;

    public ChatAdapter(ArrayList<ChatItem> myChatList) {
        this.myChatList = myChatList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (viewType == 0) {
            view = inflater.inflate(R.layout.item_chat_left, parent, false);
            return new ViewHolder(view);
        }
        else {
            view = inflater.inflate(R.layout.item_chat_right, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (myChatList.get(position).getView()  == 0) {
            content = holder.itemView.findViewById(R.id.chat_text_left);
            content.setText(myChatList.get(position).getContent());
        }
        else {
            content = holder.itemView.findViewById(R.id.chat_text_right);
            content.setText(myChatList.get(position).getContent());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return myChatList.get(position).getView();
    }

    @Override
    public int getItemCount() {
        return myChatList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
