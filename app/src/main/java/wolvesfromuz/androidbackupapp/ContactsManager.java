package wolvesfromuz.androidbackupapp;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
       String EmailCONTENT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
       String DATA = ContactsContract.CommonDataKinds.Email.DATA;

       cursor = contentResolver.query(CONTENT_URI, null, null, null, null);

       if(cursor.getCount() > 0)
       {
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
                       String x = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                       contact.phoneNos.add(x);
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

   public void saveContacts(JSONArray jsonArray) throws JSONException
   {
       for(int index = 0; index < jsonArray.length(); index++)
       {
           ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
           JSONObject jsonObject = (JSONObject) jsonArray.get(index);
           JSONArray phones = jsonObject.getJSONArray("phoneNos");
           JSONArray emails = jsonObject.getJSONArray("emails");

           int rawContactInsertIndex = ops.size();

           ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                   .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                   .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());

           ops.add(ContentProviderOperation
                   .newInsert(ContactsContract.Data.CONTENT_URI)
                   .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,rawContactInsertIndex)
                   .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                   .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, jsonObject.get("displayName"))
                   .build());
           for(int phoneIndex = 0; phoneIndex < phones.length(); phoneIndex++)
           {
               ops.add(ContentProviderOperation
                       .newInsert(ContactsContract.Data.CONTENT_URI)
                       .withValueBackReference(
                               ContactsContract.Data.RAW_CONTACT_ID,   rawContactInsertIndex)
                       .withValue(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                       .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phones.get(phoneIndex))
                       .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build());
           }


           for(int emailIndex = 0; emailIndex < emails.length(); emailIndex++)
           {
               ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                       .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                       .withValue(ContactsContract.Data.MIMETYPE,
                               ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                       .withValue(ContactsContract.CommonDataKinds.Email.DATA, emails.get(emailIndex))
                       .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                       .build());
           }

           try
           {
               contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
           }
           catch (RemoteException e)
           {
               Log.d("ContractManager", e.getMessage());
           }
           catch (OperationApplicationException e)
           {
               Log.d("ContractManager", e.getMessage());
           }
       }
   }

   public void deleteContacts()
   {
       Cursor cur = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
               null, null, null, null);
       while (cur.moveToNext()) {
           try{
               String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
               Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
               System.out.println("The uri is " + uri.toString());
               contentResolver.delete(uri, null, null);
           }
           catch(Exception e)
           {
               System.out.println(e.getStackTrace());
           }
       }
   }
}
