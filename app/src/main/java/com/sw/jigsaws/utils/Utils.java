package com.sw.jigsaws.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by suiyujie on 2015/10/30.
 */
public class Utils {

    // 随机数
    private final static Random sRandom = new Random(System.currentTimeMillis());

    /**
     * 获取多个参数的最小值
     */
    public static int min(int... params) {
        int min = params[0];

        for (int param : params) {
            if (param < min)
                min = param;
        }
        return min;
    }

    /**
     * 生成指定范围内的随机整数。大于等于  小于等于
     */
    public static int randomInt(final int floor, final int ceil) {
        if (floor > ceil) {
            return floor;
        }

        int realFloor = floor + 1;
        int realCeil = ceil + 1;

        return (sRandom.nextInt(realCeil) % (realCeil - realFloor + 1) + realFloor) - 1;
    }

    /**
     * 随机选择数组。
     */
    public static List<?> randomSelect(List<?> items, int num) {
        // 采用删除差额方式

        ArrayList<Object> list = new ArrayList<Object>(items.size());
        list.addAll(items);

        if (num >= items.size()) {
            return list;
        }

        do {
            int index = Utils.randomInt(0, list.size() - 1);
            list.remove(index);
        } while (list.size() > num);

        return list;
    }

    /**
     * 随机选择数组,排除部分。
     */
    public static <T> List<T> randomSelect(List<T> items, int num, List<T> excludes) {
        // 采用删除差额方式

        ArrayList<T> list = new ArrayList<T>(items.size());
        list.addAll(items);

        if (num >= items.size()) {
            return list;
        }

        for (Object object : excludes) {
            list.remove(object);
        }

        do {
            int index = Utils.randomInt(0, list.size() - 1);
            list.remove(index);
        } while (list.size() > num);

        return list;
    }

    public static <K, V> K randomSelectMapKey(HashMap<K, V> items) {
        if (items == null || items.isEmpty()) {
            return null;
        }
        List<K> list = new ArrayList<K>(items.keySet());
        return (K) randomSelectOne(list);
    }

    public static <K, V> V randomSelectMapValue(HashMap<K, V> items) {
        if (items == null || items.isEmpty()) {
            return null;
        }
        List<V> list = new ArrayList<V>(items.values());
        return (V) randomSelectOne(list);
    }

    /**
     * 随机选择数组。
     */
    public static List<?> randomSelectWithRepeat(List<?> items, int num) {
        ArrayList<Object> list = new ArrayList<Object>();

        while (list.size() < num) {
            list.add(items.get(Utils.randomInt(0, items.size() - 1)));
        }

        return list;
    }

    /**
     * 随机一个
     *
     * @param <T>
     */
    public static <T> T randomSelectOne(List<T> items) {
        if (items == null || items.isEmpty()) {
            return null;
        }
        int index = Utils.randomInt(0, items.size() - 1);
        return (T) items.get(index);
    }
}
