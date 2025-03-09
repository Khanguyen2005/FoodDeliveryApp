package com.ltmb.ltmobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

import Fragment.RestaurantDetailFragment;

public class RestaurantActivity extends AppCompatActivity {
    ImageView imgRes;
    TextView nameRes,starRes,evaluateRes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_restaurant);

        Intent intent = getIntent();
        if(intent != null){
            String id = intent.getStringExtra("id");
            String name = intent.getStringExtra("name");
            String star = intent.getStringExtra("star");
            String evaluate = intent.getStringExtra("evaluate");
            String image = intent.getStringExtra("image");
            String background = intent.getStringExtra("backgroundImage");
            RestaurantDetailFragment fragment = RestaurantDetailFragment.newInstance(id,name, star, evaluate, image,background);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout_res, fragment);
            transaction.commit();
        }

    }
}