package com.hanul.caramelhomecchiato.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.data.Reaction;

public class ReactionAdapter extends BaseAdapter<Reaction> {
	@NonNull @Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_reaction, parent, false));
	}

	public static final class ViewHolder extends BaseAdapter.ViewHolder<Reaction> {
		private final TextView tvUserName;
		private final TextView tvReaction;

		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			tvUserName = itemView.findViewById(R.id.textViewUserName);
			tvReaction = itemView.findViewById(R.id.textViewReaction);
		}

		@Override
		protected void setItem(int position, Reaction reaction) {

		}
	}
}