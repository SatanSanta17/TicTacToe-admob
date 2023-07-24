package com.example.testingad.admanagers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class InterstitialAdManager {

    private static final String TAG = "InterstitialAdManager";
    public InterstitialAd interstitialAd;
    private Context context;
    private String adUnitId;

    public InterstitialAdManager(Context context, String adUnitId) {
        this.context = context;
        this.adUnitId = adUnitId;
        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        loadInterstitialAd();
    }
    public void loadInterstitialAd() {
            AdRequest adRequest = new AdRequest.Builder().build();
            InterstitialAd.load(
                    context,
                    adUnitId,
                    adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            InterstitialAdManager.this.interstitialAd = interstitialAd;
                            Log.i(TAG, "onAdLoaded");
//                            Toast.makeText(context, "Interstitial Ad Loaded", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            Log.i(TAG, loadAdError.getMessage());
                            interstitialAd = null;
                            String error = String.format(
                                    "domain: %s, code: %d, message: %s",
                                    loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());
//                            Toast.makeText(context, "Interstitial Ad failed to load with error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
    }
    public void showInterstitialAd(Activity activity) {
        interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                InterstitialAdManager.this.interstitialAd = null;
                Log.d(TAG, "The ad was dismissed.");
//              Toast.makeText(context, "Interstitial Ad dismissed", Toast.LENGTH_SHORT).show();
                InterstitialAdManager.this.loadInterstitialAd();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                InterstitialAdManager.this.interstitialAd = null;
                Log.d(TAG, "The ad failed to show.");
//                                    Toast.makeText(context, adError.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdShowedFullScreenContent() {
                Log.d(TAG, "The ad was shown.");
            }
        });
        if (interstitialAd != null) {
            interstitialAd.show(activity);
        } else {
//            Toast.makeText(activity, "Interstitial Ad did not load", Toast.LENGTH_SHORT).show();
            loadInterstitialAd();
        }
    }
}
