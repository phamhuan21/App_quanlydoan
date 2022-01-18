package com.example.phamhuan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.phamhuan.ModelClass.model;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class myadapter extends FirebaseRecyclerAdapter<model,myadapter.myviewholder>
{
    public myadapter(@NonNull FirebaseRecyclerOptions<model> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull model model)
    {
        holder.name.setText(model.getName());
        holder.distance.setText(model.getDistance());
        holder.charge.setText(model.getCharge());
        if (model.getStatus().toUpperCase().equals("OPEN")) {
            holder.close.setVisibility(View.GONE);
            holder.open.setVisibility(View.VISIBLE);
        } else {
            holder.open.setVisibility(View.GONE);
            holder.close.setVisibility(View.VISIBLE);
        }
        Glide.with(holder.img.getContext()).load(model.getImage()).into(holder.img);
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search,parent,false);
        return new myviewholder(view);
    }


    class myviewholder extends RecyclerView.ViewHolder
    {
        ImageView img;
        TextView name,distance,charge,open,close;
        public myviewholder(@NonNull View itemView)
        {
            super(itemView);
            img=(ImageView)itemView.findViewById(R.id.cat_image);
            name=(TextView)itemView.findViewById(R.id.cat_name);
            distance=(TextView)itemView.findViewById(R.id.cat_distance);
            charge=(TextView)itemView.findViewById(R.id.cat_charge);
            open=(TextView)itemView.findViewById(R.id.cat_open);
            close=(TextView)itemView.findViewById(R.id.cat_close);
        }
    }
}
