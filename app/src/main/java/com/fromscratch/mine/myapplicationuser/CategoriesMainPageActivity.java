package com.fromscratch.mine.myapplicationuser;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.firebase.crash.FirebaseCrash;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class CategoriesMainPageActivity extends AppCompatActivity
        implements CategoryGridAdapter.SetOncLickListener{



    private RecyclerView CategoriesGrid1;
    private CategoryGridAdapter gridAdapter;
    private Toast networkInfo;


    protected void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        FirebaseCrash.log("Activity started");
       // FirebaseCrash.report(new Exception("Starting CategoriesMainPageActivity onCreate "));
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int moviesNumber = ((int) dpWidth / 101);
        setContentView(R.layout.activity_categories_main_page);

        setRecyclerViews(moviesNumber);
        networkInfo = null;

        loadSavedData(savedInstanceState);
        if(!isNetworkAvailable())
            notifyUser();
        FirebaseCrash.log("Activity created");
        FirebaseCrash.report(new Exception("NOW"));

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

        }



        return true;
    }



    @Override
    public void SetOnclick(Category category) {

        Intent intent = new Intent(this, ProductsMainPageActivity.class);
        intent.putExtra("ID",category.getCategory_id());
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
        outState.putParcelableArrayList("Categories", gridAdapter.getCategories());
        super.onSaveInstanceState(outState);


    }





    private void getData() {
        ApiInterface apiService = APIClient.getClient().create(ApiInterface.class);
        Call<CategoriesResponse> call = apiService.getAllCategories();
        call.enqueue(new Callback<CategoriesResponse>() {
            @Override
            public void onResponse(@NonNull Call<CategoriesResponse> call
                    , @NonNull retrofit2.Response<CategoriesResponse> response) {

                if (response != null&&response.body()!=null) {
                    List<Category> a = response.body().getCategories();
                    if(a!=null)
                    gridAdapter.setCategories(a);
                    }
                }

            @Override
            public void onFailure(Call<CategoriesResponse> call, Throwable t) {

            }


        });


    }
    private  void setRecyclerViews(int moviesNumber){
        gridAdapter = new CategoryGridAdapter(this);
        CategoriesGrid1 = findViewById(R.id.categoriesGrid);
        CategoriesGrid1.setLayoutManager(new GridLayoutManager(this, moviesNumber));
        CategoriesGrid1.setHasFixedSize(true);
        CategoriesGrid1.setAdapter(gridAdapter);


    }

    private void loadSavedData(Bundle savedInstanceState){
        List<Category> categories;



        if (savedInstanceState != null) {

             categories= savedInstanceState.getParcelableArrayList("Categories");

            gridAdapter.setCategories(categories);


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