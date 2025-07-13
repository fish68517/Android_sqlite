package com.example.xiaoshuo.utils;

import com.example.xiaoshuo.models.Chapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChapterDataManager {
    
    private static final Map<String, List<Chapter>> bookChaptersMap = new HashMap<>();
    
    // 初始化章节数据
    static {
        // 星河巨舰
        List<Chapter> xingheChapters = new ArrayList<>();
        xingheChapters.add(new Chapter(1, 0, "第一章：废舰重生",
                "在宇宙的垃圾场中，主角发现了一艘被遗弃的古老星舰。当他踏入舰桥时，星舰的古老AI意外激活，将他认作新的舰长。一段星河史诗就此拉开序幕。", 1, false));
        xingheChapters.add(new Chapter(2, 0, "第二章：第一次跃迁",
                "修复了基本功能后，主角启动了星舰的跃迁引擎。陌生的星域展现在眼前，但也立刻遭遇了资源短缺的危机。他们必须在一个荒芜的星球上寻找补给。", 2, false));
        xingheChapters.add(new Chapter(3, 0, "第三章：星际海盗",
                "在补充资源时，他们遭遇了臭名昭著的星际海盗团。凭借着古老星舰出人意料的性能和主角的智谋，他们险之又险地击退了敌人，并缴获了第一批战利品。", 3, false));
        xingheChapters.add(new Chapter(4, 0, "第四章：贸易与冲突",
                "抵达一个繁华的贸易站，主角试图用战利品换取升级部件。然而，海盗的后台——一个强大的商业集团找上门来，他们被迫卷入了一场商业战争。", 4, false));
        xingheChapters.add(new Chapter(5, 0, "第五章：舰队的雏形",
                "在商业战争中，主角结识了更多志同道合的伙伴，并解救了一些被奴役的舰船。他的麾下第一次有了多艘星舰，一支新生舰队的雏形开始形成。", 5, false));
        bookChaptersMap.put("星河巨舰", xingheChapters);

        // 都市修仙奇才
        List<Chapter> dushiChapters = new ArrayList<>();
        dushiChapters.add(new Chapter(1, 0, "第一章：灵气复苏",
                "城市中，无人察觉的角落，古老的灵气开始悄然复苏。主角只是一个普通的上班族，却在一次加班的深夜，感受到了身体的异样。", 1, false));
        dushiChapters.add(new Chapter(2, 0, "第二章：公园奇遇",
                "他在公园晨练时，遇到一位神秘的老人，获赠一本残破的古籍。按照古籍上的吐纳之法修炼，他竟真的引气入体，踏上了修仙之路。", 2, false));
        dushiChapters.add(new Chapter(3, 0, "第三章：初试锋芒",
                "在一次地铁骚乱中，主角牛刀小试，用刚学会的法术制止了歹徒，却也引起了城市中一个秘密组织的注意。", 3, false));
        dushiChapters.add(new Chapter(4, 0, "第四章：都市妖影",
                "他发现，随着灵气复苏，一些潜伏在人类社会的妖物也开始活跃。他在组织的引导下，开始处理这些超自然事件，守护城市的安宁。", 4, false));
        dushiChapters.add(new Chapter(5, 0, "第五章：守护者联盟",
                "随着遇到的同道中人越来越多，主角意识到单打独斗的局限性。他倡议成立了一个由修仙者组成的联盟，共同应对日益增多的威胁。", 5, false));
        bookChaptersMap.put("都市修仙奇才", dushiChapters);

        // 历史的尘埃
        List<Chapter> lishiChapters = new ArrayList<>();
        lishiChapters.add(new Chapter(1, 0, "第一章：陌生的王朝",
                "一声惊雷，历史系高材生发现自己身处一个从未在史书中记载过的王朝。面对陌生的环境和语言，他必须尽快适应，才能生存下去。", 1, false));
        lishiChapters.add(new Chapter(2, 0, "第二章：小试牛刀",
                "凭借对历史发展规律的理解和一些现代知识，他帮助一个小村庄解决了水源问题，被村民奉为智者，获得了初步的立足之地。", 2, false));
        lishiChapters.add(new Chapter(3, 0, "第三章：乱世的开端",
                "王朝内部腐败，边境战事吃紧，农民起义此起彼伏。他意识到，一个大乱世即将来临，自己必须在乱世中找到一条出路。", 3, false));
        lishiChapters.add(new Chapter(4, 0, "第四章：辅佐明主",
                "在颠沛流离中，他遇到了一位胸怀大志、爱民如子的皇子。他决定用自己的知识辅佐这位皇子，希望能为这个时代带来一线光明。", 4, false));
        lishiChapters.add(new Chapter(5, 0, "第五章：改变历史",
                "在他的帮助下，皇子势力日益壮大。他引入了许多先进的制度和技术，深刻地影响了历史的走向。但他也在思考，这样的改变究竟是好是坏。", 5, false));
        bookChaptersMap.put("历史的尘埃", lishiChapters);

        // 最终防线
        List<Chapter> zuizhongChapters = new ArrayList<>();
        zuizhongChapters.add(new Chapter(1, 0, "第一章：末日降临",
                "一种未知的病毒席卷全球，城市沦陷，秩序崩溃。主角在混乱中幸存下来，带着家人艰难地逃离了满是丧尸的城市。", 1, false));
        zuizhongChapters.add(new Chapter(2, 0, "第二章：寻找避难所",
                "他们在荒野中流浪，寻找传说中的幸存者基地。一路上，他们不仅要面对丧尸的威胁，还要提防人性的险恶。", 2, false));
        zuizhongChapters.add(new Chapter(3, 0, "第三章：建立基地",
                "历经艰辛，他们找到了一个废弃的工厂，并决定以此为据点，建立一个属于自己的避难所。他们加固防御，搜寻物资，吸引了更多幸存者加入。", 3, false));
        zuizhongChapters.add(new Chapter(4, 0, "第四章：尸潮来袭",
                "他们的基地被庞大的尸潮发现。一场惨烈的守卫战就此打响，每个人都为了生存而拼尽全力。在战斗中，主角的领导才能逐渐显现。", 4, false));
        zuizhongChapters.add(new Chapter(5, 0, "第五章：希望的火种",
                "成功抵御尸潮后，基地声名远扬。他们开始尝试恢复农业生产，建立新的社会秩序。虽然前路漫漫，但他们成为了末世中延续文明的希望火种。", 5, false));
        bookChaptersMap.put("最终防线", zuizhongChapters);

        // 神级铁匠
        List<Chapter> shenjiChapters = new ArrayList<>();
        shenjiChapters.add(new Chapter(1, 0, "第一章：异界与锤子",
                "主角醒来发现自己身处一个剑与魔法的世界，脑海里多了一个“神级锻造系统”。他捡起路边的锤子，敲打了第一块铁矿石。", 1, false));
        shenjiChapters.add(new Chapter(2, 0, "第二章：第一件作品",
                "他在一个新手村的铁匠铺当学徒，系统发布的第一个任务是打造一把完美的匕首。当他将成品交给委托人时，对方的惊愕表情让他意识到自己能力的强大。", 2, false));
        shenjiChapters.add(new Chapter(3, 0, "第三章：声名鹊起",
                "他打造的武器因其卓越的品质而声名远扬。无论是冒险者还是王国骑士，都渴望能拥有一件他打造的装备。订单源源不断，他也因此积累了第一桶金。", 3, false));
        shenjiChapters.add(new Chapter(4, 0, "第四章：矮人王国的挑战",
                "他的名声传到了以锻造闻名的矮人王国。矮人王向他发起了锻造挑战，赌注是传说中的“火焰之心”。这是一场关乎荣誉的对决。", 4, false));
        shenjiChapters.add(new Chapter(5, 0, "第五章：神兵出世",
                "在与矮人王的对决中，主角倾尽全力，最终打造出了一把震古烁今的神器。神器出世之时，天地变色。他不仅赢得了荣誉，也成为了这个世界公认的“神匠”。", 5, false));
        bookChaptersMap.put("神级铁匠", shenjiChapters);

        // 深宫谍影
        List<Chapter> shengongChapters = new ArrayList<>();
        shengongChapters.add(new Chapter(1, 0, "第一章：以宫女之名",
                "她怀着血海深仇，隐姓埋名进入皇宫，成为一名最卑微的宫女。她必须小心翼翼，隐藏自己的才华和恨意，等待复仇的时机。", 1, false));
        shengongChapters.add(new Chapter(2, 0, "第二章：初遇太子",
                "在一次宫廷宴会上，她巧妙地化解了一场针对太子的阴谋，引起了太子的注意。太子欣赏她的聪慧，将她调到身边伺候，却不知她接近自己的真实目的。", 2, false));
        shengongChapters.add(new Chapter(3, 0, "第三章：权力的棋子",
                "她游走在太子与各大权贵之间，利用他们之间的矛盾，搜集仇人的罪证。她时而是温顺的宫女，时而是传递情报的密探，每一步都如履薄冰。", 3, false));
        shengongChapters.add(new Chapter(4, 0, "第四章：身份危机",
                "她的一个无心之举，险些暴露了自己的真实身份。在太子和仇家的双重怀疑下，她凭借惊人的胆识和智谋，再次化险为夷，并成功将祸水东引。", 4, false));
        shengongChapters.add(new Chapter(5, 0, "第五章：复仇的抉择",
                "当时机成熟，她将所有证据呈现在皇帝面前。仇人被绳之以法，大仇得报。但她也发现，自己对那位被她利用的太子，产生了复杂的情感。", 5, false));
        bookChaptersMap.put("深宫谍影", shengongChapters);

        // 医手遮天
        List<Chapter> yishouChapters = new ArrayList<>();
        yishouChapters.add(new Chapter(1, 0, "第一章：穿越成弃妃",
                "现代天才外科医生，一朝穿越，竟成了不受宠的王妃。面对冷漠的王爷和充满敌意的侧妃，她决定用自己的医术，在这个时代活出尊严。", 1, false));
        yishouChapters.add(new Chapter(2, 0, "第二章：瘟疫爆发",
                "京城附近爆发瘟疫，人心惶惶。她不顾王府阻拦，主动请缨前往疫区。凭借现代医学知识，她成功研制出药方，控制了疫情，赢得了百姓的爱戴。", 2, false));
        yishouChapters.add(new Chapter(3, 0, "第三章：王爷的改观",
                "她的才华和仁心，让冷漠的王爷对她刮目相看。在共同经历了生死考验后，王爷开始重新审视这位他从未放在眼里的王妃。", 3, false));
        yishouChapters.add(new Chapter(4, 0, "第四章：宫廷暗斗",
                "她的名声传到宫中，皇后请她为皇子治病。然而，皇子的病却牵扯出一桩陈年宫廷秘案。她被卷入其中，处境危险。", 4, false));
        yishouChapters.add(new Chapter(5, 0, "第五章：医者仁心",
                "在王爷的帮助下，她揭开了宫廷秘案的真相。她不仅治好了皇子的病，也用医术和智慧，赢得了所有人的尊重。她决定开办医馆，将自己的医术传授给更多人。", 5, false));
        bookChaptersMap.put("医手遮天", yishouChapters);

        // 赛博之城
        List<Chapter> saiboChapters = new ArrayList<>();
        saiboChapters.add(new Chapter(1, 0, "第一章：霓虹下的阴影",
                "在2077年的夜之城，主角是一名私家侦探。他接受了一个寻常的寻人委托，却没想到这起案件背后，隐藏着巨大的阴谋。", 1, false));
        saiboChapters.add(new Chapter(2, 0, "第二章：数据幽灵",
                "调查过程中，他发现目标人物似乎与一个传说中的黑客“数据幽灵”有关。为了追查线索，他不得不潜入守卫森严的公司网络。", 2, false));
        saiboChapters.add(new Chapter(3, 0, "第三章：公司的獠牙",
                "他的行为触动了巨型企业“荒坂”的利益。荒坂派出了精锐的赛博格杀手追杀他，城市中的追逐战一触即发。", 3, false));
        saiboChapters.add(new Chapter(4, 0, "第四章：意识上传",
                "在一位神秘线人的帮助下，他得知目标人物并非失踪，而是参与了一项危险的“意识上传”实验。这项技术一旦成功，将彻底改变人类的定义。", 4, false));
        saiboChapters.add(new Chapter(5, 0, "第五章：攻入核心",
                "为了阻止荒坂的阴谋，主角决定与反抗组织合作，攻入荒坂塔的核心数据库。在虚拟与现实的交错中，他将面对最终的抉择。", 5, false));
        bookChaptersMap.put("赛博之城", saiboChapters);
    }
    
    /**
     * 根据书名获取章节列表
     * @param bookTitle 书名
     * @return 章节列表
     */
    public static List<Chapter> getChaptersByBookTitle(String bookTitle) {
        return bookChaptersMap.getOrDefault(bookTitle, new ArrayList<>());
    }
    
    /**
     * 根据书名和章节索引获取章节
     * @param bookTitle 书名
     * @param chapterIndex 章节索引
     * @return 章节对象
     */
    public static Chapter getChapter(String bookTitle, int chapterIndex) {
        List<Chapter> chapters = getChaptersByBookTitle(bookTitle);
        if (chapters.isEmpty() || chapterIndex < 0 || chapterIndex >= chapters.size()) {
            return null;
        }
        return chapters.get(chapterIndex);
    }
} 