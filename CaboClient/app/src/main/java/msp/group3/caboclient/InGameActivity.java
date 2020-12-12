package msp.group3.caboclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.animation.ObjectAnimator;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * this is an example for a zoomable and scrollable layout
 */
public class InGameActivity extends AppCompatActivity {

    private ZoomLayout zoomLayout;
    private ImageButton player1_card1;
    private ImageButton player1_card2;
    private ImageButton player1_card3;
    private ImageButton player1_card4;

    private ImageButton player2_card1;
    private ImageButton player2_card2;
    private ImageButton player2_card3;
    private ImageButton player2_card4;

    private ImageButton player3_card1;
    private ImageButton player3_card2;
    private ImageButton player3_card3;
    private ImageButton player3_card4;

    private ImageButton player4_card1;
    private ImageButton player4_card2;
    private ImageButton player4_card3;
    private ImageButton player4_card4;

    private ImageButton chatButton;

    private InGameChatFragment chatFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.ingame_activity);
        zoomLayout = (ZoomLayout) findViewById(R.id.zoom_layout);
        chatButton = (ImageButton) findViewById(R.id.chat_button);
        instantiatePlayerCardDecks();
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

        chatFragment = new InGameChatFragment();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_chat, InGameChatFragment.class, null)
                    .hide(chatFragment)
                    .commit();
        }

        player1_card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),
                        "Card 1 Player 1 is clicked", Toast.LENGTH_SHORT).show();
                //growCardAnimation(player1_card1);
            }
        });
        player1_card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),
                        "Card 1 Player 1 is clicked", Toast.LENGTH_SHORT).show();
                //growCardAnimation(player1_card2);
            }
        });
        player1_card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),
                        "Card 1 Player 1 is clicked", Toast.LENGTH_SHORT).show();
                //growCardAnimation(player1_card3);
            }
        });
        player1_card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),
                        "Card 1 Player 1 is clicked", Toast.LENGTH_SHORT).show();
                //growCardAnimation(player1_card4);
            }
        });

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),
                        "Open chat...", Toast.LENGTH_SHORT).show();
                getSupportFragmentManager().beginTransaction().show(chatFragment);
            }
        });
    }

    private void instantiatePlayerCardDecks(){
        player1_card1 = findViewById(R.id.player1_card1_imageButton);
        player1_card2 = findViewById(R.id.player1_card2_imageButton);
        player1_card3 = findViewById(R.id.player1_card3_imageButton);
        player1_card4 = findViewById(R.id.player1_card4_imageButton);

        player2_card1 = findViewById(R.id.player2_card1_imageButton);
        player2_card2 = findViewById(R.id.player2_card2_imageButton);
        player2_card3 = findViewById(R.id.player2_card3_imageButton);
        player2_card4 = findViewById(R.id.player2_card4_imageButton);

        player3_card1 = findViewById(R.id.player3_card1_imageButton);
        player3_card2 = findViewById(R.id.player3_card2_imageButton);
        player3_card3 = findViewById(R.id.player3_card3_imageButton);
        player3_card4 = findViewById(R.id.player3_card4_imageButton);

        player4_card1 = findViewById(R.id.player4_card1_imageButton);
        player4_card2 = findViewById(R.id.player4_card2_imageButton);
        player4_card3 = findViewById(R.id.player4_card3_imageButton);
        player4_card4 = findViewById(R.id.player4_card4_imageButton);
    }

    private void growCardAnimation(ImageButton card){
        //bounds remain the same only image changes
        ObjectAnimator.ofFloat(card, "scaleX", 1.0f, 1.3f).setDuration(600).start();
        ObjectAnimator.ofFloat(card, "scaleY", 1.0f, 1.3f).setDuration(600).start();
        ObjectAnimator.ofFloat(card, "x", -15).setDuration(600).start();
        ObjectAnimator.ofFloat(card, "y", -15).setDuration(600).start();
    }

}
