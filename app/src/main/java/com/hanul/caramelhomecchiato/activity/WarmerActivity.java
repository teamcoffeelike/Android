package com.hanul.caramelhomecchiato.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hanul.caramelhomecchiato.CaramelHomecchiatoApp;
import com.hanul.caramelhomecchiato.R;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class WarmerActivity extends AppCompatActivity{
	private static final String TAG = "WarmerActivity";
	private static final UUID BLUETOOTH_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

	private static final int REQUEST_ENABLE_BT = 10;

	private ImageButton buttonBluetooth;
	private ImageButton buttonWarmer;


	@Override protected void onCreate(@Nullable Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_warmer);

		buttonBluetooth = findViewById(R.id.buttonBluetooth);
		buttonWarmer = findViewById(R.id.buttonWarmer);

		checkBluetooth();
	}

	void checkBluetooth(){
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if(bluetoothAdapter==null){
			Toast.makeText(getApplicationContext(), "기기가 블루투스를 지원하지 않습니다.", Toast.LENGTH_LONG).show();
			return;
		}

		if(!bluetoothAdapter.isEnabled()){
			Toast.makeText(getApplicationContext(), "현재 블루투스가 비활성 상태입니다.", Toast.LENGTH_LONG).show();
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}else selectDevice(bluetoothAdapter);
	}

	// 블루투스 지원하며 활성 상태인 경우.
	void selectDevice(BluetoothAdapter bluetoothAdapter){
		Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

		for(BluetoothDevice d : bondedDevices){
			if(d.getName().equals("CARAMEL")){
				CaramelHomecchiatoApp app = (CaramelHomecchiatoApp)getApplication();
				app.executorService.submit(() -> {
					try{
						BluetoothSocket socket = d.createRfcommSocketToServiceRecord(BLUETOOTH_UUID);
						socket.connect();

						OutputStream outputStream = socket.getOutputStream();
						InputStream inputStream = socket.getInputStream();

						// 데이터 수신 준비.
						//beginListenForData(socket);

					}catch(Exception e){
						Toast.makeText(getApplicationContext(),
								"블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
					}
				});
				return;
			}
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
				Toast.makeText(getApplicationContext(), "블루투스를 사용할 수 없어 프로그램을 종료안해",
						Toast.LENGTH_SHORT).show();
			}
		}
	}
}
