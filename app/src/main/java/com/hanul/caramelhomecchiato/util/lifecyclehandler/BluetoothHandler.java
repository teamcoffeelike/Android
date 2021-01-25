package com.hanul.caramelhomecchiato.util.lifecyclehandler;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.annotation.WorkerThread;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.hanul.caramelhomecchiato.CaramelHomecchiatoApp;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BluetoothHandler extends LifecycleHandler{
	private static final String TAG = "BluetoothHandler";
	private static final UUID BLUETOOTH_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

	private final ExecutorService executorService = Executors.newSingleThreadExecutor();

	private State state = State.LOADING;
	@Nullable private OnStateChanged onStateChanged;
	private OnTemperatureChanged onTemperatureChanged;

	@Nullable private BluetoothSocket socket;

	public BluetoothHandler(ComponentActivity activity){
		super(activity);
	}
	public BluetoothHandler(Fragment fragment, CaramelHomecchiatoApp app){
		super(fragment);
	}
	public BluetoothHandler(Context context, LifecycleOwner lifecycleOwner, CaramelHomecchiatoApp app){
		super(context, lifecycleOwner);
	}

	public void setOnStateChanged(@Nullable OnStateChanged onStateChanged){
		this.onStateChanged = onStateChanged;
	}

	public void setOnTemperatureChanged(OnTemperatureChanged onTemperatureChanged){
		this.onTemperatureChanged = onTemperatureChanged;
	}

	@RequiresPermission(Manifest.permission.BLUETOOTH)
	public boolean searchDeviceAndRun(BluetoothAdapter bluetoothAdapter){
		getContext();
		for(BluetoothDevice d : bluetoothAdapter.getBondedDevices()){
			if(d.getName().equals("CARAMEL")){
				executorService.submit(() -> start(d));
				return true;
			}
		}
		// 디바이스 못 찾음
		return false;
	}

	@WorkerThread
	private void start(BluetoothDevice d){
		try{
			socket = d.createRfcommSocketToServiceRecord(BLUETOOTH_UUID);
			socket.connect();

			getStateAndTemperature();

		}catch(Exception e){
			Log.e(TAG, "start: ", e);
			ContextCompat.getMainExecutor(getContext()).execute(() -> {
				Toast.makeText(getContext(),
						"블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
			});
			setState(State.FAIL);
			closeSocket();
		}
	}

	@WorkerThread
	private boolean readBoolean(InputStream inputStream) throws IOException{
		while(true){
			int read = inputStream.read();
			if(read==-1) continue;
			return read!=0;
		}
	}

	@WorkerThread
	private int readInt(InputStream inputStream) throws IOException{
		byte[] bytes = new byte[4];
		for(int i = 0; i<4; ){
			int read = inputStream.read();
			if(read==-1) continue;
			bytes[i] = (byte)read;
			i++;
		}
		return bytes[0]<<24|bytes[1]<<16|bytes[2]<<8|bytes[3];
	}

	@WorkerThread
	private void setState(State state){
		this.state = state;
		ContextCompat.getMainExecutor(getContext()).execute(() -> {
			if(onStateChanged!=null) onStateChanged.onStateChanged(state);
		});
	}

	@WorkerThread
	private void setTemperature(int temperature){
		ContextCompat.getMainExecutor(getContext()).execute(() -> {
			if(onTemperatureChanged!=null) onTemperatureChanged.onTemperatureChanged(temperature);
		});
	}

	private void getStateAndTemperature() throws IOException{
		socket.getOutputStream().write('3');
		InputStream inputStream = socket.getInputStream();

		boolean state = readBoolean(inputStream);   //warmer on/off
		int temp = readInt(inputStream);           //warmer 온도
		setState(state ? State.WARMER_ON : State.WARMER_OFF);
		setTemperature(temp);
	}

	@Override protected void onDestroy(){
		executorService.shutdown();
		onStateChanged = null;
		closeSocket();
	}

	public boolean isConnected(){
		return socket!=null;
	}

	@MainThread
	public void setWarmerOn(boolean warmerOn){
		if(socket!=null){
			executorService.submit(() -> {
				if(socket!=null){
					try{
						socket.getOutputStream().write(warmerOn ? '1' : '2');
						setState(warmerOn ? State.WARMER_ON : State.WARMER_OFF);
					}catch(IOException e){
						Log.e(TAG, "setWarmerOn: ", e);
						setState(State.FAIL);

						closeSocket();
					}
				}
			});
		}
	}

	@MainThread
	public void requestTemperature(){
		if(socket!=null){
			executorService.submit(() -> {
				if(socket!=null){
					try{
						getStateAndTemperature();
					}catch(IOException e){
						Log.e(TAG, "setWarmerOn: ", e);
						setState(State.FAIL);

						closeSocket();
					}
				}
			});
		}
	}

	private void closeSocket(){
		if(socket!=null){
			try{
				socket.close();
			}catch(IOException e2){
				Log.e(TAG, "closeSocket: ", e2);
			}
			socket = null;
		}
	}

	@FunctionalInterface
	public interface OnStateChanged{
		void onStateChanged(State state);
	}

	@FunctionalInterface
	public interface OnTemperatureChanged{
		void onTemperatureChanged(int temperature);
	}

	public enum State{
		LOADING,
		FAIL,
		WARMER_ON,
		WARMER_OFF;
	}
}