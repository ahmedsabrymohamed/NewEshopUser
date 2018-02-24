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

public class CategoryGridAdapter extends RecyclerView.Adapter<CategoryGridAdapter.GridViewHolder> {

    private final List<Category> categories;
    private final  SetOncLickListener mListener;

    CategoryGridAdapter(SetOncLickListener Listener){

        this.mListener=Listener;
        categories =new ArrayList<>();

    }


    @Override
    public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View root=inflater.inflate(R.layout.category_grid_item,parent,false);
        return new GridViewHolder(root);
    }

    @Override
    public void onBindViewHolder(GridViewHolder holder, int position) {


        Uri uri=Uri.parse(categories.get(position).getImage());

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(500, 500))
                .build();
        holder.CategoryImage.setController(
                Fresco.newDraweeControllerBuilder()
                        .setOldController(holder.CategoryImage.getController())
                        .setImageRequest(request)
                        .build());
        holder.title.setText(categories.get(position).getName());


//

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    private void insertItem(Category category){

        categories.add(category);
        notifyItemInserted(categories.size()-1);
    }
    public void refresh(){

        categories.clear();
        notifyDataSetChanged();


    }

    public class GridViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        final SimpleDraweeView CategoryImage;
        final TextView title ;
        final TextView rate;
        GridViewHolder(View itemView) {
            super(itemView);

            CategoryImage =itemView.findViewById(R.id.movieDraweeView);
            title=itemView.findViewById(R.id.gridTitleTextView);
            rate =itemView.findViewById(R.id.gridDateTextView);




            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            mListener.SetOnclick(categories.get(getAdapterPosition()));

        }
    }

    public void setCategories(List<Category> categories) {
        if(categories !=null)
            for(Category x: categories)
                insertItem(x);
    }



    public interface SetOncLickListener{
        void SetOnclick(Category category);
    }
    public ArrayList getCategories(){
        return (ArrayList) categories;
    }
}
