package com.example.campusguide;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RouteDetailActivity extends AppCompatActivity {
    public static final String EXTRA_ROUTE_TYPE = "route_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_detail);
        findViewById(R.id.button_back).setOnClickListener(v -> finish());

        String type = getIntent().getStringExtra(EXTRA_ROUTE_TYPE);
        if (type == null) type = "classic";
        RouteInfo route = buildRoute(type);
        ((TextView) findViewById(R.id.text_route_name)).setText(route.name);
        ((TextView) findViewById(R.id.text_route_summary)).setText(route.summary);

        LinearLayout container = findViewById(R.id.layout_route_steps);
        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < route.steps.length; i++) {
            View row = inflater.inflate(R.layout.item_route_step, container, false);
            ((TextView) row.findViewById(R.id.text_step_number)).setText(String.valueOf(i + 1));
            ((TextView) row.findViewById(R.id.text_step_name)).setText(route.steps[i]);
            ((TextView) row.findViewById(R.id.text_step_hint)).setText(route.hints[i]);
            container.addView(row);
        }

        findViewById(R.id.button_start_navigation).setOnClickListener(v ->
                Toast.makeText(this, "模拟导航已开始：请前往第 1 站“" + route.steps[0] + "”", Toast.LENGTH_LONG).show());
    }

    private RouteInfo buildRoute(String type) {
        if ("life".equals(type)) {
            return new RouteInfo("高效学习生活路线", "约 35 分钟 · 1.3 公里 · 学习生活一站体验",
                    new String[]{"创新实验楼", "中央图书馆", "第一食堂", "综合体育馆"},
                    new String[]{"参观成果展示区", "体验安静自习空间", "补充能量并短暂休息", "在运动中结束行程"});
        }
        if ("relax".equals(type)) {
            return new RouteInfo("湖畔活力路线", "约 25 分钟 · 0.9 公里 · 适合傍晚休闲",
                    new String[]{"湖心花园", "综合体育馆", "第一食堂"},
                    new String[]{"沿环湖步道慢行", "体验校园运动氛围", "品尝校园特色餐饮"});
        }
        return new RouteInfo("初识校园经典路线", "约 45 分钟 · 1.8 公里 · 新生首次到访推荐",
                new String[]{"校园南门", "校史馆", "中央图书馆", "湖心花园"},
                new String[]{"从校园主入口出发", "了解学校历史与文化", "认识主要学习空间", "在湖畔景观处结束行程"});
    }

    private static class RouteInfo {
        final String name;
        final String summary;
        final String[] steps;
        final String[] hints;

        RouteInfo(String name, String summary, String[] steps, String[] hints) {
            this.name = name;
            this.summary = summary;
            this.steps = steps;
            this.hints = hints;
        }
    }
}
