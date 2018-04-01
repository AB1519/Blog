package com.example.bangadi.myblog.Data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bangadi.myblog.Model.Blog;
import com.example.bangadi.myblog.R;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

/**
 * Created by BAngadi on 3/30/2018.
 */

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<Blog> blogList;

    public  BlogRecyclerAdapter(Context context, List<Blog> blogList){
        this.context = context;
        this.blogList = blogList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_row,parent,false);  // assigning the post row as a view
        return new ViewHolder(view,context);
    }

    //binding the view and data
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
       Blog blog = blogList.get(position);
       String imageUrl = null;
       holder.title.setText(blog.getTitle());
       holder.desc.setText(blog.getDesc());

       // converting the date into human readable form
       java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
       String formattedDate = dateFormat.format(new Date(Long.valueOf(blog.getTimestamp())).getTime());
        holder.timestamp.setText(formattedDate);

        imageUrl = blog.getImage();


        //using picaso library to load image
        Picasso.with(context).load(imageUrl).into(holder.image); //passing the image into list
    }

    @Override
    public int getItemCount() {
        return blogList.size(); //populating the items
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title,desc,timestamp;
        public ImageView image;
        String userid;
        public ViewHolder(View View, Context ctx) {
            super(View);

            context =ctx;
            // Instansiating the variables/ widgets

            title = (TextView) View.findViewById(R.id.postTitleList);
            desc =(TextView) View.findViewById(R.id.postTextList);
            timestamp = (TextView) View.findViewById(R.id.timestampList);
            image =(ImageView) View.findViewById(R.id.postImageList);

            userid=null;


            View.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

        }
    }
}
