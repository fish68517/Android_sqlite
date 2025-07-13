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
    private final String[] POST_TYPES = {"讨论", "书评", "求书", "分享", "活动"};
    
    // 用户名
    private final String[] USER_NAMES = {
            "书虫一枚", "墨香书斋", "纸上烟云", "阅读达人", "书海漫游", 
            "文字控", "夜读者", "书中自有", "悦读时光", "书香人生",
            "读书人", "书友会", "书迷", "书荒救星", "文学青年"
    };
    
    // 书籍标题
    private final String[] BOOK_TITLES = {
            "三体", "活着", "百年孤独", "红楼梦", "围城", 
            "平凡的世界", "白夜行", "解忧杂货店", "人间失格", "1984",
            "追风筝的人", "月亮与六便士", "撒哈拉的故事", "明朝那些事", "哈利·波特"
    };
    
    // 帖子标题模板
    private final String[] POST_TITLE_TEMPLATES = {
            "《%s》读后感",
            "求推荐类似《%s》的书",
            "谁看过《%s》？来聊聊",
            "《%s》这本书怎么样？值得一读吗",
            "刚看完《%s》，有几个问题想讨论",
            "《%s》里的人物塑造真是绝了",
            "为什么《%s》这么受欢迎？",
            "《%s》的结局真是让人意外",
            "《%s》的翻译版本哪个更好？",
            "寻找《%s》的电子版"
    };
    
    // 帖子内容模板
    private final String[] POST_CONTENT_TEMPLATES = {
            "最近读完了《%s》，感触很深。%s这个角色的成长真的很打动人，尤其是在%s那一段，几乎让我落泪。不知道大家对这本书有什么看法？",
            
            "前段时间读了《%s》，被里面的故事深深吸引。作者的文笔真的很棒，尤其是对%s的描写，仿佛身临其境。推荐给喜欢%s题材的书友们。",
            
            "求推荐类似《%s》风格的书！我特别喜欢这种%s的氛围，以及%s的情节设计。已经读过了同作者的其他作品，希望能找到相似的好书。",
            
            "《%s》这本书真的值得一读吗？看到很多人推荐，但是我担心%s的部分会有点难以接受。有读过的朋友能分享一下感受吗？",
            
            "关于《%s》中%s的结局，我有不同的理解。我认为作者是想表达%s的主题，而不仅仅是表面上的故事。不知道大家怎么看？",
            
            "《%s》的电影/剧版要出了！不知道会不会还原书中%s的场景，那可是整本书最精彩的部分。期待演员能诠释出%s的神韵。"
    };
    
    // 评论内容模板
    private final String[] COMMENT_TEMPLATES = {
            "我也很喜欢这本书，尤其是%s的部分写得太好了！",
            "同感，作者对%s的描写非常传神。",
            "我倒是觉得这本书有点过誉了，特别是%s的情节设计不够合理。",
            "推荐你也看看《%s》，风格很像，但更加%s。",
            "这本书的翻译很重要，我看的是%s版本，感觉翻译得不错。",
            "我认为书中%s的角色其实象征着%s，这是我的理解。",
            "读完这本书后我整个人都%s了，真的很震撼。",
            "我觉得这本书适合在%s的时候阅读，能更好地体会其中的情感。",
            "这本书的续集据说要出了，期待作者能继续%s的故事。",
            "我已经读了三遍了，每次都有新的感受，特别是对%s的理解。"
    };
    
    // 填充词汇
    private final String[] FILL_WORDS_CHARACTERS = {
            "主角", "配角", "反派", "男主", "女主", "配角", "长辈", "师父", "徒弟", "对手"
    };
    
    private final String[] FILL_WORDS_PLOTS = {
            "结局", "开头", "高潮", "转折", "伏笔", "悬念", "冲突", "和解", "相遇", "离别"
    };
    
    private final String[] FILL_WORDS_THEMES = {
            "成长", "爱情", "友情", "亲情", "正义", "背叛", "救赎", "牺牲", "奋斗", "追求"
    };
    
    private final String[] FILL_WORDS_FEELINGS = {
            "感动", "震撼", "沉思", "释然", "愤怒", "欣喜", "悲伤", "迷惑", "豁然开朗", "五味杂陈"
    };
    
    private final String[] FILL_WORDS_TIMES = {
            "夜深人静时", "雨天", "假期", "旅行途中", "心情低落时", "闲暇时光", "通勤路上", "睡前", "清晨", "周末午后"
    };
    
    private final String[] FILL_WORDS_STYLES = {
            "深刻", "幽默", "温暖", "悬疑", "治愈", "写实", "浪漫", "黑暗", "励志", "哲理"
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
        if (contentTemplate.contains("%s") && contentTemplate.indexOf("%s") != contentTemplate.lastIndexOf("%s")) {
            if (contentTemplate.indexOf("%s", contentTemplate.indexOf("%s") + 2) != contentTemplate.lastIndexOf("%s")) {
                content = String.format(contentTemplate, bookTitle, character, theme);
            } else {
                content = String.format(contentTemplate, bookTitle, style);
            }
        } else {
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