package com.example.coffee.screens.bottom.Shop;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.coffee.R;
import com.example.coffee.adapters.RecycleNearlyAdapter;
import com.example.coffee.adapters.RecycleAllShopAdapter;
import com.example.coffee.callbacks.ShopCallback;
import com.example.coffee.models.Shop.Shop;
import com.example.coffee.models.Shop.ShopResponse;
import com.example.coffee.screens.bottom.Home.PromoActivity;
import com.example.coffee.services.ShopService;
import com.example.coffee.utils.Logger;

import java.util.ArrayList;

public class ShopFragment extends Fragment {
    
    private RecyclerView recyclerViewNearbyPlace, recyclerViewAllShop;
    private TextView tvViewAllHottest, tvViewAllShop, tvViewAllNearby;
    private ShopService shopService;
    private ArrayList<Shop> shopsNearby;
    private ArrayList<Shop> shopsAllShop;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.shop_fragment, container, false);

        //init view
        init(view);

        // init data
        shopsNearby = new ArrayList<>();
        shopsAllShop = new ArrayList<>();

        // init service
        shopService = new ShopService();

        // call api
        initShop();
        initAllShop();

        // handle onclick
        handleOnClick();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void init(View view){
        recyclerViewNearbyPlace = view.findViewById(R.id.recycleViewNearbyPlace);
        recyclerViewAllShop = view.findViewById(R.id.recycleViewAllShop);
        tvViewAllHottest = view.findViewById(R.id.tvViewAllHottest);
        tvViewAllShop = view.findViewById(R.id.tvViewShopAll);
        tvViewAllNearby = view.findViewById(R.id.tvViewShopNearby);
    }

    public void handleOnClick(){
        tvViewAllShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PlaceListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("Page Title", "All Shop");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        tvViewAllHottest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PromoActivity.class);
                startActivity(intent);
            }
        });
        tvViewAllNearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PlaceListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("Page Title", "Nearby Place");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }

    public void initShop() {
        shopService.getShops(5, 0, "ASC", "id", new ShopCallback() {
            @Override
            public void onSuccess(boolean value, ShopResponse shopResponse) {
                Logger.log("SHOPS", shopResponse);
                shopsNearby.addAll(shopResponse.getShops());
                renderPlace(recyclerViewNearbyPlace, shopsNearby);
            }

            @Override
            public void onFailed(boolean value) {
                Logger.log("RESPONSE", "ERROR");
            }
        });
    }

    public void initAllShop(){
        shopService.getAllShop(new ShopCallback() {
            @Override
            public void onSuccess(boolean value, ShopResponse shopResponse) {
                Logger.log("ALL SHOPS", shopResponse);

                shopsAllShop.addAll(shopResponse.getShops());
                renderAllShop(recyclerViewAllShop, shopsAllShop);
            }

            @Override
            public void onFailed(boolean value) {
                Logger.log("RESPONSE", "ERROR");
            }
        });
    }


    public void renderPlace(RecyclerView recyclerViewNearbyPlace, ArrayList<Shop> data) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewNearbyPlace.setLayoutManager(linearLayoutManager);
        RecycleNearlyAdapter adapter = new RecycleNearlyAdapter(getContext(), data);
        recyclerViewNearbyPlace.setAdapter(adapter);
    }

    public void renderAllShop(RecyclerView recyclerViewAllShop, ArrayList<Shop> data) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerViewAllShop.setLayoutManager(linearLayoutManager);
        RecycleAllShopAdapter adapter = new RecycleAllShopAdapter(getContext(), data);
        recyclerViewAllShop.setAdapter(adapter);
    }
}
