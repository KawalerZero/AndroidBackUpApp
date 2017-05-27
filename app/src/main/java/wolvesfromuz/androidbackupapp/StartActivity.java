package wolvesfromuz.androidbackupapp;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * Created by Alicja on 18.05.2017.
 */

public class StartActivity extends Activity
{
    private ImageView startImage;
    private Animation rotateAnimation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        startImage = (ImageView) findViewById(R.id.start_image);
        rotateAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);

        startImage.startAnimation(rotateAnimation);
        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                finish();
                Intent intent = new Intent(StartActivity.this, HomeActivity.class);
                startActivity(intent);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
