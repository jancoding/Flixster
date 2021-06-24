package com.example.flixster.models;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel // annotation indicates class is Parcelable
public class Review {

    String authorName;
    String date;
    String content;

    public Review() {

    }

    public Review(JSONObject jsonObject) throws JSONException {
        authorName = jsonObject.getString("author");
        date = jsonObject.getString("created_at");
        content = jsonObject.getString("content");
    }

    public static List<Review> fromJsonArray(JSONArray reviewsJsonArray) throws JSONException {
        List<Review> reviews = new ArrayList<>();
        for (int i = 0; i < reviewsJsonArray.length(); i++) {
            reviews.add(new Review(reviewsJsonArray.getJSONObject(i)));
        }
        return reviews;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }
}
