package com.encrpyt.whatsapp.whatsappencrypt.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.encrpyt.whatsapp.whatsappencrypt.Conversation;
import com.encrpyt.whatsapp.whatsappencrypt.Message;
import com.encrpyt.whatsapp.whatsappencrypt.R;

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
//            MAHEncryptor mahEncryptor = MAHEncryptor.newInstance("This is sample SecretKeyPhrase");
//            Chat = mahEncryptor.decode(Chat);
            Chat = Chat.replaceAll("\n"," ");
            holder.Chat.setText(Chat);
        } catch (Exception e) {
            Log.e("Index Adapter", e.toString());
        }
        if (Integer.parseInt(message.getCount()) > 0) {
            holder.CountParent.setVisibility(View.VISIBLE);
            holder.Count.setText(message.getCount());
        } else holder.CountParent.setVisibility(View.GONE);
        holder.Hold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent converse = new Intent(con, Conversation.class);
                converse.putExtra("name", message.getName());
                converse.putExtra("number", message.getNumber());
                converse.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                con.startActivity(converse);
            }
        });
    }

    @Override
    public int getItemCount() {
        return Index.size();
    }

    static class IndexViewHolder extends RecyclerView.ViewHolder {

        TextView Name, Chat, Count;
        View divider;
        RelativeLayout Hold,CountParent;

        IndexViewHolder(View itemView) {
            super(itemView);
            Name = (TextView) itemView.findViewById(R.id.index_name);
            Chat = (TextView) itemView.findViewById(R.id.index_chat);
            Count = (TextView) itemView.findViewById(R.id.count);
            divider = itemView.findViewById(R.id.divider);
            Hold = (RelativeLayout) itemView.findViewById(R.id.hold);
            CountParent = (RelativeLayout) itemView.findViewById(R.id.count_parent);
        }
    }
}
