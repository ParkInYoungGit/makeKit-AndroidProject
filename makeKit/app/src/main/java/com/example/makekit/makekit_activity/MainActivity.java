package com.example.makekit.makekit_activity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.example.makekit.R;
import com.example.makekit.makekit_adapter.CartAdapter;
import com.example.makekit.makekit_adapter.SectionPageAdapter;
import com.example.makekit.makekit_asynctask.CartNetworkTask;
import com.example.makekit.makekit_bean.Cart;
import com.example.makekit.makekit_fragment.CategoryFragment;
import com.example.makekit.makekit_fragment.ChatListFragment;
import com.example.makekit.makekit_fragment.HomeFragment;
import com.example.makekit.makekit_fragment.MypageFragment;
import com.example.makekit.makekit_sharVar.SharVar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    final static String TAG = "MainActivity";


    private static final int SEARCH_ADDRESS_ACTIVITY = 10000;
    Fragment fragment = new Fragment();
    private EditText et_address;
    private BottomNavigationView mBottomNV;
    private ViewPager mViewPager;
    SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());
    Button btnStart;
    ActionBar actionBar;
    BottomNavigationView bottomNavigationView;
    String macIP, cartNo;
    String email = null;
    String urlAddrBase, urlAddr;
    int checkAlarm = 0;
    String cartNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().getItem(2).isEnabled();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.getBackground().setAlpha(0);
//        bottomNavigationView.setBackground(null);


        // ???????????? ?????? ??????????????? ??? ????????? ??? ?????? ?????? ----------------------------------------------
        SharedPreferences sf = getSharedPreferences("appData", MODE_PRIVATE);
        macIP = sf.getString("macIP","");
        email = sf.getString("useremail","");
        // -------------------------------------------------------------------------------------

        // ?????? 1??????
        if (checkAlarm != 1){
            Alarm();
        }else if (checkAlarm == 1){
            finish();
        }





        macIP = SharVar.macIP;
        email = SharVar.userEmail;
        urlAddrBase = SharVar.urlAddrBase;
        urlAddr = urlAddrBase + "jsp/cartno_productview_check.jsp?useremail=" + email;

        // ?????? ???????????? ??????
        FloatingActionButton fab = findViewById(R.id.fab_search);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("macIP", macIP);
                intent.putExtra("useremail", email);
                startActivity(intent);
            }
        });

        actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.makekit_side_logo);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_USE_LOGO);



//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                // ?????? ????????? ????????????
////                Intent intent = new Intent(MainActivity.this, RegisterPeopleActivity.class);
////                startActivity(intent);
//            }
//        });


    
        mBottomNV = findViewById(R.id.nav_view);

        mBottomNV.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() { //NavigationItemSelecte
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                // ?????? ??? ????????? ????????? ????????? ????????????.!
                BottomNavigate(menuItem.getItemId());
                return true;
            }
        });
        mBottomNV.setSelectedItemId(R.id.navigation_1);

    }


    private void BottomNavigate(int id) {  //BottomNavigation ????????? ?????? (?????? ??? 3??? ??????)
        String tag = String.valueOf(id+R.id.fab_search);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment currentFragment = fragmentManager.getPrimaryNavigationFragment();
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment);
        }

        Fragment fragment = fragmentManager.findFragmentByTag(tag);

        if (fragment == null) {
            if (id == R.id.navigation_1) {  // ?????? ????????? 1??? ??????

                fragment = new HomeFragment();  // ??????????????? 1????????? ??????
                Bundle bundle = new Bundle(1);
                bundle.putString("useremail", email);
                bundle.putString("macIP", macIP);
                fragment.setArguments(bundle);

            } else if (id == R.id.navigation_2) {

                fragment = new CategoryFragment();
                Bundle bundle = new Bundle(2);
                bundle.putString("useremail", email);
                bundle.putString("macIP", macIP);
                fragment.setArguments(bundle);

            } else if (id == R.id.navigation_4) {
                Log.v("email", "email:"+email);

                if(email.equals("")){
                fragment = new HomeFragment();
                    new AlertDialog.Builder(MainActivity.this)
                            .setIcon(R.drawable.alert)
                            .setTitle("MakeKit ????????? ??????")
                            .setMessage("?????? ???????????????.\n?????? ????????? ????????? ??? ?????? ???????????????.")
                            // ??????????????? ???????????? ??? alert ????????? ?????? ?????? ?????????
                            .setCancelable(false)
                            // ?????? ?????? ???????????? ??????!
                            .setPositiveButton("??????", null)
                            .show();

                }else {
                    fragment = new ChatListFragment();
                    Bundle bundle2 = new Bundle(2);
                    bundle2.putString("useremail", email);
                    bundle2.putString("macIP", macIP);
                    fragment.setArguments(bundle2);
                }
            } else if (id == R.id.navigation_5) {

                if(email.equals("")){
                    fragment = new HomeFragment();
                    new AlertDialog.Builder(MainActivity.this)
                            .setIcon(R.drawable.alert)
                            .setTitle("MakeKit ????????? ??????")
                            .setMessage("?????? ??????????????????.\n?????????????????? ????????? ??? ?????? ???????????????.")
                            // ??????????????? ???????????? ??? alert ????????? ?????? ?????? ?????????
                            .setCancelable(false)
                            // ?????? ?????? ???????????? ??????!
                            .setPositiveButton("??????", null)
                            .show();

                }else {
                        fragment = new MypageFragment();
                        Bundle bundle = new Bundle(2);
                        bundle.putString("useremail", email);
                        bundle.putString("macIP", macIP);
                        fragment.setArguments(bundle);
                    }
            }

            fragmentTransaction.add(R.id.content_layout, fragment, tag);
        } else {
            fragmentTransaction.show(fragment);
        }

        fragmentTransaction.setPrimaryNavigationFragment(fragment);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.commitNow();
    }



    //=========================================================================?????? ??????
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.top_list, menu);
        return true;
    }

    //????????? ??????, ToolBar??? ????????? ????????? select ???????????? ???????????? ??????
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {

            case R.id.menu_product: // ?????? ???????????? ?????? ??????
                if(email.equals("")){
                    fragment = new HomeFragment();
                    new AlertDialog.Builder(MainActivity.this)
                            .setIcon(R.drawable.alert)
                            .setTitle("MakeKit ????????? ??????")
                            .setMessage("????????? ??? ?????? ???????????????.")
                            // ??????????????? ???????????? ??? alert ????????? ?????? ?????? ?????????
                            .setCancelable(false)
                            // ?????? ?????? ???????????? ??????!
                            .setPositiveButton("??????", null)
                            .show();

                }else {
                    Intent productIntent = new Intent(MainActivity.this, ProductSalesWriteActivity.class);
                    startActivity(productIntent);
                    return true;
                }

            case R.id.menu_gps: // GPS??? ?????? ??????

                if(email.equals("")){
                    fragment = new HomeFragment();
                    new AlertDialog.Builder(MainActivity.this)
                            .setIcon(R.drawable.alert)
                            .setTitle("MakeKit ????????? ??????")
                            .setMessage("????????? ??? ?????? ???????????????.")
                            // ??????????????? ???????????? ??? alert ????????? ?????? ?????? ?????????
                            .setCancelable(false)
                            // ?????? ?????? ???????????? ??????!
                            .setPositiveButton("??????", null)
                            .show();

                }else {
                    Intent GPSintent = new Intent(MainActivity.this, MapActivity.class);
                    startActivity(GPSintent);
                    return true;
                }


            case R.id.menu_cart: // ??????????????? ?????? ??????

                if(email.equals("")){
                    fragment = new HomeFragment();
                    new AlertDialog.Builder(MainActivity.this)
                            .setIcon(R.drawable.alert)
                            .setTitle("MakeKit ????????? ??????")
                            .setMessage("????????? ??? ?????? ???????????????.")
                            // ??????????????? ???????????? ??? alert ????????? ?????? ?????? ?????????
                            .setCancelable(false)
                            // ?????? ?????? ???????????? ??????!
                            .setPositiveButton("??????", null)
                            .show();

                }else {
                    connectSelectCartData(urlAddr);
                    Intent intent = new Intent(MainActivity.this, CartActivity.class);
                    intent.putExtra("cartNo", cartNumber);
                    startActivity(intent);
                    return true;
                }


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
//                Intent intent3 = new Intent(MainActivity.this, MainActivity.class);
//
//                startActivity(intent3);
                return super.onOptionsItemSelected(item);
//                return true;
        //-----------------------------------------------------------
        }
    }
    // ???????????? ??????
    private void Alarm() {
          checkAlarm=1;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

//                else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            Toast.makeText(getApplicationContext(), "???????????????", Toast.LENGTH_SHORT).show();
            /**
             * ????????? ?????? ????????????
             */
//                    BitmapDrawable bitmapDrawable = (BitmapDrawable)getResources().getDrawable(R.mipmap.ic_launcher);
//                    Bitmap bitmap = bitmapDrawable.getBitmap();
            /**
             * ????????? ???????????? ????????? ??????????????? ????????? ?????????????????????.
             */

            int importance = NotificationManager.IMPORTANCE_HIGH;
            String Noti_Channel_ID = "Noti";
            String Noti_Channel_Group_ID = "Noti_Group";

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel(Noti_Channel_ID, Noti_Channel_Group_ID, importance);

//                    notificationManager.deleteNotificationChannel("testid"); ????????????

            /**
             * ????????? ????????? ???????????? ???????????? ????????? ????????? ????????? ??????????????????.
             */
//            if (notificationManager.getNotificationChannel(Noti_Channel_ID) != null) {
//                Toast.makeText(getApplicationContext(), "????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(getApplicationContext(), "????????? ????????? ????????????.", Toast.LENGTH_SHORT).show();
//                notificationManager.createNotificationChannel(notificationChannel);
//            }

            notificationManager.createNotificationChannel(notificationChannel);
//                    Log.e("????????????","===="+notificationManager.getNotificationChannel("testid1"));
//                    notificationManager.getNotificationChannel("testid");
            int NOTIFICATION_ID = 1;
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), Noti_Channel_ID)
                    .setLargeIcon(null).setSmallIcon(R.drawable.makekit_logo)
                    .setWhen(System.currentTimeMillis()).setShowWhen(true).
                            setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentTitle("????????? ?????? ??????!!")     // ??????
                    .setContentText("?????? ????????? ?????? ?????????!")  // ????????? ?????????
                    .setContentIntent(notificationPendingIntent)
                    .setAutoCancel(true)   // ????????? ?????????
                    .setDefaults(NotificationCompat.DEFAULT_ALL)    // ??????
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

//            Intent notificationIntent = new Intent(this, MainActivity.class);

//            PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, NOTIFICATION_, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//            PendingIntent launchIntent = getLaunchIntent(NOTIFICATION_ID, getBaseContext());


//                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            notificationManager.notify(0,builder.build());
//            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            // Will display the notification in the notification bar
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }


    // select cartNo
    private void connectSelectCartData(String urlAddr) {
        try {
            CartNetworkTask cartNetworkTask = new CartNetworkTask(MainActivity.this, urlAddr, "selectCartNo");

            Object object = cartNetworkTask.execute().get();
            cartNumber = (String) object;
            Log.v(TAG, "Cart mp : " + cartNumber);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
