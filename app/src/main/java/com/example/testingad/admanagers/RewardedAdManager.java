package com.example.testingad.admanagers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;

import com.example.testingad.TwoPlayer;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class RewardedAdManager {
    public RewardedAd rewardedAd;
    TwoPlayer twoPlayer;
    boolean isLoading;
    private Context context;
    private String AD_UNIT_ID;
    public RewardedAdManager(Context context, String AD_UNIT_ID) {
        this.context = context;
        this.AD_UNIT_ID = AD_UNIT_ID;
        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        loadRewardedAd();
        twoPlayer=new TwoPlayer();
    }

     public  void loadRewardedAd() {
        if (rewardedAd == null) {
            isLoading = true;
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(
                    context,
                    AD_UNIT_ID,
                    adRequest,
                    new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error.
                            Log.d("TAG", loadAdError.getMessage());
                            rewardedAd = null;
                            RewardedAdManager.this.isLoading = false;
//                            Toast.makeText(context, "onAdFailedToLoad", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            RewardedAdManager.this.rewardedAd = rewardedAd;
                            Log.d("TAG", "onAdLoaded");
                            RewardedAdManager.this.isLoading = false;
//                            Toast.makeText(context, "onAdLoaded", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

     public void showRewardedVideo(Activity activity) {
         rewardedAd.setFullScreenContentCallback(
                 new FullScreenContentCallback() {
                     @Override
                     public void onAdShowedFullScreenContent() {
                         // Called when ad is shown.
                         Log.d("TAG", "onAdShowedFullScreenContent");
//                         Toast.makeText(context, "onAdShowedFullScreenContent", Toast.LENGTH_SHORT).show();
                     }
                     @Override
                     public void onAdFailedToShowFullScreenContent(AdError adError) {
                         // Called when ad fails to show.
                         Log.d("TAG", "onAdFailedToShowFullScreenContent");
                         // Don't forget to set the ad reference to null so you
                         // don't show the ad a second time.
                         RewardedAdManager.this.rewardedAd = null;
//                         Toast.makeText(context, "onAdFailedToShowFullScreenContent", Toast.LENGTH_SHORT).show();
                     }

                     @Override
                     public void onAdDismissedFullScreenContent() {
                         // Called when ad is dismissed.
                         // Don't forget to set the ad reference to null so you
                         // don't show the ad a second time.
                         RewardedAdManager.this.rewardedAd = null;
                         Log.d("TAG", "onAdDismissedFullScreenContent");
//                         Toast.makeText(context, "onAdDismissedFullScreenContent", Toast.LENGTH_SHORT).show();
                         // Preload the next rewarded ad.
                         RewardedAdManager.this.loadRewardedAd();
                     }
                 });
        if (rewardedAd == null) {
            Log.d("TAG", "The rewarded ad wasn't ready yet.");
            return;
        }
        rewardedAd.show(
                activity,
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
