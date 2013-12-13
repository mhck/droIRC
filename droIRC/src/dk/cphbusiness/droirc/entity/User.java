package dk.cphbusiness.droirc.entity;

public class User {

	private int id;
	private String userid;
	private String nickname;
	private String email;
	private String perform;
	
	public User(int id, String name) {
		this.id = id;
		this.nickname = name;
		userid = "droIRC";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPerform() {
		return perform;
	}

	public void setPerform(String perform) {
		this.perform = perform;
	}
	
	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}	
}
