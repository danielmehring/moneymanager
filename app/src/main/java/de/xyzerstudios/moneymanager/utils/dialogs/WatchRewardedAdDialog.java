package de.xyzerstudios.moneymanager.utils.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.activities.PortfoliosActivity;
import de.xyzerstudios.moneymanager.activities.add.AddPortfolioActivity;

public class WatchRewardedAdDialog extends AppCompatDialogFragment {

    private final RewardedAd rewardedAd;
    private final Activity activity;

    private LinearLayout buttonCancelAd, buttonPlayReward;

    public WatchRewardedAdDialog(RewardedAd rewardedAd, Activity activity) {
        this.rewardedAd = rewardedAd;
        this.activity = activity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_view_rew_ad, null);
        builder.setView(view);

        if (rewardedAd == null) {
            Toast.makeText(activity, getString(R.string.ad_is_loading), Toast.LENGTH_SHORT).show();
            dismiss();
        }

        buttonPlayReward = view.findViewById(R.id.buttonPlayReward);
        buttonCancelAd = view.findViewById(R.id.buttonCancelAd);

        buttonPlayReward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rewardedAd.show(activity, new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        dismiss();
                        Intent intent = new Intent(activity, AddPortfolioActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });

        buttonCancelAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return builder.create();
    }

}
