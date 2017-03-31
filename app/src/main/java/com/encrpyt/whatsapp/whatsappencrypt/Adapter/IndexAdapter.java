package com.encrpyt.whatsapp.whatsappencrypt.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.encrpyt.whatsapp.whatsappencrypt.Conversation;
import com.encrpyt.whatsapp.whatsappencrypt.Message;
import com.encrpyt.whatsapp.whatsappencrypt.R;
import com.mobapphome.mahencryptorlib.MAHEncryptor;

import java.util.ArrayList;
import java.util.List;

public class IndexAdapter extends RecyclerView.Adapter<IndexAdapter.IndexViewHolder> {
    private LayoutInflater mLayoutInflater;
    private List<Message> Index = new ArrayList<>();
    private Context con;

    public IndexAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        this.con = context;
    }

    public void setIndex(List<Message> myIndex) {
        this.Index = myIndex;
        notifyDataSetChanged();
    }

    @Override
    public IndexViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.single_index, parent, false);
        return new IndexViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IndexViewHolder holder, int position) {
        final Message message = Index.get(position);
        String Chat = message.getChat();
        String Name = message.getName();
        holder.Name.setText(Name);
        try {
            MAHEncryptor mahEncryptor = MAHEncryptor.newInstance("This is sample SecretKeyPhrase");
            Chat = mahEncryptor.decode(Chat);
            holder.Chat.setText(Chat);
        } catch (Exception e) {
            Log.e("Index Adapter", e.toString());
        }
        holder.Hold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent converse = new Intent(con, Conversation.class);
                converse.putExtra("name", message.getName());
                converse.putExtra("number", message.getNumber());
                con.startActivity(converse);
            }
        });
    }

    @Override
    public int getItemCount() {
        return Index.size();
    }

    static class IndexViewHolder extends RecyclerView.ViewHolder {

        TextView Name, Chat;
        View divider;
        LinearLayout Hold;

        IndexViewHolder(View itemView) {
            super(itemView);
            Name = (TextView) itemView.findViewById(R.id.index_name);
            Chat = (TextView) itemView.findViewById(R.id.index_chat);
            divider = itemView.findViewById(R.id.divider);
            Hold = (LinearLayout) itemView.findViewById(R.id.hold);
        }
    }
}
