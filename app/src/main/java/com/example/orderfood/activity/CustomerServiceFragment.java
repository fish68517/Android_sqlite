package com.example.orderfood.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.orderfood.R;

public class CustomerServiceFragment extends Fragment {

    private Button callButton, emailButton, chatButton, feedbackButton;
    private EditText feedbackEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_service, container, false);

        // 初始化控件
        callButton = view.findViewById(R.id.contact_call);
        emailButton = view.findViewById(R.id.contact_email);
        chatButton = view.findViewById(R.id.start_chat);
        feedbackButton = view.findViewById(R.id.submit_feedback);
        feedbackEditText = view.findViewById(R.id.feedback_edit_text);

        // 电话客服
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到拨号页面
                Intent intent = new Intent(Intent.ACTION_DIAL); // 使用 ACTION_DIAL 打开拨号界面
                intent.setData(Uri.parse("tel:" + "18828864020"));   // 设置号码
                // 检查是否有 Activity 可以处理 Intent
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    // 提示用户无拨号应用
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("联系客服")
                            .setMessage("请拨打号码：188288620107")
                            .setPositiveButton("复制号码", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("电话号码", "18828864020");
                                    clipboard.setPrimaryClip(clip);
                                    Toast.makeText(getActivity(), "号码已复制", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("关闭", null)
                            .show();
                }           // 启动拨号页面
            }
        });

        // 邮件客服
        emailButton.setOnClickListener(v -> {
            // 创建发送邮件的 Intent
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("355359166@qq.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "客户咨询");

            // 检查是否有应用可以处理邮件 Intent
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                // 如果有邮件应用，打开邮件客户端
                startActivity(Intent.createChooser(intent, "选择邮件客户端"));
            } else {
                // 如果没有邮件客户端，提示用户
                //Toast.makeText(getActivity(), "未找到邮件客户端，请安装邮件应用", Toast.LENGTH_SHORT).show();

                // 或者提供手动复制的功能
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("未找到邮件客户端")
                        .setMessage("您可以手动发送邮件至：355359166@qq.com")
                        .setPositiveButton("复制邮件地址", (dialog, which) -> {
                            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("邮件地址", "355359166@qq.com");
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(getActivity(), "邮件地址已复制", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("关闭", null)
                        .show();
            }
        });

        // 在线聊天
        chatButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "正在连接在线客服...", Toast.LENGTH_SHORT).show();
            // 可以在这里启动聊天界面
        });

        // 用户反馈
        // 在 Activity 中处理用户反馈的逻辑
        feedbackButton.setOnClickListener(v -> {
            String feedback = feedbackEditText.getText().toString().trim();
            if (feedback.isEmpty()) {
                Toast.makeText(getContext(), "请输入反馈内容", Toast.LENGTH_SHORT).show();
            } else {

                // 显示提示信息
                Toast.makeText(getContext(), "感谢您的反馈！我们将尽快处理。", Toast.LENGTH_SHORT).show();

                // 清空输入框
                feedbackEditText.setText("");
            }
        });


        return view;
    }
}
