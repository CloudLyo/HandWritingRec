package com.cloudnew.poweroff.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cloudnew.poweroff.R;

public class inputActivity extends AppCompatActivity {

    EditText editText;
    Button btn_ok;
    String str;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        editText = findViewById(R.id.my_edit);
        btn_ok = findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str = editText.getText().toString();
                Log.d("input",str);
                if (str.length()>1) Toast.makeText(inputActivity.this,"请输入单个字符",Toast.LENGTH_SHORT).show();
                else if (str.length()<=0) Toast.makeText(inputActivity.this,"请输入一个字符",Toast.LENGTH_SHORT).show();
                else{
                    Intent intent = new Intent();
                    intent.putExtra("ch",str);
                    setResult(RESULT_OK,intent);
                    finish();
                }
            }
        });
    }
}
