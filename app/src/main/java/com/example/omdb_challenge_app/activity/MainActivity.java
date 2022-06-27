package com.example.omdb_challenge_app.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.omdb_challenge_app.R;
import com.example.omdb_challenge_app.adapters.MovieAdapter;
import com.example.omdb_challenge_app.databinding.ActivityMainBinding;
import com.example.omdb_challenge_app.model.Movie;
import com.example.omdb_challenge_app.util.Constraints;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {
    List<Movie> movelist;
    MovieAdapter movieAdapter;

    JsonObjectRequest ObjectRequest;
    RequestQueue      requestQueue;
    String url;
    String search_value  = "Marvel";
    private static SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    String searchByMenuaBar = "";

    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setTitle(Constraints.FilmListTitle);

        getData(search_value);

    }

    private void init() {
        movelist     = new ArrayList<>();
        movieAdapter = new MovieAdapter(movelist);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        //binding.chooseButton.setLayoutManager(gridLayoutManager);
        binding.recyclerviewList.setLayoutManager(gridLayoutManager);
        binding.recyclerviewList.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerviewList.setAdapter(movieAdapter);
    }

    private String getUrl(String value) {
        return Constraints.url_API + Constraints.search_q1 + Constraints.key_API + Constraints.search_q2 + value + Constraints.search_q3;
    }
    private void getData(String searchValue) {
        url     = getUrl(searchValue);
        ObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                JSONArray jsonArray = null;
                try {
                    jsonArray = response.getJSONArray("Search");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.length(); i++) {

                        try {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            Movie movie = new Movie();

                            movie.setPoster(jsonObject.getString(Constraints.poster));
                            movie.setTitle(jsonObject.getString(Constraints.title));
                            movie.setYear(jsonObject.getString(Constraints.year));
                            movie.setImdbID(jsonObject.getString(Constraints.imdbID));

                            movelist.add(movie);
                            if (i == jsonArray.length() - 1)
                                movieAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w("onErrorResponse", error.toString());
            }
        });

        requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(ObjectRequest);

        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);
       // final MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Do whatever you need
                return true; // KEEP IT TO TRUE OR IT DOESN'T OPEN !!
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Do whatever you need
                onBackPressed();
                return true; // OR FALSE IF YOU DIDN'T WANT IT TO CLOSE!
            }
        });


        if (searchMenuItem != null) {
            searchView = (SearchView) searchMenuItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {

                    getData(newText);
                    searchByMenuaBar = newText;
                    return true;
                }
                @Override
                public boolean onQueryTextSubmit(String query) {

                    searchView.clearFocus();
                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
//                    Toast t = Toast.makeText(MainActivity.this, "close", Toast.LENGTH_SHORT);
//                    t.show();
//                    ClearSearchManagerValue();
                    searchByMenuaBar = "";

                    getData(search_value);

                    return false;

                }
            });
            searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean newViewFocus) {
                    if (!newViewFocus) {
                        //getData(searchByMenuaBar);
                        //Collapse the action item.
                        searchMenuItem.collapseActionView();
                    }
                }
            });
            KeyboardVisibilityEvent.setEventListener(
                    this,
                    new KeyboardVisibilityEventListener() {
                        @Override
                        public void onVisibilityChanged(boolean isOpen) {
                            if (!isOpen) {
                                View focusedView = getWindow().getCurrentFocus();
                                if (focusedView != null && focusedView instanceof SearchView) { // does SearchView have focus?
                                    searchView.clearFocus();
                                }
                            }
                        }
                    });
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            return false;

        }
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (!searchView.isIconified()) {
            searchView.setIconified(true);

        } else {
            final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(Constraints.EXIt)
                    .setConfirmText(Constraints.YES)
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            //sDialog.dismissWithAnimation();
                            ActivityCompat.finishAffinity(MainActivity.this);
                        }
                    })
                    .setCancelButton(Constraints.NO, new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                        }
                    });
        pDialog.show();
        pDialog.setCancelable(false);
    }

    }
}
