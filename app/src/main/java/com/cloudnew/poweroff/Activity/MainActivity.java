package com.cloudnew.poweroff.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudnew.poweroff.Adapter.CharAdapter;
import com.cloudnew.poweroff.View.MyView;
import com.cloudnew.poweroff.R;
import com.cloudnew.poweroff.controller.ModHandler;
import com.cloudnew.poweroff.model.Result;
import com.cloudnew.poweroff.controller.XMLHandler;
import com.cloudnew.poweroff.model.Char;
import com.cloudnew.poweroff.tools.MyPair;
import com.cloudnew.poweroff.tools.StringHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MainActivity extends AppCompatActivity {
    String url = "/sdcard/trainMod.xml";
    //String server = "http://192.168.43.27:8080";
    String server = "http://2l070934x3.51mypc.cn:8080";
    String serverPath = server+"/trainMod.xml";
    String updataUrl = server+"/updata";
    XMLHandler xmlHandler;
    ModHandler modHandler;
    CharAdapter charAdapter;
    LinearLayoutManager layoutManager;
    String nowString = new String();
    ArrayList<MyPair<Integer, Integer>> myPath = new ArrayList<>();
    RecyclerView recyclerView;
    Button btn_clear, btn_add;
    ArrayList<Result> mCharList = new ArrayList<>();
    Vector<Char> mod;
    MyView myView;
    long time = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initView();
        try {
            initMod();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        charAdapter = new CharAdapter(mCharList);
        recyclerView = findViewById(R.id.myRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(charAdapter);
        charAdapter.setOnItemClickListener(new CharAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                modHandler.updataModByChar(nowString,((TextView)view).getText().toString());
                btn_clear.callOnClick();
            }
        });
        myView = findViewById(R.id.my_canvas);
        myView.setOnDrawListenler(new MyView.onDrawListenler() {
            @Override
            public void onDraw(int x, int y) {
                //if (System.currentTimeMillis()-time>100){
                myPath.add(new MyPair<>(x, y));
                if (myPath.size() >= 2) {
                    int x1, x2, y1, y2;
                    x1 = myPath.get(myPath.size() - 2).getT1();
                    x2 = myPath.get(myPath.size() - 1).getT1();
                    y1 = myPath.get(myPath.size() - 2).getT2();
                    y2 = myPath.get(myPath.size() - 1).getT2();
                    nowString = StringHandler.handleString(nowString + StringHandler.handleDirector(x1, y1, x2, y2));
                    modHandler.getResault(nowString, mCharList,10);
                    charAdapter.notifyDataSetChanged();
                }

                //}
                time = System.currentTimeMillis();
            }
        });

        btn_clear = findViewById(R.id.btn_clear);
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myView.clearScreen();
                myPath.clear();
                nowString = "";
                mCharList.clear();
                charAdapter.notifyDataSetChanged();
            }
        });

        btn_add = findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, inputActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    private void initMod() throws BrokenBarrierException, InterruptedException {
        xmlHandler = new XMLHandler(url, this, serverPath);
        if (xmlHandler.getDocument() == null) Log.d("msg4", "null");
        mod = xmlHandler.getMod();
        modHandler = new ModHandler(mod);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            final char ch = data.getStringExtra("ch").charAt(0);
            modHandler.updataModByChar(nowString, ch + "");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    Request request = new Request.Builder().url(updataUrl+"?name = "+ch+"&str = "+nowString).build();
                    try {
                        okHttpClient.newCall(request).execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            btn_clear.callOnClick();
        }
    }

    @Override
    protected void onStop() {
        xmlHandler.updataMod(mod);
        super.onStop();
    }
}
