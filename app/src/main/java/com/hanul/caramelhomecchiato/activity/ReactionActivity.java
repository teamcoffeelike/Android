package com.hanul.caramelhomecchiato.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.hanul.caramelhomecchiato.R;

public class ReactionActivity extends AppCompatActivity {
	private EditText etWriteComment;
	private ImageButton buttonAddComment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reaction);

		etWriteComment = findViewById(R.id.editTextWriteComment);
		buttonAddComment = findViewById(R.id.buttonAddComment);

		/* TODO 버튼 누르면 입력한 댓글 등록 */
		buttonAddComment.setOnClickListener(v -> {

		});

	}
}