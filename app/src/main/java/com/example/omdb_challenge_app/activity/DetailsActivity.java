package com.example.omdb_challenge_app.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.omdb_challenge_app.R;
import com.example.omdb_challenge_app.databinding.ActivityDetailsBinding;
import com.example.omdb_challenge_app.model.MovieDetail;
import com.example.omdb_challenge_app.util.Constraints;

import org.json.JSONException;
import org.json.JSONObject;


public class DetailsActivity extends AppCompatActivity{

    JsonObjectRequest   objectRequest;
    RequestQueue        requestQueue;
    String url = Constraints.url_API;
    String getposter = "";
    ActivityDetailsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_details);


        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setTitle(Constraints.Details);

        myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_close_white));
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        init();

    }

    private void init() {

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String id = bundle.getString(Constraints.MOVIEID);
            url += Constraints.search_q4 + id;
        }

        showData();
    }

    private void showData() {

        String URL_JSON = url + "&" + Constraints.key_API;

        objectRequest = new JsonObjectRequest(Request.Method.GET, URL_JSON, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                MovieDetail details = new MovieDetail();

                try {

                    details.setPoster(response.getString("Poster"));


                    getposter = details.getPoster();
                    Glide.with(DetailsActivity.this).load(details.getPoster()).centerCrop().into(binding.imageViewPoster);

                    details.setTitle(response.getString("Title"));
                    binding.textViewTitle.setText(details.getTitle());

                    details.setReleased(response.getString("Released"));
                    binding.textViewReleased.setText(details.getReleased());

                    details.setRuntime(response.getString("Runtime"));
                    binding.textViewRuntime.setText(details.getRuntime());

                    details.setGenre(response.getString("Genre"));
                    binding.textViewGenre.setText(details.getGenre());

                    details.setDirector(response.getString("Director"));
                    binding.textViewDirector.setText(details.getDirector());

                    details.setProduction(response.getString("Production"));
                    binding.textViewProduction.setText(details.getProduction());

                    details.setImdbRating(response.getString("imdbRating"));
                    binding.textViewImdbRating.setText(details.getImdbRating());

                    details.setWriter(response.getString("Writer"));
                    binding.textViewWriter.setText(details.getWriter());

                    details.setActors(response.optString("Actors", "/"));
                    binding.textViewActors.setText(details.getActors());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w("onErrorResponse", error.toString());
            }
        });

        requestQueue = Volley.newRequestQueue(DetailsActivity.this);
        requestQueue.add(objectRequest);
    }

}




