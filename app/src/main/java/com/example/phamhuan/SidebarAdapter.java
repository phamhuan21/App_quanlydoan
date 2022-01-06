package com.example.phamhuan;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SidebarAdapter extends RecyclerView.Adapter<SidebarAdapter.UserViewHolder> {

    private List<String> titleList;
    private List<String> subtitleList;
    private List<Integer> iconList;
    private DrawerLayout drawer;
    private Context context;

    public SidebarAdapter(Context ctx, DrawerLayout drawer) {
        this.context = ctx;
        this.drawer = drawer;
        this.titleList = new ArrayList<>();
        this.iconList = new ArrayList<>();
        this.subtitleList = new ArrayList<>();
        if(Constant.AdminLogin.equals("true")) {
            titleList.add("Thêm Nhà Hàng");
            titleList.add("Thêm Món Ăn");
            titleList.add("Liên Lạc");
            iconList.add(R.drawable.ic_baseline_add_business_24);
            iconList.add(R.drawable.ic_baseline_post_add_24);
            iconList.add(R.drawable.info);
            subtitleList.add("Tên, Địa chỉ, Ảnh,...");
            subtitleList.add("Tên, Giá món, Ảnh,...");
            subtitleList.add("Kết nối qua MXH");
        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.lsv_menu,parent,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, final int position) {
        holder.image.setImageResource(iconList.get(position));
        holder.title.setText(titleList.get(position));
        holder.sub_title.setText(subtitleList.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performClick(context,drawer,titleList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return titleList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView sub_title;
        public UserViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            sub_title = itemView.findViewById(R.id.sub_title);
        }
    }

    private static void performClick(Context ctx,DrawerLayout drawer,String title){
        drawer.closeDrawer(GravityCompat.START);
        switch (title) {
            case "Thêm Nhà Hàng": {
                Intent policy = new Intent(ctx, AddRestaurant.class);
                ctx.startActivity(policy);
                break;
            }
            case "Thêm Món Ăn": {
                Intent policy = new Intent(ctx,AddFood.class);
                ctx.startActivity(policy);
                break;
            }
            case "Liên Lạc": {
                Intent policy = new Intent(Intent.ACTION_VIEW);
                policy.setData(Uri.parse("https://www.facebook.com/thiensukin"));
                ctx.startActivity(policy);
                break;
            }
        }
    }

}