package com.triocodes.krishikkaran.Volley;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * Created by Hafi on 7/21/2015.
 */
public class VolleyConnectivity extends JsonObjectRequest {


    public VolleyConnectivity(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }
}
