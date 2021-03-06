package com.example.makekit.makekit_adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.makekit.R;
import com.example.makekit.makekit_activity.ProdutctViewActivity;
import com.example.makekit.makekit_bean.ProductData;
import com.example.makekit.makekit_sharVar.SharVar;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.CustomViewHolder> {

    String pNo;

    Context mContext = null;
    int layout = 0;
    ArrayList<ProductData> data = null;
    LayoutInflater inflater = null;
    String urlImage;
    private DecimalFormat myFormatter;

    private String urlImageReal;
    private AdapterView.OnItemClickListener mListener = null;

    public ProductListAdapter(Context mContext, int layout, ArrayList<ProductData> data, String urlImage) {
        this.mContext = mContext;
        this.layout = layout;
        this.data = data;
        this.urlImage = urlImage;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

//    private ArrayList<ProductData> arrayList;
//
//    public ProductListAdapter(ProductList mContext, int productitem_layout, ArrayList<ProductData> arrayList, String urlAddrBase) {
//        this.arrayList = arrayList;
//    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.productitem_layout, parent, false);
        //     ????????? xml ??????
        ProductListAdapter.CustomViewHolder vh = new ProductListAdapter.CustomViewHolder(v);


        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
//?????? ???????????? ????????????


        if (data.get(position).getProduct_image().equals("null")) {
            holder.product_image.setVisibility(View.INVISIBLE);
        } else {

            urlImageReal = urlImage + "image/" + data.get(position).getProduct_image();
            holder.product_image.loadUrl(urlImageReal);

            WebSettings webSettings = holder.product_image.getSettings();

            webSettings.setUseWideViewPort(true);       // wide viewport??? ??????????????? ??????
            webSettings.setLoadWithOverviewMode(true);  // ???????????? ???????????? ??? ?????? ????????? ????????? ?????? ??????
            // Initial webview
            holder.product_image.setWebViewClient(new WebViewClient());

            // Enable JavaScript
            holder.product_image.getSettings().setJavaScriptEnabled(true);
            holder.product_image.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

            // WebView ??????
//            holder.product_image.setBackgroundColor(R.color.white); //??????
            holder.product_image.setBackgroundColor(0);
            holder.product_image.setHorizontalScrollBarEnabled(false); //?????? ?????????
            holder.product_image.setVerticalScrollBarEnabled(false);   //?????? ?????????
            holder.product_image.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY); // ????????? ?????? ??????
            holder.product_image.setScrollbarFadingEnabled(false);
            holder.product_image.setInitialScale(30);

            // ?????? ?????? ?????? ???????????? (?????????)
            webSettings.setBuiltInZoomControls(false);   // ??? ????????? ??????
            webSettings.setSupportZoom(false);

        }


        holder.product_image.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        holder.product_image.setHorizontalScrollBarEnabled(false); //?????? ?????????
        holder.product_image.setVerticalScrollBarEnabled(false);   //?????? ?????????

        holder.product_image.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY); // ????????? ?????? ??????
        holder.product_image.setScrollbarFadingEnabled(false);


        holder.product_title.setText(data.get(position).getProduct_title());
        holder.product_subtitle.setText(data.get(position).getSub_title());
        myFormatter = new DecimalFormat("###,###");
        String formattedStringPrice = myFormatter.format(Integer.parseInt(data.get(position).getProduct_price()));
        holder.product_price.setText(formattedStringPrice+" ???");


        holder.product_image.setTag(position);//???????????????
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Context context = v.getContext();
////                Intent intent = new Intent(v.getContext(), ProdutctViewActivity.class);
////
////                pNo = data.get(position).getProductNo();
////                Log.v("pNo",pNo);
////                intent.putExtra("macIP", SharVar.macIP);
////                intent.putExtra("useremail", SharVar.userEmail);
////                intent.putExtra("productNo",pNo);
////
////                v.getContext().startActivity(intent);
//            }
//        });

        holder.product_image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent intent = new Intent(v.getContext(), ProdutctViewActivity.class);

                pNo = data.get(position).getProductNo();
                Log.v("pNo",pNo);
                intent.putExtra("macIP", SharVar.macIP);
                intent.putExtra("useremail", SharVar.userEmail);
                intent.putExtra("productNo",pNo);

                v.getContext().startActivity(intent);

                return false;
            }
        });


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {

        protected WebView product_image;
        protected TextView product_title;
        protected TextView product_subtitle;
        protected TextView product_price;


        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.product_image = itemView.findViewById(R.id.product_image);
            this.product_title = itemView.findViewById(R.id.product_title);
            this.product_subtitle = itemView.findViewById(R.id.product_subtitle);
            this.product_price = itemView.findViewById(R.id.product_price);
        }
    }

    //?????????????????????
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        this.mListener = listener ;
    }

}
