package com.cloudnew.poweroff.tools;

public class StringHandler {
    public static int Levenshtein_dis(String s1,String s2){
        int dis[][] = new int[s1.length()+1][s2.length()+1];
        dis[0][0]=0;
        for (int i=1;i<=s1.length();++i) dis[i][0]=i;
        for (int j=1;j<=s2.length();++j) dis[0][j]=j;
        for (int i=1;i<=s1.length();++i){
            for (int j=1;j<=s2.length();++j){
                int cost=1;
                if (s1.charAt(i-1)==s2.charAt(j-1)) cost=0;
                int min;
                if (dis[i-1][j-1]+cost<dis[i][j-1]+1) min=dis[i-1][j-1]+cost;
                else min=dis[i][j-1]+1;
                if (min>dis[i-1][j]+1) min=dis[i-1][j]+1;
                dis[i][j]=min;
            }
        }
        return dis[s1.length()][s2.length()];
    }
    public static String handleString(String str){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(str.charAt(0));
        for (int i=1;i<str.length();++i){
            if (stringBuilder.charAt(stringBuilder.length()-1) != str.charAt(i)) stringBuilder.append(str.charAt(i));
        }
        return stringBuilder.toString();
    }
    public static char handleDirector(int x1,int y1,int x2,int y2){
        double cos = (double)(x2-x1)/Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
        if (cos<-1.0) cos=-1.0;
        if (cos>1.0) cos=1.0;
        double pi = Math.PI;
        double rec = Math.acos(cos);
        if (rec >= 15*pi/8||rec < pi/8) return '0';
        if (rec >= pi/8 && rec <3*pi/8) return '1';
        if (rec >=3*pi/8 && rec < 5*pi/8) return '2';
        if (rec >=5*pi/8 && rec < 7*pi/8) return '3';
        if (rec >=7*pi/8 && rec < 9*pi/8) return '4';
        if (rec >=9*pi/8 && rec < 11*pi/8) return '5';
        if (rec >=11*pi/8 && rec < 13*pi/8) return '6';
        if (rec >=13*pi/8 && rec < 15*pi/2) return '7';
        return '8';
    }
}
