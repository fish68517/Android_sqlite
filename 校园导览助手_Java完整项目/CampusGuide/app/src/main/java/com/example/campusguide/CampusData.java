package com.example.campusguide;

import com.example.campusguide.model.Place;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CampusData {
    private CampusData() { }

    public static List<Place> getPlaces() {
        return new ArrayList<>(Arrays.asList(
                new Place(1, "中央图书馆", "学习空间", "馆藏丰富，设有安静自习区与研讨室",
                        "中央图书馆是校园的知识中心，共设六层阅览空间。馆内提供纸质图书借阅、电子资源检索、团队研讨室、24 小时自习区等服务。高峰时段建议提前预约座位。",
                        "周一至周日 07:30—22:30", "距当前位置约 320 米", R.drawable.place_library),
                new Place(2, "校史馆", "文化展馆", "了解学校发展历程与代表性科研成果",
                        "校史馆通过历史照片、实物档案和互动展项展示学校的发展历程。这里适合作为新生入学教育、校友返校和访客参观的第一站。团体参观可提前联系讲解服务。",
                        "周二至周日 09:00—17:00", "距当前位置约 480 米", R.drawable.place_museum),
                new Place(3, "第一食堂", "餐饮服务", "品类齐全，支持校园卡与移动支付",
                        "第一食堂提供地方风味、营养套餐、面食和轻食等多个档口。一层适合快速用餐，二层设有更宽敞的就餐区。午餐高峰为 11:40—12:30，建议错峰前往。",
                        "每日 06:30—21:00", "距当前位置约 260 米", R.drawable.place_canteen),
                new Place(4, "综合体育馆", "运动健身", "篮球、羽毛球、游泳等综合运动空间",
                        "综合体育馆包含篮球场、羽毛球场、健身房和游泳馆。部分场地面向师生免费开放，热门时段需要在校内系统预约。入馆请穿着适合运动的鞋服。",
                        "每日 08:00—21:30", "距当前位置约 760 米", R.drawable.place_stadium),
                new Place(5, "湖心花园", "校园景观", "湖畔步道与休闲亭组成的校园热门景观",
                        "湖心花园位于校园中轴线东侧，环湖步道绿树成荫，设有休息座椅和观景亭。春季花期与傍晚时分景色尤佳，也是校园摄影和休闲散步的热门地点。",
                        "全天开放", "距当前位置约 540 米", R.drawable.place_lake),
                new Place(6, "创新实验楼", "教学科研", "开放实验室、创客空间与学术报告厅",
                        "创新实验楼聚集了多个校级实验平台和大学生创客空间。一层设有成果展示区与学术报告厅，高楼层实验区域需刷卡进入。访客请先在服务台登记。",
                        "工作日 08:00—21:00", "距当前位置约 620 米", R.drawable.place_lab)
        ));
    }

    public static Place findPlace(int id) {
        for (Place place : getPlaces()) {
            if (place.getId() == id) return place;
        }
        return getPlaces().get(0);
    }
}
