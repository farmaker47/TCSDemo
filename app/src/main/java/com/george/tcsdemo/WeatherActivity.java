package com.george.tcsdemo;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.FloatingActionButton;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.george.tcsdemo.data.HitsList;
import com.george.tcsdemo.data.Lista;
import com.george.tcsdemo.interfaces.TcsInterface;
import com.george.tcsdemo.utils.SimpleIdlingResource;
import com.george.tcsdemo.utils.SoloupisEmptyRecyclerView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherActivity extends AppCompatActivity {

    //free api key with limitation of 60 calls per minute
    public static final String API_KEY = "ec715631be02bd4b13f25d478d34ad8e";
    public static final String METRIC_UNITS = "metric";
    private static final String HITS_LIST_FOR_ROTATION = "hits_list_for_rotation";
    private static final String BUNDLE_RECYCLER_LAYOUT = "recycler_layout";
    Retrofit retrofit;
    String latitude, longtitude, address;
    private ArrayList<Lista> hitaList;
    private SoloupisEmptyRecyclerView mRecyclerView;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private LinearLayoutManager layoutManager;
    private ProgressBar progressBar;
    private ImageView imageView;
    private TextView scrollingTextView;
    private Parcelable savedRecyclerLayoutState;

    ///TO BE USED WITH TESTS
    @Nullable
    private SimpleIdlingResource mIdlingResource;

    /**
     * Only called from test, creates and returns a new {@link SimpleIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the IdlingResource instance
        getIdlingResource();

        //to use with unit tests when fab button is pressed without  passing from MapsActivity
        latitude = "37.0298763";
        longtitude = "22.1370342";

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        setTitle("");

        // Enable the Up button
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        hitaList = new ArrayList();
        progressBar = findViewById(R.id.progressSearchFragment);
        imageView = findViewById(R.id.imageSearchFragment);

        //Rolling title
        scrollingTextView = findViewById(R.id.scrollingTextView);
        findViewById(R.id.scrollingTextView).setSelected(true);

        //check if first time created or it is after rotation
        if (savedInstanceState != null) {
            savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            if (savedInstanceState.containsKey(HITS_LIST_FOR_ROTATION)) {
                hitaList = savedInstanceState.getParcelableArrayList(HITS_LIST_FOR_ROTATION);
            }
        }

        mRecyclerView = findViewById(R.id.recyclerViewSearchFragment);
        //setting the empty view, only with custom Recycler view
        mRecyclerView.setEmptyView(imageView);

        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerViewAdapter = new RecyclerViewAdapter(this, hitaList);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        //Checking info of intent passed from MapsActivity
        Intent intent = getIntent();
        if (intent.hasExtra(MapsActivity.LATITUDE_FROM_MAPS)) {
            latitude = intent.getStringExtra(MapsActivity.LATITUDE_FROM_MAPS);
        }
        if (intent.hasExtra(MapsActivity.LONGTITUDE_FROM_MAPS)) {
            longtitude = intent.getStringExtra(MapsActivity.LONGTITUDE_FROM_MAPS);
        }
        if (intent.hasExtra(MapsActivity.ADDRESS_TO_PASS)) {
            address = intent.getStringExtra(MapsActivity.ADDRESS_TO_PASS);
        }

        //set text to ActionBar textview
        scrollingTextView.setText(getString(R.string.daysForecastForScrollingTextView) + address);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mRecyclerViewAdapter.setHitsData(new ArrayList<Lista>());
                progressBar.setVisibility(View.VISIBLE);
                //Start action to fetch info with Retrofit
                fetchInfo(latitude, longtitude, METRIC_UNITS, API_KEY);

            }
        });

        //new instance of retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(TcsInterface.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Check if there is internet connection
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // If there is a network connection ask for location
        if (networkInfo != null && networkInfo.isConnected()) {
            if (savedInstanceState == null) {
                fetchInfo(latitude, longtitude, METRIC_UNITS, API_KEY);
                //show progress bar
                progressBar.setVisibility(View.VISIBLE);

                //set idle to false
                if (mIdlingResource != null) {
                    mIdlingResource.setIdleState(false);
                }
            } else {
                mRecyclerViewAdapter = new RecyclerViewAdapter(this, hitaList);
                mRecyclerView.setAdapter(mRecyclerViewAdapter);
            }

        } else {
            Toast.makeText(WeatherActivity.this, getString(R.string.connectToInternet), Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // layoutmanager so the recyclerview go back at same position, and list of results
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, layoutManager.onSaveInstanceState());
        //save list so on rotation don't call again the API
        outState.putParcelableArrayList(HITS_LIST_FOR_ROTATION, hitaList);
    }

    private void fetchInfo(String latitude, String longtitude, String metric, String key) {

        TcsInterface tcsInterface = retrofit.create(TcsInterface.class);
        Call<HitsList> call = tcsInterface.getAllWeatherData(latitude, longtitude, metric, key);
        call.enqueue(new Callback<HitsList>() {
            @Override
            public void onResponse(Call<HitsList> call, Response<HitsList> response) {

                HitsList hitsList = response.body();
                Log.d("MASTER", hitsList.toString());

                if (hitsList != null) {
                    hitaList = hitsList.getResults();
                    if (hitsList.getResults() == null) {
                        Toast.makeText(WeatherActivity.this, getString(R.string.noResultsFound), Toast.LENGTH_LONG).show();
                    }
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 200ms
                            progressBar.setVisibility(View.INVISIBLE);

                            mRecyclerViewAdapter.setHitsData(hitaList);
                            //we reset position to 0
                            mRecyclerView.smoothScrollToPosition(0);
                            layoutManager.scrollToPositionWithOffset(0, 0);

                            //running the animation at the beggining of showing the list
                            runLayoutAnimation(mRecyclerView);

                            if (savedRecyclerLayoutState != null) {
                                layoutManager.onRestoreInstanceState(savedRecyclerLayoutState);
                            }

                            //set idle to true
                            if (mIdlingResource != null) {
                                mIdlingResource.setIdleState(true);
                            }
                        }
                    }, 200);

                }

            }

            @Override
            public void onFailure(Call<HitsList> call, Throwable t) {

            }
        });

    }

    //animation when we are populating the recyclerview
    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }


}


