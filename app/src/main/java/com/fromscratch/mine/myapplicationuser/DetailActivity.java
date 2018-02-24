package com.fromscratch.mine.myapplicationuser;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class DetailActivity extends AppCompatActivity {

    private Product product =null;
    private ListAdapter reviewsAdapter;
    private LikeButton likeButton;
    private  SimpleDraweeView productPoster;
    myDbAdapter helper;
    FirebaseAuth mAuth;
    TextView productRate;
    Button rateIt;
    EditText editText;
    Button add_comment;


    private Toast favoriteToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth=FirebaseAuth.getInstance();
        helper = new myDbAdapter(this);
        int orientation=getResources().getConfiguration().orientation;

        if(orientation== Configuration.ORIENTATION_LANDSCAPE)
            setContentView(R.layout.activity_detail_landscap);
        else
            setContentView(R.layout.activity_detail);

        likeButton=findViewById(R.id.favorite_button);
        rateIt=findViewById(R.id.rateIt);

        product =new Product();
        Intent intent=getIntent();
        if(intent!=null){
            Bundle bundle=intent.getExtras();
            if(bundle!=null){

                product =bundle.getParcelable("Product");
                likeButton.setLiked(product.isFavorite());

            }
        }



        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;

        productPoster =  findViewById(R.id.movieDraweeView);


        likeButton.setOnLikeListener(new OnLikeListener(){

            @Override
            public void liked(LikeButton likeButton) {

                product.setFavorite(true);
                helper.insertData(Integer.toString(product.getId()));
                if(favoriteToast!=null)
                    favoriteToast.cancel();
                favoriteToast=Toast.makeText(getApplicationContext(),"Movie is marked as favorite :)",Toast.LENGTH_LONG);
                favoriteToast.show();

            }

            @Override
            public void unLiked(LikeButton likeButton) {

                product.setFavorite(false);

                helper.delete(Integer.toString(product.getId()));
                if(favoriteToast!=null)
                    favoriteToast.cancel();
                favoriteToast=Toast.makeText(getApplicationContext(),"Product is removed from favorite movies :(",Toast.LENGTH_LONG);
                favoriteToast.show();



            }
        });
        productRate =  findViewById(R.id.ratingBar);
        productRate.setText(Double.toString(product.getRate()));

        TextView productName =  findViewById(R.id.movieOriginalTitle);
        productName.setText(product.getName());

        TextView productDescription =  findViewById(R.id.movieOverview);
        productDescription.setText(product.getDescription());


        /*TextView voteCount = (TextView) findViewById(R.id.movieVoteCount);
        voteCount.setText(getString(R.string.vote_count)+ product.getVote_count());*/

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) productPoster.getLayoutParams();





        params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        params.height =(int)(dpHeight*0.55);

        if(orientation== Configuration.ORIENTATION_LANDSCAPE)
        {

            params.width = (int)(dpWidth*0.35);
            params.height =LinearLayout.LayoutParams.MATCH_PARENT;
        }


        productPoster.setLayoutParams(params);



        RecyclerView reviewsList=(RecyclerView) findViewById(R.id.reviews_list);


        LinearLayoutManager reviewsLayoutManager = new LinearLayoutManager(this);



        reviewsList.setLayoutManager(reviewsLayoutManager);
        reviewsList.setHasFixedSize(true);

        reviewsAdapter=new ListAdapter();


        reviewsList.setAdapter(reviewsAdapter);

        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if(networkInfo!=null&&networkInfo.isConnected()) {
            fetchComments();
            changePoster();

        }
        if(savedInstanceState!=null)
        {
            rateIt.setVisibility(savedInstanceState.getInt("rateIt"));
        }
        else{
            isRating();
        }
        add_comment=findViewById(R.id.add_comment);
        editText=(EditText) findViewById(R.id.commentText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(editText.getText().toString().trim().isEmpty())
                    add_comment.setEnabled(false);
                else
                    add_comment.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }









    private void changePoster(){

        Uri uri=Uri.parse(product.getImage());

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .build();
        productPoster.setController(
                Fresco.newDraweeControllerBuilder()
                        .setOldController(productPoster.getController())
                        .setImageRequest(request)
                        .build());
    }



    private void fetchComments(){

        ApiInterface apiService = APIClient.getClient().create(ApiInterface.class);
        Call<CommentsResponse> call = apiService.getAllComments(Integer.toString(product.getId()));
        call.enqueue(new retrofit2.Callback<CommentsResponse>() {
            @Override
            public void onResponse(@NonNull Call<CommentsResponse> call
                    , @NonNull retrofit2.Response<CommentsResponse> response) {
                List<Comment> a=response.body().getComments();
                ArrayList<Comment>comments=new ArrayList<>();
                if(a!=null) {
                    for(int i=0;i<a.size();i++)
                    {
                        if(a.get(i).getAppear()==1)
                            comments.add(a.get(i));
                    }
                }
                reviewsAdapter.setComments(comments);

            }

            @Override
            public void onFailure(@NonNull Call<CommentsResponse> call, @NonNull Throwable t) {
                // Log error here since request failed

            }
        });

    }



    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        likeButton.setLiked(savedInstanceState.getBoolean("like"));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("like",likeButton.isLiked());
        outState.putInt("rateIt",rateIt.getVisibility());
        super.onSaveInstanceState(outState);

    }
    @Override
    protected void onStart() {
        super.onStart();

    }

    private void isRating() {

        ApiInterface apiService = APIClient.getClient().create(ApiInterface.class);
        Call<RateResponse> call = apiService.isRated(Integer.toString(product.getId())
                ,"\""+mAuth.getCurrentUser().getEmail()+"\"");
        call.enqueue(new retrofit2.Callback<RateResponse>() {
            @Override
            public void onResponse(@NonNull Call<RateResponse> call
                    , @NonNull retrofit2.Response<RateResponse> response) {


                if(response.body()!=null) {
                    if(response.body().getSuccess()!=0)
                        rateIt.setVisibility(response.body().getRate()==0? View.VISIBLE:View.GONE);
                    else
                        Toast.makeText(getApplicationContext(),response.body().getMessage(),Toast.LENGTH_LONG).show();
                }

            }
            @Override
            public void onFailure(@NonNull Call<RateResponse> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }
    private void updateRating(float rate) {
        ApiInterface apiService = APIClient.getClient().create(ApiInterface.class);
        Call<RateResponse> call = apiService.updateRate(Integer.toString(product.getId())
                ,mAuth.getCurrentUser().getEmail(),Float.toString(rate));
        call.enqueue(new retrofit2.Callback<RateResponse>() {
            @Override
            public void onResponse(@NonNull Call<RateResponse> call
                    , @NonNull retrofit2.Response<RateResponse> response) {

                if(response.body()!=null)
                {
                    productRate.setText(Double.toString(response.body().getRate()));
                    Toast.makeText(getApplicationContext(),response.body().getMessage(),Toast.LENGTH_LONG).show();
                    rateIt.setVisibility(View.GONE);
                }


            }
            @Override
            public void onFailure(@NonNull Call<RateResponse> call, @NonNull Throwable t) {
            }
        });

    }
    public void rateProduct(View view){
        final Dialog rankDialog = new Dialog(this, R.style.FullHeightDialog);
        rankDialog.setContentView(R.layout.rate_dialog);
        rankDialog.setCancelable(true);
        final RatingBar ratingBar = (RatingBar)rankDialog.findViewById(R.id.product_rate_dialog);

        TextView text = (TextView) rankDialog.findViewById(R.id.rank_dialog_text1);
        text.setText(product.getName());

        Button updateButton = (Button) rankDialog.findViewById(R.id.rank_dialog_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRating(ratingBar.getRating());
                rankDialog.dismiss();
            }
        });
        //now that the dialog is set up, it's time to show it
        rankDialog.show();

    }
    public void AddComment(View view) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null&&!editText.getText().toString().equals("")) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            String s=editText.getText().toString();
            DataLoader dataLoader=new DataLoader(this);
            dataLoader.createComment(Integer.toString(product.getId()),s,name,email);
            Toast.makeText(this,"Your comment Under Review",Toast.LENGTH_LONG).show();

        }
        else {
            Toast.makeText(this,"Please make shure to write a comment First",Toast.LENGTH_LONG).show();

        }
    }

}
