package com.hp.jlam.practice;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

//was action bar activity before...

/**
If you're using the v7 appcompat library,
your activity should instead extend ActionBarActivity,
which is a subclass of FragmentActivity (for more information, read Adding the Action Bar).
*/

public class CheckWeatherPro extends ActionBarActivity
    implements CheckWeatherInput.OnBeginGetWeatherListener
{

    private CheckWeatherTask myTask;

    private CheckWeatherInput checkWeatherInputFragment;
    private CheckWeatherOutput checkWeatherOutputFragment;
    private WeatherLocation weatherLocationQueryResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weatherpro);

        // need to deal with pause as well?
        if(this.myTask != null)
        {
            this.myTask = new CheckWeatherTask();



            //this.myTask.useProxy = true;
        }

       if(findViewById(R.id.weatherpro_container) != null)
        {
            if (savedInstanceState != null) {
                return;
            }

            this.checkWeatherInputFragment = new CheckWeatherInput();
            this.checkWeatherInputFragment.mGetWeatherCallback = this;


            this.checkWeatherOutputFragment = new CheckWeatherOutput();

            // set args

            //checkWeatherOutputFragment.

            // add to main layout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.weatherpro_container, this.checkWeatherInputFragment, "checkWeatherInput").commit();

            // why is this on top
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.weatherpro_container, this.checkWeatherOutputFragment, "checkWeatherOutput").commit();

            // this isn't working
            // can't do it here, onCreate layout not ready?
            //this.UpdateProgress("Ready");
            // specify weights so top fragment takes up less space


        }

        /**
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.check_weather, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBeginGetWeather(String location)
    {
        // callback called here
        this.ToggleSpinner(true);
        this.checkWeatherInputFragment.EnableButton(false);

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            // can i reuse?
            CheckWeatherTask checkWeatherTask = new CheckWeatherTask();
            checkWeatherTask.activity = this;
            checkWeatherTask.execute(location);
        } else
        {
            this.ToggleSpinner(false);
            this.UpdateProgress("Unable to connect. Network not available.");
            // how to communicate from activity to fragment?
            //textView.setText("Unable to connect.");
        }
    }

    public void UpdateResult(WeatherLocation result)
    {
        //CheckWeatherOutput outputFragment =
          //      (CheckWeatherOutput)(getSupportFragmentManager().findFragmentByTag("checkWeatherInput"));
        Log.d("UpdateResult", "Processing WeatherResult...");
        this.ToggleSpinner(false);
        this.checkWeatherOutputFragment.UpdateResults(result);
        this.checkWeatherInputFragment.EnableButton(true);
        this.weatherLocationQueryResult = result;
        //outputFragment.UpdateResults(result);
               // getFragmentManager().findFragmentByTag("checkWeatherInput");
    }

    public  void onClickButtonOutput(View view)
    {
        switch (view.getId())
        {
            case R.id.buttonAddLocation:
                this.addLocation(view);
                break;
            default:
                Log.d("onClickButtonOutput", "No function available for id " + view.getId());
                break;
        }
    }

    public void addLocation(View view)
    {


        // need to pass this back to other activity
        // OR write directly to the db
        // what to do?
        // also need to also navigate back to the main screen as well
        Log.v("CheckWeatherPro - addLocation", "In addLocation");
        // todo: only ensure you can click add if success

        // need to deep copy?

        // add to database
        Log.d("CheckWeatherPro - addLocation", "Adding entry to database...");
        WeatherAppStorage weatherAppStorage = new WeatherAppStorage(this);
        long result = weatherAppStorage.addWeatherLocation(this.weatherLocationQueryResult);

        // navigate back to previous activity
        // pass along result
        Log.v("CheckWeatherPro - addLocation", "Creating intent for result.");
        Intent output = new Intent();
        output.putExtra("newItemId", result);
        setResult(RESULT_OK, output);
        Log.d("CheckWeatherPro - addLocation", "About to call finish()");
        finish();
        // to do: how to make activity update/refresh list?

    }

    public void UpdateProgress(String message)
    {
        Log.d("UpdateProgress", message);
        //this.checkWeatherOutputFragment.UpdateProgress(message);

        //outputFragment.UpdateProgress(message);
    }

    public void ToggleSpinner(boolean showSpinner)
    {
        Log.d("ToggleSpinner", "showSpinner:" + showSpinner);
        if(showSpinner)
        {
            this.checkWeatherOutputFragment.ShowSpinner();
        }
        else
        {
            this.checkWeatherOutputFragment.HideSpinner();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_weatherpro, container, false);
            return rootView;
        }
    }

}
