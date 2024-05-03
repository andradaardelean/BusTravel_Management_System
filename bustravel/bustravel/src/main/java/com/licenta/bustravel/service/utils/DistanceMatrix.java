package com.licenta.bustravel.service.utils;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class DistanceMatrix {
   private static final String API_KEY = "AIzaSyCGicmysd15IkTu4H7JJBV4IG90KWSYp-w";

   private static final Logger LOGGER = LoggerFactory.getLogger(DistanceMatrix.class);
   
    public static String getData(String source, String destination) throws Exception {
        var url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + source + "&destinations=" + destination + "&key=" + API_KEY;
        var request = HttpRequest.newBuilder().GET().uri(new URI(url)).build();
        var client = HttpClient.newBuilder().build();
        var response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString()).body();

        LOGGER.info(response);
        return response;
    }

    public static Map<String, String> parseData(String response) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject json = (JSONObject) parser.parse(response);
            JSONArray rows = (JSONArray) json.get("rows");
            JSONObject elements = (JSONObject) rows.get(0);
            JSONArray elementsArray = (JSONArray) elements.get("elements");
            JSONObject element = (JSONObject) elementsArray.get(0);
            JSONObject distance = (JSONObject) element.get("distance");
            JSONObject duration = (JSONObject) element.get("duration");
            Map<String,String> distanceMap = new HashMap<>();
            distanceMap.put("distanceText", (String) distance.get("text"));
            distanceMap.put("durationText", (String) duration.get("text"));
            distanceMap.put("distanceValue", String.valueOf(distance.get("value")));
            distanceMap.put("durationValue", String.valueOf(duration.get("value")));
            return distanceMap;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}

