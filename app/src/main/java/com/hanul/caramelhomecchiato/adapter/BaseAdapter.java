package com.hanul.caramelhomecchiato.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 일반화시킨 RecyclerView Adapter
 *
 * @param <T> RecyclerView가 사용하는 데이터 타입
 */
public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseAdapter.ViewHolder<T>>{
	private final List<T> elements = new ArrayList<>();

	public List<T> elements(){
		return elements;
	}

	@Override public void onBindViewHolder(@NonNull BaseAdapter.ViewHolder<T> holder, int position){
		holder.setItem(position, elements.get(position));
	}
	@Override public int getItemCount(){
		return elements.size();
	}


	public static abstract class ViewHolder<T> extends RecyclerView.ViewHolder{
		public ViewHolder(@NonNull View itemView){
			super(itemView);
		}

		protected abstract void setItem(int position, T element);
	}
}
