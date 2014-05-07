package com.hp.jlam.practice.weatherapi;

// probably should group all of the weather api stuff into a namespace (package?)

import android.util.Log;

import com.hp.jlam.practice.WeatherLocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by lamjon on 4/22/2014.
 */
public class ResultsSerializer
{
    public static FutureDailyForecast ProcessFutureDailyForecastJSON(String jsonString) throws JSONException, IllegalArgumentException

    {
        JSONObject jsonObject = new JSONObject(jsonString);

        // get out number of days? (necessary?) maybe for validation

        int expectedNumOfDays = jsonObject.getInt("cnt");

        JSONArray jsonArray = jsonObject.getJSONArray("list");

        if (jsonArray.length() != expectedNumOfDays)
        {
            throw new IllegalArgumentException("Forecast length (days) does not match array length.");
        }

        FutureDailyForecast futureDailyForecast = new FutureDailyForecast();

        for(int i = 0; i < jsonArray.length(); i++)
        {
            DailyForecast dailyForecast = new DailyForecast();
            dailyForecast.high = jsonArray.getJSONObject(i).getJSONObject("temp").getDouble("max");
            dailyForecast.low = jsonArray.getJSONObject(i).getJSONObject("temp").getDouble("min");

            dailyForecast.date = new Date(Long.parseLong(jsonArray.getJSONObject(i).getString("dt")) * 1000);
            futureDailyForecast.futureForecast.add(dailyForecast);
        }

        return  futureDailyForecast;
    }

    public static WeatherLocation ParseWeatherInfo(String jsonString) throws JSONException, IllegalArgumentException
    {
        WeatherLocation weatherLocation = new WeatherLocation();

        // json parsing goes here
        // load into json object

        // is an array per spec?

        // todo:for entries like Hong Kong (no location name) need to display country in location spot
        Log.d("ParseWeatherInfo", "Attempting to parse JSON response");
        JSONObject jsonObject = new JSONObject(jsonString);
        // get first node
        //JSONObject firstNode = jsonArray.getJSONObject(0);

        // pull out json objects that has weather info
        Log.d("ParseWeatherInfo", "Getting weather array and first object");
        JSONArray weather = jsonObject.getJSONArray("weather");

        weatherLocation.setWeather(weather.getJSONObject(0).getString("main"));

        // null string?
        Log.d("ParseWeatherInfo", "Getting location name (if present?)");
        if(jsonObject.has("name"))
        {
            weatherLocation.setLocation(jsonObject.getString("name"));
        }

        // this should always be present
        Log.d("ParseWeatherInfo", "Getting country.");
        weatherLocation.setCountry(jsonObject.getJSONObject("sys").getString("country"));

        // for now display in kelvin
        Log.d("ParseWeatherInfo", "Getting temperature.");
        weatherLocation.setTemperature(Double.toString(jsonObject.getJSONObject("main").getDouble("temp")));
        // get the id of the location. this is different from sql row id
        Log.d("ParseWeatherInfo", "Getting location id.");
        weatherLocation.setLocation_id((jsonObject.getInt("id")));
        Log.d("ParseWeatherInfo", "id value:" + Integer.toString(jsonObject.getInt("id")));
        return weatherLocation;
    }

}