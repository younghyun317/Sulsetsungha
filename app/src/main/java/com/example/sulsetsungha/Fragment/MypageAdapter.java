//package com.example.sulsetsungha.Fragment;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.sulsetsungha.R;
//
//import java.util.ArrayList;
//
//public class MypageAdapter extends RecyclerView.Adapter<MypageAdapter.ViewHolder>{
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        private String id;
//        private String company;
//        private String title;
//        private String context;
//        private String dday;
//        private String donation;
//        private int progressbar;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            txt_company = (TextView) itemView.findViewById(R.id.txtName);
//            txt_title = (TextView) itemView.findViewById(R.id.txtSummary);
//            prg_donation = (ProgressBar) itemView.findViewById(R.id.prgbarDonation);
//            txt_dday = (TextView) itemView.findViewById(R.id.txtDday);
//            txt_donation = (TextView) itemView.findViewById(R.id.txtDonation);
//        }
//
//    }
//
//    private ArrayList<MypageRecyclerViewItem> mList = null;
//
//    public MypageAdapter(ArrayList<MypageRecyclerViewItem> mList) {
//        this.mList = mList;
//    }
//
//    // 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        Context context = parent.getContext();
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//        View view = inflater.inflate(R.layout.activity_recycler_item, parent, false);
//        RecyclerViewAdapter.ViewHolder vh = new RecyclerViewAdapter.ViewHolder(view);
//        return vh;
//    }
//
//    // position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
//        RecyclerViewItem item = mList.get(position);
//
//        holder.imgView_item.setImageResource(R.drawable.ic_launcher_background);   // 사진 없어서 기본 파일로 이미지 띄움
//        holder.txt_main.setText(item.getMainText());
//        holder.txt_sub.setText(item.getSubText());
//    }
//
//    @Override
//    public int getItemCount() {
//        return mList.size();
//    }}
