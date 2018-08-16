package com.cloudnew.poweroff.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.cloudnew.poweroff.model.Char;
import com.cloudnew.poweroff.tools.MyPair;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;

public class XMLHandler {
    private Document document;
    private String url;
    private Context context;
    private String serverPath;
    private boolean isOk = false;
    public XMLHandler(String url, Context context, String serverPath) {
        this.url = url;
        this.serverPath = serverPath;
        this.context = context;
        setDocument();
    }

    /*
    ** 获取xml文本
     */
    @SuppressLint("HandlerLeak")
    public void setDocument() {
        try {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (getFileFromService()) isOk = true;
                }
            });
            t.start();
            t.join();
            if (!isOk) {
                Toast.makeText(context, "获取网络模型失败，将使用本地模型", Toast.LENGTH_SHORT).show();
                Log.d("serverresult", "获取网络模型失败，将使用本地模型");
            }else{
                Toast.makeText(context, "获取网络模型成功", Toast.LENGTH_SHORT).show();
            }
            File f = new File(url);
            if (!f.exists() || f.length() == 0) {
                f.createNewFile();
                FileOutputStream outputStream = new FileOutputStream(f);
                outputStream.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<chars>\n</chars>".getBytes());
            }
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            document = dBuilder.parse(f);
            document.getDocumentElement().normalize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean getFileFromService() {
        InputStream is;
        File file;
        URLConnection urlcon;
        FileOutputStream os;
        try {
            URL ur = new URL(serverPath);
            urlcon = ur.openConnection();
            urlcon.setConnectTimeout(1000);
            is = urlcon.getInputStream();
            file = new File(url);
            os = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) > 0) {
                os.write(buffer, 0, len);
            }
            is.close();
            os.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Document getDocument() {
        return document;
    }

    /*
     ** 获取模型
     */
    public Vector<Char> getMod() {
        Vector<Char> v = new Vector<Char>();
        String name;
        String stand_str;
        int stand_cnt, sample_num;
        double stand_ave;
        MyPair<String, Integer> pair;
        Vector<MyPair<String, Integer>> feat_str;
        Char ch;
        if (document == null) Log.d("msg3", "null");
        NodeList nList = document.getElementsByTagName("char");
        for (int i = 0; i < nList.getLength(); ++i) {
            Node nNode = nList.item(i);
            feat_str = new Vector<>();
            Element element = (Element) nNode;
            name = element.getAttribute("name");
            stand_str = element.getAttribute("stand_str");
            stand_ave = Double.parseDouble(element.getAttribute("stand_ave"));
            stand_cnt = Integer.parseInt(element.getAttribute("stand_cnt"));
            sample_num = Integer.parseInt(element.getAttribute("sample_num"));
            NodeList nList1 = element.getElementsByTagName("feature");
            for (int j = 0; j < nList1.getLength(); ++j) {
                pair = new MyPair<>(((Element) nList1.item(j)).getAttribute("str"), Integer.parseInt(((Element) nList1.item(j)).getAttribute("cnt")));
                feat_str.add(pair);
            }
            ch = new Char(name, stand_str, stand_cnt, sample_num, stand_ave, feat_str);
            v.add(ch);
        }
        return v;
    }

    /*
    ** 更新模型
     */
    public void updataMod(Vector<Char> newMod) {
        Node node = document.getElementsByTagName("chars").item(0);
        document.removeChild(node);
        Element root = document.createElement("chars");
        for (int i = 0; i < newMod.size(); ++i) {
            Char a = newMod.elementAt(i);
            Element newChar = document.createElement("char");
            newChar.setAttribute("name", a.getName());
            newChar.setAttribute("stand_str", a.getStand_str());
            newChar.setAttribute("stand_cnt", Integer.toString(a.getStand_cnt()));
            newChar.setAttribute("stand_ave", Double.toString(a.getStand_ave()));
            newChar.setAttribute("sample_num", Integer.toString(a.getSample_num()));
            Vector<MyPair<String, Integer>> feature_v = a.getFeat_str();
            for (int j = 0; j < feature_v.size(); ++j) {
                Element feature = document.createElement("feature");
                feature.setAttribute("str", feature_v.elementAt(j).getT1());
                feature.setAttribute("cnt", Integer.toString(feature_v.elementAt(j).getT2()));
                newChar.appendChild(feature);
            }
            root.appendChild(newChar);
        }
        document.appendChild(root);
        try {
            TransformerFactory tfFactory = TransformerFactory.newInstance();
            Transformer tf = tfFactory.newTransformer();
            tf.transform(new DOMSource(document), new StreamResult(new File(url)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
