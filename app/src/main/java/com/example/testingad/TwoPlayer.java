package com.example.testingad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.testingad.admanagers.InterstitialAdManager;
import com.example.testingad.admanagers.RewardedAdManager;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.util.Stack;

public class TwoPlayer extends AppCompatActivity implements View.OnClickListener{
    // Constants representing players and empty cells in the grid
    private static final int PLAYER_X = 1;
    private static final int PLAYER_O = 2;
    private static final int EMPTY_CELL = 0;

    // 2D array to represent the Tic Tac Toe grid
    private int[][] gameGrid = new int[3][3];

    // Boolean to keep track of the current player (X or O)
    private boolean isPlayerX = true;

//    status
    TextView t1;
//    Handler for delay
    private Handler handler = new Handler();

//    stack used to store the last move
    private Stack<int[]> moveStack = new Stack<>();

//    for rewarded ad
    MyDialogueFragment myDialog;
    RewardedAdManager rewardedAdManager;

    public RewardedAd rewardedAd;
    boolean isLoading;

//   for ads
    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712";
    private InterstitialAdManager interstitialAdManager;

    boolean noWin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_player);

        t1=findViewById(R.id.status);

        noWin=true;

//      InterstitialAd initiation
        interstitialAdManager=new InterstitialAdManager(this,AD_UNIT_ID);

        loadRewardedAd();

        // Inside your activity or fragment
        myDialog = new MyDialogueFragment(this,AD_UNIT_ID);

//        back to home page
        Button goback=findViewById(R.id.goBack);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(TwoPlayer.this, MainActivity.class);
                startActivity(intent);
                showInterstitialAd();
            }
        });

//      Undo btn
        Button undo=findViewById(R.id.btn_undo);
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//          add before watching
                showRewardedVideo();
//                undoMove();
//                myDialog.show(getSupportFragmentManager(),"My Fragment");
            }
        });

//      Restart game
        Button reset=findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetGameGrid();
                showInterstitialAd();
            }
        });
        // Set click listeners for all the buttons in the grid
        findViewById(R.id.btn_00).setOnClickListener(this);
        findViewById(R.id.btn_01).setOnClickListener(this);
        findViewById(R.id.btn_02).setOnClickListener(this);
        findViewById(R.id.btn_10).setOnClickListener(this);
        findViewById(R.id.btn_11).setOnClickListener(this);
        findViewById(R.id.btn_12).setOnClickListener(this);
        findViewById(R.id.btn_20).setOnClickListener(this);
        findViewById(R.id.btn_21).setOnClickListener(this);
        findViewById(R.id.btn_22).setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        // Get the tag of the clicked button representing row and column
        String tag = view.getTag().toString();
        String[] rowAndCol = tag.split(",");
        int row = Integer.parseInt(rowAndCol[0]);
        int col = Integer.parseInt(rowAndCol[1]);
        // Check if the clicked cell is empty
        if (gameGrid[row][col] == EMPTY_CELL && noWin) {
            // Mark the cell with the current player's symbol (X or O)
            gameGrid[row][col] = isPlayerX ? PLAYER_X : PLAYER_O;
            // Set the button's text with the player's symbol
            Button clickedButton = (Button) view;
            clickedButton.setText(isPlayerX ? "X" : "O");
            moveStack.push(new int[]{row, col});
            // Check for a win or draw
            if (checkWin(PLAYER_X)) {
                showMessage("Player X wins!");
                t1.setText("Player X wins!");
                noWin=false;
            } else if (checkWin(PLAYER_O)) {
                showMessage("Player O wins!");
                t1.setText("Player O wins!");
                noWin=false;
            } else if (isGridFull()) {
                showMessage("It's a draw!");
                t1.setText("It's a draw!");
//                resetGameGrid();
            } else {
                // Switch to the other player for the next turn
                isPlayerX = !isPlayerX;
            }
        }
        else if(!noWin){
            // Cell is already occupied, show a message
            if(checkWin(PLAYER_O)){
                showMessage("O has already won!");
            }
            else if(checkWin(PLAYER_X)){
                showMessage("X has already won!");
            }

        }
        else {
            // Cell is already occupied, show a message
            showMessage("Cell is already occupied!");
        }
    }
    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private boolean checkWin(int player) {
        // Check rows, columns, and diagonals for a win
        for (int i = 0; i < 3; i++) {
            if (gameGrid[i][0] == player && gameGrid[i][1] == player && gameGrid[i][2] == player) {
                return true; // Row win
            }
            if (gameGrid[0][i] == player && gameGrid[1][i] == player && gameGrid[2][i] == player) {
                return true; // Column win
            }
        }
        if (gameGrid[0][0] == player && gameGrid[1][1] == player && gameGrid[2][2] == player) {
            return true; // Diagonal win (top-left to bottom-right)
        }
        return gameGrid[0][2] == player && gameGrid[1][1] == player && gameGrid[2][0] == player; // Diagonal win (top-right to bottom-left)
    }
    private boolean isGridFull() {
        // Check if all cells are occupied
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (gameGrid[i][j] == EMPTY_CELL) {
                    return false;
                }
            }
        }
        return true;
    }
    private void resetGameGrid() {
        // Clear the game grid and reset the buttons
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                gameGrid[i][j] = EMPTY_CELL;
                int buttonId = getResources().getIdentifier("btn_" + i + j, "id", getPackageName());
                Button button = findViewById(buttonId);
                button.setText("");
            }
        }
        isPlayerX = true; // Reset the player to X for a new game
        noWin=true;
    }
    public void undoMove() {
        if (!moveStack.isEmpty()) {
//          undo
                int[] lastMove = moveStack.pop();
                int row = lastMove[0];
                int col = lastMove[1];
                // Revert the move in the game grid
                gameGrid[row][col] = EMPTY_CELL;
                // Find the corresponding button and set its text to empty
                int buttonId = getResources().getIdentifier("btn_" + row + col, "id", getPackageName());
                Button button = findViewById(buttonId);
                button.setText("");
                // Switch back to the player's turn
                isPlayerX = !isPlayerX;
        } else {
            showMessage("No moves to undo!");
        }
        noWin=true;
    }
//    calling the show and load ad method from InterstitialAdManager class
    private void showInterstitialAd() {
        interstitialAdManager.showInterstitialAd(this);
    }
    public  void loadRewardedAd() {
        if (rewardedAd == null) {
            isLoading = true;
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(
                    this,
                    AD_UNIT_ID,
                    adRequest,
                    new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error.
                            Log.d("TAG", loadAdError.getMessage());
                            rewardedAd = null;
                            TwoPlayer.this.isLoading = false;
//                            Toast.makeText(context, "onAdFailedToLoad", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            TwoPlayer.this.rewardedAd = rewardedAd;
                            Log.d("TAG", "onAdLoaded");
                            TwoPlayer.this.isLoading = false;
//                            Toast.makeText(context, "onAdLoaded", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void showRewardedVideo() {
        rewardedAd.setFullScreenContentCallback(
                new FullScreenContentCallback() {
                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                        Log.d("TAG", "onAdShowedFullScreenContent");
                        undoMove();
//                         Toast.makeText(context, "onAdShowedFullScreenContent", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when ad fails to show.
                        Log.d("TAG", "onAdFailedToShowFullScreenContent");
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        TwoPlayer.this.rewardedAd = null;
//                         Toast.makeText(context, "onAdFailedToShowFullScreenContent", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        TwoPlayer.this.rewardedAd = null;
                        Log.d("TAG", "onAdDismissedFullScreenContent");
//                         Toast.makeText(context, "onAdDismissedFullScreenContent", Toast.LENGTH_SHORT).show();
                        // Preload the next rewarded ad.
                        TwoPlayer.this.loadRewardedAd();
                    }
                });
        if (rewardedAd == null) {
            Log.d("TAG", "The rewarded ad wasn't ready yet.");
            return;
        }
        rewardedAd.show(
                this,
                new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        // Handle the reward.
                        Log.d("TAG", "The user earned the reward.");
                        int rewardAmount = rewardItem.getAmount();
                        String rewardType = rewardItem.getType();
                    }
                });
    }
}