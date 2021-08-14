package com.example.sulsetsungha;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

//import com.bumptech.glide.Glide;

public class DonationAdapter extends ArrayAdapter implements AdapterView.OnItemClickListener {

    String TAG = DonationFragment.class.getSimpleName();

    private Context context;
    private List list;
    private LayoutInflater layoutInflater;
    private ProgressBar prgbarDonation;
    private int value;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();
    }

    class ViewHolder {
        public TextView txt_company;
        public TextView txt_title;
        public ProgressBar prg_donation;
        public TextView txt_dday;
        public TextView txt_donation;
    }

    public DonationAdapter(Context context, ArrayList list){
        super(context, 0, list);
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.list = list;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        //View v = layoutInflater.inflate(R.layout.item_donation, null);
        final ViewHolder viewHolder;

        EditText edtPoint = new EditText(getContext());

        if (convertView == null){
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            convertView = layoutInflater.inflate(R.layout.item_donation, parent, false);
        }

        viewHolder = new ViewHolder();
        viewHolder.txt_company = (TextView)convertView.findViewById(R.id.txtName);
        viewHolder.txt_title = (TextView)convertView.findViewById(R.id.txtSummary);
        //viewHolder.prg_donation = (ProgressBar)convertView.findViewById(R.id.prgbarDonation);
        viewHolder.txt_dday = (TextView)convertView.findViewById(R.id.txtDday);
        viewHolder.txt_donation = (TextView)convertView.findViewById(R.id.txtDonation);

        final DonationFragment.Sponsor sponsor = (DonationFragment.Sponsor)list.get(position);
        viewHolder.txt_company.setText(sponsor.getCompany().toString());
        viewHolder.txt_title.setText(sponsor.getTitle().toString());
        viewHolder.txt_dday.setText(sponsor.getDday().toString());
        viewHolder.txt_donation.setText(sponsor.getDonation().toString());

//        TextView textView = v.findViewById(R.id.txtName);
//        textView.setText(data.get(position));

        View bodyView = (View)convertView.findViewById(R.id.body);
        Button buttonView = (Button)convertView.findViewById(R.id.btnDonation);
        prgbarDonation = (ProgressBar)convertView.findViewById(R.id.prgbarDonation);

        viewHolder.prg_donation = prgbarDonation;

        bodyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "click list body", Toast.LENGTH_SHORT).show();
            }
        });

        buttonView.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "prgbarDonation : " + viewHolder.prg_donation.getTag(position));

                AlertDialog.Builder dlg = new AlertDialog.Builder(context);
                dlg.setTitle("후원을 진행하시겠습니까?");
                dlg.setView(edtPoint);
                dlg.setIcon(R.drawable.ic_android_black_24dp);
                dlg.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //buttonView.removeView
                    }
                });
                dlg.setPositiveButton("후원", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        value = Integer.parseInt(edtPoint.getText().toString()); //문자형 -> 정수형 변환
                        viewHolder.prg_donation.incrementProgressBy(value);

                        //prgbarDonation.setProgress(Integer.parseInt(edtPoint.getText().toString()));
                    }

                });
                dlg.show();
            }
        });

        return convertView;
    }


}
