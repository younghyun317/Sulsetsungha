package com.example.sulsetsungha.donation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.sulsetsungha.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DonationFragment extends Fragment {

    String TAG = DonationFragment.class.getSimpleName();

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
    String today = dateFormat.format(Calendar.getInstance().getTime());
    ArrayList<Sponsor> sponsors;
    ListView donationListView;
    private static DonationAdapter donationAdapter;
    ImageButton btnDonationLikeList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = (View)inflater.inflate(R.layout.fragment_donation, container, false);

        btnDonationLikeList = (ImageButton) view.findViewById(R.id.btnDonationLikeList);
        btnDonationLikeList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(DonationFragment.donationAdapter.getContext(), DonationLikeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        final RequestQueue queue = Volley.newRequestQueue(getActivity());
        final String url = "http://3.38.51.117:8000/donation/";

        //ImageRequest imageRequest = new ImageRequest(url, res)

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            //후원 리스트
                            sponsors = new ArrayList<>();
                            //Log.d("response", "response : " + response.getJSONObject(0).getString("company").toString());
                            //Log.d("length", "length : " + response.getJSONArray(1).toString());

                            String id, company, title, context, deadline, imageurl;
                            long dday, target_amount, current_amount;
                            double percent;
                            ImageView imageView;

                            //int percent;
                            Date currentCal, targetCal; //현재 날짜, 비교 날짜

                            DownloadImageTask downloadImageTask = new DownloadImageTask((ImageView)view.findViewById(R.id.imageView13));

                            for (int i=0; i < response.length(); i++) {
                                id = response.getJSONObject(i).getString("id").toString();
                                company = response.getJSONObject(i).getString("company").toString();
                                title = response.getJSONObject(i).getString("title").toString();
                                context = response.getJSONObject(i).getString("context").toString();
                                deadline = response.getJSONObject(i).getString("deadline").toString();
                                target_amount = Long.parseLong(response.getJSONObject(i).getString("target_amount"));
                                current_amount = Long.parseLong(response.getJSONObject(i).getString("current_amount"));
                                imageurl = response.getJSONObject(i).getString("photo").toString();

                                //percent = Math.round((current_amount/target_amount)*100);
                                percent = ((current_amount * 1.0)/target_amount)*100;
                                percent = Math.round(percent);
                                Log.d(TAG, "current_amount : " + String.valueOf(current_amount).toString());
                                Log.d(TAG, "target_amount : " + String.valueOf(target_amount).toString());
                                Log.d(TAG, "percent : " + String.valueOf(percent).toString());
                                currentCal = dateFormat.parse(today);
                                targetCal = dateFormat.parse(deadline);

                                // Date로 변환된 두 날짜를 계산한 뒤 그 리턴값으로 long type 변수를 초기화 하고 있다.
                                // 연산결과 -950400000. long type 으로 return 된다.
                                long calDate = targetCal.getTime() - currentCal.getTime();
                                // Date.getTime() 은 해당날짜를 기준으로1970년 00:00:00 부터 몇 초가 흘렀는지를 반환해준다.
                                // 이제 24*60*60*1000(각 시간값에 따른 차이점) 을 나눠주면 일수가 나온다.
                                long calDateDays = calDate / ( 24*60*60*1000);

                                dday = Math.abs(calDateDays);
                                //imageView = (ImageView)new DownloadImageTask((ImageView)view.findViewById(R.id.imageView13)).execute(imageurl);
                                sponsors.add(new Sponsor(id, company, title, context, Long.toString(dday), Double.toString(percent)));
                                //new DownloadImageTask((ImageView)view.findViewById(R.id.)).execute(imageurl);
                            }

                            donationListView = (ListView)view.findViewById(R.id.listView_donation);
                            donationAdapter = new DonationAdapter(getContext(), sponsors);
                            donationListView.setAdapter(donationAdapter);

                            donationAdapter.notifyDataSetChanged();//갱신하기

                            donationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    //각 아이템을 분간 할 수 있는 position과 뷰
                                    String selectedItem = (String) view.findViewById(R.id.txtName).getTag().toString();
                                    Toast.makeText(getContext(), "Clicked: " + position +" " + selectedItem, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (JSONException | ParseException e) {
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
        return view;
    }

    class Sponsor {
        private String id;
        private String company;
        private String title;
        private String context;
        private String dday;
        private String donation;
        //private ImageView image;
        private int progressbar;
        //private ProgressBar progressbar;

        public Sponsor(String id, String company, String title, String context, String dday, String donation) {
            //this.request = request;
            this.id = id;
            this.company = company;
            this.title = title;
            this.context = context;
            this.dday = dday;
            this.donation = donation;
            //this.image = image;
            //this.progressbar = progressbar;
        }

        public String getId() { return id; }

        public String getCompany() {
            return company;
        }

        public String getTitle() {
            return title;
        }

        public String getContext() { return context; }

        public String getDday() {
            return dday;
        }

        public String getDonation() {
            return donation;
        }

//        //public ImageView getImage() {
//            return image;
//        }
    } //class Sponsor

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

    //기부하기 좋아요 목록 가져오기
//    private void getDonationLikeList() {
//        final RequestQueue queue = Volley.newRequestQueue(getActivity());
//        final String url = "http://3.38.51.117:8000/get/like/donation/";
//
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        String token = sharedPreferences.getString("access_token", null);
//
//        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
//                url,
//                null,
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        try {
//                            Log.d(TAG, "like_list_response : " + response.toString());
//                            String donation_id;
//
//                            for (int i=0; i < response.length(); i++) {
//                                donation_id = response.getJSONObject(i).getString("id").toString();
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                    }
//                })
//        {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                return  give_token(token);
//            }
//        };
//
//        queue.add(jsonArrayRequest);
//    }
//
//    Map<String, String> give_token(String token) {
//        HashMap<String, String> headers = new HashMap<>();
//        headers.put("Authorization", "Bearer " + token);
//
//        return headers;
//    }

}