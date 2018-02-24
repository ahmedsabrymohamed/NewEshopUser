package com.fromscratch.mine.myapplicationuser;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;

public class MyFavouActivity extends AppCompatActivity implements GridAdapter.SetOncLickListener {

    private RecyclerView productRecyclerView;
    private GridAdapter productAdabter;
    private GridLayoutManager layoutManager;

    myDbAdapter helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_main_page);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        productAdabter=new GridAdapter(this);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int productNumber=((int)dpWidth/101);


        productRecyclerView=(RecyclerView) findViewById(R.id.categoriesGrid);
        layoutManager=new GridLayoutManager(MyFavouActivity.this,productNumber);
        productRecyclerView.setLayoutManager(layoutManager);
        productRecyclerView.setAdapter(productAdabter);
        helper = new myDbAdapter(this);
        String data = helper.getData();
        List<String> Mylist=new ArrayList<>(Arrays.asList(data.split(" ")));
        for(int i=0;i<Mylist.size();i++) {
            getData(Mylist.get(i));

        }

    }

    private void getData(String id)
    {
        ApiInterface apiService = APIClient.getClient().create(ApiInterface.class);
        Call<ProductsResponse> call = apiService.getOneProduct(id);
        call.enqueue(new retrofit2.Callback<ProductsResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductsResponse> call
                    , @NonNull retrofit2.Response<ProductsResponse> response) {
                if (response != null&&response.body()!=null) {
                    List<Product> a = response.body().getProducts();
                    if(a!=null)
                        productAdabter.setProducts(a);
                }
            }
            @Override
            public void onFailure(@NonNull Call<ProductsResponse> call, @NonNull Throwable t) {
            }
        });

    }

    @Override
    public void SetOnclick(Product product) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favor_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
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
        return super.onOptionsItemSelected(item);
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
}
