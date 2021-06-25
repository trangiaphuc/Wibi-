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

public class UserOnlineAdapter extends RecyclerView.Adapter<UserOnlineAdapter.ViewHolder> {

    private Context context;
    private List<User> userList;

    public UserOnlineAdapter(Context context, List<User> users){
        this.context = context;
        this.userList = users;
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_online, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull UserOnlineAdapter.ViewHolder holder, int position) {

        User  user = userList.get(position);

        holder.txtOnlineName.setText(user.getFullname());

        if (user.getImgURL().equals("default")){
            holder.avatar.setImageResource(R.drawable.app_icon);
        }
        else
        {
            //load avatar
            Glide.with(context).load(user.getImgURL()).into(holder.avatar);
        }

        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, MessageActivity.class).
                        addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).putExtra("id", user.getId()));
            }
        });


    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView avatar;
        private TextView txtOnlineName;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            avatar = itemView.findViewById(R.id.userAvatarOnline);
            txtOnlineName = itemView.findViewById(R.id.txtOnlineFullName);
        }
    }
}
