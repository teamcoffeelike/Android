package com.hanul.caramelhomecchiato.util;

import java.util.regex.Pattern;

public final class Validate{
	private Validate(){}

	private static final Pattern NAME_REGEX = Pattern.compile("[^\t\n]{1,40}");
	private static final Pattern EMAIL_REGEX = Pattern.compile("[^@]+@[^@]+");
	private static final Pattern PHONE_NUMBER_REGEX = Pattern.compile("(\\d{3})[ -]?(\\d{4})[ -]?(\\d{4})");
	private static final Pattern PASSWORD_REGEX = Pattern.compile("\\S{3,63}");
	private static final Pattern POST_TEXT_REGEX = Pattern.compile(".{0,1000}");
	private static final Pattern MOTD_REGEX = Pattern.compile(".{0,100}");

	public static boolean name(String name){
		return NAME_REGEX.matcher(name).matches();
	}
	public static boolean email(String email){
		return EMAIL_REGEX.matcher(email).matches();
	}
	public static boolean phoneNumber(String phoneNumber){
		return PHONE_NUMBER_REGEX.matcher(phoneNumber).matches();
	}
	public static boolean password(String password){
		return PASSWORD_REGEX.matcher(password).matches();
	}
	public static boolean postText(String text){
		return POST_TEXT_REGEX.matcher(text).matches();
	}
	public static boolean motd(String motd){
		return MOTD_REGEX.matcher(motd).matches();
	}
}
