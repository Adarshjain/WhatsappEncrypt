package com.encrpyt.whatsapp.whatsappencrypt;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobapphome.mahencryptorlib.MAHEncryptor;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolderChats> {
    private LayoutInflater mLayoutInflater;
    private List<Message> MyMessages = new ArrayList<>();

    ChatAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
    }

    void setMyMessages(List<Message> myMessages) {
        this.MyMessages = myMessages;
        notifyItemRangeChanged(0, myMessages.size());
    }

    @Override
    public ViewHolderChats onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.single_chat_item, parent, false);
        return new ViewHolderChats(view);
    }

    @Override
    public void onBindViewHolder(ViewHolderChats holder, int position) {
        Message message = MyMessages.get(position);
        String Chat = message.getChat();
        String Direction = message.getDirection();
        if (Direction.equals("r")) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.END);
            params.gravity = Gravity.END;
            holder.Chat.setLayoutParams(params);
        } else if (Direction.equals("l")) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.START);
            params.gravity = Gravity.START;
            holder.Chat.setLayoutParams(params);
        }
        try {
            MAHEncryptor mahEncryptor = MAHEncryptor.newInstance("This is sample SecretKeyPhrase");
            Chat = mahEncryptor.decode(Chat);
            holder.Chat.setText(Chat);
        } catch (Exception e) {
            Log.e("ChatAdapter", e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return MyMessages.size();
    }

    static class ViewHolderChats extends RecyclerView.ViewHolder {

        TextView Chat;

        ViewHolderChats(View itemView) {
            super(itemView);
            Chat = (TextView) itemView.findViewById(R.id.schat);
        }
    }
}
