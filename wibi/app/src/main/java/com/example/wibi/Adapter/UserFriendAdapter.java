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
import com.example.wibi.Models.User;
import com.example.wibi.R;
import com.example.wibi.UserInteraction.UserProfileActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserFriendAdapter extends RecyclerView.Adapter<UserFriendAdapter.ViewHolder> {
    private Context context;
    private List<User> userList;

    public UserFriendAdapter(Context context, List<User> users){
        this.userList = users;
        this.context = context;
        notifyDataSetChanged();
    }
    @NonNull
    @NotNull
    @Override
    public UserFriendAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_friend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull UserFriendAdapter.ViewHolder holder, int position) {

        User user = userList.get(position);
        holder.txtFriendName.setText(user.getFullname());

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
                Intent intent = new Intent(context, UserProfileActivity.class);
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
        private CircleImageView avatar;
        private TextView txtFriendName;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.userAvatarFr);
            txtFriendName = itemView.findViewById(R.id.lblFriendFullName);
        }
    }
}
