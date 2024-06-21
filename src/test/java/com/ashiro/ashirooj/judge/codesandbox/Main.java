package com.ashiro.ashirooj.judge.codesandbox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ashiro
 * @description
 */
class Main {
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> hashtable = new HashMap<Integer, Integer>();
        for (int i = 0; i < nums.length; ++i) {
            if (hashtable.containsKey(target - nums[i])) {
                return new int[]{hashtable.get(target - nums[i]), i};
            }
            hashtable.put(nums[i], i);
        }
        return new int[0];
    }

    public static void main(String[] args) {
        Main main = new Main();
        String nums = args[0];
        // 将字符串转回数组
        String[] splitString = nums.substring(1, nums.length() - 1).split(",");
        int[] newArray = new int[splitString.length];
        for (int i = 0; i < splitString.length; i++) {
            newArray[i] = Integer.parseInt(splitString[i]);
        }
        String target = args[1];
        int target1 = Integer.parseInt(target);
        int[] result = main.twoSum(newArray, target1);
        System.out.println(Arrays.toString(result));
    }
}