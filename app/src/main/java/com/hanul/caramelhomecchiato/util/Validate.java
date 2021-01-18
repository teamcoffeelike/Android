package com.hanul.caramelhomecchiato.util;

import java.util.regex.Pattern;

public final class Validate{
	private Validate(){}

	public static final int MAX_RECIPE_STEPS = 30;

	private static final Pattern NAME_REGEX = Pattern.compile("\\s*[^\t\n]{1,40}\\s*");
	private static final Pattern EMAIL_REGEX = Pattern.compile("\\s*[^@]+@[^@]+\\s*");
	private static final Pattern PHONE_NUMBER_REGEX = Pattern.compile("\\s*(0\\d\\d)[ -]?(\\d{4})[ -]?(\\d{4})\\s*");
	private static final Pattern PASSWORD_REGEX = Pattern.compile(".{3,63}");
	private static final Pattern POST_TEXT_REGEX = Pattern.compile("(?:\\s|\\S){0,1000}");
	private static final Pattern MOTD_REGEX = Pattern.compile("\\s*.{0,100}\\s*");
	private static final Pattern RECIPE_TITLE_REGEX = Pattern.compile("\\s*.{0,100}\\s*");
	private static final Pattern RECIPE_STEP_REGEX = Pattern.compile("(?:\\s|\\S){0,1000}");

	public static boolean name(CharSequence name){
		return NAME_REGEX.matcher(name).matches();
	}
	public static boolean email(CharSequence email){
		return EMAIL_REGEX.matcher(email).matches();
	}
	public static boolean phoneNumber(CharSequence phoneNumber){
		return PHONE_NUMBER_REGEX.matcher(phoneNumber).matches();
	}
	public static boolean password(CharSequence password){
		return PASSWORD_REGEX.matcher(password).matches();
	}
	public static boolean postText(CharSequence text){
		return POST_TEXT_REGEX.matcher(text).matches();
	}
	public static boolean motd(CharSequence motd){
		return MOTD_REGEX.matcher(motd).matches();
	}
	public static boolean recipeTitle(CharSequence text){
		return RECIPE_TITLE_REGEX.matcher(text).matches();
	}
	public static boolean recipeStep(CharSequence motd){
		return RECIPE_STEP_REGEX.matcher(motd).matches();
	}
}
