package com.hanul.caramelhomecchiato.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Choreographer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.util.lifecyclehandler.BluetoothHandler;

public class WarmerActivity extends AppCompatActivity{
	private static final String TAG = "WarmerActivity";

	private static final int REQUEST_ENABLE_BT = 10;

	private ImageButton buttonBluetooth;
	private ImageButton buttonWarmer;
	private TextView textViewBluetooth;
	private TextView textViewWarmer;
	private TextView textViewTemperature;

	private final BluetoothHandler bluetoothHandler = new BluetoothHandler(this);

	private boolean warmerOn = false;

	@Override protected void onCreate(@Nullable Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_warmer);

		buttonBluetooth = findViewById(R.id.buttonBluetooth);
		buttonWarmer = findViewById(R.id.buttonWarmer);
		textViewBluetooth = findViewById(R.id.textViewBluetooth);
		textViewWarmer = findViewById(R.id.textViewWarmer);
		textViewTemperature = findViewById(R.id.textViewTemperature);

		buttonBluetooth.setOnClickListener(v -> {
			BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			if(!bluetoothAdapter.isEnabled()){
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
		});

		buttonWarmer.setOnClickListener(v -> {
			if(warmerOn){
				bluetoothHandler.setWarmerOn(false);
				warmerOn = false;
			}else{
				bluetoothHandler.setWarmerOn(true);
				warmerOn = true;
			}
			updateWarmerButtonState();
		});

		bluetoothHandler.setOnStateChanged(state -> {
			switch(state){
				case LOADING:
					break;
				case FAIL:
					Toast.makeText(this, "블루투스 연결에 실패하였습니다.", Toast.LENGTH_SHORT).show();
					buttonBluetooth.setBackgroundTintList(null);
					textViewBluetooth.setText("블루투스 상태 : 비활성화");
					this.warmerOn = false;
					updateWarmerButtonState();
					break;
				case WARMER_ON:
				case WARMER_OFF:
					bluetoothIcon();
					break;
			}
		});

		bluetoothHandler.setOnTemperatureChanged(temperature -> {
			textViewTemperature.setText(getString(R.string.n_temperature, temperature));
		});

		checkBluetooth();
		requestTemperature();
	}

	private void checkBluetooth(){
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if(bluetoothAdapter==null){
			Toast.makeText(getApplicationContext(), "기기가 블루투스를 지원하지 않습니다.", Toast.LENGTH_LONG).show();
			return;
		}

		if(!bluetoothAdapter.isEnabled()){
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}else selectDevice(bluetoothAdapter);
	}

	// 블루투스 지원하며 활성 상태인 경우.
	private void selectDevice(BluetoothAdapter bluetoothAdapter){
		bluetoothHandler.searchDeviceAndRun(bluetoothAdapter);
		bluetoothIcon();
	}

	private void requestTemperature(){
		if(warmerOn) bluetoothHandler.requestTemperature();
		Choreographer.getInstance().postFrameCallbackDelayed(
				frameTimeNanos -> requestTemperature(),
				3000);
	}

	private void bluetoothIcon(){
		buttonBluetooth.setBackgroundTintList(new ColorStateList(new int[][]{new int[0]},
				new int[]{ContextCompat.getColor(this, R.color.BlueTooth)}));
		textViewBluetooth.setText("블루투스 상태 : 활성화");
	}

	private void updateWarmerButtonState(){
		if(warmerOn){
			buttonWarmer.setBackgroundTintList(null);
			textViewTemperature.setVisibility(View.VISIBLE);
			textViewWarmer.setVisibility(View.GONE);
		}else{
			buttonWarmer.setBackgroundTintList(new ColorStateList(new int[][]{new int[0]},
					new int[]{ContextCompat.getColor(this, R.color.empty_attachment)}));
			textViewTemperature.setVisibility(View.GONE);
			textViewWarmer.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode==REQUEST_ENABLE_BT){
			if(resultCode==RESULT_OK){
				BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				if(bluetoothAdapter==null){
					Toast.makeText(getApplicationContext(), "기기가 블루투스를 지원하지 않습니다.", Toast.LENGTH_LONG).show();
					return;
				}

				selectDevice(bluetoothAdapter);
			}else if(resultCode==RESULT_CANCELED){ // 블루투스 비활성화 상태 (종료)
				Toast.makeText(getApplicationContext(), "블루투스 비활성화 상태이므로 \n워머를 사용할 수 없습니다.",
						Toast.LENGTH_LONG).show();
			}
		}
	}
}