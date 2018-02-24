package com.fromscratch.mine.myapplicationuser;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;

public class ProductsMainPageActivity extends AppCompatActivity implements GridAdapter.SetOncLickListener,GridTrendAdapter.SetOncLickListener {



    private RecyclerView productsGrid1;
    private RecyclerView productsGrid2;
    private RecyclerView productsGrid3;
    private RecyclerView productsTrendGrid1;
    private RecyclerView productsTrendGrid2;
    private RecyclerView productsTrendGrid3;
    private GridAdapter gridAdapter1;
    private GridAdapter gridAdapter2;
    private GridAdapter gridAdapter3;
    private GridTrendAdapter gridTrendAdapter1;
    private GridTrendAdapter gridTrendAdapter2;
    private GridTrendAdapter gridTrendAdapter3;
    private int categoryID;
    private Toast networkInfo;


    protected void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(this);
        super.onCreate(savedInstanceState);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int moviesNumber = ((int) dpWidth / 101);
        setContentView(R.layout.activity_products_main_page);
        if(getIntent()!=null)
        categoryID=getIntent().getIntExtra("ID",1);

        setRecyclerViews(moviesNumber);
        networkInfo = null;

        loadSavedData(savedInstanceState);
        if(!isNetworkAvailable())
            notifyUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.category_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {


            case R.id.favorite_item:
            startActivity(new Intent(this,MyFavouActivity.class));
            finish();
                break;

            case R.id.logout_item:
                AuthUI.getInstance().signOut(this).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                });
                break;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

        }



        return true;
    }



    @Override
    public void SetOnclick(Product product) {

        final Intent intent = new Intent(this, DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("Product", setFavorite(product));
        intent.putExtras(bundle);
        startActivity(intent);

    }


    private void notifyUser() {
        if (networkInfo != null)
            networkInfo.cancel();
        networkInfo = Toast.makeText(getApplication(), "There is No INTERNET Connection", Toast.LENGTH_LONG);
        networkInfo.show();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("productList1", gridAdapter1.getProducts());
        outState.putParcelableArrayList("productList2", gridAdapter2.getProducts());
        outState.putParcelableArrayList("productList3", gridAdapter3.getProducts());
        outState.putParcelableArrayList("trendProductList1", gridTrendAdapter1.getProducts());
        outState.putParcelableArrayList("trendProductList2", gridTrendAdapter2.getProducts());
        outState.putParcelableArrayList("trendProductList3", gridTrendAdapter3.getProducts());
        outState.putInt("ID",categoryID);
        super.onSaveInstanceState(outState);


    }



    public Product setFavorite(Product product) {
        myDbAdapter helper = new myDbAdapter(this);
        String data = helper.getData();
        List<String> Mylist = new ArrayList<>(Arrays.asList(data.split(" ")));

        for (int i = 0; i < Mylist.size(); i++) {
            if (Mylist.get(i).equals(Integer.toString(product.getId()))) {
                product.setFavorite(true);
                return product;
            }
        }
        product.setFavorite(false);
        return product;
    }

    private void getData() {
        ApiInterface apiService = APIClient.getClient().create(ApiInterface.class);
        Call<ProductsResponse> call = apiService.getAllProducts(Integer.toString(categoryID));
        call.enqueue(new retrofit2.Callback<ProductsResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductsResponse> call
                    , @NonNull retrofit2.Response<ProductsResponse> response) {
                List<Product> a = response.body().getProducts();
                ArrayList<Product> products1 = new ArrayList<>();
                ArrayList<Product> products3 = new ArrayList<>();
                ArrayList<Product> products2 = new ArrayList<>();
                ArrayList<Product> trend1 = new ArrayList<>();
                ArrayList<Product> trend2 = new ArrayList<>();
                ArrayList<Product> trend3 = new ArrayList<>();
                if (a != null) {
                    for (int i = 0; i < a.size(); i++) {
                        switch (a.get(i).getRange()){
                            case 1:
                                if(a.get(i).getTrend()==1)
                                    trend1.add(a.get(i));
                                else
                                    products1.add(a.get(i));
                                break;
                            case 2:
                                if(a.get(i).getTrend()==2)
                                    trend2.add(a.get(i));
                                else
                                    products2.add(a.get(i));
                                break;
                            case 3:
                                if(a.get(i).getTrend()==3)
                                    trend3.add(a.get(i));
                                else
                                    products3.add(a.get(i));
                                break;
                        }

                    }

                    gridAdapter1.setProducts(products1);
                    gridAdapter2.setProducts(products2);
                    gridAdapter3.setProducts(products3);
                    gridTrendAdapter1.setProducts(trend1);
                    gridTrendAdapter2.setProducts(trend2);
                    gridTrendAdapter3.setProducts(trend3);
                }


            }

            @Override
            public void onFailure(@NonNull Call<ProductsResponse> call, @NonNull Throwable t) {
            }
        });


    }
    private  void setRecyclerViews(int moviesNumber){
        gridAdapter1 = new GridAdapter(this);
        productsGrid1 = findViewById(R.id.moviesGrid1);
        productsGrid1.setLayoutManager(new GridLayoutManager(this, moviesNumber));
        productsGrid1.setHasFixedSize(true);
        productsGrid1.setAdapter(gridAdapter1);
        /////////////////////////////////////////
        gridAdapter2 = new GridAdapter(this);
        productsGrid2 = findViewById(R.id.moviesGrid2);
        productsGrid2.setLayoutManager(new GridLayoutManager(this, moviesNumber));
        productsGrid2.setHasFixedSize(true);
        productsGrid2.setAdapter(gridAdapter2);
        ///////////////////////////////////////////
        gridAdapter3 = new GridAdapter(this);
        productsGrid3 = findViewById(R.id.moviesGrid3);
        productsGrid3.setLayoutManager(new GridLayoutManager(this, moviesNumber));
        productsGrid3.setHasFixedSize(true);
        productsGrid3.setAdapter(gridAdapter3);
        ////////////////////////////////////////////
        ////////////////////////////////////////////
        ////////////////////////////////////////////
        gridTrendAdapter1 = new GridTrendAdapter(this);
        productsTrendGrid1 = findViewById(R.id.moviesGridTrend1);
        productsTrendGrid1.setLayoutManager(new GridLayoutManager(this, 3));
        //productsTrendGrid1.setHasFixedSize(true);
        productsTrendGrid1.setAdapter(gridTrendAdapter1);
        ////////////////////////////////////////////
        gridTrendAdapter2 = new GridTrendAdapter(this);
        productsTrendGrid2 = findViewById(R.id.moviesGridTrend2);
        productsTrendGrid2.setLayoutManager(new GridLayoutManager(this, 3));
        //productsTrendGrid2.setHasFixedSize(true);
        productsTrendGrid2.setAdapter(gridTrendAdapter2);
        //////////////////////////////////////////////
        gridTrendAdapter3 = new GridTrendAdapter(this);
        productsTrendGrid3 = findViewById(R.id.moviesGridTrend3);
        productsTrendGrid3.setLayoutManager(new GridLayoutManager(this, 3));
        //productsTrendGrid3.setHasFixedSize(true);
        productsTrendGrid3.setAdapter(gridTrendAdapter3);

    }

    @Override
    public void SetOnclickTrend(Product product) {
        final Intent intent = new Intent(this, DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("Product", setFavorite(product));
        intent.putExtras(bundle);
        startActivity(intent);
    }
    private void loadSavedData(Bundle savedInstanceState){
        List<Product> productList1;
        List<Product> productList2;
        List<Product> productList3;
        List<Product> trendProductList1;
        List<Product> trendProductList2;
        List<Product> trendProductList3;


        if (savedInstanceState != null) {
            categoryID=savedInstanceState.getInt("ID");
            productList1 = savedInstanceState.getParcelableArrayList("productList1");
            productList2 = savedInstanceState.getParcelableArrayList("productList2");
            productList3 = savedInstanceState.getParcelableArrayList("productList3");
            trendProductList1 = savedInstanceState.getParcelableArrayList("trendProductList1");
            trendProductList2 = savedInstanceState.getParcelableArrayList("trendProductList2");
            trendProductList3 = savedInstanceState.getParcelableArrayList("trendProductList3");
            gridAdapter1.setProducts(productList1);
            gridAdapter2.setProducts(productList2);
            gridAdapter3.setProducts(productList3);
            gridTrendAdapter1.setProducts(trendProductList1);
            gridTrendAdapter2.setProducts(trendProductList2);
            gridTrendAdapter3.setProducts(trendProductList3);

        } else {
            getData();
        }

    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}