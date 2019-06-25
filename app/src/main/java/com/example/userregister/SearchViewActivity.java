package com.example.userregister;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import android.widget.Toast;
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

public class SearchViewActivity extends AppCompatActivity implements View.OnClickListener {
    private String username;
    private ListView listView;
    private StringAdapter stringAdapter;
    private List<String> contentlist = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_view);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        init();

    }

        public void init(){

        listView = findViewById(R.id.search_view_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent goSelect = new Intent(SearchViewActivity.this, MyAdapter.class);
                goSelect.putExtra("content", contentlist.get(position));
                goSelect.putExtra("position", String.valueOf(position));
                goSelect.putExtra("username", username);
                startActivityForResult(goSelect,1);
            }
        });
        // listView 监听器
        stringAdapter = new StringAdapter(SearchViewActivity.this,R.layout.item_list,contentlist);
        listView.setAdapter(stringAdapter);

        SearchView searchView = findViewById(R.id.search_view);
        searchView.setSubmitButtonEnabled(false);

         //searchView.setSuggestionsAdapter(adapter)
        //监听mSearchAutoComplete文本框焦点改变，例如点返回键后SearchView折叠mSearchAutoComplete失去了焦点时执行
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
               //Log.i(TAG,"onFocusChange");

            }
        });
        //searchView折叠监听，当前项目中点击返回键时执行
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {

            @Override
            public boolean onClose() {
                //Log.i(TAG,"OnCloseListener");
                return false;
            }
        });

        //监听文本变化，调用查询
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            //X右边的"搜索提交"按钮
            @Override
            public boolean onQueryTextSubmit(String text) {
                contentlist.clear();
                searchnote(username, text);
                Toast.makeText(SearchViewActivity.this, ":"+text, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                // 文本改变的时候回调

                return false;
            }
        });



        }

        public void searchnote(final String username ,final String word){


        //请求地址
        String url = "http://94.191.87.62:8001/searchnote/";    //注①
        String tag = "SearchNote";    //注②

        //取得请求队列
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        //防止重复请求，所以先取消tag标识的请求队列
        requestQueue.cancelAll(tag);

        //创建StringRequest，定义字符串请求的请求方式为POST(省略第一个参数会默认为GET方式)

            final StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    JSONObject jsonobject = null;
                    JSONArray json = null;
                    try {
                        jsonobject = new JSONObject(response);
                        json = jsonobject.getJSONArray("results");
                        for(int i = 0;i < json.length();i++){
                                String content = json.get(i).toString();
                                contentlist.add(content);
                            }
                             stringAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //做自己的请求异常操作，如Toast提示（“无网络连接”等）
                            //Log.e("TAG", e.getMessage(), e);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("TAG", error.getMessage(), error);
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                     Map<String, String> params = new HashMap<>();
                     params.put("username", username);
                     params.put("word", word);

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

    @Override
    public void onClick(View v) {

    }

}
