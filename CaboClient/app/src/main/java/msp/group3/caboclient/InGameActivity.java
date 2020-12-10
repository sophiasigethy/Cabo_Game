package msp.group3.caboclient;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewTreeObserver;

/**
 * this is an example for a zoomable and scrollable layout
 */
public class InGameActivity extends AppCompatActivity {

    ZoomLayout zoomLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingame_activity);
        zoomLayout = (ZoomLayout) findViewById(R.id.zoom_layout);
        ViewTreeObserver vto = zoomLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                zoomLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = zoomLayout.getMeasuredWidth();
                int height = zoomLayout.getMeasuredHeight();
                zoomLayout.setContentSize(width, height);
            }
        });
    }


}
