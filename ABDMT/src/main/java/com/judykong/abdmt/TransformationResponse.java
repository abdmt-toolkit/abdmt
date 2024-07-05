package com.judykong.abdmt;

import java.util.List;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

public class TransformationResponse {

    @SerializedName("transformations")
    public List<TransformationItem> items;

    // public constructor is necessary for collections
    public TransformationResponse() {
        items = new ArrayList<TransformationItem>();
    }
}
