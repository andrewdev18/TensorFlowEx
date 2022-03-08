package org.tensorflow.lite.examples.classification;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.examples.classification.databinding.ActivityMapviewBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ActivityMapviewBinding binding;

    private String countryCode;
    RequestQueue requestQueue;

    TextView nameView;

    private String name = "Waiting data";
    private String flagURL;

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
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        binding = ActivityMapviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getData();
        loadScreenData();
    }

    private void loadScreenData(){
        nameView = findViewById(R.id.name);
        nameView.setText(name);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void getData(){
        String urlApi = "http://www.geognos.com/api/en/countries/info/" + countryCode + ".json";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                urlApi, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            System.out.println(response.toString());
                            JSONObject obj = response.getJSONObject(0);
                            /*
                            JSONObject obj = response.getJSONObject(0);
                            JSONArray result = obj.getJSONArray("Results");
                            JSONObject resultObj = result.getJSONObject(0);
                            String getName = resultObj.getString("Name");
                            Toast.makeText(getApplicationContext(), getName, Toast.LENGTH_LONG).show();
                            */
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tag","onErrorRespone: " + error.getMessage());
            }
        });
        requestQueue.add(request);
    }
}