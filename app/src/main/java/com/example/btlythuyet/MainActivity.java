package com.example.btlythuyet;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Telephony;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btlythuyet.MessageAdapter;
import com.example.btlythuyet.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<String> messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khai báo RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        // Kiểm tra quyền và đọc tin nhắn
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            // Nếu chưa có quyền, yêu cầu quyền
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS)) {
                // Hiển thị thông báo giải thích cho người dùng
                new AlertDialog.Builder(this)
                        .setTitle("Permission Required")
                        .setMessage("This app needs SMS permission to read your messages.")
                        .setPositiveButton("OK", (dialog, which) -> {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS}, 1);
                        })
                        .setNegativeButton("Cancel", null)
                        .create()
                        .show();
            } else {
                // Nếu không nên hiển thị giải thích, chỉ cần yêu cầu quyền
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 1);
            }
        } else {
            readMessages(); // Nếu đã có quyền, đọc tin nhắn ngay
        }
    }

    private void readMessages() {
        Cursor cursor = getContentResolver().query(Telephony.Sms.CONTENT_URI,
                new String[]{Telephony.Sms._ID, Telephony.Sms.BODY},
                null, null, null);

        if (cursor != null) {
            int bodyIndex = cursor.getColumnIndex(Telephony.Sms.BODY);
            messageList.clear(); // Xóa danh sách trước khi thêm mới
            while (cursor.moveToNext()) {
                messageList.add(cursor.getString(bodyIndex));
            }
            cursor.close();
        }
        messageAdapter.notifyDataSetChanged(); // Cập nhật adapter
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readMessages(); // Nếu quyền được cấp, đọc tin nhắn
            } else {
                // Nếu quyền bị từ chối, thông báo cho người dùng
                Toast.makeText(this, "Permission denied to read SMS.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}