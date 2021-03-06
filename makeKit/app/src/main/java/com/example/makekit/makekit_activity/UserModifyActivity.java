package com.example.makekit.makekit_activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import com.example.makekit.makekit_fragment.DatePickerFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;

import com.example.makekit.R;
import com.example.makekit.makekit_adapter.UserAdapter;
import com.example.makekit.makekit_asynctask.CUDNetworkTask;
import com.example.makekit.makekit_asynctask.UserNetworkTask;
import com.example.makekit.makekit_bean.User;
import com.example.makekit.makekit_sharVar.SharVar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserModifyActivity extends AppCompatActivity {

    final static String TAG = "First";

    private static final int SEARCH_ADDRESS_ACTIVITY = 10000;
    public static final String pattern1 = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&])[A-Za-z[0-9]$@$!%*#?&]{8,20}$"; // ??????, ??????, ????????????
    public static final String pattern2 = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$";

    private int _beforeLenght = 0;
    private int _afterLenght = 0;
    int imageCheck = 0;

    // ?????? ????????? ????????? ?????? ?????????
    private final int REQ_CODE_SELECT_IMAGE = 100;
    private String img_path = new String();
    private Bitmap image_bitmap_copy = null;
    private Bitmap image_bitmap = null;
    String imageName = null;
    private String f_ext = null;
    File tempSelectFile;

    int pwcheck;
    String url;

    String urlAddrBase = null;
    String urlAddr1 = null;
    String urlJsp = null;
//    String urlAddr3 = null;
//    String urlAddr3 = null;

    ArrayList<User> members;   // ???, ?????????
    UserAdapter adapter;


    String macIP;
    String email;
    String urlImage;
    String urlImage1;
    Matcher match;

    EditText user_pwcheck, user_tel, user_addressdetail;
    TextView user_email, user_pw, user_address, user_birth, tv_pwCheckMsg_user, currentPW, user_name;
    String useremail, username, userpw, useraddress, useraddressdetail, usertel, userbirth, userimage;
    Button update_btn;
    TextView fieldCheck;
    WebView user_image;
    ImageView user_image1;
    TextView tv_editPeopleImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_modify);

        // Thread ??????
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());

        // ???????????? ????????? ??????
        Intent intent = getIntent();
        macIP = SharVar.macIP;
        email = SharVar.userEmail;
        urlAddrBase = SharVar.urlAddrBase;


//        macIP = "192.168.2.2";
//        email = "son@naver.com";

        // jsp ????????? ?????? ??????
        urlAddr1 = urlAddrBase + "jsp/user_info_all.jsp?email=" + email;
        urlJsp = urlAddrBase + "jsp/";
        url =   urlJsp +"multipartRequest.jsp";

        // ========================================== ????????? + ???????????? ???????????? ????????????
        connectSelectGetData(urlAddr1);   // urlAddr1???  connectSelectGetData??? urlAddr2??? ????????????
        urlImage = urlAddrBase + "image/" + members.get(0).getImage();


        // ========================================== ???????????? ????????????
        user_image = findViewById(R.id.user_image);
        user_image1 = findViewById(R.id.user_image1);
        user_email = findViewById(R.id.user_email);
        user_name = findViewById(R.id.user_name);
        user_pw = findViewById(R.id.user_pw);
        user_address = findViewById(R.id.user_address);
        user_addressdetail = findViewById(R.id.user_addressdetail);
        user_tel = findViewById(R.id.user_tel);
        user_birth = findViewById(R.id.user_birth);
        tv_editPeopleImage = findViewById(R.id.tv_editPeopleImage);
        fieldCheck = findViewById(R.id.tv_fieldCheck_findId);
//

        user_pwcheck = findViewById(R.id.user_pwcheck);
        tv_pwCheckMsg_user = findViewById(R.id.tv_pwCheckMsg_user);

        //  --------------------------------------------- Select DB?????? ????????????

        String userimage = members.get(0).getImage();
        if (members.get(0).getImage().equals("null")) {
            urlImage = urlAddrBase + "image/ic_defaultpeople.jpg";
            user_image.loadUrl(urlImage);
        } else {

            // Initial webview
            user_image.setWebViewClient(new WebViewClient());

            // Enable JavaScript
            user_image.getSettings().setJavaScriptEnabled(true);
            user_image.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

            // WebView ??????
            WebSettings webSettings = user_image.getSettings();
            webSettings.setUseWideViewPort(true);       // wide viewport??? ??????????????? ??????
            webSettings.setLoadWithOverviewMode(true);  // ???????????? ???????????? ??? ?????? ????????? ????????? ?????? ??????
            //iv_viewPeople.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

            user_image.setBackgroundColor(0); //?????????
            user_image.setBackgroundResource(R.drawable.layout_outline);
            user_image.setHorizontalScrollBarEnabled(false); //?????? ?????????
            user_image.setVerticalScrollBarEnabled(false);   //?????? ?????????
            user_image.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY); // ????????? ?????? ??????
            user_image.setScrollbarFadingEnabled(false);
            user_image.setInitialScale(10);

            // ?????? ?????? ?????? ???????????? (?????????)
            webSettings.setBuiltInZoomControls(false);   // ??? ????????? ??????
            webSettings.setSupportZoom(false);

            user_image.loadUrl(urlImage); // ?????? URL


        }

//        if (members.get(0).getImage() == null) {
////            urlAddr1 = urlAddr + "people_query_all.jsp?peopleimage=" + peopleimage;
////            String result = connectCheckData(urlAddr1);
//            urlImage = urlImage+"ic_defaultpeople.jpg";
//            user_image.loadUrl(urlImage);
//            user_image.setWebChromeClient(new WebChromeClient());//????????? ?????? ?????? ??????//??? ????????? ????????? ???????????? alert??? ?????? ??????
//            user_image.setWebViewClient(new ViewPeopleActivity.WebViewClientClass());//???????????? ?????? ?????? ????????? ?????? ??????//????????? ?????? ????????? ???????????? ??????
//
////        } else if(peopleimage.length() != 0) {
//            // } else if(peopleimage.equals("!=null")) {
//        } else if(members.get(0).getImage() != null) {
////            urlAddr1 = urlAddr + "people_query_all.jsp?peopleimage=" + peopleimage;
////            String result = connectCheckData(urlAddr1);
//            urlImage = urlImage + members.get(0).getImage();
//            user_image.loadUrl(urlImage);
//            user_image.setWebChromeClient(new WebChromeClient());//????????? ?????? ?????? ??????//??? ????????? ????????? ???????????? alert??? ?????? ??????
//            user_image.setWebViewClient(new ViewPeopleActivity.WebViewClientClass());//???????????? ?????? ?????? ????????? ?????? ??????//????????? ?????? ????????? ???????????? ??????
//        }
        String useremail = members.get(0).getEmail();
        user_email.setText(useremail);

        String username = members.get(0).getName();
        user_name.setText(username);

        String userpw = members.get(0).getPw();
        user_pw.setText(userpw);

        String useraddress = members.get(0).getAddress();
        user_address.setText(useraddress);

        String useraddressdetail = members.get(0).getAddressdetail();
        user_addressdetail.setText(useraddressdetail);

        String usertel = members.get(0).getTel();
        user_tel.setText(usertel);

        String userbirth = members.get(0).getBirth();
        user_birth.setText(userbirth);


        //  ---------------------------------------------
        currentPW = user_pw;
        user_pw.addTextChangedListener(changeListener_pw);
        user_pwcheck.addTextChangedListener(changeListener_pwcheck);
        user_tel.addTextChangedListener(changeListener_tel);
        user_name.addTextChangedListener(changeListener_name);

        //  ---------------------------------------------  Update


        update_btn = findViewById(R.id.user_update_btn);
        update_btn.setOnClickListener(onClickListener);
        tv_editPeopleImage.setOnClickListener(onClickListener);

        findViewById(R.id.user_birth_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(v);
            }
        });

        user_address = findViewById(R.id.user_address);

        Button btn_update_user = (Button) findViewById(R.id.userModiAddress_button);

        if (btn_update_user != null) {
            btn_update_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(UserModifyActivity.this, WebViewActivity.class);
                    i.putExtra("macIP", macIP);
                    startActivityForResult(i, SEARCH_ADDRESS_ACTIVITY);
                }
            });
        }

//        TextInputLayout inputLayoutPW = findViewById(R.id.InputLayoutPw_join);
//        TextInputLayout inputLayoutPWCheck = findViewById(R.id.InputLayoutPwCheck_join);
//
//        inputLayoutPW.setPasswordVisibilityToggleEnabled(true);
//        inputLayoutPWCheck.setPasswordVisibilityToggleEnabled(true);

    }//============================


    @Override
    public void onResume() {
        super.onResume();
        // Select
        urlAddr1 = urlAddrBase + "jsp/user_info_all.jsp?email=" + email;
        connectSelectGetData(urlAddr1);
        Log.v(TAG, "onResume()");
    }

    // NetworkTask?????? ?????? ???????????? ????????? (Select)  String urlAddr2??? urlAddr1??? ????????? UserNetworkTask??? ????????????
    private ArrayList<User> connectSelectGetData(String urlAddr2) {

        try {
            UserNetworkTask userNetworkTask = new UserNetworkTask(UserModifyActivity.this, urlAddr2, "selectUser");
            Object obj = userNetworkTask.execute().get();
            members = (ArrayList<User>) obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return members;
    }


    // Update ????????????
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()) {
                case R.id.user_update_btn:
                    if (imageCheck == 1) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                doMultiPartRequest();
                            }
                        }).start();
                    }
                    Log.v(TAG, "image Name : " + imageName);
                    useremail = user_email.getText().toString();
                    userpw = user_pw.getText().toString();
                    username = user_name.getText().toString();
                    useraddress = user_address.getText().toString();
                    useraddressdetail = user_addressdetail.getText().toString();
                    usertel = user_tel.getText().toString();
                    userbirth = user_birth.getText().toString();
                    if (imageCheck == 1) {
                        userimage = imageName;
                    }

                    updatePeople();
                    checkField();

//                    userInfoCheck();

                    break;

                case R.id.tv_editPeopleImage:


                    intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
                    user_image.setVisibility(View.GONE);
                    user_image1.setVisibility(View.VISIBLE);

                    break;


            }
        }
    };

    // people Update data ??????
    private void updatePeople() {
        String urlAddr3 = "";
        urlAddr3 = urlAddrBase + "jsp/user_update.jsp?userEmail=" + useremail + "&userPw=" + userpw + "&userName=" + username + "&userAddress=" + useraddress + "&userAddressDetail=" + useraddressdetail + "&userTel=" + usertel + "&userBirth=" + userbirth + "&userImage=" + userimage;
        Log.v(TAG, urlAddr3);

        connectUpdateData(urlAddr3);

    }

    private void connectUpdateData(String urlAddr) {
        try {
            CUDNetworkTask updatenetworkTask = new CUDNetworkTask(UserModifyActivity.this, urlAddr);
            updatenetworkTask.execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void showDatePicker(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void processDatePickerResult(int year, int month, int day) {
        String month_string = Integer.toString(month + 1);
        String day_string = Integer.toString(day);
        String year_string = Integer.toString(year);
        String dateMessage = (month_string + "/" + day_string + "/" + year_string);

        TextView birth = findViewById(R.id.user_birth);

        birth.setText(dateMessage);

    }


    // ????????? ?????? ??????

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 10000:

                    String data = intent.getExtras().getString("data");
                    if (data != null) {
                        user_address.setText(data);
                    }
                    break;

                case 100:

                    try {
//                        urlImage = urlAddrBase + "image/";
//
//                        img_path = getImagePathToUri(intent.getData()); //???????????? URI??? ?????? ??????????????? ??????.
//                        user_image.loadUrl(urlImage);
//                        Toast.makeText(getBaseContext(), "urlImage1 : " + urlImage, Toast.LENGTH_SHORT).show();
//                        Log.v("test", String.valueOf(intent.getData()));
//                        // Initial webview
//
//                        // Initial webview
//                        user_image.setWebViewClient(new WebViewClient());
//
//                        // Enable JavaScript
//                        user_image.getSettings().setJavaScriptEnabled(true);
//                        user_image.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//
//                        // WebView ??????
//                        WebSettings webSettings = user_image.getSettings();
//                        webSettings.setUseWideViewPort(true);       // wide viewport??? ??????????????? ??????
//                        webSettings.setLoadWithOverviewMode(true);  // ???????????? ???????????? ??? ?????? ????????? ????????? ?????? ??????
//                        //iv_viewPeople.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//
//                        user_image.setBackgroundColor(0); //?????????
//                        user_image.setBackgroundResource(R.drawable.layout_outline);
//                        user_image.setHorizontalScrollBarEnabled(false); //?????? ?????????
//                        user_image.setVerticalScrollBarEnabled(false);   //?????? ?????????
//                        user_image.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY); // ????????? ?????? ??????
//                        user_image.setScrollbarFadingEnabled(false);
//                        user_image.setInitialScale(10);
//
//                        // ?????? ?????? ?????? ???????????? (?????????)
//                        webSettings.setBuiltInZoomControls(false);   // ??? ????????? ??????
//                        webSettings.setSupportZoom(false);
//
//                        user_image.loadUrl(urlImage);
//
//                        Log.v("here", "urlImage1 : " + urlImage);// ?????? URL
////                    imageCheck=1;
////                    img_path = getImagePathToUri(intent.getData()); //???????????? URI??? ?????? ??????????????? ??????.
////                    Toast.makeText(getBaseContext(), "img_path : " + img_path, Toast.LENGTH_SHORT).show();
////                    Log.v("test", String.valueOf(intent.getData()));
////                    //???????????? ????????????????????? ??????
////                    image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), intent.getData());
////
////                    //image_bitmap ?????? ????????? ???????????? ???????????? ??????????????? ?????????. width: 400 , height: 300
////                    image_bitmap_copy = Bitmap.createScaledBitmap(image_bitmap, 400, 300, true);
//                        //editImage.setImageBitmap(image_bitmap_copy);
//
//                        // ?????? ?????? ??? ?????? ?????????(?????? ??????)
//                        String date = new SimpleDateFormat("yyyyMMddHmsS").format(new Date());
//                        imageName = date + "." + f_ext;
//                        tempSelectFile = new File("/data/data/com.example.makekit/", imageName);
//                        OutputStream out = new FileOutputStream(tempSelectFile);
//                        image_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//
//                        // ?????? ?????? ????????? ?????? img_path ?????????
//                        img_path = "/data/data/com.example.makekit/" + imageName;
//
                        imageCheck=1;
                        img_path = getImagePathToUri(intent.getData()); //???????????? URI??? ?????? ??????????????? ??????.
                        Toast.makeText(getBaseContext(), "img_path : " + img_path, Toast.LENGTH_SHORT).show();
                        Log.v("test", String.valueOf(intent.getData()));
                        //???????????? ????????????????????? ??????
                        image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), intent.getData());

                        //image_bitmap ?????? ????????? ???????????? ???????????? ??????????????? ?????????. width: 400 , height: 300
                        image_bitmap_copy = Bitmap.createScaledBitmap(image_bitmap, 400, 300, true);
                        //editImage.setImageBitmap(image_bitmap_copy);

                        // ?????? ?????? ??? ?????? ?????????(?????? ??????)
                        String date = new SimpleDateFormat("yyyyMMddHmsS").format(new Date());


                            user_image1.setImageBitmap(image_bitmap_copy);
                            imageName = date+"."+f_ext;
                            tempSelectFile = new File("/data/data/com.example.makekit/", imageName);    // ????????? ????????? ????????? ????????? ?????? ??? ??????
                            img_path = "/data/data/com.example.makekit/"+imageName;



                        OutputStream out = new FileOutputStream(tempSelectFile);
                        image_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);                        // ?????? ????????? ?????? ?????? ?????????

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    // ?????? touch ??? ????????? ?????????
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View focusView = getCurrentFocus();
        if (focusView != null) {
            Rect rect = new Rect();
            focusView.getGlobalVisibleRect(rect);
            int x = (int) ev.getX(), y = (int) ev.getY();
            if (!rect.contains(x, y)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                focusView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    // pw ????????? text ?????? ??? listener
    TextWatcher changeListener_pw = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            // pw ?????? ???
            String pwCheck = user_pw.getText().toString().trim();
            Boolean check = pwdRegularExpressionChk(pwCheck);

            if (pwCheck.length() == 0) {
                user_pw.setError(null);

            } else {
                if (check == false) {
                    user_pw.setError("??????????????? ??????, ???????????? ???????????? ?????? 8??? ?????? ??????????????????.");
                }
            }
        }
    };

    // pw ????????? text ?????? ??? listener
    TextWatcher changeListener_pwcheck = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            // pwcheck ?????? ??? ?????? ?????? message
            if (user_pwcheck.getText().toString().trim().length() != 0) {
                if ((user_pwcheck.getText().toString().trim()).equals(user_pw.getText().toString().trim())) {
                    tv_pwCheckMsg_user.setTextColor(getResources().getColor(R.color.black));
                    tv_pwCheckMsg_user.setText("???????????? ??????");

                } else {
                    tv_pwCheckMsg_user.setTextColor(getResources().getColor(R.color.red));
                    tv_pwCheckMsg_user.setText("???????????? ?????????");
                }
            }
        }
    };

    // ???????????? ???/???/??? ?????? ??????
    public boolean pwdRegularExpressionChk(String newPwd) {
        boolean chk = false;  // ????????????, ??????, ?????? ?????? (8~10 ??????)
        match = Pattern.compile(pattern1).matcher(newPwd);
        if (match.find()) {
            chk = true;
        }
        return chk;
    }

    // ????????? field check
    private void checkField() {
        String userPW = user_pw.getText().toString().trim();
        String userPWCheck = user_pwcheck.getText().toString().trim();
        tv_pwCheckMsg_user.setText("");

        if (userPW.length() == 0) {
            tv_pwCheckMsg_user.setText("????????? ??????????????? ??????????????????.");

        } else if (userPW.length() != 0) {
            if (user_pwcheck.equals(userPW)) {
                tv_pwCheckMsg_user.setText("?????? ??????????????? ???????????????.");

            } else {
                Boolean check = pwdRegularExpressionChk(userPW);

                if (check == false) {
                    tv_pwCheckMsg_user.setText("??????????????? ??????, ???????????? ???????????? \n?????? 8??? ?????? ??????????????????.");

                } else {
                    if (userPWCheck.length() == 0) {
                        tv_pwCheckMsg_user.setText("????????? ???????????? ????????? ??????????????????.");

                    } else if ((user_pwcheck.getText().toString().trim()).equals(user_pw.getText().toString().trim())) {
//                        updateUser(userPW);
                        Intent intent1 = new Intent(UserModifyActivity.this, MainActivity.class);
                        startActivity(intent1);

                    } else {
                        tv_pwCheckMsg_user.setText("??????????????? ???????????? ????????????. \n?????? ??????????????????.");
                        user_pwcheck.setText("");
                        Toast.makeText(UserModifyActivity.this, "??????????????? ???????????? ????????????. \n?????? ??????????????????.", Toast.LENGTH_SHORT).show();

                    }
                }
            }

        }


    }

    // name text
    TextWatcher changeListener_name = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    // phone text
    TextWatcher changeListener_tel = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            _beforeLenght = s.length();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //fieldCheck.setText("");
            _afterLenght = s.length();
            // ?????? ???
            if (_beforeLenght > _afterLenght) {
                // ?????? ?????? ???????????? -??? ???????????? ?????????
                if (s.toString().endsWith("-")) {
                    user_tel.setText(s.toString().substring(0, s.length() - 1));
                }
            }
            // ?????? ???
            else if (_beforeLenght < _afterLenght) {
                if (_afterLenght == 4 && s.toString().indexOf("-") < 0) {
                    user_tel.setText(s.toString().subSequence(0, 3) + "-" + s.toString().substring(3, s.length()));
                } else if (_afterLenght == 9) {
                    user_tel.setText(s.toString().subSequence(0, 8) + "-" + s.toString().substring(8, s.length()));
                } else if (_afterLenght == 14) {
                    user_tel.setText(s.toString().subSequence(0, 13) + "-" + s.toString().substring(13, s.length()));
                }
            }
            user_tel.setSelection(user_tel.length());

        }

        @Override
        public void afterTextChanged(Editable s) {
            String phoneCheck = user_tel.getText().toString().trim();
            boolean flag = Pattern.matches(pattern2, phoneCheck);

            if (phoneCheck.length() == 0) {
                user_tel.setError(null);
            } else {
                if (flag == false) {
                    user_tel.setError("????????? ????????? ?????? ??????????????????.");
                }
            }
        }
    };


    public String getImagePathToUri(Uri data) {
        //???????????? ????????? ???????????? ????????? ?????????
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        //???????????? ?????? ???
        String imgPath = cursor.getString(column_index);
        Log.d("test", imgPath);

        //???????????? ?????? ???
        String imgName = imgPath.substring(imgPath.lastIndexOf("/") + 1);

        // ????????? ??? ??????
        f_ext = imgPath.substring(imgPath.length()-3, imgPath.length());
//        this.imageName = imgName;

        return imgPath;
    }//end of getImagePathToUri()
    //?????? ??????
    private void doMultiPartRequest() {

        File f = new File(img_path);

        DoActualRequest(f);
    }

    //?????? ?????????
    private void DoActualRequest(File file) {
        OkHttpClient client = new OkHttpClient();

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", file.getName(),
                        RequestBody.create(MediaType.parse("image/*"), file))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // user ?????? ??????
//    private void userInfoCheck() {
//
//        String userName = user_name.getText().toString().trim();
//        String userPhone = user_tel.getText().toString().trim();
//
//        if (userName.length() == 0) {
//            fieldCheck.setText("????????? ??????????????????");
//            user_name.setFocusableInTouchMode(true);
//            user_name.requestFocus();
//
//        } else if (userPhone.length() == 0) {
//            fieldCheck.setText("????????? ????????? ??????????????????");
//            user_tel.setFocusableInTouchMode(true);
//            user_tel.requestFocus();
//
//        }
//
//    }
//    // user pw ??? data ??????
//    private void updateUser(String userPW) {
//        String urlAddr1 = "";
//        urlAddr1 = urlAddr + "&pw=" + userPW;
//
//        Log.v(TAG, urlAddr1);
//        String result = connectUpdateData(urlAddr1);
//
//        if (result.equals("1")) {
//            Toast.makeText(MypagePWActivity02.this, "???????????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
//            finish();
//
//        } else {
//            Toast.makeText(MypagePWActivity02.this, "???????????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
//
//        }
//    }
}

