package com.example.sulsetsungha.community;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sulsetsungha.R;

import java.util.ArrayList;
import java.util.List;

class CommentAdapter extends ArrayAdapter implements AdapterView.OnItemClickListener {
    String TAG = CommentAdapter.class.getSimpleName();

    private Context context;
    private List list;
    private LayoutInflater layoutInflater;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();
    }

    class ViewHolder {
        public TextView txt_cmt_writer;
        public TextView txt_comment_time;
        public TextView txt_cmt;
    }

    public CommentAdapter(Context context, ArrayList list){
        super(context, 0, list);
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.list = list;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final CommentAdapter.ViewHolder viewHolder;
        viewHolder = new CommentAdapter.ViewHolder();

        if (convertView == null){
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            convertView = layoutInflater.inflate(R.layout.item_comment, parent, false);
        }

        viewHolder.txt_cmt_writer = (TextView)convertView.findViewById(R.id.txtCmtWriter);
        viewHolder.txt_comment_time = (TextView)convertView.findViewById(R.id.txtCommentTime);
        viewHolder.txt_cmt = (TextView)convertView.findViewById(R.id.txtCmt);

        final CommunityDetailActivity.Comment comment = (CommunityDetailActivity.Comment)list.get(position);
        viewHolder.txt_cmt_writer.setText("익명" + comment.getPost_id().toString());
        viewHolder.txt_comment_time.setText(comment.getTime().toString());
        viewHolder.txt_cmt.setText(comment.getComment().toString());

        convertView.setTag(viewHolder);
        return convertView;
    }


}
