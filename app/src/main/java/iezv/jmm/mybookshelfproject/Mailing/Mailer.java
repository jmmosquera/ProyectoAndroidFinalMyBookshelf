package iezv.jmm.mybookshelfproject.Mailing;

import android.content.Context;
import android.database.Cursor;

import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

public class Mailer {

    Context context;

    public Mailer(Context context){
        this.context = context;
    }



    public List<Contact> getContacts(){
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String proyection[] = null;
        String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = ? and " +
                ContactsContract.Contacts.HAS_PHONE_NUMBER + " = ? ";
        String arguments[] = new String[]{"1","1"};
        Cursor cursor = context.getContentResolver().query(uri, proyection, selection, arguments, null);
        int indexId = cursor.getColumnIndex(ContactsContract.Contacts._ID);
        int indexName = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        List<Contact> list = new ArrayList<>();
        Contact contact;
        while(cursor.moveToNext()){
            contact = new Contact();
            contact.setId(cursor.getLong(indexId));
            contact.setNombre(cursor.getString(indexName));
            list.add(contact);
        }
        return list;
    }

    public List<String> getMails(long id){
        Uri uri = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String proyection[] = null;
        String selection = ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?";
        String arguments[] = new String[]{id+""};
        String order = ContactsContract.CommonDataKinds.Email.ADDRESS;
        Cursor cursor = context.getContentResolver().query(uri, proyection, selection, arguments, order);
        int indexPh = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
        List<String> list = new ArrayList<>();
        String address;
        while(cursor.moveToNext()){
            address = cursor.getString(indexPh);
            list.add(address);
        }
        return list;
    }

}
