package com.hanul.caramelhomecchiato.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.activity.JoinActivity.JoinType;
import com.hanul.caramelhomecchiato.network.JoinService;
import com.hanul.caramelhomecchiato.util.Auth;
import com.hanul.caramelhomecchiato.util.BaseCallback;
import com.hanul.caramelhomecchiato.util.SpinnerHandler;
import com.hanul.caramelhomecchiato.util.Validate;

import retrofit2.Call;
import retrofit2.Response;

import static kotlin.text.StringsKt.isBlank;

public class JoinFormFragment extends Fragment{
	private static final String TAG = "JoinFormFragment";

	private static final String EXTRA_TYPE = "type";

	private EditText editTextName;
	private EditText editTextEmailPhone;
	private EditText editTextPassword;
	private EditText editTextPasswordCheck;

	private TextView textViewNameConfirm;
	private TextView textViewEmailPhoneConfirm;
	private TextView textViewPasswordConfirm;
	private TextView textViewPasswordCheckConfirm;

	private ImageView imgEmailPhone;

	private JoinType type;

	private final SpinnerHandler spinnerHandler = new SpinnerHandler(this);

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_join_form, container, false);

		editTextName = view.findViewById(R.id.editTextName);
		editTextEmailPhone = view.findViewById(R.id.editTextEmailPhone);
		editTextPassword = view.findViewById(R.id.editTextPassword);
		editTextPasswordCheck = view.findViewById(R.id.editTextPasswordCheck);

		textViewNameConfirm = view.findViewById(R.id.textViewNameConfirm);
		textViewEmailPhoneConfirm = view.findViewById(R.id.textViewEmailPhoneConfirm);
		textViewPasswordConfirm = view.findViewById(R.id.textViewPasswordConfirm);
		textViewPasswordCheckConfirm = view.findViewById(R.id.textViewPasswordCheckConfirm);

		imgEmailPhone = view.findViewById(R.id.imgEmailPhone);

		editTextName.addTextChangedListener(new TextWatcher(){
			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			@Override public void onTextChanged(CharSequence s, int start, int before, int count){
				updateNameConfirm();
			}
			@Override public void afterTextChanged(Editable s){}
		});
		editTextEmailPhone.addTextChangedListener(new TextWatcher(){
			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			@Override public void onTextChanged(CharSequence s, int start, int before, int count){
				updateEmailPhoneConfirm();
			}
			@Override public void afterTextChanged(Editable s){}
		});
		editTextPassword.addTextChangedListener(new TextWatcher(){
			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			@Override public void onTextChanged(CharSequence s, int start, int before, int count){
				updatePasswordConfirm();
				updatePasswordCheckConfirm();
			}
			@Override public void afterTextChanged(Editable s){}
		});
		editTextPasswordCheck.addTextChangedListener(new TextWatcher(){
			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			@Override public void onTextChanged(CharSequence s, int start, int before, int count){
				updatePasswordCheckConfirm();
			}
			@Override public void afterTextChanged(Editable s){}
		});

		view.findViewById(R.id.buttonConfirm).setOnClickListener(v -> submit());

		if(savedInstanceState!=null){
			JoinType joinType = (JoinType)savedInstanceState.getSerializable(EXTRA_TYPE);
			if(joinType!=null) setJoinType(joinType);
		}

		return view;
	}

	@Override public void onSaveInstanceState(@NonNull Bundle outState){
		if(type!=null) outState.putSerializable(EXTRA_TYPE, type);
	}

	public void setJoinType(JoinType type){
		if(this.type!=type){
			this.type = type;

			editTextEmailPhone.setText("");
			switch(type){
				case WITH_PHONE:
					editTextEmailPhone.setHint(R.string.join_form_phone_number);
					editTextEmailPhone.setInputType(InputType.TYPE_CLASS_PHONE);
					imgEmailPhone.setImageResource(R.drawable.ic_join_phone);
					break;
				case WITH_EMAIL:
					editTextEmailPhone.setHint(R.string.join_form_email);
					editTextEmailPhone.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
					imgEmailPhone.setImageResource(R.drawable.ic_join_email);
					break;
				case WITH_KAKAO:
				default:
					throw new IllegalArgumentException("type");
			}
		}
	}

	private void submit(){
		String error = getErrorForName();
		if(error!=null||
				(error = errorForEmailPhone())!=null||
				(error = errorForPassword())!=null||
				(error = errorForPasswordCheck())!=null){
			Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
			return;
		}

		String name = editTextName.getText().toString().trim();
		String emailOrPhone = editTextEmailPhone.getText().toString().trim();
		String password = editTextPassword.getText().toString();

		Call<JsonObject> call;

		JoinType type = this.type;
		switch(type){
			case WITH_PHONE:
				call = JoinService.INSTANCE.joinWithPhoneNumber(name, emailOrPhone, password);
				break;
			case WITH_EMAIL:
				call = JoinService.INSTANCE.joinWithEmail(name, emailOrPhone, password);
				break;
			default:
				throw new IllegalArgumentException("type");
		}

		spinnerHandler.show();

		call.enqueue(new BaseCallback(){
			@Override public void onSuccessfulResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull JsonObject result){
				Auth.getInstance().setLoginData(result);
				FragmentActivity activity = getActivity();
				if(activity!=null){
					activity.setResult(Activity.RESULT_OK);
					activity.finish();
				}
			}
			@Override public void onErrorResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull String error){
				spinnerHandler.dismiss();
				switch(error){
					case "bad_name": Toast.makeText(getContext(), "부적합한 이름입니다.", Toast.LENGTH_SHORT).show(); return;
					case "bad_email": Toast.makeText(getContext(), "부적합한 이메일입니다.", Toast.LENGTH_SHORT).show(); return;
					case "bad_phone_number": Toast.makeText(getContext(), "부적합한 전화번호입니다.", Toast.LENGTH_SHORT).show(); return;
					case "bad_password": Toast.makeText(getContext(), "부적합한 비밀번호입니다.", Toast.LENGTH_SHORT).show(); return;
					case "user_exists":
						switch(type){
							case WITH_PHONE: Toast.makeText(getContext(), "동일한 전화번호를 가진 유저가 이미 존재합니다.", Toast.LENGTH_SHORT).show(); return;
							case WITH_EMAIL: Toast.makeText(getContext(), "동일한 이메일을 가진 유저가 이미 존재합니다.", Toast.LENGTH_SHORT).show(); return;
							case WITH_KAKAO: default: throw new IllegalStateException("type");
						}
					default:
						Log.e(TAG, "join: error: "+error);
						Toast.makeText(getContext(), "예상치 못한 오류가 발생하여 회원가입을 완료할 수 없습니다.", Toast.LENGTH_SHORT).show();
				}
			}
			@Override public void onFailedResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
				Log.e(TAG, "join: Failure : "+response.errorBody());
				Toast.makeText(getContext(), "예상치 못한 오류가 발생하여 회원가입을 완료할 수 없습니다.", Toast.LENGTH_SHORT).show();
				spinnerHandler.dismiss();
			}
			@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
				Log.e(TAG, "join: 예상치 못한 오류", t);
				Toast.makeText(getContext(), "예상치 못한 오류가 발생하여 회원가입을 완료할 수 없습니다.", Toast.LENGTH_SHORT).show();
				spinnerHandler.dismiss();
			}
		});
	}

	private void updateNameConfirm(){
		if(editTextName.getText().length()!=0){
			String error = getErrorForName();
			if(error!=null){
				textViewNameConfirm.setVisibility(View.VISIBLE);
				textViewNameConfirm.setText(error);
				return;
			}
		}
		textViewNameConfirm.setVisibility(View.INVISIBLE);
	}
	private void updateEmailPhoneConfirm(){
		if(editTextEmailPhone.getText().length()!=0){
			String error = errorForEmailPhone();
			if(error!=null){
				textViewEmailPhoneConfirm.setVisibility(View.VISIBLE);
				textViewEmailPhoneConfirm.setText(error);
				return;
			}
		}
		textViewEmailPhoneConfirm.setVisibility(View.INVISIBLE);
	}
	private void updatePasswordConfirm(){
		if(editTextPassword.getText().length()!=0){
			String error = errorForPassword();
			if(error!=null){
				textViewPasswordConfirm.setVisibility(View.VISIBLE);
				textViewPasswordConfirm.setText(error);
				return;
			}
		}
		textViewPasswordConfirm.setVisibility(View.INVISIBLE);
	}
	private void updatePasswordCheckConfirm(){
		if(editTextPasswordCheck.getText().length()!=0){
			String error = errorForPasswordCheck();
			if(error!=null){
				textViewPasswordCheckConfirm.setVisibility(View.VISIBLE);
				textViewPasswordCheckConfirm.setText(error);
				return;
			}
		}
		textViewPasswordCheckConfirm.setVisibility(View.INVISIBLE);
	}

	/**
	 * @return 이름 editText의 오류. 없으면 {@code null}.
	 */
	@Nullable private String getErrorForName(){
		Editable text = editTextName.getText();
		if(isBlank(text)){
			return "이름을 입력해주세요.";
		}else if(!Validate.name(text)){
			return "잘못된 이름입니다.";
		}else return null;
	}

	/**
	 * @return 이메일/휴대폰 번호 editText의 오류. 없으면 {@code null}.
	 */
	@Nullable private String errorForEmailPhone(){
		Editable text = editTextEmailPhone.getText();
		switch(type){
			case WITH_PHONE:
				if(isBlank(text)){
					return "핸드폰 번호를 입력해주세요.";
				}else if(!Validate.phoneNumber(text)){
					return "잘못된 핸드폰 번호입니다.";
				}else return null;
			case WITH_EMAIL:
				if(isBlank(text)){
					return "이메일을 입력해주세요.";
				}else if(!Validate.email(text)){
					return "잘못된 이메일입니다.";
				}else return null;
			case WITH_KAKAO:
			default:
				throw new IllegalStateException("type");
		}
	}

	/**
	 * @return 비밀번호 editText의 오류. 없으면 {@code null}.
	 */
	@Nullable private String errorForPassword(){
		Editable text = editTextPassword.getText();
		if(text.length()==0){
			return "비밀번호를 입력해주세요.";
		}else if(!Validate.password(text)){
			return "잘못된 비밀번호입니다. (3자 이상)";
		}else return null;
	}

	/**
	 * @return 비밀번호 editText의 오류. 없으면 {@code null}.
	 */
	@Nullable private String errorForPasswordCheck(){
		Editable password = editTextPassword.getText();
		Editable passwordConfirm = editTextPasswordCheck.getText();
		if(password.length()==0){
			return null;
		}else if(passwordConfirm.length()==0){
			return "비밀번호를 다시 입력해주세요.";
		}else if(!password.toString().equals(passwordConfirm.toString())){
			return "비밀번호가 일치하지 않습니다.";
		}else return null;
	}
}
