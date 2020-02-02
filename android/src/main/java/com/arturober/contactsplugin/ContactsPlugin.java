package com.arturober.contactsplugin;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.PluginRequestCodes;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;

@NativePlugin(
        permissions = {Manifest.permission.READ_CONTACTS},
        permissionRequestCode = ContactsPlugin.REQUEST_READ_CONTACTS
)
public class ContactsPlugin extends Plugin {
    private Context context;
    static final int REQUEST_READ_CONTACTS = 8000;

    public void load() {
        // Get singleton instance of database
        context = getContext();
    }

    @PluginMethod()
    public void getContacts(PluginCall call) {
        if(!hasPermission(Manifest.permission.READ_CONTACTS)) {
            pluginRequestPermissions(new String[]{
                    Manifest.permission.READ_CONTACTS
            }, REQUEST_READ_CONTACTS);
            saveCall(call);
        } else {
            readContacts(call);
        }
    }

    private void readContacts(PluginCall call) {
        JSArray contacts = new JSArray();

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            JSObject contact = new JSObject();
            contact.put("name", cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)));

            if(cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                Cursor pCur = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                        new String[]{cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))}, null);

                JSArray phones = new JSArray();
                while (pCur.moveToNext()) {
                    String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    phones.put(phone);
                }
                contact.put("phones", phones);
                pCur.close();
            }
            contacts.put(contact);
        }
        cursor.close();

        JSObject res = new JSObject();
        res.put("contacts", contacts);
        call.success(res);
    }

    @Override
    protected void handleRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.handleRequestPermissionsResult(requestCode, permissions, grantResults);

        PluginCall savedCall = getSavedCall();
        if (savedCall == null) {
            return;
        }

        for(int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                savedCall.error("User denied reading contacts");
                return;
            }
        }

        if (savedCall.getMethodName().equals("getContacts")) {
            readContacts(savedCall);
        } else {
            savedCall.resolve();
            savedCall.release(bridge);
        }
    }
}
