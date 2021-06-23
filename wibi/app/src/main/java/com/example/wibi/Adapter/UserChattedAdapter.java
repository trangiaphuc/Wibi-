package com.example.wibi.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wibi.Message.MessageActivity;
import com.example.wibi.Models.LastMessage;
import com.example.wibi.Models.User;
import com.example.wibi.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserChattedAdapter extends RecyclerView.Adapter<UserChattedAdapter.ViewHolder> {

    private Context context;
    private List<User> userList;
    private List<LastMessage> lastMessageList;

    final static String ONLINE = "online";
    final static String OFFLINE = "offline";

    public UserChattedAdapter(Context context, List<User> userList, List<LastMessage> lastMessagesList)
    {
        this.context= context;
        this.userList = userList;
        this.lastMessageList = lastMessagesList;
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
    public void onBindViewHolder(@NonNull @NotNull UserChattedAdapter.ViewHolder holder, int position) {

        User user = userList.get(position);
        LastMessage lastMessage = lastMessageList.get(position);

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


        holder.txtLastMess.setText(lastMessage.getMessage());
        holder.txtLastMess.setVisibility(View.VISIBLE);
        if (lastMessage.getIsSeen().equals("true")){
            holder.txtLastMess.setTextColor(Color.rgb(153,153,153));
            holder.isSeen.setBackground(context.getDrawable(R.drawable.is_seen));
        }
        else
        {holder.txtLastMess.setTextColor(Color.rgb(0,0,0) );
            holder.isSeen.setBackground(context.getDrawable(R.drawable.is_not_seen));}



    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView avatar, status, isSeen;
        public TextView lblUserFullName, txtLastMess;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            lblUserFullName = itemView.findViewById(R.id.lblUserFullName);
            avatar = itemView.findViewById(R.id.userAvatar);
            status = itemView.findViewById(R.id.status);
            isSeen = itemView.findViewById(R.id.isSeen);
            txtLastMess = itemView.findViewById(R.id.txtLastMes);
        }
    }
}
