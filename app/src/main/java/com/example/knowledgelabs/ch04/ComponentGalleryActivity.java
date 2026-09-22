package com.example.knowledgelabs.ch04;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.knowledgelabs.R;

import java.util.ArrayList;
import java.util.List;

public class ComponentGalleryActivity extends Activity {
    private final List<String> contacts = new ArrayList<>();
    private ContactAdapter adapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_component_gallery);

        EditText nameInput = findViewById(R.id.etContactName);
        progressBar = findViewById(R.id.progressGallery);
        RecyclerView recyclerView = findViewById(R.id.recyclerContacts);
        contacts.add("张老师 · 13800000001");
        contacts.add("李同学 · 13800000002");
        contacts.add("王同学 · 13800000003");
        adapter = new ContactAdapter(contacts, this::showContactDialog);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        findViewById(R.id.btnAddContact).setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                nameInput.setError("请输入联系人姓名");
                return;
            }
            contacts.add(name + " · 号码待补充");
            adapter.notifyItemInserted(contacts.size() - 1);
            progressBar.setProgress(Math.min(10, contacts.size()));
            nameInput.setText("");
            Toast.makeText(this, "已添加联系人控件项", Toast.LENGTH_SHORT).show();
        });
    }

    private void showContactDialog(String contact) {
        new AlertDialog.Builder(this)
                .setTitle("联系人详情")
                .setMessage(contact + "\n\n本页演示 RecyclerView、ImageView、布局与 AlertDialog。")
                .setPositiveButton("知道了", null)
                .show();
    }

    private interface OnContactClickListener {
        void onClick(String contact);
    }

    private static final class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.Holder> {
        private final List<String> items;
        private final OnContactClickListener listener;

        private ContactAdapter(List<String> items, OnContactClickListener listener) {
            this.items = items;
            this.listener = listener;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            String contact = items.get(position);
            holder.text.setText(contact);
            holder.itemView.setOnClickListener(v -> listener.onClick(contact));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        private static final class Holder extends RecyclerView.ViewHolder {
            private final TextView text;

            private Holder(@NonNull View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.tvContactRow);
            }
        }
    }
}
