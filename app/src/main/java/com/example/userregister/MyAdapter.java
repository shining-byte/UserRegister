package com.example.userregister;

import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


// 修改便签
public class MyAdapter extends AppCompatActivity implements View.OnClickListener {
    private Button saveButton, cancelButton;
    private EditText editText;
    private Intent intent;

    private String a,username, oldcontent;
    private String results,result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_adapter);
        intent = getIntent();
        editText = findViewById(R.id.list_content_cell);
        oldcontent = intent.getStringExtra("content");
        editText.setText(oldcontent);
        a = intent.getStringExtra("position");
        username = intent.getStringExtra("username");
        result = intent.getStringExtra("result");
        results = intent.getStringExtra("results");
        init();
    }

    protected void init(){

        saveButton = (Button)findViewById(R.id.save_add);
        cancelButton = (Button)findViewById(R.id.cancel_add);

        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.save_add:
                String content = editText.getText().toString();
                if (content.equals(oldcontent)){
                    finish();
                } else {
//                    if (result.equals("1")){
                        changenote(content, a,username);
                        Intent intent = new Intent();
//                        修改后的内容
                        intent.putExtra("read",content);
                        intent.putExtra("position",a );
//                        intent.putExtra("request","1" );
                        setResult(1,intent);
                        finish();
//                    } else if (results.equals("2")){
//                        Intent intent = new Intent();
//                        intent.putExtra("read",content);
//                        intent.putExtra("request","2" );
//                        setResult(1,intent);
//                        System.out.println("333333333333333333333333"+results);
//                        finish();
//                    }

                }

                break;

            case R.id.cancel_add:
                finish();
                break;


        }
    }

    public void changenote(final String contents, final String nos, final String usernames){


        //请求地址
        String url = "http://94.191.87.62:8001/modifynote/";    //注①
        String tag = "ModifyNote";    //注②

        //取得请求队列
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        //防止重复请求，所以先取消tag标识的请求队列
        requestQueue.cancelAll(tag);

        //创建StringRequest，定义字符串请求的请求方式为POST(省略第一个参数会默认为GET方式)
        final StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String result = String.valueOf(new JSONObject(response).get("status"));
                            //JSONObject jsonObject = (JSONObject) new JSONObject(response).get("token");  //注③
                            //String result = jsonObject.getString();  //注④
                            if (result.equals("1")) {  //注⑤
//                                System.out.println("注册成功");
                                Toast.makeText(MyAdapter.this, "修改成功", Toast.LENGTH_SHORT).show();

                                //做自己的登录成功操作，如页面跳转
                            } if(result.equals("0")) {
                                Toast.makeText(MyAdapter.this, "修改失败", Toast.LENGTH_SHORT).show();
                                //做自己的登录失败操作，如Toast提示
                            }
                        } catch (JSONException e) {
                            //做自己的请求异常操作，如Toast提示（“无网络连接”等）
                            Log.e("TAG", e.getMessage(), e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //做自己的响应错误操作，如Toast提示（“请稍后重试”等）
                Log.e("TAG", error.getMessage(), error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("content", contents);  //注⑥
                params.put("username", usernames);
                params.put("no", nos);

                return params;
            }
        };

        //设置Tag标签
        request.setTag(tag);

        //将请求添加到队列中
        requestQueue.add(request);

        // 注册成功, 跳转到登录页面
        // ... ... 待完成

    }

}
