package com.example.omdb_challenge_app.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

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
import com.google.android.material.tabs.TabLayout;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    List<Movie> movelist = new ArrayList<>();
    MovieAdapter movieAdapter;

    JsonObjectRequest ObjectRequest;
    RequestQueue      requestQueue;
    String url;
    String search_value  = "Marvel";
    private static SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    String searchByMenuaBar = "";

    static ActivityMainBinding binding;
    static FragmentManager manager = null;
    final Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setTitle(Constraints.FilmListTitle);

        manager = getSupportFragmentManager();

        new BgTask(getApplicationContext(), search_value).execute();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    init();
                } catch (Exception e){
                    Log.i("Exrrr_","error_"+e.getMessage());
                }

            }
        }, 300);


        binding.btnSearch.setOnClickListener(this);
        binding.titleClear.setOnClickListener(this);

    }
    private void init() {
        if (movelist != null && movelist.size() > 0) {
            movieAdapter = new MovieAdapter(movelist, 0);

            GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
            //binding.chooseButton.setLayoutManager(gridLayoutManager);
            binding.recyclerviewList.setLayoutManager(gridLayoutManager);
            binding.recyclerviewList.setItemAnimator(new DefaultItemAnimator());
            binding.recyclerviewList.setAdapter(movieAdapter);


            int tablLaySize = 0;
            double tabLayDoubleSize = 0.0;
            if (movelist.size() >= 6) {
                tablLaySize = movelist.size() / 6;  // 12/4 = 3
                tabLayDoubleSize = movelist.size() % 6;
            }

            int tablLayOtherSize = 0;
            if (tabLayDoubleSize > 0.00) {
                tablLayOtherSize = 1;

            }

            tabLayoutFun(getApplicationContext(),binding,tablLaySize,tablLayOtherSize,movelist);
        } else {
            List<Movie> movelistempty = new ArrayList<>();
            movieAdapter = new MovieAdapter( movelistempty, 0);

            GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
            binding.recyclerviewList.setLayoutManager(gridLayoutManager);
            binding.recyclerviewList.setItemAnimator(new DefaultItemAnimator());
            binding.recyclerviewList.setAdapter(movieAdapter);

            tabLayoutFun(getApplicationContext(),binding,0,0,movelist);
        }
    }

    public static List<Integer> tabPostionCount = new ArrayList<>();

    private String getUrl(String value) {
        return Constraints.url_API + Constraints.search_q1 + Constraints.key_API + Constraints.search_q2 + value + Constraints.search_q3;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSearch:

                doFun(binding.editSearchTitle.getText().toString());

                break;
            case R.id.titleClear:
                binding.editSearchTitle.setText("");
                doFun("");
                break;
        }
    }

    private void doFun(String sv) {
        new BgTask(getApplicationContext(), sv).execute();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    init();
                } catch (Exception e){
                    Log.i("Exrrrdddd_","error_"+e.getMessage());
                }

            }
        }, 300);
    }

    private class BgTask extends AsyncTask<List<Movie>, Void, List<Movie>> {
        Context context;
        String searchValue;
        List<Movie> result = null;
        public BgTask(Context c,String s) {
            context = c;
            searchValue = s;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected List<Movie> doInBackground(List<Movie>... voids) {

            result = getData(searchValue);
            return result;
        }
        @Override
        protected void onPostExecute(List<Movie> movie) {
            super.onPostExecute(movie);
        }
    }
    private List<Movie> getData(String searchValue) {
        url = getUrl(searchValue);
        if (searchValue != null) {
            ObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    JSONArray jsonArray = null;
                    try {
                        jsonArray = response.getJSONArray("Search");
                    } catch (Exception e) {
                        e.printStackTrace();
                        movelist = null;
                    }
                    if (jsonArray != null) {
                        try {
                            movelist.clear();
                        } catch (Exception e) {
                            movelist = new ArrayList<>();
                        }
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
                                    try {

                                        movieAdapter.notifyDataSetChanged();
                                    } catch (Exception e) {

                                    }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        movelist = null;
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
        } else {
            movelist = null;
        }
        return movelist;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);
       // final MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        searchMenuItem.setVisible(false);
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

    private static void tabLayoutFun(Context context, ActivityMainBinding mbinding, int tablLaySize,
                                     int layOthSize, List<Movie> movielist) {
        // try {
        mbinding.tabLayout.removeAllTabs();

        tabPostionCount.clear();
        int count = 0;
        for (int i = 1 ; i <= tablLaySize ; i ++){
            count ++;
            mbinding.tabLayout.addTab(mbinding.tabLayout.newTab().setText(String.valueOf(i)).setIcon(R.drawable.outline_live_tv_24));
          //  mbinding.tabLayout.setElevation(30);

            tabPostionCount.add(count);
        }
        if (layOthSize > 0){
            count++;
            mbinding.tabLayout.addTab(mbinding.tabLayout.newTab().setText(String.valueOf(count)).setIcon(R.drawable.outline_live_tv_24));


            tabPostionCount.add(count);
        }
        mbinding.tabLayout.setElevation(30);
        mbinding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mbinding.tabLayout.setTabTextColors(Color.parseColor("#FFFFFF"), Color.parseColor("#FFFFFF"));
        mbinding.tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FFFFFF"));

        mbinding.pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(
                mbinding.tabLayout));
        mbinding.tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            public MovieAdapter movieAdapter;

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mbinding.pager.setCurrentItem(tab.getPosition());
                if (movielist!= null && movielist.size() > 0) {
                    Collections.reverse(movielist);
                    movieAdapter = new MovieAdapter(movielist, tab.getPosition());
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
                    binding.recyclerviewList.setLayoutManager(gridLayoutManager);
                    binding.recyclerviewList.setItemAnimator(new DefaultItemAnimator());
                    binding.recyclerviewList.setAdapter(movieAdapter);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                Log.i("SFDf","onTabUnselected");
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

                Log.i("SFDf","onTabReselected");
            }
        });
    }
    }
