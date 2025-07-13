package com.example.xiaoshuo.utils;

import com.example.xiaoshuo.R;
import com.example.xiaoshuo.models.Book;
import com.example.xiaoshuo.models.AudioBook;
import com.example.xiaoshuo.models.Category;
import com.example.xiaoshuo.models.Ranking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class BookDataManager {

    // 小说封面图片资源名称
    public static final String[] NOVEL_COVERS = {
            "novel_cover_1",  // 单独神在异界
            "novel_cover_2",  // 开局获得神级买卖
            "novel_cover_3",  // 武道巅峰
            "novel_cover_4",  // 绝地战龙
            "novel_cover_5",  // 王女之说
            "novel_cover_6",  // 天命妖刀人
            "novel_cover_7",  // 捡漏我觉醒了黄金瞳
            "novel_cover_8",  // 一念永恒
            "novel_cover_9",  // 万界毒尊
            "novel_cover_10", // 剑来
            "novel_cover_11", // 古风仙侠图
            "novel_cover_12", // 任生缘
            "novel_cover_13", // 情难自已
            "novel_cover_14", // 不可逆
            "novel_cover_15", // 都市小说
            "novel_cover_16", // 妖孽兵王
            "novel_cover_17", // 魔道祖师
    };

    // 生成男生频道小说数据
    public static List<Book> getMaleBooks() {
        List<Book> books = new ArrayList<>();
        books.add(new Book(UUID.randomUUID().toString(), "单独神在异界", "冥蓝妖姬", 
                "在异世界获得神级力量，主角必须面对各种挑战和敌人，成为最强者。", 
                NOVEL_COVERS[0], "玄幻", 1209));
        books.add(new Book(UUID.randomUUID().toString(), "开局获得神级买卖", "小兵樱", 
                "主角意外获得一个可以进行神级交易的系统，从此人生逆转。", 
                NOVEL_COVERS[1], "游戏", 856));
        books.add(new Book(UUID.randomUUID().toString(), "武道巅峰", "紫菜", 
                "普通青年通过不懈努力，登上武道之巅，成就传奇人生。", 
                NOVEL_COVERS[2], "武侠", 1532));
        books.add(new Book(UUID.randomUUID().toString(), "绝地战龙", "罗晨永恒", 
                "一个退伍特种兵回归都市，凭借超凡战斗力和军事素养，解决各种危机。", 
                NOVEL_COVERS[3], "都市", 763));
        books.add(new Book(UUID.randomUUID().toString(), "天命妖刀人", "血红", 
                "一把拥有神秘力量的刀与一个命运多舛的少年，共同走过的传奇之路。", 
                NOVEL_COVERS[5], "武侠", 1024));
        books.add(new Book(UUID.randomUUID().toString(), "捡漏我觉醒了黄金瞳", "布凡", 
                "主角意外获得鉴宝神眼，从此能够识别一切宝物，人生发生翻天覆地的变化。", 
                NOVEL_COVERS[6], "都市", 689));
        books.add(new Book(UUID.randomUUID().toString(), "一念永恒", "新雨哥", 
                "修道之路漫长而艰辛，唯有保持初心，方可得道成仙。", 
                NOVEL_COVERS[7], "修真", 1876));
        books.add(new Book(UUID.randomUUID().toString(), "万界毒尊", "四夜暴君", 
                "被世界抛弃的少年，凭借毒功逆天崛起，征服万界。", 
                NOVEL_COVERS[8], "玄幻", 1342));
        books.add(new Book(UUID.randomUUID().toString(), "剑来", "烽火戏诸侯", 
                "这是一个关于剑的故事，一个少年剑客的成长历程。", 
                NOVEL_COVERS[9], "仙侠", 2015));
        books.add(new Book(UUID.randomUUID().toString(), "妖孽兵王", "天涯的盐巴", 
                "退役特种兵回归都市，凭借超强实力，守护心爱之人，成就不凡人生。", 
                NOVEL_COVERS[15], "都市", 782));
        return books;
    }

    // 生成女生频道小说数据
    public static List<Book> getFemaleBooks() {
        List<Book> books = new ArrayList<>();
        books.add(new Book(UUID.randomUUID().toString(), "王女之说", "河岁岁", 
                "被算计的王女重生归来，以自己的聪明才智，扭转乾坤，成就一番霸业。", 
                NOVEL_COVERS[4], "宫廷", 645));
        books.add(new Book(UUID.randomUUID().toString(), "古风仙侠图", "流萤", 
                "一幅神秘画卷引领女主角进入仙侠世界，展开一段奇幻旅程。", 
                NOVEL_COVERS[10], "仙侠", 832));
        books.add(new Book(UUID.randomUUID().toString(), "任生缘", "安之", 
                "前世恩怨，今生相遇。命中注定的姻缘，究竟能否修成正果？", 
                NOVEL_COVERS[11], "古言", 578));
        books.add(new Book(UUID.randomUUID().toString(), "情难自已", "阿斯顿", 
                "都市白领与霸道总裁的爱恨情仇，一段扣人心弦的浪漫故事。", 
                NOVEL_COVERS[12], "现言", 432));
        books.add(new Book(UUID.randomUUID().toString(), "不可逆", "可爱多", 
                "一对青梅竹马阴差阳错地错过，多年后再相遇时，是否还能重拾旧情？", 
                NOVEL_COVERS[13], "现代", 367));
        books.add(new Book(UUID.randomUUID().toString(), "魔道祖师", "墨香铜臭", 
                "一个被误解的天才修士，死后重生，携手旧友，踏上寻找真相之路。", 
                NOVEL_COVERS[16], "耽美", 1253));
        return books;
    }

    // 生成有声书数据
    public static List<AudioBook> getAudioBooks() {
        List<AudioBook> audioBooks = new ArrayList<>();
        audioBooks.add(new AudioBook(UUID.randomUUID().toString(), "单独神在异界", "冥蓝妖姬", 
                "有声版：在异世界获得神级力量，主角必须面对各种挑战和敌人，成为最强者。", 
                NOVEL_COVERS[0], "玄幻", 80, "李谷一"));
        audioBooks.add(new AudioBook(UUID.randomUUID().toString(), "开局获得神级买卖", "小兵樱", 
                "有声版：主角意外获得一个可以进行神级交易的系统，从此人生逆转。", 
                NOVEL_COVERS[1], "游戏", 65, "刘凯"));
        audioBooks.add(new AudioBook(UUID.randomUUID().toString(), "武道巅峰", "紫菜", 
                "有声版：普通青年通过不懈努力，登上武道之巅，成就传奇人生。", 
                NOVEL_COVERS[2], "武侠", 120, "王宇"));
        audioBooks.add(new AudioBook(UUID.randomUUID().toString(), "王女之说", "河岁岁", 
                "有声版：被算计的王女重生归来，以自己的聪明才智，扭转乾坤，成就一番霸业。", 
                NOVEL_COVERS[4], "宫廷", 75, "白雪"));
        return audioBooks;
    }

    // 生成分类数据
    public static List<Category> getCategories(boolean isMale) {
        List<Category> categories = new ArrayList<>();
        // 不再添加"全部"分类
        
        if (isMale) {
            categories.add(new Category("玄幻", "玄幻小说"));
            categories.add(new Category("武侠", "武侠小说"));
            categories.add(new Category("仙侠", "仙侠小说"));
            categories.add(new Category("都市", "都市小说"));
            categories.add(new Category("游戏", "游戏小说"));
            categories.add(new Category("科幻", "科幻小说"));
        } else {
            categories.add(new Category("古言", "古代言情"));
            categories.add(new Category("现言", "现代言情"));
            categories.add(new Category("仙侠", "仙侠奇缘"));
            categories.add(new Category("宫廷", "宫廷小说"));
            categories.add(new Category("穿越", "穿越小说"));
            categories.add(new Category("耽美", "耽美小说"));
        }
        return categories;
    }

    // 获取排行榜数据
    public static List<Ranking> getRankings() {
        List<Ranking> rankings = new ArrayList<>();
        
        // 人气榜
        List<Book> popularBooks = new ArrayList<>();
        popularBooks.add(new Book(UUID.randomUUID().toString(), "一念永恒", "新雨哥", 
                "修道之路漫长而艰辛，唯有保持初心，方可得道成仙。", 
                NOVEL_COVERS[7], "修真", 1876));
        popularBooks.add(new Book(UUID.randomUUID().toString(), "剑来", "烽火戏诸侯", 
                "这是一个关于剑的故事，一个少年剑客的成长历程。", 
                NOVEL_COVERS[9], "仙侠", 2015));
        popularBooks.add(new Book(UUID.randomUUID().toString(), "魔道祖师", "墨香铜臭", 
                "一个被误解的天才修士，死后重生，携手旧友，踏上寻找真相之路。", 
                NOVEL_COVERS[16], "耽美", 1253));
        rankings.add(new Ranking("popular", "人气榜", "根据阅读量和评分综合排名", popularBooks));
        
        // 新书榜
        List<Book> newBooks = new ArrayList<>();
        newBooks.add(new Book(UUID.randomUUID().toString(), "单独神在异界", "冥蓝妖姬", 
                "在异世界获得神级力量，主角必须面对各种挑战和敌人，成为最强者。", 
                NOVEL_COVERS[0], "玄幻", 1209));
        newBooks.add(new Book(UUID.randomUUID().toString(), "开局获得神级买卖", "小兵樱", 
                "主角意外获得一个可以进行神级交易的系统，从此人生逆转。", 
                NOVEL_COVERS[1], "游戏", 856));
        newBooks.add(new Book(UUID.randomUUID().toString(), "天命妖刀人", "血红", 
                "一把拥有神秘力量的刀与一个命运多舛的少年，共同走过的传奇之路。", 
                NOVEL_COVERS[5], "武侠", 1024));
        rankings.add(new Ranking("new", "新书榜", "最新上架的热门作品", newBooks));
        
        // 完结榜
        List<Book> completedBooks = new ArrayList<>();
        completedBooks.add(new Book(UUID.randomUUID().toString(), "武道巅峰", "紫菜", 
                "普通青年通过不懈努力，登上武道之巅，成就传奇人生。", 
                NOVEL_COVERS[2], "武侠", 1532));
        completedBooks.add(new Book(UUID.randomUUID().toString(), "王女之说", "河岁岁", 
                "被算计的王女重生归来，以自己的聪明才智，扭转乾坤，成就一番霸业。", 
                NOVEL_COVERS[4], "宫廷", 645));
        completedBooks.add(new Book(UUID.randomUUID().toString(), "情难自已", "阿斯顿", 
                "都市白领与霸道总裁的爱恨情仇，一段扣人心弦的浪漫故事。", 
                NOVEL_COVERS[12], "现言", 432));
        rankings.add(new Ranking("completed", "完结榜", "已完结的精品小说", completedBooks));
        
        // 新人榜
        List<Book> newAuthorBooks = new ArrayList<>();
        newAuthorBooks.add(new Book(UUID.randomUUID().toString(), "捡漏我觉醒了黄金瞳", "布凡", 
                "主角意外获得鉴宝神眼，从此能够识别一切宝物，人生发生翻天覆地的变化。", 
                NOVEL_COVERS[6], "都市", 689));
        newAuthorBooks.add(new Book(UUID.randomUUID().toString(), "任生缘", "安之", 
                "前世恩怨，今生相遇。命中注定的姻缘，究竟能否修成正果？", 
                NOVEL_COVERS[11], "古言", 578));
        newAuthorBooks.add(new Book(UUID.randomUUID().toString(), "不可逆", "可爱多", 
                "一对青梅竹马阴差阳错地错过，多年后再相遇时，是否还能重拾旧情？", 
                NOVEL_COVERS[13], "现代", 367));
        rankings.add(new Ranking("newAuthor", "新人榜", "新晋作者的优秀作品", newAuthorBooks));
        
        return rankings;
    }
} 