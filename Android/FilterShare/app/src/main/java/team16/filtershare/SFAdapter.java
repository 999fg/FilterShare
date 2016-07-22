package team16.filtershare;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by shinjaemin on 2016. 7. 10..
 */

public class SFAdapter extends RecyclerView.Adapter<SFAdapter.ViewHolder> {

    private ArrayList<SFData> sfDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView filterTitle;
        public TextView filterLikes;
        public TextView filterHashtags;
        public TextView filterMadeby;

        public ViewHolder (View view) {
            super(view);
            imageView = (ImageView)view.findViewById(R.id.test_image);
            filterTitle = (TextView)view.findViewById(R.id.filter_title);
            filterLikes = (TextView)view.findViewById(R.id.filter_likes);
            filterHashtags = (TextView)view.findViewById(R.id.filter_hashtags);
            filterMadeby = (TextView)view.findViewById(R.id.filter_madeby);
        }
    }

    public SFAdapter(ArrayList<SFData> SFDataset) {
        sfDataset = SFDataset;
    }

    @Override
    public SFAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sf_view, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int filter_likes = sfDataset.get(position).likes;
        String filter_hashtags = "";
        String[] hashtags = sfDataset.get(position).hashtags;
        holder.imageView.setImageBitmap(sfDataset.get(position).imgbit);
        holder.filterTitle.setText(sfDataset.get(position).title);
        if (filter_likes > 999999)
            holder.filterLikes.setText(Double.parseDouble(String.format("%.2f",(double) filter_likes / 1000000)) + "M times used");
        else if (filter_likes > 999)
            holder.filterLikes.setText(Double.parseDouble(String.format("%.2f",(double) filter_likes / 1000)) + "k times used");
        else
            holder.filterLikes.setText(filter_likes + " times used");
        for(int i = 0; i < hashtags.length; i++)
            filter_hashtags += "#"+hashtags[i]+" ";
        holder.filterHashtags.setText(filter_hashtags);
        holder.filterMadeby.setText(sfDataset.get(position).madeby);
    }

    @Override
    public int getItemCount() {
        return sfDataset.size();
    }
}

class SFData{
    public Bitmap imgbit;
    public String title;
    public int likes;
    public String[] hashtags;
    public String madeby;
    public SFData(Bitmap imgbit, String title, int likes, String[] hashtags, String madeby){
        this.imgbit = imgbit;
        this.title = title;
        this.likes = likes;
        this.hashtags = hashtags;
        this.madeby = madeby;
    }
}