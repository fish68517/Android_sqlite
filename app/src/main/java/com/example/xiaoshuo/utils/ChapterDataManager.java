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
        // 战神狂飙
        List<Chapter> zhanShenChapters = new ArrayList<>();
        zhanShenChapters.add(new Chapter(1, 0, "第一章：战神觉醒", 
                "萧战自昏迷中醒来，发现身处陌生荒野，体内战神血脉隐隐躁动。往昔记忆浮现，他曾是家族天才，却遭人陷害。正思索间，遇凶残野兽来袭，战神血脉觉醒，一拳轰杀，开启异世战神之路。", 1, false));
        zhanShenChapters.add(new Chapter(2, 0, "第二章：初入村落", 
                "萧战清理野兽尸体，获神秘晶核。循着气息找村落，途中听闻附近有凶寇为祸。入村后，村民惊惶，原来凶寇将至。萧战感村民质朴，决定留下，以战神之威，守护这方小天地。", 2, false));
        zhanShenChapters.add(new Chapter(3, 0, "第三章：凶寇来袭", 
                "凶寇大举进攻村庄，村民仓皇逃窜。萧战挺身而出，战神血脉全面爆发，一人独战数十凶寇。凶寇首领见状，亲自出手，却不是萧战对手。萧战一举击杀首领，余众溃散，村民欢呼，视萧战为英雄。", 3, false));
        zhanShenChapters.add(new Chapter(4, 0, "第四章：神秘老者", 
                "村民设宴感谢萧战，席间，一位神秘老者出现，自称云游道士。老者一眼看出萧战体内战神血脉，告知萧战此乃远古战神传承，世间罕见。临走前，老者指点萧战前往百里外的青云宗寻求机缘。", 4, false));
        zhanShenChapters.add(new Chapter(5, 0, "第五章：青云宗", 
                "告别村民，萧战踏上青云宗之路。沿途遇各类妖兽袭击，萧战一一击退，战斗经验不断累积。几日后，萧战终于来到青云宗山门，却被告知需通过入门考核才能入宗。", 5, false));
        bookChaptersMap.put("战神狂飙", zhanShenChapters);
        
        // 网游：开局获得神级天赋
        List<Chapter> wangYouChapters = new ArrayList<>();
        wangYouChapters.add(new Chapter(1, 0, "第一章：神级天赋", 
                "林风进入全新网游《幻世》，创建角色时触发隐藏彩蛋，获得神级天赋\"全知洞察\"，可看穿怪物弱点、装备秘密。手持新手武器，踏入新手村，轻松碾压普通怪物，惊呆周围玩家。", 1, false));
        wangYouChapters.add(new Chapter(2, 0, "第二章：初显身手", 
                "林风用天赋找到新手村隐藏精英怪，击杀后爆稀有装备。美女玩家唐雪主动组队，二人探索更深处区域，却遇其他玩家觊觎装备，林风凭借神级天赋与唐雪配合，击退来犯者。", 2, false));
        wangYouChapters.add(new Chapter(3, 0, "第三章：组建公会", 
                "林风与唐雪决定组建公会，取名\"洞察者\"。他们通过游戏论坛招募成员，吸引了不少玩家加入。林风利用天赋带领公会成员攻克新手村隐藏副本，获得大量稀有资源，公会实力迅速提升。", 3, false));
        wangYouChapters.add(new Chapter(4, 0, "第四章：副本攻略", 
                "《幻世》开放首个大型副本\"绿龙巢穴\"，各大公会竞相攻略。林风带领洞察者公会成员，凭借天赋找出绿龙弱点，成为首个通关副本的公会，获得巨量游戏币和稀有装备，公会声望大涨。", 4, false));
        wangYouChapters.add(new Chapter(5, 0, "第五章：隐藏职业", 
                "林风发现自己的天赋在升级后获得新能力，可以看到隐藏职业的转职要求。他按要求完成一系列高难度任务，成功转职为极其罕见的\"幻影刺客\"，获得特殊技能，实力再次飙升。", 5, false));
        bookChaptersMap.put("网游：开局获得神级天赋", wangYouChapters);
        
        // 武道巅峰
        List<Chapter> wuDaoChapters = new ArrayList<>();
        wuDaoChapters.add(new Chapter(1, 0, "第一章：资质平凡", 
                "叶尘出身武道世家旁支，却因资质平凡遭人轻视。在家族武道场，他偷学基础拳法，被族中天才子弟叶风发现并羞辱。叶尘暗自发誓，定要在武道上闯出一片天，当晚，他在月下苦练，感悟武道真意。", 1, false));
        wuDaoChapters.add(new Chapter(2, 0, "第二章：机缘巧合", 
                "叶尘在城外山林遇受伤老者，老者实为武道高人，见叶尘心性坚韧，传他一阶武技《烈风拳》。叶尘勤修苦练，拳法初成，回村时，恰逢叶风带人挑衅，叶尘以新学拳法，让叶风吃瘪。", 2, false));
        wuDaoChapters.add(new Chapter(3, 0, "第三章：武道大会", 
                "村里举办武道大会，叶尘报名参加。初赛中，他运用《烈风拳》接连击败多名对手。决赛遇上叶风，对方实力强大，叶尘险些落败，关键时刻，他领悟拳法真意，以伤换伤，最终击败叶风，获得冠军。", 3, false));
        wuDaoChapters.add(new Chapter(4, 0, "第四章：拜师求艺", 
                "武道大会后，叶尘声名鹊起，老者再次现身，自称「百拳门」掌门李悟空，邀请叶尘入门。叶尘欣然同意，跟随老者前往百拳门山门，开始了真正的武道之旅。", 4, false));
        wuDaoChapters.add(new Chapter(5, 0, "第五章：百拳门规", 
                "叶尘正式成为百拳门弟子，开始学习门规。百拳门讲究\"拳出百变，道法自然\"，要求弟子精通百种拳法。叶尘勤学苦练，很快掌握基础拳法，赢得师兄弟尊重，却也引来嫉妒。", 5, false));
        bookChaptersMap.put("武道巅峰", wuDaoChapters);
        
        // 绝地战龙
        List<Chapter> jueDiChapters = new ArrayList<>();
        jueDiChapters.add(new Chapter(1, 0, "第一章：穿越附身", 
                "秦天本是特战队员，执行任务时意外穿越到武道世界，附身于同名废柴子弟。面对家族祠堂的鄙夷，他摸不清状况，却发现体内有神秘龙纹异动，武道系统悄然激活，开启绝地逆袭。", 1, false));
        jueDiChapters.add(new Chapter(2, 0, "第二章：初战告捷", 
                "秦天利用系统，快速掌握基础武道知识，在家族演武场，挑战平日里欺压他的堂兄秦虎。凭借特战队员的战斗意识与系统辅助，秦天轻松击败秦虎，震惊族中长老，也引来神秘目光。", 2, false));
        jueDiChapters.add(new Chapter(3, 0, "第三章：龙纹觉醒", 
                "秦天在演武场胜利后，体内龙纹开始觉醒，释放出微弱龙气。家族大长老发现异常，将秦天带到密室，告知他体内龙纹乃家族祖传秘宝，千年来鲜有人能激活，秦天或是天命之人。", 3, false));
        jueDiChapters.add(new Chapter(4, 0, "第四章：秘境试炼", 
                "大长老引导秦天进入家族秘境，接受祖传试炼。秦天在秘境中与各种幻象战斗，凭借特战经验与系统辅助，一路过关。最终关卡，他面对龙形幻象，龙纹共鸣，成功通过试炼，获得龙纹第一层控制权。", 4, false));
        jueDiChapters.add(new Chapter(5, 0, "第五章：家族变故", 
                "秦天出秘境后，发现家族遭遇外敌入侵，伤亡惨重。元凶是世仇「黑虎帮」，帮主觊觎秦家龙纹秘术。秦天怒发冲冠，龙纹爆发，一人独战黑虎帮数十高手，终将敌人击退，保全家族。", 5, false));
        bookChaptersMap.put("绝地战龙", jueDiChapters);
        
        // 天命妖刀人
        List<Chapter> tianMingChapters = new ArrayList<>();
        tianMingChapters.add(new Chapter(1, 0, "第一章：刀认新主", 
                "林默在破旧小镇长大，一日，捡到嵌在石头里的古朴妖刀。触碰瞬间，妖刀认主，无数刀道残魂涌入脑海，他头痛欲裂，却也觉醒特殊天赋——可吸纳妖刀之力强化自身，从此与妖刀绑定命运。", 1, false));
        tianMingChapters.add(new Chapter(2, 0, "第二章：除魔卫道", 
                "林默发现妖刀能斩灭邪祟，小镇外常有鬼怪作祟，他持刀除魔，声名渐起。但妖刀力量失控时，会侵蚀他的神智，镇上老者告诫他，此刀乃禁忌，劝他舍弃，林默却决心探寻妖刀来历，掌控力量。", 2, false));
        tianMingChapters.add(new Chapter(3, 0, "第三章：刀魂苏醒", 
                "林默在一次除魔过程中，妖刀吸收邪祟精华，刀魂苏醒，与林默对话。刀魂自称「血魔」，曾是远古刀修，因逆天改命被镇压于刀中。血魔欲借林默之手重获自由，许诺传授刀法，林默警惕地答应，暗自提防。", 3, false));
        tianMingChapters.add(new Chapter(4, 0, "第四章：刀法小成", 
                "林默按血魔指导练习刀法，进步神速。小镇来了一支猎魔人队伍，队长郑远发现林默身上妖刀气息，欲强行收刀。林默刀法初成，与郑远交手，虽不敌，却也展现出惊人天赋，引起猎魔人注意。", 4, false));
        tianMingChapters.add(new Chapter(5, 0, "第五章：城中奇遇", 
                "猎魔人队伍邀请林默前往附近城镇，寻找更强大的邪祟。城中，林默遇见一名神秘女子，对方一眼认出他的妖刀，并透露妖刀真名为「天衍」，乃上古十大凶器之一。女子身份成谜，转瞬消失。", 5, false));
        bookChaptersMap.put("天命妖刀人", tianMingChapters);
        
        // 捡漏我觉醒了黄金瞳
        List<Chapter> jianLouChapters = new ArrayList<>();
        jianLouChapters.add(new Chapter(1, 0, "第一章：黄金瞳", 
                "苏晨本是普通古玩店伙计，意外觉醒黄金瞳，能看穿古玩真伪、年代。在一次收旧货时，凭借黄金瞳，从一堆破铜烂铁里发现宋代官窑瓷器碎片，老板惊为天人，苏晨也开启捡漏传奇。", 1, false));
        jianLouChapters.add(new Chapter(2, 0, "第二章：初试身手", 
                "苏晨应邀参加当地收藏家雅集，在现场发现多件赝品。一位老藏家不服，拿出珍藏的明代青花瓷，苏晨一眼看出是民国仿品，引发争议，经专家鉴定后证实苏晨所言不虚，名声大振。", 2, false));
        jianLouChapters.add(new Chapter(3, 0, "第三章：古玩市场", 
                "苏晨辞去伙计工作，开始在古玩市场淘宝。凭借黄金瞳，他从一堆杂物中淘得宋代哥窑笔洗，低价收入囊中。消息传出，引来古玩界大亨关注，纷纷抛出橄榄枝，但苏晨决定独自闯荡。", 3, false));
        jianLouChapters.add(new Chapter(4, 0, "第四章：黄金瞳升级", 
                "苏晨发现黄金瞳能力随着接触的古玩增多而增强，不仅能鉴别真伪，还能感知物品背后的历史。一次偶然中，他触碰一块石头，竟看到千年前的场景，黄金瞳竟有通灵之能，令他惊骇。", 4, false));
        jianLouChapters.add(new Chapter(5, 0, "第五章：古墓奇遇", 
                "苏晨受邀参与一次私人考古活动，前往北方一处古墓。借助黄金瞳，他躲过机关，成功进入墓室。墓中文物触发黄金瞳共鸣，他获得墓主记忆片段，发现一个惊天秘密——黄金瞳源自上古神器。", 5, false));
        bookChaptersMap.put("捡漏我觉醒了黄金瞳", jianLouChapters);
        
        // 一念永恒
        List<Chapter> yiNianChapters = new ArrayList<>();
        yiNianChapters.add(new Chapter(1, 0, "第一章：灵食斋", 
                "白小纯生性怕死，却因灵溪宗入门测试，被测出罕见灵根。入宗后，他在灵食斋捣鼓长生食谱，把宗门灵植当食材，闹出不少笑话，却也因独特修行思路，引起师长关注。", 1, false));
        yiNianChapters.add(new Chapter(2, 0, "第二章：宗门小比", 
                "白小纯参与宗门小比，因害怕受伤，凭借灵活身法和保命手段，巧妙避开对手攻击，最后误打误撞获胜。赛后，他被灵溪宗长老收为亲传弟子，却不知，更大的机遇与危险，在宗门外等候。", 2, false));
        yiNianChapters.add(new Chapter(3, 0, "第三章：外门任务", 
                "白小纯接到宗门外出任务，前往山脉采集灵药。途中遇险，他险些丧命，却意外获得奇特丹方。回宗后，他闭门炼丹，成功炼制出增强修为的丹药，修为大进，引起宗门高层注意。", 3, false));
        yiNianChapters.add(new Chapter(4, 0, "第四章：内门考核", 
                "白小纯因炼丹成就获得参加内门考核资格。考核中，他以炼丹技术取胜，成功晋升内门弟子。内门生活更为残酷，白小纯面对同门挑衅，他暗中修炼，准备反击。", 4, false));
        yiNianChapters.add(new Chapter(5, 0, "第五章：河坊镇之行", 
                "白小纯被派往河坊镇执行任务，在那里他遇到了神秘少女宋缺。两人联手对抗邪修，白小纯展现惊人实力，赢得宋缺认可。任务完成后，宋缺透露自己身份，邀请白小纯加入她的秘密组织。", 5, false));
        bookChaptersMap.put("一念永恒", yiNianChapters);
        
        // 万界毒尊
        List<Chapter> wanJieChapters = new ArrayList<>();
        wanJieChapters.add(new Chapter(1, 0, "第一章：毒尊系统", 
                "萧凡穿越玄幻世界，觉醒毒尊系统，开局获得\"万毒不侵\"体质。在新手村，他用毒粉轻松解决野兽，还能吸纳毒素强化自身。村民视他为怪物，他却暗自欢喜，决心以毒证道。", 1, false));
        wanJieChapters.add(new Chapter(2, 0, "第二章：毒皇散", 
                "萧凡听闻附近有剧毒妖花，能炼制高阶毒药，冒险前往。途中遇邪修抢夺妖花，萧凡以毒系武技对抗，邪修不敌，仓皇而逃。他收获妖花，炼制出\"毒皇散\"，实力再上一层楼。", 2, false));
        wanJieChapters.add(new Chapter(3, 0, "第三章：毒宗入门", 
                "萧凡因毒技引人注目，被路过的毒宗长老发现，邀请他入宗。毒宗是修真界专修毒道的宗门，萧凡如鱼得水，很快展现天赋。然而，他的系统吸收毒素过多，引起宗门高层警觉，暗中监视他。", 3, false));
        wanJieChapters.add(new Chapter(4, 0, "第四章：蛇谷试炼", 
                "毒宗新弟子需参加蛇谷试炼，谷中毒蛇遍布。萧凡凭借毒尊体质，不惧毒蛇，还吸收了大量蛇毒，实力暴涨。试炼中，他意外发现谷底有古老祭坛，触发系统共鸣，获得远古毒尊传承。", 4, false));
        wanJieChapters.add(new Chapter(5, 0, "第五章：宗门危机", 
                "毒宗遭到敌对宗门袭击，萧凡所在的外门弟子区首当其冲。敌人毒术高超，连长老都难以抵挡。危急时刻，萧凡启动远古毒尊传承，释放奇特毒雾，击退敌人，保全宗门，也因此被提拔为核心弟子。", 5, false));
        bookChaptersMap.put("万界毒尊", wanJieChapters);
        
        // 剑来
        List<Chapter> jianLaiChapters = new ArrayList<>();
        jianLaiChapters.add(new Chapter(1, 0, "第一章：惊蛰之日", 
                "小镇少年陈平安，在惊蛰之日，听老秀才讲学问，窗外春雷响，江湖暗流涌动。此时，远来高人布局，陈平安的命运，因这场雷雨，与江湖开始纠缠，他将踏上改变人生的旅程。", 1, false));
        jianLaiChapters.add(new Chapter(2, 0, "第二章：门前长剑", 
                "陈平安晨起开门，发现门口有陌生长剑。正疑惑，村里来神秘访客，称受高人所托观察他。访客话语隐晦，提及\"机缘\"，陈平安懵懂间，预感生活将因这扇\"门\"，进入新天地。", 2, false));
        jianLaiChapters.add(new Chapter(3, 0, "第三章：踏入江湖", 
                "陈平安带着长剑离开村子，跟随访客前往北方。途中遇剑客挑战，访客袖手旁观，陈平安凭借朴素剑法与敏锐直觉，险胜对手。访客赞其有剑骨，剑道天赋不凡，让陈平安隐约明白，他的人生将与剑紧密相连。", 3, false));
        jianLaiChapters.add(new Chapter(4, 0, "第四章：雪中悟剑", 
                "陈平安随访客到达北方小城，被安排在客栈修习剑法。一场大雪中，他在雪地练剑，偶然顿悟，剑招越发灵动。城中高手暗中观察，惊其悟性，却也引来杀机。一夜，刺客突袭，陈平安仓促应战，险些丧命。", 4, false));
        jianLaiChapters.add(new Chapter(5, 0, "第五章：剑气初成", 
                "受伤的陈平安被访客救下，带到山中隐居地休养。访客传授他内功心法，助其疗伤。康复后，陈平安刻苦修炼，终于在一次雷雨天气中，体内剑气初成，一剑斩开山石，展现非凡天赋。访客决定带他前往更高深的剑道世界。", 5, false));
        bookChaptersMap.put("剑来", jianLaiChapters);
        
        // 妖孽兵王
        List<Chapter> yaoNieChapters = new ArrayList<>();
        yaoNieChapters.add(new Chapter(1, 0, "第一章：退役归来", 
                "叶凌曾是国际顶尖特种兵，因任务受伤退役，回归都市。在酒吧遇美女被骚扰，他出手教训混混，展现超强战斗力，引起地下势力注意，也让美女苏瑶对他好奇，想探究其身份。", 1, false));
        yaoNieChapters.add(new Chapter(2, 0, "第二章：商业陷阱", 
                "叶凌帮苏瑶解决家族企业难题，识破商业陷阱。却遭对手雇佣的杀手暗杀，叶凌凭借特种兵作战经验，轻松反杀。此事过后，苏瑶家族力邀他加盟，叶凌也在都市，渐入江湖纷争。", 2, false));
        yaoNieChapters.add(new Chapter(3, 0, "第三章：神秘组织", 
                "叶凌发现苏家背后有神秘势力操控，决定暗中调查。一次偶然中，他遭遇黑衣人袭击，对方身手不凡，使用特殊武器。激战中，叶凌发现对方与自己曾执行的一次秘密任务有关，引起他的警觉。", 3, false));
        yaoNieChapters.add(new Chapter(4, 0, "第四章：老战友", 
                "叶凌在市中心遇到昔日战友王强，二人叙旧。王强告知他，退役特种兵近期频频遭遇意外，疑似有组织在猎杀他们。叶凌决定联合老战友，共同调查背后黑手，保护战友安全。", 4, false));
        yaoNieChapters.add(new Chapter(5, 0, "第五章：地下拳场", 
                "叶凌追查线索，潜入地下拳场。他假扮选手参赛，在比赛中识破对手身份，是昔日任务中的敌对特工。叶凌击败对手，获取重要情报，发现一个国际组织正在收集退役特种兵的基因样本，目的不明。", 5, false));
        bookChaptersMap.put("妖孽兵王", yaoNieChapters);
        
        // 王女之说
        List<Chapter> wangNvChapters = new ArrayList<>();
        wangNvChapters.add(new Chapter(1, 0, "第一章：入宫", 
                "大楚王朝，Princess 涟漪本是民间孤女，因皇室血脉溯源，被接入王宫。初入宫门，红墙深院里，她面对繁文缛节手足无措，却因纯真性格，被皇帝暗中关注，而宫廷的权谋漩涡，已悄然将她卷入。", 1, false));
        wangNvChapters.add(new Chapter(2, 0, "第二章：相遇", 
                "涟漪在御花园遇大皇子楚明轩，明轩温润有礼，帮她化解迷路窘境。交谈间，二人对民间与宫廷的不同生活，各有感悟。但这一幕被贵妃眼线看到，贵妃忌惮涟漪得宠，开始谋划针对她的算计。", 2, false));
        wangNvChapters.add(new Chapter(3, 0, "第三章：宫斗初体验", 
                "涟漪被安排与各位公主一同学习礼仪。课堂上，三公主故意刁难她，让她出丑。涟漪凭借民间生活的机智，巧妙化解，反而赢得皇帝夸赞。贵妃得知后，派心腹在涟漪膳食中下毒，所幸被明轩及时发现。", 3, false));
        wangNvChapters.add(new Chapter(4, 0, "第四章：身世之谜", 
                "涟漪遇到一位年迈宫女，对方认出她与已故皇后相似。宫女暗中告诉她，自己当年曾是皇后身边人，皇后生下龙凤胎后不久离奇去世，女婴下落不明。涟漪开始怀疑自己是失散的公主，决心探寻真相。", 4, false));
        wangNvChapters.add(new Chapter(5, 0, "第五章：危机四伏", 
                "涟漪暗中调查身世，接近太后。太后对她格外亲切，赐予她一枚玉佩，称是皇后遗物。贵妃得知后，派刺客夜袭涟漪寝宫。危急关头，楚明轩赶到救援，二人联手制服刺客，却也因此陷入更大的宫廷漩涡。", 5, false));
        bookChaptersMap.put("王女之说", wangNvChapters);
        
        // 古风仙侠图
        List<Chapter> guFengChapters = new ArrayList<>();
        guFengChapters.add(new Chapter(1, 0, "第一章：灵花圃", 
                "青璃是青羽峰小弟子，负责看守门派灵花圃。一日，灵花圃突现奇异光芒，一朵并蒂仙花绽放，引来了觊觎仙花的邪修。青璃为护仙花，激发体内隐藏灵力，与邪修缠斗，却不知，这仙花与她身世有关。", 1, false));
        guFengChapters.add(new Chapter(2, 0, "第二章：主峰修行", 
                "青璃不敌邪修，危急时刻，门派长老云风赶到，击退邪修。云风发现青璃灵力特殊，带她回主峰修行。青璃在主峰，结识了天才弟子萧逸，二人因对仙侠之道的不同理解，时有争论，却也在相处中，暗生情愫。", 2, false));
        guFengChapters.add(new Chapter(3, 0, "第三章：秘境寻宝", 
                "门派开启百年一次的秘境探索，青璃被选中参加。秘境中，她与萧逸同组，二人配合默契，闯过多道关卡。在秘境深处，青璃被一面古镜吸引，触碰时，镜中显现她幼时记忆片段，暗示她非凡身世，引发她的困惑。", 3, false));
        guFengChapters.add(new Chapter(4, 0, "第四章：仇家寻上门", 
                "青璃从秘境归来，被告知有人来访。来者是邪修宗门的长老，声称青璃是他们宗门丢失的圣女，要带她回去。云风出面拒绝，双方剑拔弩张。萧逸暗中告诉青璃，邪修所言或有真相，但目的不纯，建议她先留在青羽峰，待时机成熟再查身世。", 4, false));
        guFengChapters.add(new Chapter(5, 0, "第五章：仙花异变", 
                "青璃看守的并蒂仙花突然变异，花蕊中飞出一只灵蝶，与青璃灵力产生共鸣。云风解释，此灵蝶是仙花精魄，千年难得一见，选择与青璃结缘，是大机缘。灵蝶融入青璃体内，她修为大进，也发现自己可以通过灵蝶，看到一些过去的画面。", 5, false));
        bookChaptersMap.put("古风仙侠图", guFengChapters);
        
        // 任生缘
        List<Chapter> renShengChapters = new ArrayList<>();
        renShengChapters.add(new Chapter(1, 0, "第一章：诗结缘", 
                "江南水乡，沈茵茵与书生顾长卿相遇，二人因一首诗结缘，时常相约游湖论诗。顾长卿家境贫寒，却才华横溢，沈茵茵不顾家人反对，倾心于他，而沈家长辈已为她选定豪门亲事，爱情与家族压力，开始拉扯。", 1, false));
        renShengChapters.add(new Chapter(2, 0, "第二章：赴京赶考", 
                "顾长卿为娶沈茵茵，决定赴京赶考求功名。沈茵茵偷偷变卖首饰，为他凑路费。送别时，二人在长亭立下誓言，定要相守一生。可沈家长辈得知后，强行将沈茵茵禁足，还设计让顾长卿赶考途中遇险。", 2, false));
        renShengChapters.add(new Chapter(3, 0, "第三章：遇险山道", 
                "顾长卿在山间小路遭遇强盗埋伏，幸得路过的侠客相救。侠客见他才学不凡，资质过人，收他为徒，教授武艺。顾长卿一边赶考，一边习武，不知这是命运的安排，为他日后的复杂处境埋下伏笔。", 3, false));
        renShengChapters.add(new Chapter(4, 0, "第四章：被迫婚约", 
                "沈茵茵被家中逼婚，对方是当地富商之子。婚期将近，她绝望之际，收到顾长卿高中榜眼的消息。然而，富商子弟恼怒，派人阻截顾长卿归来的路，沈茵茵只能在无尽等待中，度过每一个日夜。", 4, false));
        renShengChapters.add(new Chapter(5, 0, "第五章：锦衣还乡", 
                "顾长卿官拜翰林院编修，锦衣还乡，却得知沈茵茵婚期将近。他在婚礼当日带着圣旨赶到，皇帝赐婚，指婚沈茵茵与他。沈家长辈无力反抗，富商震怒却不敢违抗圣意。一对有情人终成眷属，顾长卿也开始了仕途生涯。", 5, false));
        bookChaptersMap.put("任生缘", renShengChapters);
        
        // 情难自已
        List<Chapter> qingNanChapters = new ArrayList<>();
        qingNanChapters.add(new Chapter(1, 0, "第一章：误会初生", 
                "大理寺少卿之女苏锦，偶遇微服私访的太子萧煜。苏锦性格豪爽，误把萧煜当登徒子，大打出手。萧煜欣赏她的直率，隐瞒身份与她相交，二人在京城街头，破解小案、惩治恶少，渐生好感。", 1, false));
        qingNanChapters.add(new Chapter(2, 0, "第二章：身份曝光", 
                "苏锦得知萧煜身份，又惊又怕，刻意疏远。萧煜却借大理寺查案之机，频繁接触苏锦。查案中，苏锦展现出过人的断案天赋，帮萧煜识破凶手阴谋，二人关系，也在并肩作战中，重新拉近。", 2, false));
        qingNanChapters.add(new Chapter(3, 0, "第三章：宫廷争斗", 
                "萧煜对苏锦的关注引起朝中非议，太后欲为太子选妃，意在牵制萧煜与苏锦走近。宫中暗流涌动，太子与二皇子党争激烈。苏锦不谙宫廷争斗，却因聪慧机敏，多次帮萧煜化解危机，成为他最信任的人。", 3, false));
        qingNanChapters.add(new Chapter(4, 0, "第四章：刺杀疑云", 
                "一日，萧煜遭遇刺杀，苏锦冒险相救，却发现刺客是大理寺的线人。大理寺陷入嫌疑，苏父被革职查办。苏锦决心查明真相，为父洗清冤屈，同时也要证明萧煜的信任没有错付。", 4, false));
        qingNanChapters.add(new Chapter(5, 0, "第五章：真相大白", 
                "苏锦追查刺杀案，发现是二皇子一党所为，意在嫁祸大理寺，离间太子与朝中重臣。真相大白，萧煜平反苏父冤屈，晋升苏锦为大理寺女史，公开表明对她的赏识。二人的情感也在危机中升华，萧煜决定不惜一切，也要守护这段感情。", 5, false));
        bookChaptersMap.put("情难自已", qingNanChapters);
        
        // 不可逆
        List<Chapter> buKeNiChapters = new ArrayList<>();
        buKeNiChapters.add(new Chapter(1, 0, "第一章：庶女入府", 
                "将军府庶女叶清歌，被嫡姐设计，嫁入身患重病的靖王府。新婚夜，她发现靖王萧景睿虽病弱，却智谋过人。二人在王府，面对嫡母刁难、权力倾轧，叶清歌凭借聪慧，一次次化解危机，也对萧景睿渐生同情与好感。", 1, false));
        buKeNiChapters.add(new Chapter(2, 0, "第二章：王府生变", 
                "萧景睿为给叶清歌撑腰，在朝堂上巧妙布局，让刁难者吃瘪。叶清歌则在府中，整顿内务，改善王府处境。可外界传言，靖王命不久矣，叶清歌却决心陪他面对，而萧景睿的病情，也暗藏蹊跷。", 2, false));
        buKeNiChapters.add(new Chapter(3, 0, "第三章：神秘药方", 
                "叶清歌发现萧景睿服用的药有问题，暗中寻访名医。一位隐居老医生告诉她，靖王所患并非不治之症，是被人下了慢性毒药。老医生给她一个秘方，需要一味珍贵药材。叶清歌冒险前往药山寻找，却不知，这也是一场精心设计的陷阱。", 3, false));
        buKeNiChapters.add(new Chapter(4, 0, "第四章：药山遇险", 
                "叶清歌在药山遇袭，幸得神秘黑衣人相救。黑衣人自称「影卫」，是萧景睿暗中培养的死士。原来萧景睿早知自己被下毒，却隐忍不发，暗中调查真凶。叶清歌终获药材，回府后，发现王府暗处，远比她想象的复杂。", 4, false));
        buKeNiChapters.add(new Chapter(5, 0, "第五章：真相浮出", 
                "叶清歌为萧景睿熬药，见他病情好转，心中欢喜。萧景睿向她透露，他被下毒与先皇遗诏有关，有人欲除掉他。二人同心同德，布下反击大局。一场宫廷宴会上，叶清歌机智助萧景睿识破敌人诡计，二人展现出不凡默契，也在生死患难中，情愫暗生。", 5, false));
        bookChaptersMap.put("不可逆", buKeNiChapters);
        
        // 魔道祖师
        List<Chapter> moDaoChapters = new ArrayList<>();
        moDaoChapters.add(new Chapter(1, 0, "第一章：重生归来", 
                "夷陵老祖魏无羡身死十三年后，莫玄羽献舍重生。他附身莫玄羽，在莫家庄遇邪祟，以诡异手段解决，引得蓝氏子弟蓝思追、蓝景仪注意。魏无羡假名莫玄羽，开始探寻重生后的世界，却不知，旧人旧怨，正慢慢浮现。", 1, false));
        moDaoChapters.add(new Chapter(2, 0, "第二章：云深不知处", 
                "魏无羡随蓝氏子弟回云深不知处，途中回忆起往昔与蓝忘机的纠葛。在云深不知处，他因不拘小节，触犯蓝氏家规，却也凭借重生后的奇异能力，发现云深不知处暗藏的邪祟异动，与蓝忘机的交集，也逐渐增多。", 2, false));
        moDaoChapters.add(new Chapter(3, 0, "第三章：初见蓝忘机", 
                "在云深不知处藏书阁，魏无羡与蓝忘机相遇。蓝忘机不认识他，却感到莫名熟悉。二人因一起查案结伴而行，前往乱葬岗调查诡异命案。途中，魏无羡的记忆不断涌现，发现十三年前的恩怨，比他想象的更加复杂。", 3, false));
        moDaoChapters.add(new Chapter(4, 0, "第四章：夜猎寻踪", 
                "魏无羡与蓝忘机在夜猎中遇见金凌，金凌对魏无羡莫名敌意，却不知他就是魏无羡。调查中，三人发现邪祟源头指向不祥之地阴虎山。魏无羡凭借阴虎山古怪气息，逐渐恢复记忆，想起当年与蓝忘机的生死之约。", 4, false));
        moDaoChapters.add(new Chapter(5, 0, "第五章：身份暴露", 
                "阴虎山一战，魏无羡为救蓝忘机，不得不使用鬼道秘术，身份暴露。众人震惊，蓝忘机却选择相信他。二人共同揭开当年夷陵之战的部分真相，发现当年魏无羡被杀，实则另有隐情。蓝忘机决定与魏无羡同行，寻找事情真相。", 5, false));
        bookChaptersMap.put("魔道祖师", moDaoChapters);
        
        // 单独神在异界
        List<Chapter> danDuShenChapters = new ArrayList<>();
        danDuShenChapters.add(new Chapter(1, 0, "第一章：穿越异界", 
                "李阳意外穿越到异界，发现自己拥有神级力量——可以吸收各种能量强化自身。初到异界，他身无分文，却靠神级力量猎杀魔兽，渐渐在异界站稳脚跟，开始探索这个奇幻世界的秘密。", 1, false));
        danDuShenChapters.add(new Chapter(2, 0, "第二章：初露锋芒", 
                "李阳在猎杀魔兽时救下一个商队，获得进入王城的机会。在王城中，他偶然卷入一场贵族争斗，展露实力，吸引了王城学院的注意。学院院长亲自邀请他入学，他也借此机会，开始了解异界的力量体系。", 2, false));
        danDuShenChapters.add(new Chapter(3, 0, "第三章：学院生活", 
                "李阳进入王城学院，开始系统学习异界知识。他的吸收能力远超常人，很快掌握了初级魔法和武技。同时，他也在学院中结交了志同道合的朋友，组建了自己的小队，为将来的冒险做准备。", 3, false));
        danDuShenChapters.add(new Chapter(4, 0, "第四章：秘境探险", 
                "学院组织学生进入古代遗迹秘境历练，李阳带领小队深入其中。在秘境中，他们遭遇强大敌人，李阳为救队友，激发潜能，吸收秘境能量，实力大增，成功带领大家脱险，也因此在学院中名声大噪。", 4, false));
        danDuShenChapters.add(new Chapter(5, 0, "第五章：神级觉醒", 
                "一场突如其来的魔兽攻城事件打破了王城的宁静。危急时刻，李阳挺身而出，在战斗中，他的神级力量与异界能量产生共鸣，引发神级觉醒。他一人抵挡千军，击退魔兽潮，被誉为异界救世主，也引起了更高层势力的关注。", 5, false));
        bookChaptersMap.put("单独神在异界", danDuShenChapters);
        
        // 开局获得神级买卖
        List<Chapter> kaiJuChapters = new ArrayList<>();
        kaiJuChapters.add(new Chapter(1, 0, "第一章：神级系统", 
                "张航突然获得一个神级交易系统，可以用任何物品交换等价值的其他物品。他尝试用一块普通石头交换，得到一枚铜币，验证了系统的真实性。从此，他开始利用这个系统，一步步改变自己的生活。", 1, false));
        kaiJuChapters.add(new Chapter(2, 0, "第二章：第一桶金", 
                "张航用家中旧物换取了一些有价值的古董，拿去当铺换钱。有了第一桶金后，他开始规划如何利用系统发财。他发现系统的交换基于「真实价值」而非「市场价值」，这让他能发现被低估的宝物，赚取差价。", 2, false));
        kaiJuChapters.add(new Chapter(3, 0, "第三章：商业布局", 
                "张航开了一家古董店，专门收购被低估的古物，再通过系统换取更有价值的物品。生意蒸蒸日上，但也引来了同行嫉妒。一个老牌古董商人设计陷害他，指控他贩卖赝品，张航必须想办法证明自己的清白。", 3, false));
        kaiJuChapters.add(new Chapter(4, 0, "第四章：危机与机遇", 
                "为洗脱冤屈，张航通过系统换取了一件国宝级文物，引起轰动。文物专家鉴定后确认是真品，他的名声大振。这次危机也让他意识到系统的更多可能性，不仅可以交换物品，还能交换某些「无形资产」，如技能、运气等。", 4, false));
        kaiJuChapters.add(new Chapter(5, 0, "第五章：神级升级", 
                "张航的系统突然提示可以升级，需要用一件珍贵文物交换。升级后，系统不仅能交换物品，还能交换一定的「命运」。他用这个能力帮助一位面临破产的朋友逆转命运，朋友感恩戴德，成为他的生意伙伴，两人联手，开始了更大的商业布局。", 5, false));
        bookChaptersMap.put("开局获得神级买卖", kaiJuChapters);
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