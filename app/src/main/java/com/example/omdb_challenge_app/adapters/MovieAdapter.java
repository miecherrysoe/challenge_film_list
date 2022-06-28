package com.example.omdb_challenge_app.adapters;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.omdb_challenge_app.activity.DetailsActivity;
import com.example.omdb_challenge_app.activity.MainActivity;
import com.example.omdb_challenge_app.R;
import com.example.omdb_challenge_app.model.Movie;
import com.example.omdb_challenge_app.util.Constraints;

import java.util.List;
import java.util.logging.ConsoleHandler;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>{

    List<Movie> movielist;
    int position = 0;
        public MovieAdapter(List<Movie> movielist,int count) {
        this.movielist = movielist;
        this.position = count;
    }



    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView   = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_movi, viewGroup, false);

        return new MovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MovieViewHolder movieViewHolder, final int i) {

        //movieViewHolder.title.setText("Title   "+movielist.get(i).getTitle());
        try {
            movieViewHolder.title.setText(movielist.get(i).getTitle());

            Glide.with(movieViewHolder.poster.getContext()).load(movielist.get(i).getPoster()).into(movieViewHolder.poster);

            movieViewHolder.fragment_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent detailsIntent = new Intent(((MainActivity)
                            movieViewHolder.fragment_container.getContext()), DetailsActivity.class);

                    detailsIntent.putExtra(Constraints.
                            MOVIEID, movielist.get(i).getImdbID());

                    movieViewHolder.fragment_container.getContext().startActivity(detailsIntent);
                }
            });
        } catch (Exception e){
            Log.i("Error_","err_"+e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        if (position > 0){
            int returnRemain = (6 * (position + 1)) - (6 * (position));
            try {
                if ( (movielist.size() - (6 * (position))) > returnRemain) {
                    return 6;
                } else {
                    return (movielist.size() - (6 * (position)));
                }
            } catch (Exception e){
                return 0;
            }
        } else {
            try {
                if (movielist.size() > 6) {
                    return 6;
                }
                return movielist.size();
            } catch (Exception e){
                return 0;
            }
        }

    }


    public class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView poster;
        FrameLayout fragment_container;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.textView_title);
            poster = itemView.findViewById(R.id.imageView_poster);
            fragment_container  = itemView.findViewById(R.id.fragment_container);
        }
    }

}
