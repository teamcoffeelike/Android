package com.hanul.caramelhomecchiato.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hanul.caramelhomecchiato.JoinActivity.JoinType;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.task.JoinWithEmailTask;
import com.hanul.caramelhomecchiato.task.JoinWithPhoneNumberTask;
import com.hanul.caramelhomecchiato.task.JsonResponseTask;
import com.hanul.caramelhomecchiato.util.Auth;
import com.hanul.caramelhomecchiato.util.Validate;

public class JoinFormFragment extends Fragment{
	private EditText etEmailPhone;
	private EditText etName;
	private EditText etPassword;
	private EditText etPwConfirm;
	private TextView tvPwCheck;
	private ImageView imgEmailPhone;
	private Button btnConfirm;

	private JoinType type;


	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_join_form, container, false);
		etEmailPhone = view.findViewById(R.id.editTextEmailPhone);
		etName = view.findViewById(R.id.editTextName);
		etPassword = view.findViewById(R.id.editTextPassword);
		etPwConfirm = view.findViewById(R.id.editTextPasswordConfirm);
		tvPwCheck = view.findViewById(R.id.textViewPwCheck);
		imgEmailPhone = view.findViewById(R.id.imgEmailPhone);
		btnConfirm = view.findViewById(R.id.buttonConfirm);

		TextWatcher textWatcher = new TextWatcher(){
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count){
				String password = etPassword.getText().toString();
				String confirm = etPwConfirm.getText().toString();
				if(password.equals(confirm)){
					tvPwCheck.setVisibility(View.VISIBLE);
					tvPwCheck.setText("비밀번호가 일치합니다");
					tvPwCheck.setTextColor(getResources().getColor(R.color.ForestGreenTraditional));
				}else{
					tvPwCheck.setVisibility(View.VISIBLE);
					tvPwCheck.setText("비밀번호가 일치하지 않습니다!");
					tvPwCheck.setTextColor(getResources().getColor(R.color.red));
				}
				if(etPassword.getText().toString().length()==0||etPwConfirm.getText().toString().length()==0){
					tvPwCheck.setVisibility(View.GONE);
				}
			}

			@Override
			public void afterTextChanged(Editable s){}
		};

		//비밀번호 두개 비교해서 일치
		//비밀번호 확인
		etPwConfirm.addTextChangedListener(textWatcher);
		etPassword.addTextChangedListener(textWatcher);


		btnConfirm.setOnClickListener(v -> {
			String name = etName.getText().toString();
			String password = etPassword.getText().toString();
			String pwConfirm = etPwConfirm.getText().toString();
			String emailOrPhone = etEmailPhone.getText().toString();

			JsonResponseTask<JoinFormFragment> t;

			if(!Validate.name(name)){
				Toast.makeText(getContext(), "부적합한 이름입니다.", Toast.LENGTH_SHORT).show();
				return;
			}
			if(!Validate.password(password)){
				Toast.makeText(getContext(), "부적합한 비밀번호입니다.", Toast.LENGTH_SHORT).show();
				return;
			}
			if(!password.equals(pwConfirm)){
				Toast.makeText(getContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
				return;
			}
			switch(type){
			case WITH_PHONE:
				if(!Validate.phoneNumber(emailOrPhone)){
					Toast.makeText(getContext(), "부적합한 이름입니다.", Toast.LENGTH_SHORT).show();
					return;
				}
				t = new JoinWithPhoneNumberTask<>(this, name, emailOrPhone, password);

				break;
			case WITH_EMAIL:
				if(!Validate.email(emailOrPhone)){
					Toast.makeText(getContext(), "부적합한 이름입니다.", Toast.LENGTH_SHORT).show();
					return;
				}
				t = new JoinWithEmailTask<>(this, name, emailOrPhone, password);

				break;
			default:
				throw new IllegalArgumentException("type");
			}

			t.onSucceed((frag, o) -> {
				if(o.has("error")){
					switch(o.get("error").getAsString()){
					// TODO error handling
					}
				}else{
					Auth.getInstance().setLoginData(o);
					// TODO finish
				}
			}).onCancelled((frag, o) -> {
				// TODO throbber
			}).execute();
		});

		return view;
	}


	public void setJoinType(JoinType type){
		this.type = type;

		switch(type){
		case WITH_PHONE:
			etEmailPhone.setHint("핸드폰 번호");
			imgEmailPhone.setImageResource(R.drawable.ic_join_phone);
			break;
		case WITH_EMAIL:
			etEmailPhone.setHint("이메일");
			imgEmailPhone.setImageResource(R.drawable.ic_join_email);
			break;
		case WITH_KAKAO:
		default:
			throw new IllegalArgumentException("type");
		}
	}
}
