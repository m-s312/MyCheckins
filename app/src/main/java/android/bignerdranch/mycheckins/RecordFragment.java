package android.bignerdranch.mycheckins;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class RecordFragment extends Fragment {
    private static final String ARG_Record_ID ="record_id";

    private static final String DIALOG_DATE = "date";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_PHOTO= 2;
    public Record mRecord;
    private File mPhotoFile;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mDeleteButton;
    private EditText mPlaceField;
    private EditText mDetailsField;
    private Button mReportButton;
    private ImageButton mPhotoButton;
    private Button mMapButton;
    private Button mLocationButton;
    private ImageView mPhotoView;
    private GoogleApiClient mClient;
    private TextView latitudeView;
    private TextView longitudeView;
    private Location mlastLocation;


    public static RecordFragment newInstance() {
        return new RecordFragment();
    }



    public static RecordFragment newInstance(UUID recordId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_Record_ID, recordId);
        RecordFragment fragment = new RecordFragment();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID recordId = (UUID) getArguments().getSerializable(ARG_Record_ID);
        mRecord = RecordLab.get(getActivity()).getRecord(recordId);
        mPhotoFile = RecordLab.get(getActivity()).getPhotoFile(mRecord);

        mClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks(){
                    @Override
                    public void  onConnected(@Nullable Bundle bundle){
                        LocationRequest request = LocationRequest.create();
                        request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                        request.setNumUpdates(1);
                        request.setInterval(0);

                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                                !=PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        mlastLocation = LocationServices.FusedLocationApi.getLastLocation(mClient);
                        if (mlastLocation != null){
                            double latitude = mlastLocation.getLatitude();
                            double longitude = mlastLocation.getLongitude();
                            latitudeView.setText("Lat:"+latitude);
                            longitudeView.setText("Long:"+ longitude);
                        }else{
                            latitudeView.setText("Location will not be displayed in Emulator ");
                        }
                        LocationServices.FusedLocationApi.requestLocationUpdates(mClient, request, new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                Log.i("LOCATION","got a Fix:"+ location);


                            }
                        });
                    }
                    @Override
                    public void onConnectionSuspended(int i){
                    }


                } )
                .build();
    }
    @Override
    public void onStart() {
        super.onStart();
        getActivity().invalidateOptionsMenu();
        mClient.connect();
    }
    @Override
    public void onStop() {
        super.onStop();
        mClient.disconnect();
    }
    @Override
    public void onPause() {
        super.onPause();
        RecordLab.get(getActivity())
                .updateCrime(mRecord);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_record, container, false);
        PackageManager packageManager =
                getActivity().getPackageManager();

        mTitleField = (EditText) v.findViewById(R.id.record_title);
        mTitleField.setText(mRecord.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(
                    CharSequence c, int start, int before, int count) {
                mRecord.setTitle(c.toString());
            }
            @Override
            public void beforeTextChanged(
                    CharSequence c, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable c) {
                          }
        });

        mDeleteButton = (Button) v.findViewById(R.id.record_delete);
        mDeleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                RecordLab.get(getActivity()).deleteRecord( mRecord);
                startActivity(RecordListActivity.newIntent(getActivity()));
            }
        });
        mMapButton = (Button) v.findViewById(R.id.show_map);
        mMapButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                startActivity(MapsActivity.newIntent(getActivity()));
            }
        });
        latitudeView = (TextView) v.findViewById(R.id.latitude);
        longitudeView = (TextView) v.findViewById(R.id.longitude);
       /* mLocationButton = (Button) v.findViewById(R.id.getlocation);
        mLocationButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
               displayLocation();

            }
        });
        */


        mDateButton = (Button) v.findViewById(R.id.record_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mRecord.getDate());
                dialog.setTargetFragment(RecordFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });
        mPlaceField = (EditText) v.findViewById(R.id.record_place);
        mPlaceField.setText(mRecord.getPlace());
        mPlaceField.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(
                    CharSequence c, int start, int before, int count) {
                mRecord.setPlace(c.toString());
            }

            public void beforeTextChanged(
                    CharSequence c, int start, int count, int after) {
            }

            public void afterTextChanged(Editable c) {
            }
        });
        mDetailsField = (EditText) v.findViewById(R.id.record_details);
        mDetailsField.setText(mRecord.getDetails());
        mDetailsField.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(
                    CharSequence c, int start, int before, int count) {
                mRecord.setDetails(c.toString());
            }

            public void beforeTextChanged(
                    CharSequence c, int start, int count, int after) {
            }

            public void afterTextChanged(Editable c) {
            }
        });
        mReportButton = (Button)
                v.findViewById(R.id.record_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new
                        Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT,
                        getRecordReport());
                i.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.record_report_subject));
                i = Intent.createChooser(i,
                        getString(R.string.send_report));
                startActivity(i);
            }
        });
        mPhotoButton = (ImageButton)
                v.findViewById(R.id.record_camera);
        final Intent captureImage = new
                Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "com.bignerdranch.android.mycheckins.fileprovider", mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                List<ResolveInfo> cameraActivities = getActivity().getPackageManager().queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo activity : cameraActivities) { getActivity().grantUriPermission(activity.activityInfo.packageName,uri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage,
                        REQUEST_PHOTO);

            }
        });
        mPhotoView = (ImageView)
                v.findViewById(R.id.record_photo);
        updatePhotoView();
        return v;
    }

   /* private void displayLocation(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                !=PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mlastLocation = LocationServices.FusedLocationApi.getLastLocation(mClient);
        if (mlastLocation != null){
            double latitude = mlastLocation.getLatitude();
            double longitude = mlastLocation.getLongitude();
            latitudeView.setText("Lat:"+latitude);
            longitudeView.setText("Long:"+ longitude);
        }else{
            latitudeView.setText("Location will not be displayed in Emulator ");
        }

    }
    */
    @Override

    public void onActivityResult(int requestCode, int
            resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mRecord.setDate(date);
            updateDate();
        } else if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(), "com.bignerdranch.android.mycheckins.fileprovider", mPhotoFile);
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView();
        }
    }
    private void updateDate() {
        mDateButton.setText(mRecord.getDate().toString());
    }
    private String getRecordReport() {

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mRecord.getDate()).toString();

        String report = getString(R.string.record_report,
                        mRecord.getTitle(), dateString,
                        mRecord.getPlace(),mRecord.getDetails());
        return report;
    }
    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }
}