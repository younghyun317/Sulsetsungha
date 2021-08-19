package com.example.sulsetsungha.donation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.sulsetsungha.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class DonationDetailActivity extends AppCompatActivity {

    private TextView txtDetailDday, txtDetailTitle, txtDetailCompany, txtDetailPercent, txtDetailSummary;
    private ImageButton btnDetailLike, btnDetailFighting;
    private Button btnDetailDonation;
    private ProgressBar prgDetailBar;
    private ImageView imgDonation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_detail);

        txtDetailDday = findViewById(R.id.txtDetailDday);
        txtDetailTitle = findViewById(R.id.txtDetailTitle);
        txtDetailCompany = findViewById(R.id.txtDetailCompany);
        txtDetailPercent = findViewById(R.id.txtDetailPercent);
        txtDetailSummary = findViewById(R.id.txtDetailSummary);
        btnDetailLike = findViewById(R.id.btnDetailLike);
        btnDetailFighting = findViewById(R.id.btnDetailFighting);
        btnDetailDonation = findViewById(R.id.btnDetailDonation);
        prgDetailBar = findViewById(R.id.prgDetailBar);
        imgDonation = findViewById(R.id.imgDonation);

        getDetailDonation(); //기부하기 상세보기 불러오기
    }

    //기부하기 상세보기 불러오기
    private void getDetailDonation() {
        Intent intent = getIntent();
        String id_detail = intent.getStringExtra("Id");
        String company = intent.getStringExtra("Company");
        String title = intent.getStringExtra("Title");
        String summary = intent.getStringExtra("Context");
        String dday = intent.getStringExtra("Dday");
        String percent = intent.getStringExtra("Percent");
        String donation = intent.getStringExtra("Donation");

        txtDetailDday.setText("D- " + dday);
        txtDetailTitle.setText(title);
        txtDetailCompany.setText(company);
        txtDetailSummary.setText(summary);
        txtDetailPercent.setText(percent + " %");

        prgDetailBar.setProgress((int)Math.round(Double.parseDouble(donation)));

        final RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "http://3.38.51.117:8000/donation/";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            String imageurl = "";

                            //DownloadImageTask downloadImageTask = new DownloadImageTask((ImageView)view.findViewById(R.id.imageView13));
                            for (int i=0; i < response.length(); i++) {
                                if (id_detail.equals(response.getJSONObject(i).getString("id").toString())) {
                                    imageurl = response.getJSONObject(i).getString("photo").toString();
                                }
                                //imageView = (ImageView)new DownloadImageTask((ImageView)view.findViewById(R.id.imageView13)).execute(imageurl);
                                new DownloadImageTask((ImageView)findViewById(R.id.imgDonation)).execute(imageurl);
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
                }
        );

        queue.add(jsonArrayRequest);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}