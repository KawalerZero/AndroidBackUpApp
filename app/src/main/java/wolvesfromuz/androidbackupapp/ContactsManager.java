package wolvesfromuz.androidbackupapp;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Dawid Kuźmiński on 15.05.2017.
 */

public class ContactsManager
{
    public ArrayList<Contact> contacts;
    private Contact contact;
    Cursor cursor;
    int counter = 0;
    private ContentResolver contentResolver;

    public void setCursor(ContentResolver contentResolver)
    {
        this.contentResolver = contentResolver;
    }

   public void readContacts()
   {
       contacts = new ArrayList<Contact>();
       Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
       String _ID = ContactsContract.Contacts._ID;
       String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
       String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
       Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
       String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
       String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
       Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
       String EmailCONTENT_ID = ContactsContract.CommonDataKinds.Email._ID;
       String DATA = ContactsContract.CommonDataKinds.Email.DATA;

       cursor = contentResolver.query(CONTENT_URI, null, null, null, null);

       if(cursor.getCount() > 0)
       {
            counter = 0;
           while(cursor.moveToNext())
           {
               contact = new Contact();
               String contract_id = cursor.getString(cursor.getColumnIndex(_ID));
               String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));

               int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));
               if(hasPhoneNumber > 0)
               {
                   contact.displayName = name;

                   Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " =?", new String[] {contract_id}, null);
                   while (phoneCursor.moveToNext())
                   {
                       contact.phoneNos.add(phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER)));
                   }
                   phoneCursor.close();

                   Cursor emailCursor = contentResolver.query(EmailCONTENT_URI, null, EmailCONTENT_ID + "=?", new String[]{contract_id}, null);
                   while(emailCursor.moveToNext())
                   {
                       contact.emails.add(emailCursor.getString(emailCursor.getColumnIndex(DATA)));
                   }
                   emailCursor.close();
               }
               contacts.add(contact);
           }
       }
   }
}
