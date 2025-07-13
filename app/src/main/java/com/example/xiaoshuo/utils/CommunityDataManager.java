package com.example.xiaoshuo.utils;

import com.example.xiaoshuo.models.Comment;
import com.example.xiaoshuo.models.Post;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 社区数据管理器
 */
public class CommunityDataManager {
    private static CommunityDataManager instance;
    private List<Post> allPosts;
    private Random random = new Random();

    // 帖子类型
    private final String[] POST_TYPES = {"感悟", "书评", "求助", "推荐", "资讯"};

    // 用户名
    private final String[] USER_NAMES = {
            "星海拾贝", "午夜书咖", "代码与诗", "书中自有黄金屋", "幻想家",
            "历史的旁观者", "书卷气的猫", "字里行间", "漫漫读", "故事收藏家",
            "墨水心", "寂静读者", "白日梦想家", "知识搬运工", "灵魂捕手"
    };

    // 书籍标题
    private final String[] BOOK_TITLES = {
            "人类简史", "时间简史", "物种起源", "国富论", "君主论",
            "理想国", "梦的解析", "小王子", "麦田里的守望者", "局外人",
            "罪与罚", "瓦尔登湖", "飞鸟集", "自私的基因", "万历十五年"
    };

    // 帖子标题模板
    private final String[] POST_TITLE_TEMPLATES = {
            "有没有人觉得《%s》这本书其实被高估了？",
            "看完《%s》后劲太大了，现在还没缓过来",
            "求一本和《%s》一样，能让人思考人生的书",
            "《%s》中的这个细节，是不是作者在暗示什么？",
            "新手提问，《%s》应该从哪个版本开始读？",
            "《%s》的作者真是个天才，完全猜不到结局",
            "关于《%s》的哲学思辨，想听听大家的看法",
            "最近有什么关于《%s》的改编影视剧吗？",
            "《%s》这本书，真的适合所有人读吗？",
            "终于把《%s》啃完了，感觉智商受到了碾压"
    };

    // 帖子内容模板
    private final String[] POST_CONTENT_TEMPLATES = {
            "我刚刚合上《%s》的最后一页，心情特别复杂。作者对%s的深入探讨，让我对%s这个概念有了全新的思考。尤其是关于%s的结局，你们觉得是HE还是BE？",

            "强力推荐《%s》！如果你喜欢%s类型的作品，那这本书绝对不容错过。作者的%s描写得太真实了，仿佛身临其境。我已经准备二刷了！",

            "求助万能的书友们！最近想找一些关于%s主题的书，类似《%s》那种深度的。不要太晦涩的，最好是通俗易懂一点的，有推荐的吗？",

            "《%s》这本书真的颠覆了我的认知。以前总觉得%s是一件理所当然的事，读完之后才发现背后的%s逻辑是如此复杂。强烈推荐给每一个对世界抱有好奇心的人。",

            "关于《%s》里的主角%s，我有一个大胆的猜测。我觉得他其实象征着%s，所有的行为都是为了最终的%s。不知道有没有人和我想的一样？",

            "天呐！《%s》要拍成电影了！希望导演能尊重原著，千万别魔改。书中我最喜欢的就是%s那段，不知道会怎么呈现，既期待又害怕！"
    };

    // 评论内容模板
    private final String[] COMMENT_TEMPLATES = {
            "楼主的观点很新颖！我之前完全没从%s的角度去思考过这个问题。",
            "完全同意！特别是作者对%s的分析，简直是一针见血。",
            "这个我持保留意见。我觉得书中的%s情节有点理想化了，现实中很难实现。",
            "强烈推荐你去看作者的另一本《%s》，风格一脉相承，但讨论的%s更加深刻。",
            "这本书的阅读门槛确实有点高，建议先了解一下%s相关的背景知识。",
            "我倒认为%s这个角色是作者的自我投射，代表了一种%s的精神。",
            "这本书读完后真的会引发%s的思考，值得反复品味。",
            "我是在%s的情况下读完这本书的，感受特别深刻。",
            "期待续作！作者在结尾留下了关于%s的悬念，太吊人胃口了。",
            "我已经向我所有的朋友推荐了这本书，特别是对%s感兴趣的那些。"
    };

    // 填充词汇
    private final String[] FILL_WORDS_CHARACTERS = {
            "主人公", "那个反派", "他的老师", "女主角", "次要人物", "他们的领袖", "那位科学家", "艺术家", "哲学家", "探险家"
    };

    private final String[] FILL_WORDS_PLOTS = {
            "最终的抉择", "开篇的伏笔", "故事的转折点", "高潮部分的对决", "隐藏的线索", "人物的动机", "历史背景", "社会环境", "文化冲突", "技术变革"
    };

    private final String[] FILL_WORDS_THEMES = {
            "自由意志", "人性", "道德困境", "存在的意义", "社会结构", "权力关系", "科技伦理", "历史循环", "爱与牺牲", "真理与谎言"
    };

    private final String[] FILL_WORDS_FEELINGS = {
            "深深的震撼", "长久的沉默", "豁然开朗的感觉", "一丝悲凉", "莫名的激动", "复杂的情感", "强烈的共鸣", "深深的无力感", "对未来的希望", "对过去的思考"
    };

    private final String[] FILL_WORDS_TIMES = {
            "一个下雨的午后", "独自旅行时", "夜深人静时", "面临人生抉择时", "感到迷茫时", "毕业后", "工作之余", "长假期间", "心情烦躁时", "阳光明媚的早晨"
    };

    private final String[] FILL_WORDS_STYLES = {
            "宏大叙事", "细腻的情感描写", "严谨的逻辑推理", "开放式结局", "独特的视角", "讽刺的笔法", "诗意的语言", "跨学科的探讨", "非线性叙事", "冷静的旁白"
    };

    private CommunityDataManager() {
        allPosts = generateMockPosts();
    }

    public static synchronized CommunityDataManager getInstance() {
        if (instance == null) {
            instance = new CommunityDataManager();
        }
        return instance;
    }

    /**
     * 获取所有帖子
     */
    public List<Post> getAllPosts() {
        return new ArrayList<>(allPosts);
    }

    /**
     * 获取热门帖子
     */
    public List<Post> getHotPosts() {
        List<Post> hotPosts = new ArrayList<>(allPosts);
        // 按照点赞数和评论数排序
        hotPosts.sort((p1, p2) -> {
            int score1 = p1.getLikeCount() * 2 + p1.getCommentCount() * 3;
            int score2 = p2.getLikeCount() * 2 + p2.getCommentCount() * 3;
            return score2 - score1;
        });
        return hotPosts.size() > 10 ? hotPosts.subList(0, 10) : hotPosts;
    }

    /**
     * 获取最新帖子
     */
    public List<Post> getLatestPosts() {
        List<Post> latestPosts = new ArrayList<>(allPosts);
        // 按照发布时间排序
        latestPosts.sort((p1, p2) -> p2.getPublishTime().compareTo(p1.getPublishTime()));
        return latestPosts;
    }

    /**
     * 根据类型获取帖子
     */
    public List<Post> getPostsByType(String type) {
        List<Post> typePosts = new ArrayList<>();
        for (Post post : allPosts) {
            if (type.equals(post.getPostType())) {
                typePosts.add(post);
            }
        }
        return typePosts;
    }

    /**
     * 获取帖子详情
     */
    public Post getPostById(long postId) {
        for (Post post : allPosts) {
            if (post.getId() == postId) {
                return post;
            }
        }
        return null;
    }

    /**
     * 添加新帖子
     */
    public void addPost(Post post) {
        allPosts.add(0, post);
    }

    /**
     * 点赞帖子
     */
    public void likePost(long postId) {
        Post post = getPostById(postId);
        if (post != null) {
            if (!post.isLiked()) {
                post.setLikeCount(post.getLikeCount() + 1);
                post.setLiked(true);
            } else {
                post.setLikeCount(post.getLikeCount() - 1);
                post.setLiked(false);
            }
        }
    }

    /**
     * 添加评论
     */
    public void addComment(Comment comment) {
        Post post = getPostById(comment.getPostId());
        if (post != null) {
            post.addComment(comment);
        }
    }

    /**
     * 生成模拟帖子数据
     */
    private List<Post> generateMockPosts() {
        List<Post> posts = new ArrayList<>();
        
        // 生成30个帖子
        for (int i = 1; i <= 30; i++) {
            Post post = createRandomPost(i);
            posts.add(post);
            
            // 为每个帖子添加3-10条评论
            int commentCount = 3 + random.nextInt(8);
            for (int j = 1; j <= commentCount; j++) {
                Comment comment = createRandomComment(j, post.getId());
                post.addComment(comment);
            }
        }
        
        return posts;
    }

    /**
     * 创建随机帖子
     */
    private Post createRandomPost(long id) {
        String bookTitle = BOOK_TITLES[random.nextInt(BOOK_TITLES.length)];
        String authorName = USER_NAMES[random.nextInt(USER_NAMES.length)];
        String postType = POST_TYPES[random.nextInt(POST_TYPES.length)];
        
        // 随机选择标题模板
        String titleTemplate = POST_TITLE_TEMPLATES[random.nextInt(POST_TITLE_TEMPLATES.length)];
        String title = String.format(titleTemplate, bookTitle);
        
        // 随机选择内容模板
        String contentTemplate = POST_CONTENT_TEMPLATES[random.nextInt(POST_CONTENT_TEMPLATES.length)];
        String character = FILL_WORDS_CHARACTERS[random.nextInt(FILL_WORDS_CHARACTERS.length)];
        String theme = FILL_WORDS_THEMES[random.nextInt(FILL_WORDS_THEMES.length)];
        String style = FILL_WORDS_STYLES[random.nextInt(FILL_WORDS_STYLES.length)];
        String plot = FILL_WORDS_PLOTS[random.nextInt(FILL_WORDS_PLOTS.length)];
        
        String content;
        try {
            if (contentTemplate.contains("%s") && contentTemplate.indexOf("%s") != contentTemplate.lastIndexOf("%s")) {
                if (contentTemplate.indexOf("%s", contentTemplate.indexOf("%s") + 2) != contentTemplate.lastIndexOf("%s")) {
                    content = String.format(contentTemplate, bookTitle, character, theme);
                } else {
                    content = String.format(contentTemplate, bookTitle, style);
                }
            } else {
                content = "最近读完了《" + bookTitle + "》，感触很深。情节扣人心弦，人物刻画得非常生动。";
            }
        } catch (Exception e) {
            e.printStackTrace();
            content = "最近读完了《" + bookTitle + "》，感触很深。情节扣人心弦，人物刻画得非常生动。";
        }

        
        Post post = new Post(id, title, content, authorName, "");
        post.setPostType(postType);
        post.setBookTitle(bookTitle);
        
        // 随机设置发布时间（最近30天内）
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -random.nextInt(30));
        post.setPublishTime(calendar.getTime());
        
        // 随机设置点赞数和浏览数
        post.setLikeCount(random.nextInt(200));
        post.setViewCount(post.getLikeCount() + 50 + random.nextInt(500));
        
        return post;
    }

    /**
     * 创建随机评论
     */
    private Comment createRandomComment(long id, long postId) {
        String authorName = USER_NAMES[random.nextInt(USER_NAMES.length)];
        
        // 随机选择评论模板
        String commentTemplate = COMMENT_TEMPLATES[random.nextInt(COMMENT_TEMPLATES.length)];
        
        String content;
        if (commentTemplate.contains("%s")) {
            if (commentTemplate.indexOf("%s") != commentTemplate.lastIndexOf("%s")) {
                String character = FILL_WORDS_CHARACTERS[random.nextInt(FILL_WORDS_CHARACTERS.length)];
                String theme = FILL_WORDS_THEMES[random.nextInt(FILL_WORDS_THEMES.length)];
                content = String.format(commentTemplate, character, theme);
            } else {
                String fill;
                if (commentTemplate.contains("《%s》")) {
                    fill = BOOK_TITLES[random.nextInt(BOOK_TITLES.length)];
                } else if (commentTemplate.contains("我整个人都%s了")) {
                    fill = FILL_WORDS_FEELINGS[random.nextInt(FILL_WORDS_FEELINGS.length)];
                } else if (commentTemplate.contains("在%s的时候")) {
                    fill = FILL_WORDS_TIMES[random.nextInt(FILL_WORDS_TIMES.length)];
                } else if (commentTemplate.contains("但更加%s")) {
                    fill = FILL_WORDS_STYLES[random.nextInt(FILL_WORDS_STYLES.length)];
                } else {
                    fill = FILL_WORDS_PLOTS[random.nextInt(FILL_WORDS_PLOTS.length)];
                }
                content = String.format(commentTemplate, fill);
            }
        } else {
            content = "非常赞同楼主的观点，这本书确实很值得一读！";
        }
        
        Comment comment = new Comment(id, postId, content, authorName, "");
        
        // 随机设置发布时间（最近7天内）
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -random.nextInt(7));
        comment.setPublishTime(calendar.getTime());
        
        // 随机设置点赞数
        comment.setLikeCount(random.nextInt(50));
        
        // 有20%的概率是回复其他评论
        if (random.nextInt(100) < 20 && id > 1) {
            comment.setReplyToCommentId((long)(random.nextInt((int)id - 1) + 1));
            comment.setReplyToUserName(USER_NAMES[random.nextInt(USER_NAMES.length)]);
        }
        
        return comment;
    }
    
    /**
     * 格式化日期为友好显示
     */
    public static String formatTimeAgo(Date date) {
        if (date == null) {
            return "";
        }
        
        long currentTime = System.currentTimeMillis();
        long dateTime = date.getTime();
        long timeDiff = currentTime - dateTime;
        
        if (timeDiff < TimeUnit.MINUTES.toMillis(1)) {
            return "刚刚";
        } else if (timeDiff < TimeUnit.HOURS.toMillis(1)) {
            return TimeUnit.MILLISECONDS.toMinutes(timeDiff) + "分钟前";
        } else if (timeDiff < TimeUnit.DAYS.toMillis(1)) {
            return TimeUnit.MILLISECONDS.toHours(timeDiff) + "小时前";
        } else if (timeDiff < TimeUnit.DAYS.toMillis(7)) {
            return TimeUnit.MILLISECONDS.toDays(timeDiff) + "天前";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd", Locale.getDefault());
            return sdf.format(date);
        }
    }
} 