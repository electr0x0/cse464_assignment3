package com.electro.weatherapi;

import static java.lang.String.format;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.electro.weatherapi.WeatherService;
import com.electro.weatherapi.ImageLoadTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    TextView location,temp,weather_description,humidity,pressure,sealevel;
    EditText city;

    ImageView weatherIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        location = findViewById(R.id.txtView_location);
        temp = findViewById(R.id.txtView_temp);
        weather_description = findViewById(R.id.txtView_weather_desc);
        humidity = findViewById(R.id.txtView_humidity);
        pressure = findViewById(R.id.txtView_pressure);
        sealevel = findViewById(R.id.txtView_sealevel);

        city = findViewById(R.id.et_city);

        weatherIcon = findViewById(R.id.weather_icon);

    }




    private class FetchWeatherTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            String city = params[0];
            String apiKey = "9c97efb19d28d79c44571c3f418b7f5d";
            return WeatherService.getWeatherData(city, apiKey);
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (result != null) {
                try {

                    JSONObject main = result.getJSONObject("main");
                    JSONObject sys = result.getJSONObject("sys");

                    JSONArray weatherArray = result.getJSONArray("weather");
                    JSONObject weatherObject = weatherArray.getJSONObject(0);

                    String weatherDescription = weatherObject.getString("main");
                    String country = sys.getString("country");
                    String cityName = result.getString("name");

                    double tempConvert = Double.parseDouble(main.getString("temp")) - 273.15;

                    String tempVal = format("%.2f", tempConvert) + "°C";


                    String pressureVal = main.getString("pressure") + "hPa";
                    String humidityVal = main.getString("humidity") +  "%";
                    String sealevelVal = main.getString("sea_level") + "MSL";

                    String cityCountry = cityName+","+country;

                    String iconUrl = "https://openweathermap.org/img/wn/" + weatherObject.getString("icon") + "@2x.png";

                    location.setText(cityCountry);
                    temp.setText(tempVal);
                    pressure.setText(pressureVal);
                    humidity.setText(humidityVal);
                    sealevel.setText(sealevelVal);
                    weather_description.setText(weatherDescription);

                    new ImageLoadTask(iconUrl, weatherIcon).execute();




                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Data parsing error", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(MainActivity.this, "City not found or error retrieving data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void fetchWeatherData(View view) {
        String cityVal = city.getText().toString().trim();
        if (!cityVal.isEmpty()) {
            new FetchWeatherTask().execute(cityVal);
        } else {
            Toast.makeText(MainActivity.this, "Please enter a city name", Toast.LENGTH_SHORT).show();
        }
    }
}