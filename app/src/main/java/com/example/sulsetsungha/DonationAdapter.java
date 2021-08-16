package com.example.sulsetsungha;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.bumptech.glide.Glide;

public class DonationAdapter extends ArrayAdapter implements AdapterView.OnItemClickListener {

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

    public DonationAdapter(Context context, ArrayList list){
        super(context, 0, list);
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.list = list;
    }

    //리스트뷰에서 아이템을 하나씩 가져오는 함수
    //position : 아이템의 index, convertView: index에 해당되는 view 객체, parent: view 객체가 포함된 부모
    public View getView(int position, View convertView, ViewGroup parent) {
        final RequestQueue queue = Volley.newRequestQueue(this.getContext());
        final String url = "http://3.38.51.117:8000/donation_user/";
        HashMap<String, String> donation_json = new HashMap<>();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String token = sharedPreferences.getString("access_token", null);

        //View v = layoutInflater.inflate(R.layout.item_donation, null);
        final ViewHolder viewHolder;
        viewHolder = new ViewHolder();

        EditText edtPoint = new EditText(getContext());

        if (convertView == null){
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            convertView = layoutInflater.inflate(R.layout.item_donation, parent, false);
        }

        viewHolder.txt_company = (TextView)convertView.findViewById(R.id.txtName);
        viewHolder.txt_title = (TextView)convertView.findViewById(R.id.txtSummary);
        viewHolder.prg_donation = (ProgressBar)convertView.findViewById(R.id.prgbarDonation);
        viewHolder.txt_dday = (TextView)convertView.findViewById(R.id.txtDday);
        viewHolder.txt_donation = (TextView)convertView.findViewById(R.id.txtDonation);

        final DonationFragment.Sponsor sponsor = (DonationFragment.Sponsor)list.get(position);
        viewHolder.txt_company.setText(sponsor.getCompany().toString());
        viewHolder.txt_title.setText(sponsor.getTitle().toString());
        viewHolder.txt_dday.setText(sponsor.getDday().toString());
        viewHolder.txt_donation.setText(sponsor.getDonation().toString());
        viewHolder.prg_donation.setProgress((int) Math.round(Double.parseDouble(sponsor.getDonation())));

//        TextView textView = v.findViewById(R.id.txtName);
//        textView.setText(data.get(position));

        View bodyView = (View)convertView.findViewById(R.id.body);
        Button buttonView = (Button)convertView.findViewById(R.id.btnDonation);
//        prgbarDonation = (ProgressBar)convertView.findViewById(R.id.prgbarDonation);
//
//        viewHolder.prg_donation = prgbarDonation;

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
                dlg.setMessage("포인트를 되돌릴 수 없습니다.");
                dlg.setView(edtPoint);
                dlg.setIcon(R.drawable.ic_android_black_24dp);
                dlg.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //buttonView.removeView
                        // parent 없애는 로직이 필요할듯??
                    }
                });
                dlg.setPositiveButton("후원", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        value = Integer.parseInt(edtPoint.getText().toString()); //문자형 -> 정수형 변환
                        viewHolder.prg_donation.incrementProgressBy(value);

                        //prgbarDonation.setProgress(Integer.parseInt(edtPoint.getText().toString()));

                        donation_json.put("notice_title", sponsor.getTitle().toString());
                        donation_json.put("amount", String.valueOf(value));

                        JSONObject parameter = new JSONObject(donation_json);
                        Log.d(TAG, "parameter : " + parameter.toString());

                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                                url,
                                parameter,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
//                                        try {
//                                            Log.d(TAG, "response : " + response.toString());
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
                                        Log.d(TAG, "response : " + response.toString());
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast toast = Toast.makeText(getContext(), "포인트를 초과하였습니다.", Toast.LENGTH_LONG);
                                        toast.show();
                                    }
                                })
                        {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                return give_token(token);
                            }
                        };

                        queue.add(request);
                    }

                });
                dlg.show();

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
