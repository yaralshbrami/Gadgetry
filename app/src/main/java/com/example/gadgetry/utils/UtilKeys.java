package com.example.gadgetry.utils;

import android.content.SharedPreferences;

public class UtilKeys {
    public static final String PRODUCT_TABLE ="ProductsData";
    public static final String ORDER_PRODUCT_TABLE ="OrderProductsData";
    public static String IMAGE_TABLE="ImageCollection";
    public static boolean CART_ACTIVE=false;
    public static final int IMAGE_REQUEST_CODE_GALLERY = 1;
    public static int grandTotalPrice=0;
    public static String USER_KEY ="User_Key";
    //SharedPreferences variables
    public static final String SHARED_PREFS_NAME = "general";
    public static SharedPreferences PREFS;
    public static final String TOKEN = "token";

}

