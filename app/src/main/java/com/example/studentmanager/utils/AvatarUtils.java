package com.example.studentmanager.utils;

import com.example.studentmanagement.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 头像工具类，用于随机获取头像
 */
public class AvatarUtils {

    private static final List<Integer> AVATAR_LIST = new ArrayList<>();
    private static final Random RANDOM = new Random();

    static {
        AVATAR_LIST.add(R.drawable.avatar_img);
        AVATAR_LIST.add(R.drawable.avatar_img_02);
        AVATAR_LIST.add(R.drawable.avatar_img_03);
        AVATAR_LIST.add(R.drawable.avatar_img_04);
        AVATAR_LIST.add(R.drawable.avatar_img_05);
        AVATAR_LIST.add(R.drawable.avatar_img_06);
        AVATAR_LIST.add(R.drawable.avatar_img_07);
        AVATAR_LIST.add(R.drawable.avatar_img_08);
        AVATAR_LIST.add(R.drawable.avatar_img_09);
        AVATAR_LIST.add(R.drawable.avatar_img_10);
        AVATAR_LIST.add(R.drawable.avatar_img_11);
        AVATAR_LIST.add(R.drawable.avatar_img_12);
    }

    /**
     * 从预定义的列表中随机获取一个头像资源ID
     * @return a int 头像的drawable资源ID
     */
    public static int getRandomAvatar() {
        if (AVATAR_LIST.isEmpty()) {
            return R.drawable.ic_avator; // 如果列表为空，返回一个默认的备用头像
        }
        return AVATAR_LIST.get(RANDOM.nextInt(AVATAR_LIST.size()));
    }
} 