package com.example.wibi.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wibi.Message.MessageActivity;
import com.example.wibi.Models.User;
import com.example.wibi.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private List<User> userList;

    final static String ONLINE = "online";
    final static String OFFLINE = "offline";

    public UserAdapter(Context context, List<User> userList)
    {
        this.context= context;
        this.userList = userList;
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull UserAdapter.ViewHolder holder, int position) {

        User user = userList.get(position);

        holder.lblUserFullName.setText(user.getFullname());


        if (user.getImgURL().equals("default")){
            holder.avatar.setImageResource(R.drawable.app_icon);
        }
        else
        {
            //load avatar
            Glide.with(context).load(user.getImgURL()).into(holder.avatar);
        }

        if(user.getStatus().equals(ONLINE))
            holder.status.setVisibility(View.VISIBLE);
        else holder.status.setVisibility(View.GONE);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("id", user.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView avatar, status;
        public TextView lblUserFullName;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            lblUserFullName = itemView.findViewById(R.id.lblUserFullName);
            avatar = itemView.findViewById(R.id.userAvatar);
            status = itemView.findViewById(R.id.status);

        }
    }
}
