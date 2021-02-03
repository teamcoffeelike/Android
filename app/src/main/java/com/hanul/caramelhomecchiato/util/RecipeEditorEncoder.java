package com.hanul.caramelhomecchiato.util;

import android.content.ContentResolver;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.hanul.caramelhomecchiato.data.RecipeCategory;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * <pre>
 * RecipeEditor
 *   : Mode (Function NULL)* EOF
 *   ;
 *
 * Mode
 *   : MODE_WRITE   # 레시피 새로 작성
 *   | MODE_EDIT I  # 레시피 변경
 *   ;
 *
 * Function
 *   : SET_CATEGORY S
 *   | SET_TITLE S
 *   | SET_COVER_IMAGE B
 *   | SET_TOTAL_STEP_COUNT I
 *   | NEW_STEP I
 *   | SELECT_STEP I
 *   | MOVE_STEP I I
 *   | REMOVE_STEP I
 *   | SET_STEP_IMAGE B
 *   | REMOVE_STEP_IMAGE
 *   | SET_STEP_TEXT S
 *   ;
 *
 * S : (DataInput에서 정의된 Modified UTF-8 String)
 * B : (uint8)
 * I : (int32)
 * NULL : (uint8 0)
 *
 * # 바이트 상수
 * MODE_WRITE : '1';
 * MODE_EDIT : '2';
 * SET_CATEGORY : 'c';
 * SET_TITLE : 't';
 * SET_COVER_IMAGE : 'i';
 * SET_TOTAL_STEP_COUNT : 'q';
 * NEW_STEP : 'n';
 * SELECT_STEP : 's';
 * MOVE_STEP : 'm';
 * REMOVE_STEP : 'r';
 * SET_STEP_IMAGE : 'z';
 * REMOVE_STEP_IMAGE : 'x';
 * SET_STEP_TEXT : 'v';
 * </pre>
 * ㅅㅂㅋㅋ<br>
 * 레시피 수정을 위한 작은 domain specific regular language.<br>
 * 각각의 수정 작업에 해당되는 함수들의 1차원 배열.
 * 함수는 1바이트의 선택자를 따르는 임의 바이트의 데이터 패러미터를 칭합니다. 함수의 끝 부분은 null byte로 구분됩니다.<br>
 * <br>
 * <pre>
 * c..hot_coffee.
 *
 *
 * Indicator  Parameter   Terminator
 *         _ ____________ _
 *         c ..hot_coffee .
 * (공백 문자는 존재하지 않음. '.'은 해독 불가능한 character.)
 * </pre>
 * 문자열은 Java의 DataOutputStream에서 정의하는 Modified UTF-8 String으로 전달됩니다.
 * 이미지 등의 바이너리 리소스는 인코더 내부에서 인덱싱되어 별개의 멀티파트 body로 전달됩니다.
 * 리소스 간의 연결은 1바이트의 index 번호로 전달됩니다.<br>
 * <br>
 * step count는 무조건 제공되어야 하며, 모든 추가/삭제/유지되는 step은 각자 한 번씩 '선택'되어야 합니다.
 * 비어 있는 인덱스가 있거나 기존에 존재하던 step이 아무런 작업이 이루어지지 않았다면 불완전한 작업으로 간주하게 됩니다.<br>
 * step count는 step의 선택 이전에 제공되어야 하며, step 내부 값을 수정하는 작업(내용 수정, 이미지 수정/삭제)은 step의 선택 이후에 이루어져야 합니다.
 * 모든 step 수정 작업은 마지막으로 선택한 step을 대상으로 이루어집니다.<br>
 * <br>
 * 모든 수정 작업에 대해, 두 번 이상의 값 제공 또는 이미 선택된 step의 재선택은 작업 요청에서의 오류로 간주하여 에러를 일으킵니다.
 * @see <a href="https://gist.github.com/iwasamistake/aef0409a7df3111c26e8e91cba2bbcf8">https://gist.github.com/iwasamistake/aef0409a7df3111c26e8e91cba2bbcf8</a>
 */
public class RecipeEditorEncoder{
	private static final byte MODE_WRITE = '1';
	private static final byte MODE_EDIT = '2';

	private static final byte OP_SET_CATEGORY = 'c';
	private static final byte OP_SET_TITLE = 't';
	private static final byte OP_SET_COVER_IMAGE = 'i';
	private static final byte OP_SET_TOTAL_STEP_COUNT = 'q';
	private static final byte OP_NEW_STEP = 'n';
	private static final byte OP_SELECT_STEP = 's';
	private static final byte OP_MOVE_STEP = 'm';
	private static final byte OP_REMOVE_STEP = 'r';
	private static final byte OP_SET_STEP_IMAGE = 'z';
	private static final byte OP_REMOVE_STEP_IMAGE = 'x';
	private static final byte OP_SET_STEP_TEXT = 'v';

	private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	private final DataOutputStream writer = new DataOutputStream(bytes);
	private final Map<Integer, Uri> resources = new HashMap<>();
	private int indexIncr;

	public Map<String, RequestBody> toRequestBody(ExecutorService exec,
	                                              ContentResolver contentResolver) throws ExecutionException, InterruptedException{
		MediaType imageMediaType = MediaType.parse("image/*");

		List<Future<Entry<Integer, RequestBody>>> resourceList = new ArrayList<>();
		for(Entry<Integer, Uri> e : resources.entrySet())
			resourceList.add(exec.submit(() -> new SimpleEntry<>(e.getKey(),
					RequestBody.create(
							IOUtils.read(contentResolver, e.getValue()),
							imageMediaType))));

		Map<String, RequestBody> m = new HashMap<>();

		m.put("inst\"; filename=\"inst", RequestBody.create(bytes.toByteArray(), MediaType.parse("application/x-binary")));

		for(Future<Entry<Integer, RequestBody>> f : resourceList){
			Entry<Integer, RequestBody> e = f.get();
			int key = e.getKey();
			m.put(key+"\"; filename=\""+key, e.getValue());
		}

		return m;
	}

	/////// Modes //////

	public void writeMode(){
		b(MODE_WRITE);
	}

	public void editMode(int id){
		b(MODE_EDIT);
		i(id);
	}

	/////// Cover //////

	public void setCategory(@NonNull RecipeCategory category){
		b(OP_SET_CATEGORY);
		s(category.toString());
		terminator();
	}

	public void setTitle(@NonNull String text){
		b(OP_SET_TITLE);
		s(text);
		terminator();
	}

	public void setCoverImage(@NonNull Uri coverImage){
		b(OP_SET_COVER_IMAGE);
		b(indexResource(coverImage));
		terminator();
	}

	/////// Step //////

	public void setTotalStepCount(int count){
		b(OP_SET_TOTAL_STEP_COUNT);
		i(count);
		terminator();
	}

	/**
	 * 해당 index에 새로운 페이지를 생성 후 선택합니다.
	 */
	public void newStep(int index){
		b(OP_NEW_STEP);
		i(index);
		terminator();
	}

	/**
	 * 기존 인덱스 {@code index}에 존재하는 페이지를 동일한 인덱스에 옮겨 붙인 후 선택합니다.
	 */
	public void selectStep(int index){
		b(OP_SELECT_STEP);
		i(index);
		terminator();
	}

	/**
	 * 기존 인덱스 {@code index}에 존재하던 페이지를 {@code newIndex}로 옮긴 후 선택합니다.
	 */
	public void moveStep(int index, int newIndex){
		b(OP_MOVE_STEP);
		i(index);
		i(newIndex);
		terminator();
	}

	/**
	 * 기존 인덱스 {@code index}에 존재하던 페이지를 삭제합니다.
	 */
	public void removeStep(int index){
		b(OP_REMOVE_STEP);
		i(index);
		terminator();
	}

	public void setStepImage(@NonNull Uri image){
		b(OP_SET_STEP_IMAGE);
		b(indexResource(image));
		terminator();
	}

	public void removeStepImage(){
		b(OP_REMOVE_STEP_IMAGE);
		terminator();
	}

	public void setStepText(@NonNull String text){
		b(OP_SET_STEP_TEXT);
		s(text);
		terminator();
	}

	/////// Writers //////

	private void b(byte b){
		try{
			writer.writeByte(b);
		}catch(IOException e){
			wtf(e);
		}
	}

	private void i(int i){
		try{
			writer.writeInt(i);
		}catch(IOException e){
			wtf(e);
		}
	}

	private void s(@NonNull String s){
		try{
			writer.writeUTF(s);
		}catch(IOException e){
			wtf(e);
		}
	}

	private void terminator(){
		b((byte)0);
	}

	private byte indexResource(@NonNull Uri resource){
		int i = indexIncr++;
		if(i >= 256) throw new IllegalStateException("인덱싱된 리소스가 너무 많습니다.");
		resources.put(i, resource);
		return (byte)i;
	}

	private void wtf(Exception e){
		throw new RuntimeException("Unexpected", e);
	}
}
