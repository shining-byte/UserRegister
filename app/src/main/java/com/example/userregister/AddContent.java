package com.example.userregister;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.HashMap;
import java.util.Map;


/**
 * Created by flyan on 18-6-20.
 * 添加便签
 */

public class AddContent extends AppCompatActivity implements View.OnClickListener {
    public final static String FILE_SAVE_PATH = "/storage/emulated/0/HelloNotes/";

    private String whatValue;
    private Button saveButton, cancelButton;
    private EditText ettext;
    private ImageView c_img;
    private VideoView v_video;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * 解决FileUriExposedException
         */
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_content);
        init();
    }



    private void init(){
        //view
        whatValue = getIntent().getStringExtra("what");
        Toast.makeText(this, whatValue, Toast.LENGTH_LONG).show();
        saveButton = (Button)findViewById(R.id.save_add);
        cancelButton = (Button)findViewById(R.id.cancel_add);
        ettext = (EditText)findViewById(R.id.ettext_add);
        c_img = (ImageView)findViewById(R.id.c_img_add);
        v_video = (VideoView)findViewById(R.id.v_video_add);
        //判断MainActivity传过来的值，确定用户需要添加的是什么内容
        switch (whatValue){
            case "text":
                c_img.setVisibility(View.GONE);
                v_video.setVisibility(View.GONE);
                break;

        }

        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.save_add:
                String content = ettext.getText().toString();
                postnote(content);
                Intent intent = new Intent();
                intent.putExtra("read",content);
                // 添加便签为2
                setResult(2,intent);
                finish();
                break;
            case R.id.cancel_add:
                finish();
                break;
        }
    }

    /**
     * 添加到数据库
     * @param content
     */
    public void postnote(final String content){

        Intent intent = getIntent();

        final String username = intent.getStringExtra("username");
        //检验账号和密码是否正确
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        //请求地址
        String url = "http://94.191.87.62:8001/postnote/";    //注①
        String tag = "PostNote";    //注②

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
                                Toast.makeText(AddContent.this, "保存成功", Toast.LENGTH_SHORT).show();

                                //做自己的登录成功操作，如页面跳转
                            } if(result.equals("0")) {
//                                Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                                //做自己的登录失败操作，如Toast提示
                            }
                        } catch (JSONException e) {
                            //做自己的请求异常操作，如Toast提示（“无网络连接”等）
                            Log.e("TAG", e.getMessage(), e);
                        }
                    }
                },
                new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //做自己的响应错误操作，如Toast提示（“请稍后重试”等）
                Log.e("TAG", error.getMessage(), error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("content", content);  //注⑥
                params.put("username", username);

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

