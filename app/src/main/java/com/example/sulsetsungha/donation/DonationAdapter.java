package com.example.sulsetsungha.donation;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.Window;
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
        final String url_donation_user = "http://3.38.51.117:8000/donation_user/";
        final String url_like_donation = "http://3.38.51.117:8000/like/donation/";
        HashMap<String, String> donation_json = new HashMap<>();
        HashMap<String, String> donation_like_json = new HashMap<>();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String token = sharedPreferences.getString("access_token", null);

        //View v = layoutInflater.inflate(R.layout.item_donation, null);
        final ViewHolder viewHolder;
        viewHolder = new ViewHolder();

        if (convertView == null){
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            convertView = layoutInflater.inflate(R.layout.item_donation, parent, false);
        }

        View bodyView = (View)convertView.findViewById(R.id.body);
        Button btnDonationView = (Button)convertView.findViewById(R.id.btnDonation);
        ImageButton btnDonationLikeView = (ImageButton)convertView.findViewById(R.id.btnDonationLike);
        //EditText edtPoint = new EditText(getContext());

        viewHolder.txt_company = (TextView)convertView.findViewById(R.id.txtName);
        viewHolder.txt_title = (TextView)convertView.findViewById(R.id.txtSummary);
        viewHolder.prg_donation = (ProgressBar)convertView.findViewById(R.id.prgbarDonation);
        viewHolder.txt_dday = (TextView)convertView.findViewById(R.id.txtDday);
        viewHolder.txt_donation = (TextView)convertView.findViewById(R.id.t);

        final DonationFragment.Sponsor sponsor = (DonationFragment.Sponsor)list.get(position);
        viewHolder.txt_company.setText(sponsor.getCompany().toString());
        viewHolder.txt_title.setText(sponsor.getTitle().toString());
        viewHolder.txt_dday.setText(sponsor.getDday().toString());
        viewHolder.txt_donation.setText(sponsor.getDonation().toString());
        viewHolder.prg_donation.setProgress((int) Math.round(Double.parseDouble(sponsor.getDonation())));

        bodyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DonationDetailActivity.class);
                intent.putExtra("Id", sponsor.getId());
                intent.putExtra("Company", sponsor.getCompany());
                intent.putExtra("Title", sponsor.getTitle().toString());
                intent.putExtra("Context", sponsor.getContext().toString());
                intent.putExtra("Dday", sponsor.getDday().toString());
                intent.putExtra("Percent", sponsor.getDonation().toString());
                intent.putExtra("Donation", sponsor.getDonation());

                context.startActivity(intent);

            }
        });

        donation_like_json.put("notice_title", sponsor.getTitle().toString());
        JSONObject parameter_donation_like = new JSONObject(donation_like_json);

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
                                String id;
                                Drawable unlike_drawable = getContext().getDrawable(R.drawable.button_like);
                                Drawable like_drawable = getContext().getDrawable(R.drawable.button_like_color);

                                try {
                                    id = response.getString("id").toString();
                                    if (id == sponsor.getId().toString()) {
                                        btnDonationLikeView.setImageDrawable(like_drawable);
                                    } else {
                                        btnDonationLikeView.setImageDrawable(unlike_drawable);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


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

                Dialog dlg;
                dlg = new Dialog(getContext());
                dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dlg.setContentView(R.layout.activity_pushalarm);

                dlg.show(); // 다이얼로그 띄우기

                /* 이 함수 안에 원하는 디자인과 기능을 구현하면 된다. */

                // 위젯 연결 방식은 각자 취향대로~
                // '아래 아니오 버튼'처럼 일반적인 방법대로 연결하면 재사용에 용이하고,
                // '아래 네 버튼'처럼 바로 연결하면 일회성으로 사용하기 편함.
                // *주의할 점: findViewById()를 쓸 때는 -> 앞에 반드시 다이얼로그 이름을 붙여야 한다.

                // 아니오 버튼
                Button noBtn = dlg.findViewById(R.id.btn_cancel);
                noBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 원하는 기능 구현
                        dlg.dismiss(); // 다이얼로그 닫기
                    }
                });
                // 네 버튼
                dlg.findViewById(R.id.btn_donate).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText edtPoint = dlg.findViewById(R.id.edt_point);
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

                        dlg.dismiss();
                    }
                });

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

    public void showDialog() {

    }

}
