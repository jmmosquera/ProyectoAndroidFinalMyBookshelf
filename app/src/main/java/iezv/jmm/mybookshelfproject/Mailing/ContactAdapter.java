package iezv.jmm.mybookshelfproject.Mailing;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import iezv.jmm.mybookshelfproject.R;

public class ContactAdapter extends RecyclerView.Adapter <ContactAdapter.MyViewHolder> {

    private List<Contact> contacts;
    private Context context;

    public ContactAdapter(List<Contact> contacts, Context context){
        this.contacts = contacts;
        this.context = context;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView nameMail;
        TextView mail;
        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            nameMail = itemView.findViewById(R.id.nombreMail);
            mail = itemView.findViewById(R.id.email);
        }
    }

    @NonNull
    @Override
    public ContactAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i){
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mail_item, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.MyViewHolder myViewHolder, int position){
        myViewHolder.nameMail.setText(contacts.get(position).getNombre());
        myViewHolder.mail.setText(contacts.get(position).getEmail());

    }

    @Override
    public int getItemCount(){
        return contacts.size();
    }
}
