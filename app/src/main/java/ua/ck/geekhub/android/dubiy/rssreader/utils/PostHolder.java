package ua.ck.geekhub.android.dubiy.rssreader.utils;

import java.util.ArrayList;

import ua.ck.geekhub.android.dubiy.rssreader.entity.HabraPost;

/**
 * Created by Gary on 23.11.2014.
 */
public class PostHolder {
//    private static HabraPost[] posts = {};
    private static ArrayList<HabraPost> habraPosts = new ArrayList<HabraPost>();
    private static long lastUpdate = 0;

    public static void setPosts(ArrayList<HabraPost> habraPosts) {
        PostHolder.habraPosts.addAll(habraPosts);
        PostHolder.lastUpdate = System.currentTimeMillis();
    }

    public static ArrayList<HabraPost> getPosts() {
        return PostHolder.habraPosts;
    }

    public static HabraPost getPost(int position) {
        return PostHolder.habraPosts.get(position);

    }

    public static boolean needUpdate() {
        if (System.currentTimeMillis() - PostHolder.lastUpdate > 10 * 1000 || PostHolder.habraPosts.size() == 0) {
            return true;
        }
        return false;
    }


    public PostHolder() {
    }
}
