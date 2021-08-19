package com.example.sulsetsungha.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sulsetsungha.R;
import com.example.sulsetsungha.donation.DonationAdapter;
import com.example.sulsetsungha.donation.DonationDetailActivity;
import com.example.sulsetsungha.donation.DonationFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MypageAdapter extends ArrayAdapter implements AdapterView.OnItemClickListener {

    String TAG = DonationAdapter.class.getSimpleName();

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

    public MypageAdapter(Context context, ArrayList list){
        super(context, 0, list);
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.list = list;
    }

    //리스트뷰에서 아이템을 하나씩 가져오는 함수
    //position : 아이템의 index, convertView: index에 해당되는 view 객체, parent: view 객체가 포함된 부모
    public View getView(int position, View convertView, ViewGroup parent) {
        final MypageAdapter.ViewHolder viewHolder;
        viewHolder = new MypageAdapter.ViewHolder();

        if (convertView == null){
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            convertView = layoutInflater.inflate(R.layout.item_donation, parent, false);
        }

        View bodyView = (View)convertView.findViewById(R.id.body);

        viewHolder.txt_company = (TextView)convertView.findViewById(R.id.txtName);
        viewHolder.txt_title = (TextView)convertView.findViewById(R.id.txtSummary);
        viewHolder.prg_donation = (ProgressBar)convertView.findViewById(R.id.prgbarDonation);
        viewHolder.txt_dday = (TextView)convertView.findViewById(R.id.txtDday);
        viewHolder.txt_donation = (TextView)convertView.findViewById(R.id.txtDonation);

        final MypageFragment.Sponsor sponsor = (MypageFragment.Sponsor)list.get(position);
        viewHolder.txt_company.setText(sponsor.getCompany().toString());
        viewHolder.txt_title.setText(sponsor.getTitle().toString());
        viewHolder.txt_dday.setText(sponsor.getDday().toString());
        viewHolder.txt_donation.setText(sponsor.getDonation().toString());
        viewHolder.prg_donation.setProgress((int) Math.round(Double.parseDouble(sponsor.getDonation())));

        bodyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DonationDetailActivity.class);
                intent.putExtra("Company", sponsor.getCompany());
                intent.putExtra("Title", sponsor.getTitle().toString());
                intent.putExtra("Context", sponsor.getContext().toString());
                intent.putExtra("Dday", sponsor.getDday().toString());
                intent.putExtra("Percent", sponsor.getDonation().toString());
                intent.putExtra("Donation", sponsor.getDonation());

                context.startActivity(intent);

            }
        });


        convertView.setTag(viewHolder);
        return convertView;
    }

    Map <String, String> give_token(String token) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);

        return headers;
    }

}
