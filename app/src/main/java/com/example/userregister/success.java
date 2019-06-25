package com.example.userregister;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class success extends AppCompatActivity implements View.OnClickListener{
    private Button textButton;
    private ListView listView;

    private StringAdapter stringAdapter;

    private String username;
    private Cursor cursor;
    private SQLiteDatabase daReader;
    private MyAdapter myAdapter;
    private List<String> contentlist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);
        init();
        getnote();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void init(){
        //view
        listView = (ListView)findViewById(R.id.listView_main);
        textButton = (Button) findViewById(R.id.text_button_main);

//            用户名
        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        //event
        textButton.setOnClickListener(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //cursor.moveToPosition(position)
//                System.out.println("------------------------------------------position"+position);
//                点击后跳转到编辑页面
                Intent goSelect = new Intent(success.this, MyAdapter.class);
                goSelect.putExtra("content", contentlist.get(position));
                goSelect.putExtra("position", String.valueOf(position));
                goSelect.putExtra("username", username);
                goSelect.putExtra("result", "1");
                startActivityForResult(goSelect,1);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                TextView tv = (TextView)parent.findViewById(R.id.show_text);
                AlertDialog.Builder dialog = new AlertDialog.Builder(success.this);
                dialog.setTitle("是否删除");
//                dialog.setMessage()
                dialog.setCancelable(false);
                dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

//                        点击确认方法后执行的
                    }
                });
                dialog.setNegativeButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletenote(position);
                        contentlist.remove(position);
                        stringAdapter.notifyDataSetChanged();
//                   点击取消后执行的
                    }
                });
                dialog.show();
                return true;
            }
        });
        // listView 监听器
        stringAdapter = new StringAdapter(success.this,R.layout.item_list,contentlist);
        listView.setAdapter(stringAdapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
      //  super.onActivityResult(requestCode, resultCode, data);
//        1为修改便签,需要获取位置
//        2为添加便签,新的便签追加到尾部
        System.out.println("--------------------------"+requestCode+resultCode);
        if (resultCode == 1){
                String con = data.getStringExtra("read");

                    int aa = Integer.parseInt(data.getStringExtra("position"));
                    contentlist.remove(aa);
                    contentlist.add(0,con);
                    stringAdapter.notifyDataSetChanged();
                }
        if (resultCode == 2)
                {
                    String con = data.getStringExtra("read");
                    contentlist.add(0,con);
                    stringAdapter.notifyDataSetChanged();
                }

    }

    /**
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        String flag = ((Button)v).getText().toString().trim();
        Intent addWhat = new Intent(success.this, AddContent.class);
        switch (flag){
            case "添加":
                Intent intent = getIntent();
                String username = intent.getStringExtra("username");
                addWhat.putExtra("what", "text");
                addWhat.putExtra("username", username);
                addWhat.putExtra("results", "2");
                startActivityForResult(addWhat,1);
                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.action_search:
                Intent intent = new Intent(success.this,SearchViewActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                break;

                default:

        }

        return super.onOptionsItemSelected(item);
    }

    public void getnote(){

        Intent intent = getIntent();

        final String username = intent.getStringExtra("username");
        System.out.println("username-----------------------------------"+username);
        //检验账号和密码是否正确

        //请求地址
        String url = "http://94.191.87.62:8001/getnote/";    //注①
        String tag = "GetNote";    //注②

        //取得请求队列
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        //防止重复请求，所以先取消tag标识的请求队列
        requestQueue.cancelAll(tag);

        //创建StringRequest，定义字符串请求的请求方式为POST(省略第一个参数会默认为GET方式)
        final StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println(response);
                            JSONObject jsonobject = new JSONObject(response);
                            JSONArray json = jsonobject.getJSONArray("results");
//                            System.out.println("qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq"+json + json.length());
                            for(int i = 0;i < json.length();i++){
                                //JSONObject jsonObject = json.getJSONObject(i);
//                                String content = jsonObject.getInt(i);
                                String content = json.get(i).toString();
//                                Log.d("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee",content+i);/
                                //System.out.println(content);
                                contentlist.add(content);
                             //   a = a - 1;
                            }
                             stringAdapter.notifyDataSetChanged();

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

//                params.put("content", content);  //注⑥
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

    public void deletenote(final int position){
        final String no = String.valueOf(position);
        Intent intent = getIntent();

        final String username = intent.getStringExtra("username");
        //检验账号和密码是否正确

        //请求地址
        String url = "http://94.191.87.62:8001/deletenote/";    //注①
        String tag = "DeleteNote";    //注②

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
                                Toast.makeText(success.this, "删除成功", Toast.LENGTH_SHORT).show();

                                //做自己的登录成功操作，如页面跳转
                            } if(result.equals("0")) {
                                Toast.makeText(success.this, "删除失败", Toast.LENGTH_SHORT).show();
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

//                params.put("content", content);  //注⑥
                params.put("username", username);
                params.put("no", no);

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

