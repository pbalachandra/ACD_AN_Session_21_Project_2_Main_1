package com.acadgild.balu.imdb;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener
{
    TextView    textView_title, textView_description, textView_date, textView_budget,
                textView_revenue, textView_status, textView_vote_average, textView_vote_count,
                textView_overview, textView_favorites_label, textView_watchlist_label,
                textView_posters_label, textView_trailers_label,
                textView_cast_label, textView_crew_label;
    ImageView   imageView_votes, imageView_moviePoster, imageView_favorites, imageView_watchlist;
    RatingBar   ratingBar;
    HorizontalScrollView    horizontalScrollView_posters, horizontalScrollView_trailers,
                            horizontalScrollView_cast, horizontalScrollView_crew;
    LinearLayout linearLayout_posters, linearLayout_trailers, linearLayout_cast, linearLayout_crew;

    String URL_movieDetails, URL_moviePosters, URL_movieTrailers, URL_movieCastCrew;
    String id, imdb_id;

    ImageLoader imageLoader = new ImageLoader(this);
    IMDB imdb = new IMDB();
    DBHelper dbHelper = new DBHelper(DetailActivity.this);
    ArrayList<IMDB> arrayList_imdb = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.movie);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);

        textView_title = (TextView) findViewById(R.id.textView_title);
        textView_description = (TextView) findViewById(R.id.textView_description);
        textView_date = (TextView) findViewById(R.id.textView_date);
        textView_budget = (TextView) findViewById(R.id.textView_budget);
        textView_revenue = (TextView) findViewById(R.id.textView_revenue);
        textView_status = (TextView) findViewById(R.id.textView_status);
        textView_vote_average = (TextView) findViewById(R.id.textView_vote_average);
        textView_vote_count = (TextView) findViewById(R.id.textView_vote_count);
        textView_overview = (TextView) findViewById(R.id.textView_overview);
        textView_favorites_label = (TextView) findViewById(R.id.textView_favorites_label);
        textView_watchlist_label = (TextView) findViewById(R.id.textView_watchlist_label);
        textView_posters_label = (TextView) findViewById(R.id.textView_posters_label);
        textView_trailers_label = (TextView) findViewById(R.id.textView_trailers_label);
        textView_cast_label = (TextView) findViewById(R.id.textView_cast_label);
        textView_crew_label = (TextView) findViewById(R.id.textView_crew_label);

        imageView_votes = (ImageView) findViewById(R.id.imageView_votes);
        imageView_moviePoster = (ImageView) findViewById(R.id.imageView_moviePoster);
        imageView_favorites = (ImageView) findViewById(R.id.imageView_favorites);
        imageView_watchlist = (ImageView) findViewById(R.id.imageView_watchlist);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        horizontalScrollView_posters = (HorizontalScrollView) findViewById(R.id.horizontalScrollView_posters);
        horizontalScrollView_trailers = (HorizontalScrollView) findViewById(R.id.horizontalScrollView_trailers);
        horizontalScrollView_cast = (HorizontalScrollView) findViewById(R.id.horizontalScrollView_cast);
        horizontalScrollView_crew = (HorizontalScrollView) findViewById(R.id.horizontalScrollView_crew);

        linearLayout_posters = (LinearLayout) findViewById(R.id.linearLayout_posters);
        linearLayout_trailers = (LinearLayout) findViewById(R.id.linearLayout_trailers);
        linearLayout_cast = (LinearLayout) findViewById(R.id.linearLayout_cast);
        linearLayout_crew = (LinearLayout) findViewById(R.id.linearLayout_crew);

        imageView_favorites.setOnClickListener(this);
        imageView_watchlist.setOnClickListener(this);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        Log.e("id string", id);
        imdb = dbHelper.get_entry(Integer.parseInt(id));
        if (imdb != null) {
            Log.e("ID", String.valueOf(imdb.getMovie_id()));
            Log.e("is_favorite", String.valueOf(imdb.getIs_favorite()));
            Log.e("is_watchlist", String.valueOf(imdb.getIs_watchlist()));
        }

        URL_movieDetails = "http://api.themoviedb.org/3/movie/" + id + "?api_key=8496be0b2149805afa458ab8ec27560c";
        new JSON_movieDetails().execute();
        URL_moviePosters = "http://api.themoviedb.org/3/movie/" + id + "/images?api_key=8496be0b2149805afa458ab8ec27560c";
        new JSON_moviePosters().execute();
        URL_movieTrailers = "http://api.themoviedb.org/3/movie/" + id + "/videos?api_key=8496be0b2149805afa458ab8ec27560c";
        new JSON_movieTrailers().execute();

        new JSON_movieCast().execute();
        new JSON_movieCrew().execute();
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.imageView_favorites)
        {
            imdb = dbHelper.get_entry(Integer.parseInt(id));
            if (dbHelper.MovieIDExist(Integer.parseInt(id)))
            {
                if (imdb.getIs_favorite() == 0)
                {
                    dbHelper.update_favorite(Integer.parseInt(id), 1);
                    imageView_favorites.setImageResource(R.drawable.favorite_enable_normal);
                }
                else if (imdb.getIs_favorite() == 1)
                {
                    dbHelper.update_favorite(Integer.parseInt(id), 0);
                    imageView_favorites.setImageResource(R.drawable.favorite_disable_normal);
                }
            }
            else
            {
                dbHelper.add_new_entry(Integer.parseInt(id), 1, 0);
                imageView_favorites.setImageResource(R.drawable.favorite_enable_normal);
            }

        }
        else if (v.getId() == R.id.imageView_watchlist)
        {
            imdb = dbHelper.get_entry(Integer.parseInt(id));
            if (dbHelper.MovieIDExist(Integer.parseInt(id)))
            {
                if (imdb.getIs_watchlist() == 0)
                {
                    dbHelper.update_watchlist(Integer.parseInt(id), 1);
                    imageView_watchlist.setImageResource(R.drawable.watchlist_enable_normal);
                }
                else if (imdb.getIs_watchlist() == 1)
                {
                    dbHelper.update_watchlist(Integer.parseInt(id), 0);
                    imageView_watchlist.setImageResource(R.drawable.watchlist_disable_normal);
                }
            }
            else
            {
                dbHelper.add_new_entry(Integer.parseInt(id), 0, 1);
                imageView_watchlist.setImageResource(R.drawable.watchlist_enable_normal);
            }

        }
    }

    class JSON_movieDetails extends AsyncTask<Void, Void, Void>
    {
        String str_movieImage, str_title, str_description, str_date, str_budget, str_revenue, str_status,
                str_vote_average, str_vote_count, str_overview;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            JSONObject jsonObject;
            JSONFunctions jsonFunctions_movieDetails = new JSONFunctions();
            jsonObject = jsonFunctions_movieDetails.getJSONfromURL(URL_movieDetails);
            Log.e("json", String.valueOf(jsonObject));
            try {
                str_movieImage = "http://image.tmdb.org/t/p/w500" + jsonObject.getString("poster_path");
                str_title = jsonObject.getString("title");
                str_description = jsonObject.getString("tagline");
                str_budget = getResources().getString(R.string.budget) + " " +
                        jsonObject.getString("budget");
                str_date = jsonObject.getString("release_date");
                str_revenue = getResources().getString(R.string.revenue) + " " +
                        jsonObject.getString("revenue");
                str_status = getResources().getString(R.string.status) + " " +
                        jsonObject.getString("status");
                str_vote_average = jsonObject.getString("vote_average");
                str_vote_count = jsonObject.getString("vote_count");
                str_overview = jsonObject.getString("overview");
                imdb_id = jsonObject.getString("imdb_id");
            } catch (JSONException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.e("title", str_title);
            textView_title.setText(str_title);
            imageLoader.DisplayImage(str_movieImage, imageView_moviePoster);
            textView_title.setText(str_title);
            textView_description.setText(str_description);
            textView_date.setText(format_date(str_date));
            textView_budget.setText(str_budget);
            textView_revenue.setText(str_revenue);
            textView_status.setText(str_status);
            textView_vote_average.setText("(" + str_vote_average + "/10) ");
            textView_vote_count.setText(str_vote_count + " users");
            ratingBar.setRating(Float.parseFloat(str_vote_average) / 2);
            textView_overview.setText(str_overview);

            if (imdb != null) {
                if (imdb.getIs_favorite() == 1) {
                    imageView_favorites.setImageResource(R.drawable.favorite_enable_normal);
                } else if (imdb.getIs_favorite() == 0) {
                    imageView_favorites.setImageResource(R.drawable.favorite_disable_normal);
                }

                if (imdb.getIs_watchlist() == 1) {
                    imageView_watchlist.setImageResource(R.drawable.watchlist_enable_normal);
                } else if (imdb.getIs_watchlist() == 0) {
                    imageView_watchlist.setImageResource(R.drawable.watchlist_disable_normal);
                }
            }
            else
            {
                imageView_favorites.setImageResource(R.drawable.favorite_disable_normal);
                imageView_watchlist.setImageResource(R.drawable.watchlist_disable_normal);
            }
        }
    }

    class JSON_moviePosters extends AsyncTask<Void, Void, Void>
    {
        String str_moviePoster = "";
        ArrayList<String> arrayList_moviePoster = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            JSONArray jsonArray;
            JSONObject jsonObject;
            JSONFunctions jsonFunctions_moviePosters = new JSONFunctions();
            jsonObject = jsonFunctions_moviePosters.getJSONfromURL(URL_moviePosters);
            Log.e("json", String.valueOf(jsonObject));
            try {
                jsonArray = jsonObject.getJSONArray("posters");
                arrayList_moviePoster = new ArrayList<>();
                Log.e("jsonArray Length", String.valueOf(jsonArray.length()));
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    jsonObject = jsonArray.getJSONObject(i);
                    str_moviePoster = "http://image.tmdb.org/t/p/w500" + jsonObject.getString("file_path");
                    Log.e("poster", i + ":" + str_moviePoster);
                    arrayList_moviePoster.add(str_moviePoster);
                }
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
            for (int i = 0; i < arrayList_moviePoster.size(); i++)
            {
                ImageView imageView_posters = new ImageView(getApplicationContext());
                imageView_posters.setId(i);
                imageView_posters.setPadding(5, 3, 5, 3);
                imageLoader.DisplayImage(arrayList_moviePoster.get(i), imageView_posters);
                linearLayout_posters.addView(imageView_posters);
            }
        }
    }

    class JSON_movieTrailers extends AsyncTask<Void, Void, Void>
    {
        String str_movieTrailer = "";
        ArrayList<HashMap<String, String>> arrayList_movieTrailer = null;
        HashMap<String, String> hashMap_movieTrailer;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... params)
        {
            JSONArray jsonArray;
            JSONObject jsonObject;
            JSONFunctions jsonFunctions_moviePosters = new JSONFunctions();
            jsonObject = jsonFunctions_moviePosters.getJSONfromURL(URL_movieTrailers);
            Log.e("json", String.valueOf(jsonObject));
            try {
                jsonArray = jsonObject.getJSONArray("results");
                arrayList_movieTrailer = new ArrayList<HashMap<String, String>>();
                Log.e("jsonArray Length", String.valueOf(jsonArray.length()));
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    hashMap_movieTrailer = new HashMap<String, String>();
                    jsonObject = jsonArray.getJSONObject(i);
                    str_movieTrailer = "https://www.youtube.com/watch?v=" + jsonObject.getString("key");
                    hashMap_movieTrailer.put("name", jsonObject.getString("name"));
                    hashMap_movieTrailer.put("link", str_movieTrailer);
                    arrayList_movieTrailer.add(hashMap_movieTrailer);
                }
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
            for (int i = 0; i < arrayList_movieTrailer.size(); i++)
            {
                hashMap_movieTrailer = arrayList_movieTrailer.get(i);
                TextView textView_trailers = new TextView(getApplicationContext());
                textView_trailers.setId(i);
                textView_trailers.setTextColor(Color.BLUE);
                textView_trailers.setTextSize(15);
                textView_trailers.setPadding(10, 0, 10, 0);
                textView_trailers.setClickable(true);

                textView_trailers.setText(hashMap_movieTrailer.get("name"));
                textView_trailers.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Uri trailer_web_address = Uri.parse(hashMap_movieTrailer.get("link"));
                        Log.e("address", String.valueOf(trailer_web_address));
                        Intent browser = new Intent(Intent.ACTION_VIEW, trailer_web_address);
                        startActivity(browser);

                    }
                });
//                ImageView imageView_trailers = new ImageView(getApplicationContext());
//                imageView_trailers.setId(i);
//                imageView_trailers.setPadding(5, 0, 5, 0);
//                imageView_trailers.setImageResource(R.drawable.ic_theaters_black);
                TextView textView_trailers_image = new TextView(getApplicationContext());
                textView_trailers_image.setTextColor(Color.RED);
                textView_trailers_image.setTextSize(20);
                textView_trailers_image.setPadding(10, 0, 10, 0);
                textView_trailers_image.setText("*");
                linearLayout_trailers.addView(textView_trailers);
                linearLayout_trailers.addView(textView_trailers_image);
            }
        }
    }

    class JSON_movieCast extends AsyncTask<Void, Void, Void>
    {
        String str_movieCast = "";
        ArrayList<HashMap<String, String>> arrayList_movieCast = null;
        HashMap<String, String> hashMap_movieCast;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            URL_movieCastCrew = "http://api.themoviedb.org/3/movie/" + imdb_id + "/credits?api_key=8496be0b2149805afa458ab8ec27560c";
            JSONArray jsonArray;
            JSONObject jsonObject;
            JSONFunctions jsonFunctions_movieCast = new JSONFunctions();
            jsonObject = jsonFunctions_movieCast.getJSONfromURL(URL_movieCastCrew);
            Log.e("json", String.valueOf(jsonObject));
            try {
                jsonArray = jsonObject.getJSONArray("cast");
                arrayList_movieCast = new ArrayList<HashMap<String, String>>();
                Log.e("jsonArray Length", String.valueOf(jsonArray.length()));
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    hashMap_movieCast = new HashMap<String, String>();
                    jsonObject = jsonArray.getJSONObject(i);
                    str_movieCast = "http://image.tmdb.org/t/p/w500" + jsonObject.getString("profile_path");
                    hashMap_movieCast.put("cast", str_movieCast);
                    hashMap_movieCast.put("name", jsonObject.getString("name"));
                    hashMap_movieCast.put("character", jsonObject.getString("character"));
                    arrayList_movieCast.add(hashMap_movieCast);
                }
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
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (int i = 0; i < arrayList_movieCast.size(); i++)
            {
                hashMap_movieCast = arrayList_movieCast.get(i);

                LinearLayout cast_crew_list = (LinearLayout) layoutInflater.inflate(R.layout.cast_crew_list, null);
                ImageView   imageView_cast_crew = (ImageView) cast_crew_list.findViewById(R.id.imageView_cast_crew);
                TextView    textView_name = (TextView) cast_crew_list.findViewById(R.id.textView_name);
                TextView    textView_role = (TextView) cast_crew_list.findViewById(R.id.textView_role);

                imageLoader.DisplayImage(hashMap_movieCast.get("cast"), imageView_cast_crew);
                textView_name.setText(hashMap_movieCast.get("name"));
                textView_role.setText(hashMap_movieCast.get("character"));

                linearLayout_cast.addView(cast_crew_list);
            }
        }
    }

    class JSON_movieCrew extends AsyncTask<Void, Void, Void>
    {
        String str_movieCrew = "";
        ArrayList<HashMap<String, String>> arrayList_movieCrew = null;
        HashMap<String, String> hashMap_movieCrew;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            URL_movieCastCrew = "http://api.themoviedb.org/3/movie/" + imdb_id + "/credits?api_key=8496be0b2149805afa458ab8ec27560c";
            JSONArray jsonArray;
            JSONObject jsonObject;
            JSONFunctions jsonFunctions_movieCrew = new JSONFunctions();
            jsonObject = jsonFunctions_movieCrew.getJSONfromURL(URL_movieCastCrew);
            Log.e("json", String.valueOf(jsonObject));
            try {
                jsonArray = jsonObject.getJSONArray("crew");
                arrayList_movieCrew = new ArrayList<HashMap<String, String>>();
                Log.e("jsonArray Length", String.valueOf(jsonArray.length()));
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    hashMap_movieCrew = new HashMap<String, String>();
                    jsonObject = jsonArray.getJSONObject(i);
                    str_movieCrew = "http://image.tmdb.org/t/p/w500" + jsonObject.getString("profile_path");
                    hashMap_movieCrew.put("crew", str_movieCrew);
                    hashMap_movieCrew.put("name", jsonObject.getString("name"));
                    hashMap_movieCrew.put("job", jsonObject.getString("job"));
                    arrayList_movieCrew.add(hashMap_movieCrew);
                }
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
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (int i = 0; i < arrayList_movieCrew.size(); i++)
            {
                hashMap_movieCrew = arrayList_movieCrew.get(i);

                LinearLayout cast_crew_list = (LinearLayout) layoutInflater.inflate(R.layout.cast_crew_list, null);
                ImageView   imageView_cast_crew = (ImageView) cast_crew_list.findViewById(R.id.imageView_cast_crew);
                TextView    textView_name = (TextView) cast_crew_list.findViewById(R.id.textView_name);
                TextView    textView_role = (TextView) cast_crew_list.findViewById(R.id.textView_role);

                imageLoader.DisplayImage(hashMap_movieCrew.get("crew"), imageView_cast_crew);
                textView_name.setText(hashMap_movieCrew.get("name"));
                textView_role.setText(hashMap_movieCrew.get("job"));

                linearLayout_crew.addView(cast_crew_list);
            }
        }
    }

    private String format_date(String input_date)
    {
        if (input_date.isEmpty()) {
            return null;
        }
        String str_date_array[] = input_date.split("-");

        int get_year = Integer.parseInt(str_date_array[0]);
        int get_month = Integer.parseInt(str_date_array[1]);
        int get_day = Integer.parseInt(str_date_array[2]);

        Calendar calendar_temp = Calendar.getInstance();
        calendar_temp.set(get_year, get_month - 1, get_day);
        SimpleDateFormat date_ui_format = new SimpleDateFormat("dd/MM/yyyy");
        return (date_ui_format.format(calendar_temp.getTime()));
    }
}