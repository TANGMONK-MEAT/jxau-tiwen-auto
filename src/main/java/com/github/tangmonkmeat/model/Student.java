package com.github.tangmonkmeat.model;

public class Student {
	
	private String guid;
	
	private String ip;
	
	private String account;
	
	private String password;
	
	private String miaoTixingSuccess;
	
	private String miaoTixingFiald;

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}


	public String getMiaoTixingSuccess() {
		return miaoTixingSuccess;
	}

	public void setMiaoTixingSuccess(String miaoTixingSuccess) {
		this.miaoTixingSuccess = miaoTixingSuccess;
	}

	public String getMiaoTixingFiald() {
		return miaoTixingFiald;
	}

	public void setMiaoTixingFiald(String miaoTixingFiald) {
		this.miaoTixingFiald = miaoTixingFiald;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "Student [guid=" + guid + ", ip=" + ip + ", account=" + account + ", password=" + password
				+ ", miaoTixingSuccess=" + miaoTixingSuccess + ", miaoTixingFiald=" + miaoTixingFiald + "]";
	}

	public Student(String guid, String ip, String account, String password, String miaoTixingSuccess,
			String miaoTixingFiald) {
		super();
		this.guid = guid;
		this.ip = ip;
		this.account = account;
		this.password = password;
		this.miaoTixingSuccess = miaoTixingSuccess;
		this.miaoTixingFiald = miaoTixingFiald;
	}


}
