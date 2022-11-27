package com.example.coffee.interfaces;

import static com.example.coffee.interfaces.BaseAPI.BASE_URL;

import com.example.coffee.models.Shop.GiftResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GiftInterfaceAPI {
    String GIFT_URL = String.format("%s/mission/", BASE_URL);

    @GET("?{id}")
    Call<GiftResponse> getGift(
            @Query("limit") int limit,
            @Path("id") int id
    );

}
