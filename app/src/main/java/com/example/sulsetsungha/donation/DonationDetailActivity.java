package com.example.sulsetsungha.donation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.sulsetsungha.R;

public class DonationDetailActivity extends AppCompatActivity {

    private TextView txtDetailDday, txtDetailTitle, txtDetailCompany, txtDetailPercent, txtDetailSummary;
    private ImageButton btnDetailLike, btnDetailFighting;
    private Button btnDetailDonation;
    private ProgressBar prgDetailBar;

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

        getDetailDonation(); //기부하기 상세보기 불러오기
    }

    //기부하기 상세보기 불러오기
    private void getDetailDonation() {
        Intent intent = getIntent();
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

    }
}