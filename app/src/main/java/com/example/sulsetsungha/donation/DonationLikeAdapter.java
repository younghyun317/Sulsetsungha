package com.example.sulsetsungha.donation;

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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class DonationLikeAdapter extends ArrayAdapter implements AdapterView.OnItemClickListener {

    String TAG = DonationLikeAdapter.class.getSimpleName();

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

    public DonationLikeAdapter(Context context, ArrayList list){
        super(context, 0, list);
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.list = list;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final RequestQueue queue = Volley.newRequestQueue(this.getContext());
        final String url_donation_user = "http://3.38.51.117:8000/donation_user/";
        final String url_like_donation = "http://3.38.51.117:8000/like/donation/";
        HashMap<String, String> donation_json = new HashMap<>();
        HashMap<String, String> donation_like_json = new HashMap<>();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String token = sharedPreferences.getString("access_token", null);

        //View v = layoutInflater.inflate(R.layout.item_donation, null);
        final DonationLikeAdapter.ViewHolder viewHolder;
        viewHolder = new DonationLikeAdapter.ViewHolder();

        if (convertView == null){
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            convertView = layoutInflater.inflate(R.layout.item_donation, parent, false);
        }

        View bodyView = (View)convertView.findViewById(R.id.body);
        Button btnDonationView = (Button)convertView.findViewById(R.id.btnDonation);
        ImageButton btnDonationLikeView = (ImageButton)convertView.findViewById(R.id.btnDonationLike);
        EditText edtPoint = new EditText(getContext());

        viewHolder.txt_company = (TextView)convertView.findViewById(R.id.txtName);
        viewHolder.txt_title = (TextView)convertView.findViewById(R.id.txtSummary);
        viewHolder.prg_donation = (ProgressBar)convertView.findViewById(R.id.prgbarDonation);
        viewHolder.txt_dday = (TextView)convertView.findViewById(R.id.txtDday);
        viewHolder.txt_donation = (TextView)convertView.findViewById(R.id.t);

        final DonationLikeActivity.Sponsor sponsor = (DonationLikeActivity.Sponsor)list.get(position);
        viewHolder.txt_company.setText(sponsor.getCompany().toString());
        viewHolder.txt_title.setText(sponsor.getTitle().toString());
        viewHolder.txt_dday.setText(sponsor.getDday().toString());
        viewHolder.txt_donation.setText(sponsor.getDonation().toString());
        viewHolder.prg_donation.setProgress((int) Math.round(Double.parseDouble(sponsor.getDonation())));

        donation_like_json.put("notice_title", sponsor.getTitle().toString());
        JSONObject parameter_donation_like = new JSONObject(donation_like_json);

        bodyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "click list body", Toast.LENGTH_SHORT).show();

            }
        });

        btnDonationLikeView.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.PATCH,
                        url_like_donation,
                        parameter_donation_like,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, "like_response : " + response.toString());

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

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

        btnDonationView.setOnClickListener(new Button.OnClickListener() {
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

                        JSONObject parameter_donation_user = new JSONObject(donation_json);

                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                                url_donation_user,
                                parameter_donation_user,
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
