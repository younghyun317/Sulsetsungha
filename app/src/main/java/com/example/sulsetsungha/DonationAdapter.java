package com.example.sulsetsungha;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

//import com.bumptech.glide.Glide;

class DonationAdapter extends ArrayAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private ProgressBar prgbarDonation;
    private int value;

    class ViewHolder {
        public TextView tv_name;
        public TextView tv_summary;
        public TextView tv_dday;
        public TextView tv_donation;
        public Button btn_donatuon;
    }

    public DonationAdapter(Context context, ArrayList list){
        super(context, 0, list);
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = layoutInflater.inflate(R.layout.item_donation, null);

        final EditText edtPoint = new EditText(getContext());

//        if (convertView == null) {
//            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
//            convertView = layoutInflater.inflate(R.layout.item_donation, parent, false);
//        }

//        TextView textView = v.findViewById(R.id.txtName);
//        textView.setText(data.get(position));

        View bodyView = v.findViewById(R.id.body);
        Button buttonView = v.findViewById(R.id.btnDonation);
        prgbarDonation = (ProgressBar)v.findViewById(R.id.prgbarDonation);


        bodyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "click list body", Toast.LENGTH_SHORT).show();
            }
        });

        buttonView.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(context);
                dlg.setTitle("후원을 진행하시겠습니까?");
                dlg.setView(edtPoint);
                dlg.setIcon(R.drawable.ic_android_black_24dp);
                dlg.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dlg.setPositiveButton("후원", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        value = Integer.parseInt(edtPoint.getText().toString()); //문자형 -> 정수형 변환
                        prgbarDonation.setProgress(value);
                    }
                });

                dlg.show();
            }
        });
        prgbarDonation.setProgress(value);
        return v;
    }
}
