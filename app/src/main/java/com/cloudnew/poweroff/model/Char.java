package com.cloudnew.poweroff.model;

import com.cloudnew.poweroff.tools.MyPair;

import java.util.Vector;
import com.cloudnew.poweroff.tools.MyPair;

import com.cloudnew.poweroff.tools.MyPair;

import java.util.Vector;

public class Char {
    private String name;
    private String stand_str;
    private int stand_cnt,sample_num;
    private double stand_ave;
    private Vector<MyPair<String,Integer>> feat_str;

    public Char(String name, String stand_str, int stand_cnt,int sample_num, double stand_ave, Vector<MyPair<String, Integer>> feat_str) {
        this.name = name;
        this.stand_str = stand_str;
        this.stand_cnt = stand_cnt;
        this.sample_num = sample_num;
        this.stand_ave = stand_ave;
        this.feat_str = feat_str;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStand_str() {
        return stand_str;
    }

    public void setStand_str(String stand_str) {
        this.stand_str = stand_str;
    }

    public int getStand_cnt() {
        return stand_cnt;
    }

    public void setStand_cnt(int stand_cnt) {
        this.stand_cnt = stand_cnt;
    }

    public double getStand_ave() {
        return stand_ave;
    }

    public void setStand_ave(double stand_ave) {
        this.stand_ave = stand_ave;
    }

    public Vector<MyPair<String, Integer>> getFeat_str() {
        return feat_str;
    }

    public void setFeat_str(Vector<MyPair<String, Integer>> feat_str) {
        this.feat_str = feat_str;
    }

    public int getSample_num() {
        return sample_num;
    }

    public void setSample_num(int sample_num) {
        this.sample_num = sample_num;
    }
}
