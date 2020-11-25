package com.hanul.caramelhomecchiato.data;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class Notification implements Parcelable{
	private User notifyingUser;

	public Notification(User notifyingUser){
		this.notifyingUser = notifyingUser;
	}
	protected Notification(Parcel parcel){
		this.notifyingUser = parcel.readParcelable(User.class.getClassLoader());
	}

	@Override public int describeContents(){
		return 0;
	}
	@Override public void writeToParcel(Parcel dest, int flags){
		dest.writeParcelable(getNotifyingUser(), flags);
	}

	public User getNotifyingUser(){
		return notifyingUser;
	}
	public void setNotifyingUser(User notifyingUser){
		this.notifyingUser = notifyingUser;
	}

	public abstract void accept(NotificationVisitor visitor);

	public static final class Reaction extends Notification{
		public static final Creator<Reaction> CREATOR = new Creator<Reaction>(){
			@Override public Reaction createFromParcel(Parcel in){
				return new Reaction(in);
			}
			@Override public Reaction[] newArray(int size){
				return new Reaction[size];
			}
		};

		private int reactionId;

		public Reaction(User reactedUser, int reactionId){
			super(reactedUser);
			this.reactionId = reactionId;
		}

		protected Reaction(Parcel parcel){
			super(parcel);
			reactionId = parcel.readInt();
		}

		@Override public void accept(NotificationVisitor visitor){
			visitor.visit(this);
		}

		@Override public void writeToParcel(Parcel dest, int flags){
			super.writeToParcel(dest, flags);
			dest.writeInt(reactionId);
		}

		public int getReactionId(){
			return reactionId;
		}
		public void setReactionId(int reactionId){
			this.reactionId = reactionId;
		}
	}

	public static final class Like extends Notification {
		public static final Creator<Like> CREATOR = new Creator<Like>(){
			@Override public Like createFromParcel(Parcel in){
				return new Like(in);
			}
			@Override public Like[] newArray(int size){
				return new Like[size];
			}
		};

		public Like(User likedUser){
			super(likedUser);
		}
		protected Like(Parcel parcel){
			super(parcel);
		}

		@Override public void accept(NotificationVisitor visitor){
			visitor.visit(this);
		}
	}

	public static final class Follow extends Notification {
		public static final Creator<Follow> CREATOR = new Creator<Follow>(){
			@Override public Follow createFromParcel(Parcel in){
				return new Follow(in);
			}
			@Override public Follow[] newArray(int size){
				return new Follow[size];
			}
		};

		public Follow(User likedUser){
			super(likedUser);
		}
		protected Follow(Parcel parcel){
			super(parcel);
		}

		@Override public void accept(NotificationVisitor visitor){
			visitor.visit(this);
		}
	}
}
