package android.bignerdranch.mycheckins;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.UUID;

public class RecordActivity extends SingleFragmentActivity {
    private static final int REQUEST_ERROR = 0;
    private static final String EXTRA_Record_ID = "com.bignerdranch.android.mycheckins.record_id";
    public static Intent newIntent(Context
                                           packageContext, UUID crimeId) {
        Intent intent = new Intent(packageContext,
                RecordActivity.class);
        intent.putExtra(EXTRA_Record_ID, crimeId);
        return intent;
    }
    @Override
    protected Fragment createFragment() {
        UUID recordId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_Record_ID);
        return RecordFragment.newInstance(recordId);
    }
    @Override
    protected void onResume() {
        super.onResume();
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int errorCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (errorCode != ConnectionResult.SUCCESS) {
            Dialog errorDialog = apiAvailability.getErrorDialog(this, errorCode, REQUEST_ERROR, new
                    DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            });
            errorDialog.show();
        }
    }
}
