package com.example.phamhuan;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.example.phamhuan.ModelClass.Cart;

import java.util.Map;

public class CartList {

    private static SharedPreferences sp;


    public static void addToCart(Context ctx, String kitchen, Cart cart) {
        sp = ctx.getSharedPreferences(Constant.CartPrefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(cart);
        editor.putString(kitchen, json);
        editor.apply();
    }

    public static Cart getCart(Context ctx, String food){
        sp = ctx.getSharedPreferences(Constant.CartPrefName, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sp.getString(food, "");
        return gson.fromJson(json, Cart.class);
    }

    public static Map<String, ?> getAllCart(Context context){
        sp = context.getSharedPreferences(Constant.CartPrefName,Context.MODE_PRIVATE);
        return sp.getAll();
    }

    public static void remove(Context context, String key) {
        sp = context.getSharedPreferences(Constant.CartPrefName,Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.remove(key); edit.apply();
    }

    public static void clearAll(Context context) {
        sp = context.getSharedPreferences(Constant.CartPrefName,Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.clear(); edit.apply();
    }

}
