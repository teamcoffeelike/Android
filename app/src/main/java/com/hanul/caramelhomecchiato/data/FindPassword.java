package com.hanul.caramelhomecchiato.data;

import android.os.Parcel;
import android.os.Parcelable;

public class FindPassword implements Parcelable {
	public static final Creator<FindPassword> CREATOR = new Creator<FindPassword>() {
		@Override public FindPassword createFromParcel(Parcel in) { return null; }
		@Override public FindPassword[] newArray(int size) { return new FindPassword[size]; }
	};

	private int id;
	private String name;
	private String email;
	private String phoneNumber;

	public FindPassword(int id, String name, String email, String phoneNumber) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.phoneNumber = phoneNumber;
	}

	protected FindPassword(Parcel in) {
		id = in.readInt();
		name = in.readString();
		email = in.readString();
		phoneNumber = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeString(email);
		dest.writeString(phoneNumber);
	}

	public int getId() { return id; }
	public void setId(int id) { this.id = id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	public String getPhoneNumber() { return phoneNumber; }
	public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}
