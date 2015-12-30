package ilya.myapplication;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;



@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class MainActivity extends Activity implements SensorEventListener, View.OnTouchListener {


    GoogleMap googleMap;

    private ImageView HeadImage;

    private float RotateDegree = 0f;
    private SensorManager mSensorManager;

    TextView CompOrient;
    TextView Orientation;

    private float fromPosition;
    private ViewFlipper flipper = null;

    private void addMarker() {
        if(null != googleMap) {
            googleMap.addMarker (new MarkerOptions()
                            .position(new LatLng(0, 0))
                            .title("Ты тут")
                            .draggable(true)
            );
        }
    }

    private void createmapView() {
        try {
            if(null == googleMap) {
                googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapView)).getMap();

                if(null == googleMap) {
                    Toast.makeText(getApplicationContext(), "Ошибка", Toast.LENGTH_SHORT).show();
                }

            }
        } catch (NullPointerException exception) {
            Log.e("mapApp", exception.toString());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        HeadImage = (ImageView) findViewById(R.id.CompassView);

        CompOrient = (TextView) findViewById(R.id.Head);
        Orientation = (TextView) findViewById(R.id.middle);


        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);


        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.main_layout);
        mainLayout.setOnTouchListener(this);


        flipper = (ViewFlipper) findViewById(R.id.flipper);


        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int layouts[] = new int[]{ R.layout.map};
        for (int layout : layouts)
            flipper.addView(inflater.inflate(layout, null));

        createmapView();
        addMarker();

    }

    protected void onResume(){
        super.onResume();

        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }


    @Override
    protected void onPause() {
        super.onPause();

        mSensorManager.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        float degree = Math.round(event.values[0]);
        CompOrient.setText("Отклонение от севера: " + Float.toString(degree) + "градусов");

        RotateAnimation rotateAnimation = new RotateAnimation(RotateDegree,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        rotateAnimation.setDuration(200);

        rotateAnimation.setFillAfter(true);

        HeadImage.startAnimation(rotateAnimation);
        RotateDegree = -degree;

        switch((int) degree) {
            case 0:
                Orientation.setText("Север");
                break;
            case 45:
                Orientation.setText("Северо-Восток");
                break;
            case 90:
                Orientation.setText("Восток");
                break;
            case 135:
                Orientation.setText("Юго-Восток");
                break;
            case 180:
                Orientation.setText("Юг");
                break;
            case 225:
                Orientation.setText("Юго-Запад");
                break;
            case 270:
                Orientation.setText("Запад");
                break;
            case 315:
                Orientation.setText("Северо-Запад");
                break;
        }
    }
    public boolean onTouch(View view, MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN: // Пользователь нажал на экран. Начало движения
                fromPosition = event.getX();
                break;
            case MotionEvent.ACTION_UP: // Пользователь отпустил экран. Окончание движения
                float toPosition = event.getX();
                if (fromPosition > toPosition)
                {
                    flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.go_next_in));
                    flipper.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.go_next_out));
                    flipper.showNext();
                }
                else if (fromPosition < toPosition)
                {
                    flipper.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.go_prev_in));
                    flipper.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.go_prev_out));
                    flipper.showPrevious();
                }
            default:
                break;
        }
        return true;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}



