package com.fromscratch.mine.myapplicationuser;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 *
 */

class ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {



    private List<Comment> comments;
    public ListAdapter() {

    }

    public void setComments(List<Comment> comments){
        this.comments = comments;
        this.notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater=LayoutInflater.from(parent.getContext());

            View root = inflater.inflate(  R.layout.review_item, parent, false);
            return new CommentsListViewHolder(root);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            CommentsListViewHolder viewHolder=(CommentsListViewHolder) holder;
            viewHolder.content.setText(comments.get(position).getBody());
            viewHolder.authorName.setText(comments.get(position).getCname());


    }

    @Override
    public int getItemCount() {

        return (comments !=null? comments.size():0);

    }

    class CommentsListViewHolder extends RecyclerView.ViewHolder{

        final TextView authorName;
        final TextView content;
        public CommentsListViewHolder(View itemView) {

            super(itemView);
            authorName=itemView.findViewById(R.id.review_author);
            content=itemView.findViewById(R.id.review_content);


        }
    }


}
