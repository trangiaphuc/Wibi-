package com.example.wibi.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wibi.Models.User;
import com.example.wibi.R;
import com.example.wibi.UserInteraction.Interaction;
import com.example.wibi.UserInteraction.UserProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserFriendSearchAdapter extends RecyclerView.Adapter<UserFriendSearchAdapter.ViewHolder> {

    private Context context;
    private List<User> userList;
    FirebaseUser firebaseUser;

    public UserFriendSearchAdapter(Context con, List<User> userList){
        this.context = con;
        this.userList = userList;
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_friendsearch, parent, false);
        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull UserFriendSearchAdapter.ViewHolder holder, int position) {

        User user = userList.get(position);
        holder.txtFullname.setText(user.getFullname());
        if (user.getImgURL().equals("default")){
            holder.avatar.setImageResource(R.drawable.app_icon);
        }
        else Glide.with(context).load(user.getImgURL()).into(holder.avatar);


        holder.btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                Interaction.addFriend(firebaseUser.getUid(), user.getId());
            }
        });

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

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView avatar;
        private TextView txtFullname;
        LinearLayout btnAddFriend;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            avatar = itemView.findViewById(R.id.userAvatarFriendS);
            txtFullname = itemView.findViewById(R.id.lblUserFullName);
            btnAddFriend = itemView.findViewById(R.id.btnAddNewFriend);
        }
    }
}
