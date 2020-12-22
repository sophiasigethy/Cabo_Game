package msp.group3.caboclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * this is an example for a zoomable and scrollable layout
 */
public class InGameActivity extends AppCompatActivity {

    private com.otaliastudios.zoom.ZoomLayout zoomLayout;

    private ImageButton chatButton;
    private ImageButton settingsButton;
    private Button caboButton;
    private ImageButton zoomButton;
    private int zoomBtnCount = 0;
    private int chatButtonCount = 0;

    private ImageButton playedCardsStackButton;
    private ImageButton pickCardsStackButton;

    private androidx.fragment.app.FragmentContainerView chatFragmentContainer;

    private final List<ImageButton> playerCardButtons = new ArrayList<>();
    private final List<de.hdodenhof.circleimageview.CircleImageView> playerPics = new ArrayList<>();
    private final List<TextView> playerStats = new ArrayList<>();
    private final List<TextView> playerNames = new ArrayList<>();
    private final List<Integer> cardClickCounts = new ArrayList<>();
    private final List<ConstraintLayout> playerOverviews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.ingame_activity);

        //link layout
        zoomLayout = (com.otaliastudios.zoom.ZoomLayout) findViewById(R.id.zoomlayout);
        chatButton = (ImageButton) findViewById(R.id.chat_button);
        settingsButton = (ImageButton) findViewById(R.id.settings_button);
        zoomButton = (ImageButton) findViewById(R.id.zoom_button);
        caboButton = (Button) findViewById(R.id.cabo_button);
        playedCardsStackButton = (ImageButton) findViewById(R.id.played_cards_imageButton);
        pickCardsStackButton = (ImageButton) findViewById(R.id.pick_card_imageButton);

        //TODO insert Number of Players here
        setUpPlayerStats(4);

        setUpOnClickListeners();

        //TODO: Chat fragment integration
        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putInt("some_int", 0);
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_chat, InGameChatFragment.class, bundle)
                    .commit();
        }

        chatFragmentContainer = findViewById(R.id.fragment_chat);
        chatFragmentContainer.setVisibility(View.INVISIBLE);

    }


    private void setUpOnClickListeners() {

        setAllCardsOnClickListeners();

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatButtonCount++;
                if(chatButtonCount%2==0){
                    chatFragmentContainer.setVisibility(View.INVISIBLE);
                }
                else{
                    chatFragmentContainer.setVisibility(View.VISIBLE);
                }
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),
                        "Open settings...", Toast.LENGTH_SHORT).show();
            }
        });

        zoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(zoomBtnCount%2==0){
                    zoomLayout.zoomTo(1.3f, true);
                    zoomButton.setImageResource(R.drawable.zoom_in);
                }
                else{
                    zoomLayout.zoomTo(1.0f, true);
                    zoomButton.setImageResource(R.drawable.zoom_out);
                }
                zoomBtnCount++;
            }
        });

        caboButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),
                        "CABO!.", Toast.LENGTH_SHORT).show();
            }
        });

        caboButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),
                        "CABO!", Toast.LENGTH_SHORT).show();
            }
        });

        playedCardsStackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),
                        "Discard card...", Toast.LENGTH_SHORT).show();
            }
        });

        pickCardsStackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),
                        "Pick card...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setAllCardsOnClickListeners(){
        for(ImageButton cardButton : playerCardButtons){
            cardButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   /* Toast.makeText(getApplicationContext(),
                            "Card clicked: "+getResources().getResourceEntryName(cardButton.getId()), Toast.LENGTH_SHORT).show();*/

                    zoomInOnSelectedCard(cardButton);
                    animateCardTurn(cardButton);
                    testIndicatePlayerTurn(1);
                    int index = playerCardButtons.indexOf(cardButton);
                    cardClickCounts.set(index, cardClickCounts.get(index)+1);
                    if(cardClickCounts.get(index)%2==0){
                        cardButton.setSelected(false);
                    }
                    else{
                        cardButton.setSelected(true);
                    }
                }
            });
        }
    }

    private void setUpPlayerStats(int nrPlayers){

        //TODO separate cards in cards per player
        Collections.addAll(playerCardButtons, findViewById(R.id.player1_card1_imageButton), findViewById(R.id.player1_card2_imageButton), findViewById(R.id.player1_card3_imageButton), findViewById(R.id.player1_card4_imageButton),
                findViewById(R.id.player2_card1_imageButton), findViewById(R.id.player2_card2_imageButton), findViewById(R.id.player2_card3_imageButton), findViewById(R.id.player2_card4_imageButton),
                findViewById(R.id.player3_card1_imageButton), findViewById(R.id.player3_card2_imageButton), findViewById(R.id.player3_card3_imageButton), findViewById(R.id.player3_card4_imageButton),
                findViewById(R.id.player4_card1_imageButton), findViewById(R.id.player4_card2_imageButton), findViewById(R.id.player4_card3_imageButton), findViewById(R.id.player4_card4_imageButton));

        Collections.addAll(playerPics, findViewById(R.id.player1_image_game), findViewById(R.id.player2_image_game), findViewById(R.id.player3_image_game), findViewById(R.id.player4_image_game));

        Collections.addAll(playerStats, findViewById(R.id.player1_info_game), findViewById(R.id.player2_info_game), findViewById(R.id.player3_info_game), findViewById(R.id.player4_info_game));

        Collections.addAll(playerNames, findViewById(R.id.player1_name_game), findViewById(R.id.player2_name_game), findViewById(R.id.player3_name_game), findViewById(R.id.player4_name_game));

        Collections.addAll(cardClickCounts,0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

        Collections.addAll(playerOverviews, findViewById(R.id.player1_stats_game), findViewById(R.id.player2_stats_game), findViewById(R.id.player3_stats_game), findViewById(R.id.player4_stats_game));

        for(int i=nrPlayers; i<4; i++){
            playerPics.get(i).setVisibility(View.INVISIBLE);
            playerStats.get(i).setVisibility(View.INVISIBLE);
            playerNames.get(i).setVisibility(View.INVISIBLE);
        }

        for(int i=nrPlayers*4; i<16; i++){
            playerCardButtons.get(i).setVisibility(View.INVISIBLE);
        }
    }

    private void growCardAnimation(ImageButton card){
        //bounds remain the same only image changes
        ObjectAnimator.ofFloat(card, "scaleX", 1.0f, 1.3f).setDuration(600).start();
        ObjectAnimator.ofFloat(card, "scaleY", 1.0f, 1.3f).setDuration(600).start();
        ObjectAnimator.ofFloat(card, "x", -15).setDuration(600).start();
        ObjectAnimator.ofFloat(card, "y", -15).setDuration(600).start();
    }

    private void animateCardTurn(ImageButton cardButton){
        final ObjectAnimator oa1 = ObjectAnimator.ofFloat(cardButton, "scaleX", 1f, 0f);
        final ObjectAnimator oa2 = ObjectAnimator.ofFloat(cardButton, "scaleX", 0f, 1f);
        oa1.setInterpolator(new DecelerateInterpolator());
        oa2.setInterpolator(new AccelerateDecelerateInterpolator());
        oa1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                cardButton.setImageResource(R.drawable.card_clubs_2);
                oa2.start();
            }
        });
        oa1.start();
    }

    private void zoomInOnSelectedCard(ImageButton cardButton){
        int[] location = new int[2];
        cardButton.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        Log.d("-----------PAN", "x: "+zoomLayout.getPanX()/zoomLayout.getZoom()+ " y: "+zoomLayout.getPanY()/zoomLayout.getZoom());
        Log.d("-----------SCREEN", "x: "+width+ " y: "+height);
        Log.d("-----------CARD POSITION", "x: "+x+ " y: "+y);
        //zoomLayout.panTo( 2*width-zoomLayout.getScaledPanX()+x, 2*height-zoomLayout.getScaledPanY()+y, true);
    }

    //TODO replace resources
    private int getCardResource(Card card){
        switch(card.getValue()){
            case -1: return R.drawable.card_joker_1;
            case 0: return R.drawable.card_hearts_k;
            case 1: return R.drawable.card_hearts_a;
            case 2: return R.drawable.card_clubs_2;
            case 3: return R.drawable.card_clubs_3;
            case 4: return R.drawable.card_clubs_4;
            case 5: return R.drawable.card_clubs_5;
            case 6: return R.drawable.card_clubs_6;
            case 7: return R.drawable.card_clubs_7;
            case 8: return R.drawable.card_clubs_8;
            case 9: return R.drawable.card_clubs_9;
            case 10: return R.drawable.card_clubs_10;
            case 11: return R.drawable.card_clubs_j;
            case 12: return R.drawable.card_clubs_q;
            case 13: return R.drawable.card_clubs_k;
        }
        return 0;
    }

    private void initiateCardAction(Card pickedCard){
        switch(pickedCard.getValue()){
            case 7: initiatePeekAction();
            case 8: initiatePeekAction();
            case 9: initiateSpyAction();
            case 10: initiateSpyAction();
            case 11: initiateBlindSwapAction();
            case 12: initiateBlindSwapAction();
            case 13: initiatePeekAndSwapAction();
        }
    }

    //TODO
    private void initiatePeekAndSwapAction() {
    }

    //TODO
    private void initiateBlindSwapAction() {
    }

    //TODO
    private void initiateSpyAction() {
    }

    //TODO
    private void initiatePeekAction() {
    }

    private void indicatePlayerTurn(Player player){
        scaleView(playerOverviews.get(player.getId()-1), 1.2f);
    }
    private void testIndicatePlayerTurn(int i){
        scaleView(playerOverviews.get(i-1), 1.2f);
    }

    public void scaleView(View v, float factor) {
        Animation anim = new ScaleAnimation(
                1f, factor, // Start and end values for the X axis scaling
                1f, factor,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        anim.setDuration(1000);
        v.startAnimation(anim);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        try {
            processExtraData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * created by Alain Feimer
     * bearbeitet und vervollständigt by Iris Turba (siehe unten)
     * Hier werden die vom Gamestate gesendeten Intents verarbeitet
     * Durch ihren eindeutige zugewiesenen key können sie schnell identifizeiert werden
     */
    public void processExtraData() throws JSONException {
        Intent intent = getIntent();
        String key = intent.getStringExtra("key");
        if (key!=null){
        switch (key) {
            case "Willkommen":
                //showMessage("Willkommen in Catan!");
                break;
            case "myID":
                Toast.makeText(this, "This is my Toast message!",
                        Toast.LENGTH_LONG).show();
                //myid = intent.getIntExtra("id", 0);
                //MainActivity.mWebSocketClient.send(JSON_commands.statusupdate(TypeDefs.readyForGamestart).toString());
                break;
            case "test2":
                Toast.makeText(this, "client!",
                        Toast.LENGTH_LONG).show();
                //myid = intent.getIntExtra("id", 0);

                break;
        }}
    }


}
