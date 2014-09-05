package com.pixel.singletune.app.models;

import com.pixel.singletune.app.helpers.TuneDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mrsmith on 9/2/14.
 */
public class Tune {

    public static final String TITLE = "title";
    public static final String TUNE_OBJECT_ID = "tuneObjectId";
    public static final String TUNE_AUDIO_URL = "tuneUrl";
    public static final String ARTISTE_OBJECT_ID = "artistObjectId";
    public static final String ARTISTE_NAME = "artisteName";
    public static final String TUNE_ART_URL = "tuneArtUrl";


    private String title;
    private String tuneObjectId;
    private String tuneAudioUrl;
    private String tuneArtUrl;
    private String artisteName;
    private String artisteObjectId;

    public Tune(){
        super();
    }

    public Tune(String title, String tuneObjectId, String tuneAudioUrl,String tuneArtUrl, String artisteName, String artisteObjectId){
        super();
        this.title = title;
        this.tuneObjectId = tuneObjectId;
        this.tuneAudioUrl = tuneAudioUrl;
        this.tuneArtUrl = tuneArtUrl;
        this.artisteName = artisteName;
        this.artisteObjectId = artisteObjectId;
    }

    public Tune(String jsonString){
        super();
        try {
            JSONObject json = new JSONObject(jsonString);
            title = json.optString(TITLE);
            tuneObjectId = json.optString(TUNE_OBJECT_ID);
            tuneAudioUrl = json.optString(TUNE_AUDIO_URL);
            tuneArtUrl = json.optString(TUNE_ART_URL);
            artisteName = json.optString(ARTISTE_NAME);
            artisteObjectId = json.optString(ARTISTE_OBJECT_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Tune(JSONObject object){
        try{
            this.title = object.getString(TuneDbHelper.TUNE_TITLE);
            this.tuneObjectId = object.getString(TuneDbHelper.TUNE_OBJECT_ID);
            this.tuneAudioUrl = object.getString(TuneDbHelper.TUNE_AUDIO_URL);
            this.tuneArtUrl = object.getString(TuneDbHelper.TUNE_ART_URL);
            this.artisteName = object.getString(TuneDbHelper.ARTISTE_NAME);
            this.artisteObjectId = object.getString(TuneDbHelper.ARTISTE_OBJECT_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Tune> fromJson(JSONArray jsonObject){
        ArrayList<Tune> tunes = new ArrayList<Tune>();

        for (int i = 0; i < jsonObject.length(); i++){
            try {
                tunes.add(new Tune(jsonObject.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return  tunes;
    }

    // Getters

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getTuneObjectId(){
        return tuneObjectId;
    }

    public void setTuneObjectId(String tuneObjectId){
        this.tuneObjectId = tuneObjectId;
    }

    public String getTuneAudioUrl(){
        return tuneAudioUrl;
    }

    public void setTuneAudioUrl(String tuneAudioUrl) {
        this.tuneAudioUrl = tuneAudioUrl;
    }

    // Setters

    public String getTuneArtUrl(){
        return tuneArtUrl;
    }

    public  void setTuneArtUrl(String tuneArtUrl) {
        this.tuneArtUrl = tuneArtUrl;
    }

    public String getArtisteObjectId(){
        return artisteObjectId;
    }

    public void setArtisteObjectId(String artisteObjectId){
        this.artisteObjectId = artisteObjectId;
    }

    public String getArtisteName(){
        return artisteName;
    }

    public void setArtisteName(String artisteName) {
        this.artisteName = artisteName;
    }

    @Override
    public String toString() {

        try {
            JSONObject json = new JSONObject();
            json.put(TITLE, title);
            json.put(TUNE_OBJECT_ID, tuneObjectId);
            json.put(TUNE_AUDIO_URL, tuneAudioUrl);
            json.put(TUNE_ART_URL, tuneArtUrl);
            json.put(ARTISTE_OBJECT_ID, artisteObjectId);
            json.put(ARTISTE_NAME, artisteName);
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return "{}";
    }
}
