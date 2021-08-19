package com.example.sulsetsungha.community;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sulsetsungha.R;

import java.util.ArrayList;
import java.util.List;

class MyPostAdapter extends ArrayAdapter implements AdapterView.OnItemClickListener {
    String TAG = MyPostAdapter.class.getSimpleName();

    private Context context;
    private List list;
    private LayoutInflater layoutInflater;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();
    }

    class ViewHolder {
        public ImageButton btnGoComment;
        public TextView txt_context;
        public TextView txt_time;
        public TextView txt_like_cnt;
        public TextView txt_comment_cnt;
    }

    public MyPostAdapter(Context context, ArrayList list){
        super(context, 0, list);
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.list = list;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final MyPostAdapter.ViewHolder viewHolder;
        viewHolder = new MyPostAdapter.ViewHolder();

        if (convertView == null){
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            convertView = layoutInflater.inflate(R.layout.item_community, parent, false);
        }

        viewHolder.btnGoComment = (ImageButton)convertView.findViewById(R.id.btnGoComment);
        viewHolder.txt_context = (TextView)convertView.findViewById(R.id.txtContext);
        viewHolder.txt_time = (TextView)convertView.findViewById(R.id.txtTime);
        viewHolder.txt_like_cnt = (TextView)convertView.findViewById(R.id.txtCmnLikeCnt);
        viewHolder.txt_comment_cnt = (TextView)convertView.findViewById(R.id.txtCmnCmtCnt);

        final CommunityFragment.Community community = (CommunityFragment.Community)list.get(position);
        viewHolder.txt_context.setText(community.getContext().toString());
        viewHolder.txt_time.setText(community.getTime().toString());
        viewHolder.txt_like_cnt.setText(community.getLikeCnt().toString());
        viewHolder.txt_comment_cnt.setText(community.getCommentCnt().toString());

        return convertView;
    }

}
