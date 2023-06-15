package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.weatherapp.databinding.ActivityMainBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    ArrayList<WeatherModel> weatherModelArrayList;
    Weather_Adapter adapter;

    LocationManager locationManager;
    int PERMISSION_CODE=1;

    String cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        weatherModelArrayList = new ArrayList<>();
        adapter=new Weather_Adapter(this,weatherModelArrayList);
        binding.ReView.setAdapter(adapter);
        locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);



        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_CODE);
        }
        Location location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        cityName=getCityName(location.getLongitude(),location.getLatitude());
        getWeather_info(cityName);
        binding.Search.setOnClickListener(view -> {
            String city=binding.EditCity.getText().toString();
            if (city.isEmpty()){
                Toast.makeText(this, "Enter your City name", Toast.LENGTH_SHORT).show();
            }else {
                binding.cityName.setText(city);
                getWeather_info(city);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode==PERMISSION_CODE){
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "please provide permission", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getCityName(double longitude, double latitude){

        String city_name="not found";
        Geocoder geocoder=new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses=geocoder.getFromLocation(latitude,longitude,10);
            for (Address adr:addresses){
                if (adr!=null){
                    String city=adr.getLocality();
                    if (city!=null && !city.equals("")){
                      city_name=city;
                    }else {
                        Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return city_name;
    }



    private void getWeather_info(String city_name) {

        String url="https://api.weatherapi.com/v1/forecast.json?key=f8bce2ddcfc9468799154353230406&q="+city_name+"&days=1&aqi=no&alerts=no";
        binding.cityName.setText(city_name);

        RequestQueue requestQueue= Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                binding.loading.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Update Sucess", Toast.LENGTH_SHORT).show();
                binding.home.setVisibility(View.VISIBLE);
                weatherModelArrayList.clear();

                try {
                    String temp=response.getJSONObject("current").getString("temp_c");
                    binding.temp.setText(temp+"°C");
                    int isday=response.getJSONObject("current").getInt("is_day");
                    String condition=response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionicon=response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(conditionicon)).into(binding.weatherIcon);
                    binding.condition.setText(condition);
                    if (isday==1){
                        binding.BagGround.setImageResource(R.drawable.day);
                    }else {
                        binding.BagGround.setImageResource(R.drawable.night);
                    }

                    JSONObject forcastobj = response.getJSONObject("forecast");
                    JSONObject forecastO = forcastobj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray jsonArray=forecastO.getJSONArray("hour");
                    for (int i=0;i<jsonArray.length();i++){
                        JSONObject hourobj = jsonArray.getJSONObject(i);
                        String time=hourobj.getString("time");
                        String temper=hourobj.getString("temp_c");
                        String img=hourobj.getJSONObject("condition").getString("icon");
                        String winspeed=hourobj.getString("wind_kph");
                        weatherModelArrayList.add(new WeatherModel(time,temper,img,winspeed));
                    }
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                binding.errorL.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onBackPressed() {
        showalert();
    }
    public void showalert(){
        AlertDialog.Builder builder =new AlertDialog.Builder(MainActivity.this);
        builder.setIcon(R.drawable.cloudy);
        builder.setTitle("Weather App");
        builder.setMessage("Are you sure to Exit");
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog dialog =builder.create();
        dialog.show();
    }
}