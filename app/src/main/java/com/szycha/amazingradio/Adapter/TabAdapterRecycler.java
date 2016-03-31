package com.szycha.amazingradio.Adapter;

/**
 * Created by poldek on 10.05.15.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.szycha.amazingradio.R;

import java.util.ArrayList;
import java.util.List;

import io.github.typer.Font;
import io.github.typer.Typer;

public class TabAdapterRecycler extends RecyclerView.Adapter<TabAdapterRecycler.ViewHolder> {

    private final List<ItemData> listaDanych;
    private Context contex;
    private static final int TYPE_IMAGE = 0;
    private static final int TYPE_NO_IMAGE = 1;


    public TabAdapterRecycler(Context context) {
        this.listaDanych = new ArrayList<ItemData>();
        this.contex = context;

    }


    public void addDane(String description, String title, String imageUrl, String link, String data, int imagePLayPause) {
        listaDanych.add(new ItemData(description, title, imageUrl, link, data, imagePLayPause));
        notifyDataSetChanged();
    }

    public void addDaneBrakFoto(String description, String title, String imageUrl, String link, String data, int imagePLayPause) {
        listaDanych.add(new ItemData(description, title, imageUrl, link, data, imagePLayPause));
        notifyDataSetChanged();
    }

    public void clearApplications() {
        int size = this.listaDanych.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                listaDanych.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }

    public void clearList() {
        listaDanych.clear();
        notifyDataSetChanged();
    }

    public void removeEmpty(int position) {
        listaDanych.remove(position);
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {

        if (listaDanych.get(position).getImageUrl() != null) {
            return TYPE_IMAGE;
        } else {
            return TYPE_NO_IMAGE;
        }
    }

    public void setImagePLayPause(int position, int image) {
        listaDanych.get(position).setImage(image);
        notifyDataSetChanged();
    }


    public void setImageStart() {

        for (ItemData dane : listaDanych) {

            if (dane.getImage() == R.drawable.pause) {
                dane.setImage(R.drawable.play);
            }
        }
        notifyDataSetChanged();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        //View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_foto, null);
        //ViewHolder vh = new ViewHolder(view);
        //return vh;


        View v = null;
        switch (viewType) {
            case TYPE_IMAGE:
                //ViewGroup vImage = (ViewGroup) mInflater.inflate(R.layout.card_view_foto, viewGroup, false); // był error
                //View viewImage = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_foto, null);
                //ViewHolder vhImage = new ViewHolder(viewImage);
                //return vhImage;
                try {
                    v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_foto, viewGroup, false);
                } catch (Exception e) {

                }

                break;
            case TYPE_NO_IMAGE:
                //ViewGroup vBezImage = (ViewGroup) mInflater.inflate(R.layout.card_view, viewGroup, false);
                //View viewBezImage = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view, null);
                //ViewHolder vhBezImage = new ViewHolder(viewBezImage);
                //return vhBezImage;
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view, viewGroup, false);
                break;
        }


        return new ViewHolder(v);
    }

    public String getNazwa(int position) {
        return listaDanych.get(position).getTitle();
    }

    public String getLink(int position) {
        return listaDanych.get(position).getLink();
    }

    public String getOpis(int position) {
        return listaDanych.get(position).getDescryption();
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {


        //Podaj link
        viewHolder.txtLink.setText(listaDanych.get(position).getLink());


        String pubDate = listaDanych.get(position).getData();
        viewHolder.txtData.setText(pubDate);


        switch (viewHolder.getItemViewType()) {

            case TYPE_IMAGE:
                viewHolder.txtViewTitle.setText(String.valueOf(listaDanych.get(position).getTitle()));
                viewHolder.txtViewTitle.setTypeface(Typer.set(contex).getFont(Font.ROBOTO_CONDENSED_REGULAR));
                viewHolder.db.setText(String.valueOf(listaDanych.get(position).getDescryption()));
                viewHolder.db.setTypeface(Typer.set(contex).getFont(Font.ROBOTO_LIGHT));
                viewHolder.imageViewPLayPause.setImageResource(listaDanych.get(position).getImage());

                try {

                    Picasso.with(contex)
                            .load(listaDanych.get(position).getImageUrl())
                            .placeholder(R.drawable.thumbnail)
                            .fit().centerCrop()
                            .into(viewHolder.imgViewIcon);

                    /*
                    Glide.with(contex)
                            .load(listaDanych.get(position).getImageUrl())
                            .centerCrop()
                            .placeholder(R.drawable.placeholder)
                            .crossFade()
                            .into(viewHolder.imgViewIcon);
                    */

                } catch (IllegalArgumentException e) {
                    //e.printStackTrace();
                }
                break;

            case TYPE_NO_IMAGE:
                viewHolder.txtViewTitle.setText(String.valueOf(listaDanych.get(position).getTitle()));
                viewHolder.txtViewTitle.setTypeface(Typer.set(contex).getFont(Font.ROBOTO_CONDENSED_REGULAR));
                viewHolder.db.setText(String.valueOf(listaDanych.get(position).getDescryption()));
                viewHolder.db.setTypeface(Typer.set(contex).getFont(Font.ROBOTO_LIGHT));
                //No image
                break;
        }
    }

    @Override
    public int getItemCount() {
        return listaDanych.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtViewTitle;
        public ImageView imgViewIcon;
        public TextView db;
        public TextView txtData;
        public TextView txtLink;
        public ImageView imageViewPLayPause;


        public ViewHolder(View itemView) {
            super(itemView);
            txtViewTitle = (TextView) itemView.findViewById(R.id.title);
            imgViewIcon = (ImageView) itemView.findViewById(R.id.image);
            db = (TextView) itemView.findViewById(R.id.dbText);
            txtData = (TextView) itemView.findViewById(R.id.txt_data);
            txtLink = (TextView) itemView.findViewById(R.id.txtLink);
            imageViewPLayPause = (ImageView) itemView.findViewById(R.id.image_play_pause);
        }
    }
}

