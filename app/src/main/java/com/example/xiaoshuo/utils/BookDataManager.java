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
            "novel_cover_1",  // 星河巨舰
            "novel_cover_2",  // 都市修仙奇才
            "novel_cover_3",  // 历史的尘埃
            "novel_cover_4",  // 最终防线
            "novel_cover_5",  // 神级铁匠
            "novel_cover_6",  // 诡秘档案
            "novel_cover_7",  // 全球进化
            "novel_cover_8",  // 符文之地
            "novel_cover_9",  // 虚拟神明
            "novel_cover_10", // 深宫谍影
            "novel_cover_11", // 星光下的约定
            "novel_cover_12", // 医手遮天
            "novel_cover_13", // 律政佳人
            "novel_cover_14", // 时间的旅行者
            "novel_cover_15", // 厨神之路
            "novel_cover_16", // 探险笔记
            "novel_cover_17", // 赛博之城
    };

    // 生成男生频道小说数据
    public static List<Book> getMaleBooks() {
        List<Book> books = new ArrayList<>();
        books.add(new Book(UUID.randomUUID().toString(), "星河巨舰", "银河之心",
                "在遥远的未来，人类踏入星际时代。主角从一艘废弃的古老星舰开始，逐步建立起自己的银河舰队，探索未知宇宙的奥秘。",
                NOVEL_COVERS[0], "星际科幻", 2105));
        books.add(new Book(UUID.randomUUID().toString(), "都市修仙奇才", "一叶知秋",
                "灵气复苏的现代都市，一位普通青年偶得修仙法门，从此在钢筋水泥的丛林中行侠仗义，守护一方安宁。",
                NOVEL_COVERS[1], "都市异能", 1850));
        books.add(new Book(UUID.randomUUID().toString(), "历史的尘埃", "时光旅人",
                "一位历史系高材生意外穿越到风雨飘摇的古代王朝，凭借超越时代的知识在乱世中挣扎求生，并试图改变历史的洪流。",
                NOVEL_COVERS[2], "历史穿越", 1532));
        books.add(new Book(UUID.randomUUID().toString(), "最终防线", "末日号角",
                "末日病毒爆发，丧尸横行，文明岌岌可危。主角带领一小队幸存者，建立最后的基地，为人类的延续而战。",
                NOVEL_COVERS[3], "末日生存", 988));
        books.add(new Book(UUID.randomUUID().toString(), "神级铁匠", "烈焰之锤",
                "一个游戏宅男穿越到剑与魔法的异世界，获得了打造神器的逆天能力，传说中的武器都出自他手。",
                NOVEL_COVERS[4], "奇幻冒险", 1342));
        books.add(new Book(UUID.randomUUID().toString(), "诡秘档案", "黑夜之瞳",
                "一座城市中接连发生无法用科学解释的怪事，一个专门处理超自然事件的秘密机构开始行动，揭开隐藏在现实世界下的另一面。",
                NOVEL_COVERS[5], "悬疑灵异", 789));
        books.add(new Book(UUID.randomUUID().toString(), "全球进化", "进化之光",
                "一场来自外太空的射线风暴，导致地球生物开始疯狂进化，人类不再是食物链的顶端。主角在危机中觉醒了特殊能力。",
                NOVEL_COVERS[6], "科幻末世", 1120));
        books.add(new Book(UUID.randomUUID().toString(), "符文之地", "远古巫师",
                "在一个以符文魔法为核心的世界里，一个被认为是“无魔者”的少年，走上了一条与众不同的强者之路。",
                NOVEL_COVERS[7], "西幻史诗", 1680));
        books.add(new Book(UUID.randomUUID().toString(), "虚拟神明", "代码意志",
                "在完全潜行的虚拟现实游戏中，主角发现自己可以修改游戏代码，他成为了游戏世界中唯一的“神”。",
                NOVEL_COVERS[8], "虚拟现实", 1450));
        books.add(new Book(UUID.randomUUID().toString(), "赛博之城", "霓虹魅影",
                "在科技高度发达的未来城市，机械义体、人工智能普及，主角作为一名侦探，在错综复杂的势力间游走，揭露惊天阴谋。",
                NOVEL_COVERS[16], "赛博朋克", 955));
        return books;
    }

    // 生成女生频道小说数据
    public static List<Book> getFemaleBooks() {
        List<Book> books = new ArrayList<>();
        books.add(new Book(UUID.randomUUID().toString(), "深宫谍影", "月下长安",
                "她本是前朝遗孤，为复仇进入深宫，在权力的漩涡中步步为营，如履薄冰，最终影响了整个王朝的命运。",
                NOVEL_COVERS[9], "宫廷权谋", 920));
        books.add(new Book(UUID.randomUUID().toString(), "星光下的约定", "夏夜流萤",
                "现代娱乐圈背景，一位努力的新人演员与才华横溢的顶流明星之间，从误会到相知相爱，共同成长的浪漫故事。",
                NOVEL_COVERS[10], "都市言情", 732));
        books.add(new Book(UUID.randomUUID().toString(), "医手遮天", "杏林春暖",
                "天才女医师穿越古代，凭借精湛的现代医术救死扶伤，名动京城，并卷入一场场错综复杂的宫廷斗争。",
                NOVEL_COVERS[11], "穿越重生", 1150));
        books.add(new Book(UUID.randomUUID().toString(), "律政佳人", "正义之声",
                "一位初出茅庐的女律师，在职场中不断成长，坚守正义，处理各种棘手案件，最终成为业界精英的故事。",
                NOVEL_COVERS[12], "职场励志", 680));
        books.add(new Book(UUID.randomUUID().toString(), "时间的旅行者", "光阴诗人",
                "女主角意外获得穿越时空的能力，她在不同的历史片段中穿梭，遇见了不同的人，经历了不同的事，寻找时间的真谛。",
                NOVEL_COVERS[13], "幻想罗曼", 543));
        books.add(new Book(UUID.randomUUID().toString(), "厨神之路", "人间烟火",
                "一位对美食充满热爱的少女，通过不断努力学习，参加各种烹饪大赛，最终成长为一代厨神的励志故事。",
                NOVEL_COVERS[14], "现代美食", 610));
        return books;
    }

    // 生成有声书数据
    public static List<AudioBook> getAudioBooks() {
        List<AudioBook> audioBooks = new ArrayList<>();
        audioBooks.add(new AudioBook(UUID.randomUUID().toString(), "星河巨舰", "银河之心",
                "有声版：在遥远的未来，人类踏入星际时代。主角从一艘废弃的古老星舰开始...",
                NOVEL_COVERS[0], "星际科幻", 150, "张三"));
        audioBooks.add(new AudioBook(UUID.randomUUID().toString(), "都市修仙奇才", "一叶知秋",
                "有声版：灵气复苏的现代都市，一位普通青年偶得修仙法门...",
                NOVEL_COVERS[1], "都市异能", 125, "李四"));
        audioBooks.add(new AudioBook(UUID.randomUUID().toString(), "深宫谍影", "月下长安",
                "有声版：她本是前朝遗孤，为复仇进入深宫，在权力的漩涡中步步为营...",
                NOVEL_COVERS[9], "宫廷权谋", 98, "王五"));
        audioBooks.add(new AudioBook(UUID.randomUUID().toString(), "医手遮天", "杏林春暖",
                "有声版：天才女医师穿越古代，凭借精湛的现代医术救死扶伤...",
                NOVEL_COVERS[11], "穿越重生", 110, "赵六"));
        return audioBooks;
    }

    // 生成分类数据
    public static List<Category> getCategories(boolean isMale) {
        List<Category> categories = new ArrayList<>();
        if (isMale) {
            categories.add(new Category("星际科幻", "未来宇宙的探索"));
            categories.add(new Category("都市异能", "现代都市的超凡力量"));
            categories.add(new Category("历史穿越", "回到过去改变历史"));
            categories.add(new Category("末日生存", "在绝境中求生"));
            categories.add(new Category("奇幻冒险", "剑与魔法的世界"));
            categories.add(new Category("赛博朋克", "高科技低生活"));
        } else {
            categories.add(new Category("宫廷权谋", "深宫中的爱恨情仇"));
            categories.add(new Category("都市言情", "现代都市的浪漫爱恋"));
            categories.add(new Category("穿越重生", "回到过去逆转人生"));
            categories.add(new Category("职场励志", "女性在职场的奋斗"));
            categories.add(new Category("幻想罗曼", "跨越时空的爱恋"));
            categories.add(new Category("现代美食", "美食与人生的故事"));
        }
        return categories;
    }

    // 获取排行榜数据
    public static List<Ranking> getRankings() {
        List<Ranking> rankings = new ArrayList<>();

        // 人气榜
        List<Book> popularBooks = new ArrayList<>();
        popularBooks.add(new Book(UUID.randomUUID().toString(), "星河巨舰", "银河之心",
                "在遥远的未来，人类踏入星际时代。主角从一艘废弃的古老星舰开始...",
                NOVEL_COVERS[0], "星际科幻", 2105));
        popularBooks.add(new Book(UUID.randomUUID().toString(), "医手遮天", "杏林春暖",
                "天才女医师穿越古代，凭借精湛的现代医术救死扶伤...",
                NOVEL_COVERS[11], "穿越重生", 1150));
        popularBooks.add(new Book(UUID.randomUUID().toString(), "赛博之城", "霓虹魅影",
                "在科技高度发达的未来城市，机械义体、人工智能普及...",
                NOVEL_COVERS[16], "赛博朋克", 955));
        rankings.add(new Ranking("popular", "人气榜", "根据阅读热度和评分综合排名", popularBooks));

        // 新书榜
        List<Book> newBooks = new ArrayList<>();
        newBooks.add(new Book(UUID.randomUUID().toString(), "律政佳人", "正义之声",
                "一位初出茅庐的女律师，在职场中不断成长，坚守正义...",
                NOVEL_COVERS[12], "职场励志", 680));
        newBooks.add(new Book(UUID.randomUUID().toString(), "诡秘档案", "黑夜之瞳",
                "一座城市中接连发生无法用科学解释的怪事，一个专门处理超自然事件的秘密机构开始行动...",
                NOVEL_COVERS[5], "悬疑灵异", 789));
        newBooks.add(new Book(UUID.randomUUID().toString(), "厨神之路", "人间烟火",
                "一位对美食充满热爱的少女，通过不断努力学习，参加各种烹饪大赛...",
                NOVEL_COVERS[14], "现代美食", 610));
        rankings.add(new Ranking("new", "新书榜", "最新上架的潜力作品", newBooks));

        // 完结榜
        List<Book> completedBooks = new ArrayList<>();
        completedBooks.add(new Book(UUID.randomUUID().toString(), "历史的尘埃", "时光旅人",
                "一位历史系高材生意外穿越到风雨飘摇的古代王朝...",
                NOVEL_COVERS[2], "历史穿越", 1532));
        completedBooks.add(new Book(UUID.randomUUID().toString(), "最终防线", "末日号角",
                "末日病毒爆发，丧尸横行，文明岌岌可危。主角带领一小队幸存者...",
                NOVEL_COVERS[3], "末日生存", 988));
        completedBooks.add(new Book(UUID.randomUUID().toString(), "星光下的约定", "夏夜流萤",
                "现代娱乐圈背景，一位努力的新人演员与才华横溢的顶流明星之间...",
                NOVEL_COVERS[10], "都市言情", 732));
        rankings.add(new Ranking("completed", "完结榜", "已完结的精品小说", completedBooks));

        // 热销榜
        List<Book> newAuthorBooks = new ArrayList<>();
        newAuthorBooks.add(new Book(UUID.randomUUID().toString(), "都市修仙奇才", "一叶知秋",
                "灵气复苏的现代都市，一位普通青年偶得修仙法门...",
                NOVEL_COVERS[1], "都市异能", 1850));
        newAuthorBooks.add(new Book(UUID.randomUUID().toString(), "深宫谍影", "月下长安",
                "她本是前朝遗孤，为复仇进入深宫，在权力的漩涡中步步为营...",
                NOVEL_COVERS[9], "宫廷权谋", 920));
        newAuthorBooks.add(new Book(UUID.randomUUID().toString(), "神级铁匠", "烈焰之锤",
                "一个游戏宅男穿越到剑与魔法的异世界，获得了打造神器的逆天能力...",
                NOVEL_COVERS[4], "奇幻冒险", 1342));
        rankings.add(new Ranking("hot", "热销榜", "当前最受欢迎的作品", newAuthorBooks));

        return rankings;
    }
} 