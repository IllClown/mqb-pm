package com.mqbcoding.stats;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.martoreto.aauto.vex.CarStatsClient;
import com.google.android.apps.auto.sdk.DayNightStyle;
import com.google.android.apps.auto.sdk.StatusBarController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class CardataFragment extends CarFragment {
    private final String TAG = "CardataFragment";

    private CarStatsClient mStatsClient;


    /* todo:

    driveMode_activeProfile

    serviceInspection_distance
serviceInspection_time
serviceInspection_timeState
serviceInspection_distanceState
serviceInspection_distanceUnit
serviceOil_distance
serviceOil_time
serviceOil_timeState
serviceOil_distanceState
serviceOil_distanceUnit

engineTypes_primaryEngine
engineTypes_secondaryEngine

     */



    //the various data elements included and their textviews
    private TextView mWarning0ID;
    private TextView mWarning0Value;
    private TextView mWarning1ID;
    private TextView mWarning1Value;
    private TextView mWarning2ID;
    private TextView mWarning2Value;
    private TextView mVIN;
    private TextView mOdometer;

    private int mAnimationDuration;
    private static final float DISABLED_ALPHA = 0.3f;

    private Map<String, Object> mLastMeasurements = new HashMap<>();
    private Handler mHandler = new Handler();



    public CardataFragment() {
        // Required empty public constructor
    }

    //@Override
    protected void setupStatusBar(StatusBarController sc) {
        sc.setDayNightStyle(DayNightStyle.FORCE_NIGHT);
        sc.showAppHeader();
        sc.hideBatteryLevel();
        sc.showClock();
      //  sc.hideConnectivityLevel();
        sc.showMicButton();
        sc.showTitle();
        sc.setTitle("Car data");
    }


    @Override

    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG, "onAttach");


        Intent serviceIntent = new Intent(getContext(), CarStatsService.class);
        getContext().bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

        mAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            CarStatsService.CarStatsBinder carStatsBinder = (CarStatsService.CarStatsBinder) iBinder;
            mStatsClient = carStatsBinder.getStatsClient();
            mLastMeasurements = mStatsClient.getMergedMeasurements();
            mStatsClient.registerListener(mCarStatsListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mStatsClient.unregisterListener(mCarStatsListener);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_cardata, container, false);

        //find the various layout elements and give them value
        mWarning0ID     = rootView.findViewById(R.id.txtWarning_0_WarnID);
        mWarning1ID     = rootView.findViewById(R.id.txtWarning_1_WarnID);
        mWarning2ID     = rootView.findViewById(R.id.txtWarning_2_WarnID);
        mWarning0Value  = rootView.findViewById(R.id.txtWarning_0_dynamicValue);
        mWarning1Value  = rootView.findViewById(R.id.txtWarning_1_dynamicValue);
        mWarning2Value  = rootView.findViewById(R.id.txtWarning_2_dynamicValue);
        mVIN            = rootView.findViewById(R.id.txtVIN);
        mOdometer       = rootView.findViewById(R.id.txtOdometer);

        doUpdate();

        return rootView;    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        Log.i(TAG, "onDestroyView");
        // set everything back to null
        mWarning1Value = null;
        mWarning2Value = null;
        mWarning0Value = null;
        mWarning1ID = null;
        mWarning2ID = null;
        mWarning0ID = null;
        mVIN= null;
        mOdometer = null;

        super.onDestroyView();
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDetach() {
        Log.i(TAG, "onDetach");
        mStatsClient.unregisterListener(mCarStatsListener);
        getContext().unbindService(mServiceConnection);
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    private final CarStatsClient.Listener mCarStatsListener = new CarStatsClient.Listener() {
        @Override
        public void onNewMeasurements(String provider, Date timestamp, Map<String, Object> values) {
            mLastMeasurements.putAll(values);
            postUpdate();
        }
    };
    private void postUpdate() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                doUpdate();
            }
        });
    }

    private void doUpdate() {

        //get strings from the Measurements:
        String sWarningID0    = (String) mLastMeasurements.get("Car_vehicleState_Warning_0_WarnID");
        String sWarningID1    = (String) mLastMeasurements.get("Car_vehicleState_Warning_1_WarnID");
        String sWarningID2    = (String) mLastMeasurements.get("Car_vehicleState_Warning_2_WarnID");
        String sWarningIDVal0 = (String) mLastMeasurements.get("Car_vehicleState_Warning_0_dynamicValue");
        String sWarningIDVal1 = (String) mLastMeasurements.get("Car_vehicleState_Warning_1_dynamicValue");
        String sWarningIDVal2 = (String) mLastMeasurements.get("Car_vehicleState_Warning_2_dynamicValue");
        String sVIN           = (String) mLastMeasurements.get("vehicleIdenticationNumber_VIN");
        String sOdometer      = (String) mLastMeasurements.get("totalDistance_distanceValue"); //odometer value
        String sOdometerUnits = (String) mLastMeasurements.get("totalDistance_unit"); //odometer unit (km/miles)

        //check if VIN is known, and if so, display it.
        if (sVIN == null) {
            return;
        } else {
            mVIN.setText("VIN: " + sVIN);   //Should be some WVWZZZZbladiebla string
        }

        if (sOdometer == null) {
            mOdometer.setVisibility(View.GONE);
        } else {
            mOdometer.setVisibility(View.VISIBLE);
            mOdometer.setText("Driven: " + sOdometer + " " + sOdometerUnits); // eg 71811.0 km
        }

        //check if there are any warnings, and if so, display it
        //this could probably be done in a nicer way ;-)
        if (sWarningID0 == null) {
            mWarning0ID.setVisibility(View.GONE);
            mWarning0Value.setVisibility(View.GONE);
        } else {
            mWarning0ID.setVisibility(View.VISIBLE);
            mWarning0Value.setVisibility(View.VISIBLE);
            mWarning0ID.setText(sWarningID0); //seems to be some kind of error code, like 41511.0 or 42254.0
            mWarning0Value.setText(sWarningIDVal0);//
        }
        //check if there are any warnings, and if so, display it
        //this could probably be done in a nicer way ;-)
        if (sWarningID1 == null) {
            mWarning1ID.setVisibility(View.GONE);
            mWarning1Value.setVisibility(View.GONE);
        } else {
            mWarning1ID.setVisibility(View.VISIBLE);
            mWarning1Value.setVisibility(View.VISIBLE);
            mWarning1ID.setText(sWarningID1); //seems to be some kind of error code, like 41511.0 or 42254.0
            mWarning1Value.setText(sWarningIDVal1);//
        }

        //check if there are any warnings, and if so, display it
        //this could probably be done in a nicer way ;-)
        if (sWarningID2 == null) {
            mWarning2ID.setVisibility(View.GONE);
            mWarning2Value.setVisibility(View.GONE);
        } else {
            mWarning2ID.setVisibility(View.VISIBLE);
            mWarning2Value.setVisibility(View.VISIBLE);
            mWarning2ID.setText(sWarningID2); //seems to be some kind of error code, like 41511.0 or 42254.0
            mWarning2Value.setText(sWarningIDVal2);//
        }



    }

    private void animateAlpha(View view, float alpha) {
        if (view.getAlpha() == alpha) {
            return;
        }
        if (isVisible()) {
            view.animate().alpha(alpha).setDuration(mAnimationDuration).setListener(null);
        } else {
            view.setAlpha(alpha);
        }
    }

}