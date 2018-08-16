package com.cloudnew.poweroff.controller;

import android.util.Log;

import com.cloudnew.poweroff.model.Char;
import com.cloudnew.poweroff.model.Result;
import com.cloudnew.poweroff.tools.MyPair;
import com.cloudnew.poweroff.tools.StringHandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Vector;

public class ModHandler {
    private Vector<Char> Mod;
    private int maxSample = 10;

    public ModHandler(Vector<Char> mod) {
        Mod = mod;
    }

    public Vector<Char> getMod() {
        return Mod;
    }

    public void getResault(String str, ArrayList<Result> results,int num) {
        Result result, result1;
        results.clear();
        HashMap<String, Integer> map = new HashMap<>();
        PriorityQueue<Result> p = new PriorityQueue<>(20, new Comparator<Result>() {
            @Override
            public int compare(Result o1, Result o2) {
                return o2.dis - o1.dis;
            }
        });
        Char ch;
        for (int i = 0; i < Mod.size(); ++i) {
            ch = Mod.elementAt(i);
            result = new Result(ch.getName(), StringHandler.Levenshtein_dis(str, ch.getStand_str()), i);
            if (!map.containsKey(result.str)) {
                p.add(result);
                map.put(result.str, result.dis);
            } else if (map.get(result.str) > result.dis) {
                map.put(result.str, result.dis);
                Iterator<Result> iterator = p.iterator();
                while (iterator.hasNext()) {
                    result1 = iterator.next();
                    if (result1.str == result.str) {
                        result1.dis = result.dis;
                        break;
                    }
                }
            }
            if (p.size() > num) p.poll();
        }
        while (!p.isEmpty()) {
            results.add(p.peek());
            p.poll();
        }
        Result t;
        for (int i = 0; i < results.size() - i - 1; ++i) {
            t = results.get(i);
            results.set(i, results.get(results.size() - i - 1));
            results.set(results.size() - i - 1, t);
        }
    }

    public void updataModById(String str, int index) {
        Char ch = Mod.elementAt(index);
        double stand_ave;
        int stand_dis;
        int sum_dis;
        double ave;
        if (str.equals(ch.getStand_str())) {
            if (ch.getSample_num() < maxSample) {
                ch.setStand_cnt(ch.getStand_cnt() + 1);
                ch.setSample_num(ch.getSample_num() + 1);
                stand_ave = (ch.getStand_ave() * (ch.getSample_num() - 2)) / (double)(ch.getSample_num() - 1);
                ch.setStand_ave(stand_ave);
            }
            return;
        }
        stand_dis = StringHandler.Levenshtein_dis(str, ch.getStand_str());
        sum_dis = stand_dis * ch.getStand_cnt();
        stand_ave = (ch.getStand_ave() * (ch.getSample_num() - 1+0.0001) + stand_dis) / (double)ch.getSample_num();
        int id = -1;
        for (int i = 0; i < ch.getFeat_str().size(); ++i) {
            if (str.equals(ch.getFeat_str().elementAt(i).getT1())) {
                //if (ch.getSample_num()<maxSample) ch.getFeat_str().elementAt(i).setT2(ch.getFeat_str().elementAt(i).getT2()+1);
                id = i;
                continue;
            }
            sum_dis += StringHandler.Levenshtein_dis(str, ch.getFeat_str().elementAt(i).getT1()) * ch.getFeat_str().elementAt(i).getT2();
        }
        ave = sum_dis / (double)ch.getSample_num();
        if (id < 0) {
            if (ave <= stand_ave) {
                ch.getFeat_str().add(new MyPair<>(ch.getStand_str(), ch.getStand_cnt()));
                if (ch.getSample_num() >= maxSample) {
                    ch.getFeat_str().lastElement().setT2(ch.getFeat_str().lastElement().getT2() - 1);
                    if (ch.getSample_num() > 1)
                        ave = (sum_dis - StringHandler.Levenshtein_dis(str, ch.getStand_str()) * ch.getStand_cnt()) / (double) (ch.getSample_num() - 1);
                    else ave = 0;
                    if (ch.getFeat_str().lastElement().getT2() <= 0)
                        ch.getFeat_str().removeElementAt(ch.getFeat_str().size() - 1);
                } else {
                    ch.setSample_num(ch.getSample_num() + 1);
                }
                ch.setStand_str(str);
                ch.setStand_cnt(1);
                ch.setStand_ave(ave);
            } else {
                if (ch.getSample_num() < maxSample) {
                    ch.getFeat_str().add(new MyPair<>(str, 1));
                    ch.setSample_num(ch.getSample_num() + 1);
                }
            }
        } else if (id >= 0) {
            if (ave < stand_ave) {
                String t_str = ch.getFeat_str().elementAt(id).getT1();
                int t_cnt = ch.getFeat_str().elementAt(id).getT2();
                if (ch.getSample_num() >= maxSample) {
                    ch.setStand_cnt(ch.getStand_cnt() - 1);
                    ave = (sum_dis - StringHandler.Levenshtein_dis(str, ch.getStand_str()) * ch.getStand_cnt()) / (double)(ch.getSample_num() - 1);
                } else {
                    ch.setSample_num(ch.getSample_num() + 1);
                }
                if (ch.getStand_cnt() > 0) {
                    ch.getFeat_str().elementAt(id).setT1(ch.getStand_str());
                    ch.getFeat_str().elementAt(id).setT2(ch.getStand_cnt());
                } else {
                    ch.getFeat_str().remove(id);
                }
                ch.setStand_str(t_str);
                ch.setStand_cnt(t_cnt);
                ch.setStand_ave(ave);
            } else {
                if (ch.getSample_num() < maxSample) {
                    ch.getFeat_str().elementAt(id).setT2(ch.getFeat_str().elementAt(id).getT2() + 1);
                    ch.setSample_num(ch.getSample_num() + 1);
                }
            }
        }
    }

    public void updataModByChar(String str, String name) {
        ArrayList<Result> results = new ArrayList<>();
        getResault(str, results,3);
        boolean flag = false;
        Result result = null;
        for (Result result_ : results) {
            if (result_.str.equals(name)) {
                result = result_;
                flag = true;
                break;
            }
        }
        if (flag) {
            updataModById(str, result.index);
        } else {
            Char ch = new Char(name, str, 1, 1, 0x3f3f3f3f, new Vector<MyPair<String, Integer>>());
            Mod.add(ch);
        }
    }
}