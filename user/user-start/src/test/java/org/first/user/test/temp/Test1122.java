package org.first.user.test.temp;


public class Test1122 {

    public static void main(String[] args) {
        String[] arr = new String[]{"aaa","bb","cccc","d"};
        dedSort(arr);
        System.out.println("去重后：");
        for(String a: arr) System.out.println(a);

    }

    static void dedSort(String[] strs){
        for(int si=0; si<strs.length-1; si++) {
            String minStr = strs[si];
            int minLen = strs[si].length();
            int minIdx = si;

            for (int i = si+1; i < strs.length; i++) {
                if (strs[i].length() < minLen) {
                    minStr = strs[i];
                    minLen = strs[i].length();
                    minIdx = i;
                }
            }
            if(minIdx!=si){
                String temp = strs[si];
                strs[si]=minStr;
                strs[minIdx]=temp;
            }
        }
    }
}
