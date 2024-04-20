package com.licenta.bustravel.controller;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;

@RestController
public class GeocodeController {

    @RequestMapping(path = "/geocode", method = RequestMethod.GET )
    public String getGeocode(@RequestParam String address) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String encodedAddress = URLEncoder.encode(address, "UTF-8");
        Request request = new Request.Builder()
            .url("https://google-maps-geocoding.p.rapidapi.com/geocode/json?language=en&address=" + encodedAddress)
            .get()
            .addHeader("x-rapidapi-host", "google-maps-geocoding.p.rapidapi.com")
//            .addHeader("x-rapidapi-key", {your-api-key-here}/*  Use your API Key here */)
            .build();
        ResponseBody responseBody = client.newCall(request).execute().body();
        return responseBody.string();
    }
}
