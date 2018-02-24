package com.fromscratch.mine.myapplicationuser;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * this class is the customAdapter and viewHolder used to handle the clicks and view recycling
 * of the recycler view
 */

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.GridViewHolder> {

    private final List<Product> products;
    private final  SetOncLickListener mListener;

    GridAdapter(SetOncLickListener Listener){

        this.mListener=Listener;
        products =new ArrayList<>();

    }


    @Override
    public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View root=inflater.inflate(R.layout.grid_item,parent,false);
        return new GridViewHolder(root);
    }

    @Override
    public void onBindViewHolder(GridViewHolder holder, int position) {


        Uri uri=Uri.parse(products.get(position).getImage());

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(500, 500))
                .build();
        holder.productImage.setController(
                Fresco.newDraweeControllerBuilder()
                        .setOldController(holder.productImage.getController())
                        .setImageRequest(request)
                        .build());
        holder.title.setText(products.get(position).getName());
        holder.rate.setText(Double.toString(products.get(position).getRate()).substring(0,3));

//

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    private void insertItem(Product product){

        products.add(product);
        notifyItemInserted(products.size()-1);
    }
    public void refresh(){

        products.clear();
        notifyDataSetChanged();


    }

    public class GridViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        final SimpleDraweeView productImage;
        final TextView title ;
        final TextView rate;
        GridViewHolder(View itemView) {
            super(itemView);

            productImage =itemView.findViewById(R.id.movieDraweeView);
            title=itemView.findViewById(R.id.gridTitleTextView);
            rate =itemView.findViewById(R.id.gridDateTextView);




            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            mListener.SetOnclick(products.get(getAdapterPosition()));

        }
    }

    public void setProducts(List<Product> products) {
        if(products !=null)
            for(Product x: products)
                insertItem(x);
    }



    public interface SetOncLickListener{
        void SetOnclick(Product product);
    }
    public ArrayList getProducts(){
        return (ArrayList) products;
    }
}
