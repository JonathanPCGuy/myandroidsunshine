package com.hp.jlam.practice;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.hp.jlam.practice.weatherapi.APICallResults;
import com.hp.jlam.practice.weatherapi.FutureDailyForecast;
import com.hp.jlam.practice.weatherapi.ResultsSerializer;
import com.hp.jlam.practice.weatherapi.WebInterfaceTask;

import org.json.JSONException;

public class DetailedWeatherInfo extends ActionBarActivity implements APICallResults {

    private WeatherInfoLocation weatherInfoLocationFragment;
    private WeatherInfoLocationDetail weatherInfoLocationDetailFragment;

    private WebInterfaceTask interfaceTask;

    private int location_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_weather_info);

        // finViewById not equal null?

        if(savedInstanceState != null)
        {
            return;
        }

        /**
         * 1. load and fill in top fragment with info on selected city
         * 2. load and fill in bottom fragment with forecast type
         *    right now only have (5, 7 day) forecast
         *
         */
        // todo: get all the app compat stuff sorted out

        // for now do this here
        // will we always get the value in this function?

        Intent intent = getIntent();

        this.location_id = intent.getIntExtra(ExtraConstants.EXTRA_LOCATION_ID, -1);
        // TODO: add extra to pass in location name, or maybe pull it from location id api call?
        // if id changes then we're in trouble...

        // add to main layout
        Log.d("onCreate DetailedWeatherInfo", "About to inflate top and bottom fragments.");

        // can i just do new or should i store in variables?
        this.weatherInfoLocationFragment = new WeatherInfoLocation();

        this.weatherInfoLocationFragment.SetLocationInfo(intent.getStringExtra(ExtraConstants.EXTRA_LOCATION_LOCATION),
                intent.getStringExtra(ExtraConstants.EXTRA_LOCATION_COUNTRY));

        this.weatherInfoLocationDetailFragment = new WeatherInfoLocationDetail();
        // ok, it works, but one fragment is covering the other

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.add(R.id.detailedWeatherContainer, this.weatherInfoLocationFragment, "weatherInfoLocation");

        // overlapping (or replace?) issue...

        fragmentTransaction.add(R.id.detailedWeatherContainer, this.weatherInfoLocationDetailFragment, "weatherInfoLocationDetail");

        fragmentTransaction.commit();

        // ok we now have id


        // for dev purposes, first jam in a location to the task to run
        // later on we'll use a real id
        // toasts?
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        // kick off backgroundtask to call android api
        interfaceTask = new WebInterfaceTask();
        interfaceTask.SetParentActivity(this);
        //String url = WebInterfaceTask.ConstructFutureForecastURL(location_id, 5);
        interfaceTask.execute(location_id);
        //temporarily disable show spinner
        this.ShowSpinner(true);
    }

    public void ShowSpinner(boolean showSpinner)
    {
        //todo: fix this later
        //this.weatherInfoLocationDetailFragment.ShowSpinner(showSpinner);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detailed_weather_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void UpdateResults(String jsonData)
    {
        try
        {
            FutureDailyForecast futureDailyForecast = ResultsSerializer.ProcessFutureDailyForecastJSON(jsonData);
            this.weatherInfoLocationDetailFragment.LoadWeatherResults(futureDailyForecast);
            this.ShowSpinner(false);
        }
        catch(JSONException e)
        {
            UpdateWithError(e.getMessage());
        }
    }

    @Override
    public void UpdateWithError(String errorString)
    {
        // anyway to show popup?
        // for now just log
        Log.e("UpdateWithError", errorString);
        this.ShowSpinner(false);
    }

    public void OnForecastDataReceived(String jsonData)
    {
        /*
        this.weatherInfoLocationFragment = new WeatherInfoLocation();
        this.weatherInfoLocationDetailFragment = new WeatherInfoLocationDetail();
         */

    }

    public void OnForceastDataError(String error)
    {

    }



    /**
     * A placeholder fragment containing a simple view.
     */
    /*
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_future_forecast_location, container, false);
            return rootView;
        }
    }*/

}
