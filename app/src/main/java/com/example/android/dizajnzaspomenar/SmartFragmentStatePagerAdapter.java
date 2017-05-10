package com.example.android.dizajnzaspomenar;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.dizajnzaspomenar.DetailActivity.EXTRA_POSITION;

/**
 * Created by Tena on 5/9/2017.
 */

public class ListForDetailActivity extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.details_recycler_view, container, false);
        ContentAdapter adapter = new ContentAdapter(recyclerView.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return recyclerView;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //public ImageView avator;
        public TextView name;
        public TextView description;
        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_list, parent, false));
            //avator = (ImageView) itemView.findViewById(R.id.list_avatar);
            name = (TextView) itemView.findViewById(R.id.list_title);
            description = (TextView) itemView.findViewById(R.id.list_desc);
           /* itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, com.example.android.dizajnzaspomenar.DetailActivity.class);
                    intent.putExtra(EXTRA_POSITION, getAdapterPosition());
                    context.startActivity(intent);
                }
            }); */
        }
    }

    /**
     * Adapter to display recycler view.
     */
    public static class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        // Set numbers of List in RecyclerView.

        private int LENGTH; // broj ljudi u Spomenaru

        private final String[] mPlaces;
        private final String[]  mPlaceDescription;
        //private final Drawable[] mPlaceAvators;
        private List<String> usersList = new ArrayList<>();
        private List<String> answersList = new ArrayList<>();


        public ContentAdapter(Context context) {
            Resources resources = context.getResources();
            //mPlaces = resources.getStringArray(R.array.places);

            DBAdapter db = new DBAdapter(context);
            db.open();
            //ovdje umjesto korisnika idu pitanja
            Cursor c = db.getAllQuestions();
            if (c.moveToFirst())
            {
                do {
                    usersList.add(c.getString(1));
                } while (c.moveToNext());
            }
            LENGTH = usersList.size();

            //a ovdje umjesto pitanja odgovori
            c = db.getAllAnswers();
           // Log.e("QUESTIONID", String.valueOf(getQuestionId()));
            if (c.moveToFirst())
            {
                do {
                    if( c.getInt(0) == 1 )
                    {
                        answersList.add(c.getString(3));
                        Log.e("TEKST ODGOVORA", c.getString(3));
                    }
                } while (c.moveToNext());
            }
            db.close();
            //increase();

            mPlaces = usersList.toArray(new String[0]);
            // mPlaceDescription = resources.getStringArray(R.array.place_desc);
            mPlaceDescription = answersList.toArray(new String[0]);
            TypedArray a = resources.obtainTypedArray(R.array.place_avator);
            /*mPlaceAvators = new Drawable[a.length()];

            for (int i = 0; i < mPlaceAvators.length; i++) {
                mPlaceAvators[i] = a.getDrawable(i);
            } */
            a.recycle();

        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            //holder.avator.setImageDrawable(mPlaceAvators[position % mPlaceAvators.length]);
            holder.name.setText(mPlaces[position % mPlaces.length]);
            holder.description.setText( mPlaceDescription[position %  mPlaceDescription.length]);
        }

        @Override
        public int getItemCount() {
            return LENGTH;
        }

       /* public void increase()
        {
            Globals g = Globals.getInstance();
            g.setQuestionId();
        } */

        public int getQuestionId()
        {
            Globals g = Globals.getInstance();
            return g.getQuestionId();
        }

    }

   /* public void dohvatiKorisnike() //bool
    {
        DBAdapter db = new DBAdapter(this.getContext());
        //--get all questions---
        db.open();
        Cursor c = db.getAllContacts();
        if (c.moveToFirst())
        {
            do {
                usersList.add(c.getString(1));
            } while (c.moveToNext());
        }
        db.close();
    } */

}

