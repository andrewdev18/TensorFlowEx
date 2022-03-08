package org.tensorflow.lite.examples.classification;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.examples.classification.databinding.ActivityMapviewBinding;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ActivityMapviewBinding binding;

    private String countryCode;
    RequestQueue requestQueue;

    TextView nameView;
    TextView descriptionView;
    ImageView flagImage;

    private String name = "Waiting data";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            countryCode= null;
        } else {
            countryCode= extras.getString("countrycode");
        }
        //Toast.makeText(this,"Pais: " + countryCode, Toast.LENGTH_SHORT).show();
        requestQueue = Volley.newRequestQueue(this);

        binding = ActivityMapviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        assignScreenElements();
        getJsonData();
        loadScreenData();
    }

    private void assignScreenElements(){
        descriptionView = findViewById(R.id.descripcion);
        nameView = findViewById(R.id.name);
        flagImage = findViewById(R.id.flag);
    }

    private void loadScreenData(){
        String flagURL = "http://www.geognos.com/api/en/countries/flag/" + countryCode + ".png";

        Glide.with(this)
                .load(flagURL)
                .into(flagImage);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void getJsonData() {
        String url = "http://www.geognos.com/api/en/countries/info/" + countryCode + ".json";
        StringRequest request = new StringRequest(
                Request.Method.GET, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        int size = response.length();
                        response = fixEncoding(response);
                        //descriptionView.setText(response);

                        try {
                            JSONObject mainObject = new JSONObject(response);
                            JSONObject results = mainObject.getJSONObject("Results");
                            JSONObject capital = results.getJSONObject("Capital");
                            String name = results.getString("Name");
                            Toast.makeText(getApplicationContext(), "Pais: " + name, Toast.LENGTH_LONG).show();

                            if(name == null){
                                name = "Sin datos";
                            }
                            nameView.setText(name);

                            descriptionView.setText("");
                            descriptionView.append("Pais: " + results.getString("Name") + "\n");
                            descriptionView.append("Capital: " + capital.getString("Name") + "\n");
                            descriptionView.append("Codigo de telefono: " + results.getString("TelPref") + "\n");
                            descriptionView.append("Informacion: " + results.getString("CountryInfo") + "\n");
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "No hubo respuesta", Toast.LENGTH_LONG).show();
                            //responseText.setText(e.getMessage());
                        }


                        Log.d("respuesta api ", response);
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", String.valueOf(error));
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=utf-8");
                params.put("Accept", "application/json");
                return params;
            }
        };
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(request);
        } else {
            requestQueue.add(request);
        }
    }

    private String fixEncoding(String response) {
        try {
            byte[] u = response.toString().getBytes(
                    "ISO-8859-1");
            response = new String(u, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        return response;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            Log.d("message", "Hola");
        } else if (resultCode == RESULT_OK && requestCode == 10) {
            Uri path = data.getData();
        }
    }
}