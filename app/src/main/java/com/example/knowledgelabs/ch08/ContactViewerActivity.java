package com.example.knowledgelabs.ch08;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.knowledgelabs.R;

import java.util.ArrayList;
import java.util.List;

public class ContactViewerActivity extends Activity {
    private static final int REQUEST_CONTACTS = 801;
    private final List<String> contacts = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_viewer);
        ListView listView = findViewById(R.id.listSystemContacts);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contacts);
        listView.setAdapter(adapter);
        findViewById(R.id.btnLoadContacts).setOnClickListener(v -> requestOrLoad());
    }

    private void requestOrLoad() {
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            loadContacts();
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CONTACTS);
        }
    }

    private void loadContacts() {
        contacts.clear();
        String[] columns = {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };
        try (Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                columns, null, null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC")) {
            if (cursor != null) {
                int nameColumn = cursor.getColumnIndexOrThrow(columns[0]);
                int phoneColumn = cursor.getColumnIndexOrThrow(columns[1]);
                while (cursor.moveToNext()) {
                    contacts.add(cursor.getString(nameColumn) + "\n" + cursor.getString(phoneColumn));
                }
            }
        }
        if (contacts.isEmpty()) {
            contacts.add("设备中没有可读取的联系人");
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CONTACTS && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadContacts();
        } else if (requestCode == REQUEST_CONTACTS) {
            Toast.makeText(this, "未授权，无法读取联系人", Toast.LENGTH_SHORT).show();
        }
    }
}
