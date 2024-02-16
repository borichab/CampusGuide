package com.hsm.macs.campusguide;

import android.content.Context;
import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PolygonLoader {

    private Context context;

    public PolygonLoader(Context context) {
        this.context = context;
    }

    public List<CustomPolygon> loadBuildingPolygons() {
        List<CustomPolygon> buildingPolygons = new ArrayList<>();

        // Load JSON files from res/raw directory
        int[] rawFileIds = {
                R.raw.coordinates_building_a,
                R.raw.coordinates_building_b,
                R.raw.coordinates_building_c,
                R.raw.coordinates_building_d,
                R.raw.coordinates_building_e,
                R.raw.coordinates_building_f,
                R.raw.coordinates_building_g,
                R.raw.coordinates_building_h,
                R.raw.coordinates_building_i,
                R.raw.coordinates_building_j,
                R.raw.coordinates_building_k,
                R.raw.coordinates_building_r,
                R.raw.coordinates_basketballplatz,
                R.raw.coordinates_volleyballplatz,
                R.raw.coordinates_dom_haus_1,
                R.raw.coordinates_dom_haus_2
        };

        for (int rawFileId : rawFileIds) {
            try {
                InputStream inputStream = context.getResources().openRawResource(rawFileId);
                String jsonString = readStreamToString(inputStream);

                JSONObject jsonObject = new JSONObject(jsonString);

                String title = jsonObject.getString("title");
                String description = jsonObject.getString("description");
                // Add other fields as needed

                JSONArray coordinatesArray = jsonObject.getJSONArray("coordinates");
                List<LatLng> buildingCoordinates = new ArrayList<>();

                for (int i = 0; i < coordinatesArray.length(); i++) {
                    JSONObject coordinate = coordinatesArray.getJSONObject(i);
                    double lat = coordinate.getDouble("lat");
                    double lng = coordinate.getDouble("lng");
                    buildingCoordinates.add(new LatLng(lat, lng));
                }

                // Create PolygonOptions and add it to the list
                PolygonOptions polygonOptions = new PolygonOptions()
                        .addAll(buildingCoordinates)
                        .strokeColor(Color.RED)
                        .fillColor(Color.argb(20, 255, 0, 0));

                CustomPolygon customPolygon = new CustomPolygon(polygonOptions, title, description);
                buildingPolygons.add(customPolygon);

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }

        return buildingPolygons;
    }

    private String readStreamToString(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        return stringBuilder.toString();
    }
}
