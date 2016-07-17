package com.acadgild.balu.imdb;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Switch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    ListView listView_movies;
    LazyImageLoadAdapter lazyImageLoadAdapter;
    ArrayList<HashMap<String, String>> arrayList;
    JSONArray jsonArray;
    JSONObject jsonObject;

    String URL =
            "http://api.themoviedb.org/3/movie/upcoming?api_key=8496be0b2149805afa458ab8ec27560c";
    static String MOVIE_ID = "id";
    static String MOVIE_TITLE = "original_title";
    static String RELEASE_DATE = "release_date";
    static String POPULARITY = "popularity";
    static String VOTE_COUNT = "vote_count";
    static String VOTE_AVERAGE = "vote_average";
    static String MOVIE_IMAGE = "poster_path";
    int mode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.movie);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        listView_movies = (ListView) findViewById(R.id.listView_movies);
        new DownloadJSON().execute();
        listView_movies.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                HashMap<String, String> hashMapIntent = new HashMap<String, String>();
                hashMapIntent = arrayList.get(position);
                Log.e("before intent id ", hashMapIntent.get(MainActivity.MOVIE_ID) );
                Log.e("before intent position ", String.valueOf(position));
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra("id", hashMapIntent.get(MainActivity.MOVIE_ID));
                startActivity(intent);
                Log.e("afterintent", "after intent");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_popular:
            {
                URL = "http://api.themoviedb.org/3/movie/popular?api_key=8496be0b2149805afa458ab8ec27560c";
                new DownloadJSON().execute();
                break;
            }
            case R.id.menu_upcoming:
            {
                URL = "http://api.themoviedb.org/3/movie/upcoming?api_key=8496be0b2149805afa458ab8ec27560c";
                new DownloadJSON().execute();
                break;
            }
            case R.id.menu_latest:
            {
                URL = "http://api.themoviedb.org/3/movie/latest?api_key=8496be0b2149805afa458ab8ec27560c";
                new DownloadJSON_LatestMovie().execute();
                break;
            }
            case R.id.menu_playing:
            {
                URL = "http://api.themoviedb.org/3/movie/now_playing?api_key=8496be0b2149805afa458ab8ec27560c";
                new DownloadJSON().execute();
                break;
            }
            case R.id.menu_top:
            {
                URL = "http://api.themoviedb.org/3/movie/top_rated?api_key=8496be0b2149805afa458ab8ec27560c";
                new DownloadJSON().execute();
                break;
            }

            case R.id.menu_favorites:
            {
                mode = 1;
                new DownloadJSON_Favorites_Watchlist().execute();
                break;
            }

            case R.id.menu_watchlist:
            {
                mode = 2;
                new DownloadJSON_Favorites_Watchlist().execute();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private class DownloadJSON extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            arrayList = new ArrayList<HashMap<String, String>>();
            JSONFunctions jsonFunctions = new JSONFunctions();
            jsonObject = jsonFunctions.getJSONfromURL(URL);

            try {
                jsonArray = jsonObject.getJSONArray("results");

                for (int i = 0; i < jsonArray.length(); i++) {
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    jsonObject = jsonArray.getJSONObject(i);
                    hashMap.put("id", jsonObject.getString("id"));
                    hashMap.put("original_title", jsonObject.getString("original_title"));
                    hashMap.put("release_date", jsonObject.getString("release_date"));
                    hashMap.put("popularity", jsonObject.getString("popularity"));
                    hashMap.put("vote_count", jsonObject.getString("vote_count"));
                    hashMap.put("vote_average", jsonObject.getString("vote_average"));
                    hashMap.put("poster_path", "http://image.tmdb.org/t/p/original" +
                            jsonObject.getString("poster_path"));
                    arrayList.add(hashMap);
                }

            } catch (JSONException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Locate the listview in listview_main.xml
            listView_movies = (ListView) findViewById(R.id.listView_movies);
            // Pass the results into ListViewAdapter.java
            lazyImageLoadAdapter = new LazyImageLoadAdapter(MainActivity.this, arrayList);
            // Set the adapter to the ListView
            listView_movies.setAdapter(lazyImageLoadAdapter);
        }

    }

    private class DownloadJSON_LatestMovie extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            arrayList = new ArrayList<HashMap<String, String>>();
            JSONFunctions jsonFunctions = new JSONFunctions();
            jsonObject = jsonFunctions.getJSONfromURL(URL);

            try {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("id", jsonObject.getString("id"));
                hashMap.put("original_title", jsonObject.getString("original_title"));
                hashMap.put("release_date", jsonObject.getString("release_date"));
                hashMap.put("popularity", jsonObject.getString("popularity"));
                hashMap.put("vote_count", jsonObject.getString("vote_count"));
                hashMap.put("vote_average", jsonObject.getString("vote_average"));
                hashMap.put("poster_path", "http://image.tmdb.org/t/p/original" +
                        jsonObject.getString("poster_path"));
                arrayList.add(hashMap);


            } catch (JSONException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            // Locate the listview in listview_main.xml
            listView_movies= (ListView) findViewById(R.id.listView_movies);
            // Pass the results into ListViewAdapter.java
            lazyImageLoadAdapter = new LazyImageLoadAdapter(MainActivity.this, arrayList);
            // Set the adapter to the ListView
            listView_movies.setAdapter(lazyImageLoadAdapter);
        }
    }

    private class DownloadJSON_Favorites_Watchlist extends AsyncTask<Void, Void, Void>
    {
        ArrayList<IMDB> arrayList_imdb;
        IMDB imdb;
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            arrayList_imdb = new ArrayList<>();
            arrayList_imdb.clear();

            if (mode == 1) {
                arrayList_imdb = dbHelper.get_favorite_entries();
            }
            else if (mode == 2)
            {
                arrayList_imdb = dbHelper.get_watchlist_entries();
            }

            arrayList = new ArrayList<HashMap<String, String>>();

            JSONFunctions jsonFunctions = new JSONFunctions();
            try {
                for (int i = 0; i < arrayList_imdb.size(); i++)
                {
                    URL = "http://api.themoviedb.org/3/movie/" + arrayList_imdb.get(i).getMovie_id()
                                        + "?api_key=8496be0b2149805afa458ab8ec27560c";
                    jsonObject = jsonFunctions.getJSONfromURL(URL);
                    HashMap<String, String> hashMap = new HashMap<String, String>();

                    hashMap.put("id", jsonObject.getString("id"));
                    hashMap.put("original_title", jsonObject.getString("original_title"));
                    hashMap.put("release_date", jsonObject.getString("release_date"));
                    hashMap.put("popularity", jsonObject.getString("popularity"));
                    hashMap.put("vote_count", jsonObject.getString("vote_count"));
                    hashMap.put("vote_average", jsonObject.getString("vote_average"));
                    hashMap.put("poster_path", "http://image.tmdb.org/t/p/original" +
                            jsonObject.getString("poster_path"));
                    arrayList.add(hashMap);
                }

            } catch (JSONException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Locate the listview in listview_main.xml
            listView_movies = (ListView) findViewById(R.id.listView_movies);
            // Pass the results into ListViewAdapter.java
            lazyImageLoadAdapter = new LazyImageLoadAdapter(MainActivity.this, arrayList);
            // Set the adapter to the ListView
            listView_movies.setAdapter(lazyImageLoadAdapter);
        }
    }
}
