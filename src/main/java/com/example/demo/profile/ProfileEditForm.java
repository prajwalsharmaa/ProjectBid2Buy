package com.example.demo.profile;

import org.springframework.web.multipart.MultipartFile;

public class ProfileEditForm {

	private String firstName;
	private String lastName;
	private String address;
	private Integer age;
	private MultipartFile profile;

	public ProfileEditForm() {
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public MultipartFile getProfile() {
		return profile;
	}

	public void setProfile(MultipartFile profile) {
		this.profile = profile;
	}

}
