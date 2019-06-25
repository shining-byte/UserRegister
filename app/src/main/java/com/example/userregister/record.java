package com.example.userregister;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class record extends AppCompatActivity{


    // 密码
    private EditText et_password;
    // 注册按钮
    private Button btn_register;
    // 用户名
    private EditText et_name;
    // 头像获取按钮
    private Button btn_head;

    // 密码登录
    private Button btn;
    //头像登录
    private Button btn_p;
    // 姓名字段
    private String name;
    // 密码

    private String str_picture;

    private String password;
    // onActivityResult() requestCode 值
    public static final int TAKE_PHOTO = 1;
    // 照片路径
    private Uri imageUri;
    // 头像ImageView
    private ImageView picture;
    //

    Bitmap bitmap;
    FileOutputStream fileOutputStream;

    private File file;

    public static final int CHOOSE_PHOTO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String imgpath = intent.getStringExtra("imagePath");
//        Log.e("imgpath", "----------------------"+imgpath);
        Bitmap bitmap = BitmapFactory.decodeFile(imgpath);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        { StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        et_name = (EditText) findViewById(R.id.et_name);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_register = (Button) findViewById(R.id.btn_send);
        btn_head = (Button) findViewById(R.id.btn_head);
        picture = (ImageView) findViewById(R.id.picture);
        btn_p = (Button) findViewById(R.id.btn_p);

        et_name.setText(username);
        picture.setImageBitmap(bitmap);
        btn = (Button) findViewById(R.id.btn);



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = et_name.getText().toString();
                password = et_password.getText().toString();
                passwordlogin(name, password);
            }
        });
       /* btn_head.setOnClickListener(this);


        btn_register.setOnClickListener(this);*/
//       选择图库
        btn_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPhotoFromAlbum();
            }
        });

//        跳回注册
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();

            }
        });
//        头像登录
        btn_p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = et_name.getText().toString();
                password = et_password.getText().toString();
                Bitmap bitmap = ((BitmapDrawable)picture.getDrawable()).getBitmap();


                str_picture = bitmapToBase64(bitmap);

                login(name, str_picture ,password);
            }
        });
    }





    public void register(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

//    /**
//     * 生成对应的按钮响应事件
//     * @param v
//     */
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_head:
////               getHead();
//                getPhotoFromAlbum();
//                break;
//            case R.id.btn_send:
//                name = et_name.getText().toString();
//                password = et_password.getText().toString();
//                login(name, password);
//                break;
//        }
//    }

    /**
     * 登录功能
     * @param accountNumber
     * @param password
     */
    public void login(final String accountNumber, final String str_picture, final String password){

        //检验账号和头像格式是否正确
        if (TextUtils.isEmpty(accountNumber)) {
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(str_picture)) {
            Toast.makeText(this, "头像不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        //请求地址
        String url = "http://94.191.87.62:8001/headlogin/";    //注①
        String tag = "headlogin";    //注②

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
//                                System.out.println("登录成功");
                                Toast.makeText(record.this, "登录成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(record.this, success.class);
                                intent.putExtra("username", accountNumber);
//                                intent.putExtra("imagePath", imagePath);
                                startActivity(intent);
                                //做自己的登录成功操作，如页面跳转
                            } if(result.equals("0")) {
                                Toast.makeText(record.this, "登录失败", Toast.LENGTH_SHORT).show();
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
                params.put("username", accountNumber);  //注⑥
                params.put("password", password);
                if (str_picture != null){
                    params.put("picture", str_picture);
                }
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



        public void passwordlogin(final String accountNumber, final String password){

        //检验账号和密码是否正确
        if (TextUtils.isEmpty(accountNumber)) {
            Toast.makeText(this, "姓名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        //请求地址
        String url = "http://94.191.87.62:8001/passwordlogin/";    //注①
        String tag = "passwordlogin";    //注②

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
//                                System.out.println("登录成功");
                                Toast.makeText(record.this, "登录成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(record.this, success.class);
                                intent.putExtra("username", accountNumber);
//                                intent.putExtra("imagePath", imagePath);
                                startActivity(intent);
                                //做自己的登录成功操作，如页面跳转
                            } if(result.equals("0")) {
                                Toast.makeText(record.this, "登录失败", Toast.LENGTH_SHORT).show();
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
                params.put("username", accountNumber);  //注⑥
                params.put("password", password);
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
    /**
     * 返回图片结果
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

//        switch (requestCode)
//        {
//            case TAKE_PHOTO:
//            {
//                if(resultCode == RESULT_OK)
//                {
//                    //将拍摄的照片显示出来
//                    try
//                    {
//                        Bitmap bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
//                        picture.setImageBitmap(bitmap);
//                    }
//                    catch (FileNotFoundException e)
//                    {
//                        e.printStackTrace();
//                    }
//                }
//            }
//            break;
//            default:
//                break;
//        }
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKitKat(data);

                    } else {
                        handeleImageBeforeKitKat(data);
                    }
                    break;
                }
            default:
                break;
        }
    }

    // 从相册中选择照片
    public void getPhotoFromAlbum() {
        if (ContextCompat.checkSelfPermission(record.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(record.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},1);;
        } else {
            openAlbum();
        }
    }


    /**
     * 打开相册
     */
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);  // 打开相册
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the perssion", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    /**
     * 安卓4.4以上版本的处理方法
     * @param data
     */
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
//    Toast.makeText(this,"到了handleImageOnKitKat(Intent data)方法了", Toast.LENGTH_LONG).show();
        String imagePath = null;
        Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(this, uri)){
            //如果是 document 类型的 Uri，则通过 document id 处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];//解析出数字格式的 id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            //如果是 content 类型的 uri ， 则使用普通方式处理
            imagePath = getImagePath(uri, null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            //如果是 file 类型的 Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath);//显示选中的图片
    }

    /**
     *  安卓4.4一下版本的处理方法
     * @param data
     */
    private void handeleImageBeforeKitKat(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    /**
     * 获取相片的路径
     * @param uri
     * @param selection
     * @return
     */
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过 Uri 和 selection 来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    /**
     *  在主键picture中显示相片
     * @param imagePath
     */
    private void displayImage(String imagePath) {
        if(imagePath != null){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            picture.setImageBitmap(bitmap);
        }else{
            Toast.makeText(this,"failed to get image", Toast.LENGTH_LONG).show();
        }
    }


         public static String bitmapToBase64(Bitmap bitmap) {
                String result = null;
                ByteArrayOutputStream baos = null;
                try {
                    if (bitmap != null) {
                        baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                        baos.flush();
                        baos.close();

                        byte[] bitmapBytes = baos.toByteArray();
                        result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (baos != null) {
                            baos.flush();
                            baos.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return result;
    }

    /**
     * 点击头像按钮时跳转到拍照
     */
    public void getHead() {

        ActivityCompat.requestPermissions(record.this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        //创建File对象，用于存储拍照后的图片
        File outputImage = new File(getExternalCacheDir(),"output_image.jpg");
        if(outputImage.exists())
        {
            outputImage.delete();
            try
            {
                outputImage.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        if (Build.VERSION.SDK_INT>=24)
        {
            imageUri = FileProvider.getUriForFile(record.this,"com.example.userregister.fileprovider",outputImage);
        }
        else
        {
            imageUri = Uri.fromFile(outputImage);
        }

        //启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(intent,TAKE_PHOTO);



    }

    /**
     * 使用相机
     */
    private void useCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/test/" + System.currentTimeMillis() + ".jpg");
        file.getParentFile().mkdirs();

        //改变Uri  com.xykj.customview.fileprovider注意和xml中的一致
        Uri uri = FileProvider.getUriForFile(this, "com.example.userregistetr.fileprovider", file);
        //添加权限
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, TAKE_PHOTO);
    }
}
