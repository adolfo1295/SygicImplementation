package app.external.sygic.com.sygicexternalapp;

import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sygic.sdk.remoteapi.Api;
import com.sygic.sdk.remoteapi.ApiCallback;
import com.sygic.sdk.remoteapi.ApiNavigation;
import com.sygic.sdk.remoteapi.events.ApiEvents;
import com.sygic.sdk.remoteapi.exception.GeneralException;
import com.sygic.sdk.remoteapi.exception.NavigationException;

public class MainActivity extends AppCompatActivity {

    private Button btnNavigate, btnMap;
    private Api mApi;
    private ApiCallback mApiCallback = new ApiCallback() {

        @Override
        public void onServiceDisconnected() {
            Toast.makeText(MainActivity.this, "Service disconnected", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceConnected() {
            //i dont know why but this method is never reached, so i think the api never connect correctly
            try {
                Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                mApi.registerCallback();
            } catch (RemoteException e) {
                Toast.makeText(MainActivity.this, "Connection Error "+e, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }

        @Override
        public void onEvent(int event, String data) {
            //i was debuggin and this method is never reached
            eventHandler.obtainMessage(event, data).sendToTarget();
        }
    };

    Handler eventHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            int event = msg.what;
            String data = (String)msg.obj;
            if (event == ApiEvents.EVENT_APP_STARTED)
            {
                btnNavigate.setEnabled(true);
            }
            if (event == ApiEvents.EVENT_APP_EXIT)
            {
                btnNavigate.setEnabled(false);
            }

            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init api
        initApi();

        //binding views
        bindViews();

        setClickListeners();

    }

    private void initApi(){
        ///////////////////////////////////////////////////////////////////////////////////////////// where can i find this Service? Any example? "com.sygic.fleet.SygicService"
        mApi = Api.init(getApplicationContext(), "app.external.sygic.com.sygicexternalapp", "com.sygic.fleet.SygicService", mApiCallback);
        mApi.connect();
    }

    private void bindViews(){
        btnNavigate = findViewById(R.id.btnNavigation);
        btnMap = findViewById(R.id.btnMap);
    }

    private void setClickListeners(){
        btnMap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                try {
                    mApi.show(false);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        btnNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etAddres = findViewById(R.id.etAddres);
                String address = etAddres.getText().toString();
                try {
                    mApi.show(false);
                    ApiNavigation.navigateToAddress(address, false, 0, 0);
                } catch (NavigationException | RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

    }

}
