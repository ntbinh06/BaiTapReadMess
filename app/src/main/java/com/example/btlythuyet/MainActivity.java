package com.example.btlythuyet;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SMS = 1;
    private static final int REQUEST_CODE_CALL_LOG = 2;
    private static final int REQUEST_CODE_CONTACTS = 3;

    private RecyclerView recyclerViewMessages, recyclerViewCallLogs, recyclerViewContacts;
    private MessageAdapter messageAdapter;
    private CallLogAdapter callLogAdapter;
    private ContactAdapter contactAdapter;
    private List<String> messageList, callLogList, contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khai báo RecyclerView
        recyclerViewMessages = findViewById(R.id.recyclerView);
        recyclerViewCallLogs = findViewById(R.id.recyclerView1);
        recyclerViewContacts = findViewById(R.id.recyclerView2);

        messageList = new ArrayList<>();
        callLogList = new ArrayList<>();
        contactList = new ArrayList<>();

        messageAdapter = new MessageAdapter(messageList);
        callLogAdapter = new CallLogAdapter(callLogList);
        contactAdapter = new ContactAdapter(contactList);

        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(messageAdapter);

        recyclerViewCallLogs.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCallLogs.setAdapter(callLogAdapter);

        recyclerViewContacts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewContacts.setAdapter(contactAdapter);

        // Kiểm tra quyền và đọc tin nhắn
        checkSmsPermission();
        checkCallLogPermission();
        checkContactsPermission();
    }

    private void checkSmsPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestSmsPermission();
        } else {
            readMessages();
        }
    }

    private void requestSmsPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, REQUEST_CODE_SMS);
    }

    private void readMessages() {
        Cursor cursor = getContentResolver().query(Telephony.Sms.CONTENT_URI,
                new String[]{Telephony.Sms._ID, Telephony.Sms.BODY},
                null, null, null);

        if (cursor != null) {
            int bodyIndex = cursor.getColumnIndex(Telephony.Sms.BODY);
            if (bodyIndex >= 0) {
                messageList.clear(); // Xóa danh sách trước khi thêm mới
                while (cursor.moveToNext()) {
                    messageList.add("SMS: " + cursor.getString(bodyIndex));
                }
            }
            cursor.close();
        }
        messageAdapter.notifyDataSetChanged(); // Cập nhật adapter
    }

    private void checkCallLogPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG}, REQUEST_CODE_CALL_LOG);
        } else {
            readCallLogs();
        }
    }

    private void readCallLogs() {
        Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            int numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            int typeIndex = cursor.getColumnIndex(CallLog.Calls.TYPE);
            int dateIndex = cursor.getColumnIndex(CallLog.Calls.DATE);

            if (numberIndex >= 0 && typeIndex >= 0 && dateIndex >= 0) {
                callLogList.clear(); // Xóa danh sách trước khi thêm mới
                while (cursor.moveToNext()) {
                    String callNumber = cursor.getString(numberIndex);
                    String callType = cursor.getString(typeIndex);
                    String callDate = cursor.getString(dateIndex);

                    callLogList.add("Call: " + callNumber + " (Type: " + callType + ", Date: " + callDate + ")");
                }
            }
            cursor.close();
        }
        callLogAdapter.notifyDataSetChanged(); // Cập nhật adapter
    }

    private void checkContactsPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_CONTACTS);
        } else {
            readContacts();
        }
    }

    private void readContacts() {
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);

            if (nameIndex >= 0) {
                contactList.clear(); // Xóa danh sách trước khi thêm mới
                while (cursor.moveToNext()) {
                    String contactName = cursor.getString(nameIndex);
                    contactList.add("Contact: " + contactName);
                }
            }
            cursor.close();
        }
        contactAdapter.notifyDataSetChanged(); // Cập nhật adapter
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readMessages(); // Nếu quyền được cấp, đọc tin nhắn
            } else {
                Toast.makeText(this, "Permission denied to read SMS.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_CALL_LOG) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readCallLogs(); // Nếu quyền được cấp, đọc nhật ký cuộc gọi
            } else {
                Toast.makeText(this, "Permission denied to read Call Logs.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readContacts(); // Nếu quyền được cấp, đọc danh bạ
            } else {
                Toast.makeText(this, "Permission denied to read Contacts.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}