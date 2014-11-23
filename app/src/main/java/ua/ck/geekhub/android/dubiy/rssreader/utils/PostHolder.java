package ua.ck.geekhub.android.dubiy.rssreader.utils;

import ua.ck.geekhub.android.dubiy.rssreader.entity.HabraPost;

/**
 * Created by Gary on 23.11.2014.
 */
public class PostHolder {
    private static HabraPost[] posts = {};
    private static long lastUpdate = 0;

    public static void setPosts(HabraPost[] posts) {
        PostHolder.posts = posts;
        PostHolder.lastUpdate = System.currentTimeMillis();
    }

    public static HabraPost[] getPosts() {
        return PostHolder.posts;
    }

    public static HabraPost getPost(int position) {
        return PostHolder.posts[position];
    }

    public static boolean needUpdate() {
        if (System.currentTimeMillis() - PostHolder.lastUpdate > 10 * 1000 || PostHolder.posts.length == 0) {
            return true;
        }
        return false;
    }


    public PostHolder() {
    }
}
