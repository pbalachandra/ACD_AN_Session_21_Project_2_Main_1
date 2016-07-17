package com.acadgild.balu.imdb;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by BALU on 5/15/2016.
 */
public class LazyImageLoadAdapter extends BaseAdapter
{
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<HashMap<String, String>> arrayList;
    ImageLoader imageLoader;
    HashMap<String, String> hashMap = new HashMap<String, String>();

    public LazyImageLoadAdapter(Context context, ArrayList<HashMap<String, String>> data)
    {
        this.context = context;
        arrayList = data;
        imageLoader = new ImageLoader(context);
    }

    @Override
    public int getCount()
    {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        TextView    textView_movieTitle, textView_releaseDate, textView_voteCount,
                    textView_releaseDate_label, textView_popularity_label;
        ImageView   imageView_movieImage, imageView_voteCount;
        RatingBar   ratingBar_popularity;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = layoutInflater.inflate(R.layout.movie_list, parent, false);
        // Get the position
        hashMap = arrayList.get(position);

        // Locate the TextViews in listview_item.xml
        textView_movieTitle = (TextView) itemView.findViewById(R.id.textView_movieTitle);
        textView_releaseDate_label = (TextView) itemView.findViewById(R.id.textView_releaseDate_label);
        textView_releaseDate = (TextView) itemView.findViewById(R.id.textView_releaseDate);
        textView_popularity_label = (TextView) itemView.findViewById(R.id.textView_popularity_label);
        textView_voteCount = (TextView) itemView.findViewById(R.id.textView_voteCount);

        // Locate the ImageView in listview_item.xml
        imageView_movieImage = (ImageView) itemView.findViewById(R.id.imageView_movieImage);
        imageView_voteCount = (ImageView) itemView.findViewById(R.id.imageView_voteCount);

        ratingBar_popularity = (RatingBar) itemView.findViewById(R.id.ratingBar_popularity);

        // Capture position and set results to the TextViews

        textView_movieTitle.setText(hashMap.get(MainActivity.MOVIE_TITLE));
        textView_releaseDate_label.setText(R.string.release_date);
        textView_releaseDate.setText(format_date(hashMap.get(MainActivity.RELEASE_DATE)));
        textView_popularity_label.setText(R.string.popularity);
        ratingBar_popularity.setRating(Float.parseFloat(hashMap.get(MainActivity.VOTE_AVERAGE))/2);
        imageView_voteCount.setImageResource(R.drawable.star_full);
        String vote_count = "(" + hashMap.get(MainActivity.VOTE_AVERAGE) + "/10) " +
                             "voted by " + hashMap.get(MainActivity.VOTE_COUNT) + " users.";
        textView_voteCount.setText(vote_count);

        imageLoader.DisplayImage(hashMap.get(MainActivity.MOVIE_IMAGE), imageView_movieImage);

        return itemView;
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
