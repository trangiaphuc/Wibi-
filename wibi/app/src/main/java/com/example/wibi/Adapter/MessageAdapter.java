package com.example.wibi.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wibi.Models.Chats;
import com.example.wibi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MESSAGE_TYPE_LEFT = 0;
    public static final int MESSAGE_TYPE_RIGHT = 1;

    private Context context;
    private List<Chats> chatsList;
    private String avatarURL;

    FirebaseUser firebaseUser;

    public MessageAdapter(Context context, List<Chats> chatsList, String avatarURL){
        this.context = context;
        this.chatsList = chatsList;
        this.avatarURL = avatarURL;
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if (viewType == MESSAGE_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_right, parent, false);
            return new ViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_left, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MessageAdapter.ViewHolder holder, int position) {

        Chats chat = chatsList.get(position);

        holder.message.setText(chat.getMessage());
        holder.timeSend.setText(chat.getDateSend());

        if (avatarURL.equals("default")){
            holder.avatar.setImageResource(R.drawable.app_icon);
        }
        else
        {
            //load avatar
            Glide.with(context).load(avatarURL).into(holder.avatar);
        }

        holder.message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(holder.timeSend.getVisibility() == View.GONE)
                {holder.timeSend.setVisibility(View.VISIBLE);}
                else if (holder.timeSend.getVisibility() == View.VISIBLE)
                {holder.timeSend.setVisibility(View.GONE);}

                if (holder.txtSeen.getVisibility() == View.GONE){

                    if (position == chatsList.size()-1){
                        if (chat.getIsSeen().equals("true")){
                                holder.txtSeen.setText("Seen");
                                System.out.println("iseen true");
                            }
                            else holder.txtSeen.setText("Delivered");
                            System.out.println("isSennn"+chat.getIsSeen());
                            holder.txtSeen.setVisibility(View.VISIBLE);}
                    else
                        holder.txtSeen.setVisibility(View.VISIBLE);
                }
                else if (holder.txtSeen.getVisibility() == View.VISIBLE){
                    holder.txtSeen.setVisibility(View.GONE); }
            }
        });

    }

    @Override
    public int getItemCount() {
        return chatsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView message, timeSend, txtSeen;
        CircleImageView avatar;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            message= itemView.findViewById(R.id.message_show);
            avatar = itemView.findViewById(R.id.avatar);
            timeSend = itemView.findViewById(R.id.timeSend);
            txtSeen = itemView.findViewById(R.id.txtSeen);

        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(chatsList.get(position).getSender().equals(firebaseUser.getUid()))
            return MESSAGE_TYPE_RIGHT;
        else return MESSAGE_TYPE_LEFT;
    }
}
