package com.myapplication.app.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.myapplication.app.db.StudentDbHelper;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class AddMediaActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<String> displayList; // 显示名字
    private JSONArray jsonArray; // 存完整的 JSON 数据
    private StudentDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_media); // 布局包含一个 ListView 即可

        dbHelper = new StudentDbHelper(this);
        listView = findViewById(R.id.list_view_assets_media); // 确保 xml 里有这个 id
        displayList = new ArrayList<>();

        loadAssetsData();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            try {
                JSONObject obj = jsonArray.getJSONObject(position);
                String name = obj.getString("name");
                String path = obj.getString("path");
                String author = obj.getString("author");

                dbHelper.addMedia(name, path, author);
                Toast.makeText(this, "已添加: " + name, Toast.LENGTH_SHORT).show();
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void loadAssetsData() {
        try {
            // 读取 assets/music_data.json
            InputStream is = getAssets().open("music_data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonStr = new String(buffer, StandardCharsets.UTF_8);

            jsonArray = new JSONArray(jsonStr);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                displayList.add(obj.getString("name") + " - " + obj.getString("author"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "读取 Assets 数据失败", Toast.LENGTH_SHORT).show();
        }
    }
}