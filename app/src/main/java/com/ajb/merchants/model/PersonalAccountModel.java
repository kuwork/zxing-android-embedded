package com.ajb.merchants.model;

import java.util.List;

/**
 * 个人账户信息
 * 
 * @author chenming
 * 
 */
public class PersonalAccountModel {

	/**
	 * 用户手机号
	 */
	private String personPhone;
	/**
	 * 用户金额
	 */
	private String personMoney;

	/**
	 * 会员信息
	 */
	private MemberModel personMemerMember;

	/**
	 * 钱包
	 */
	private WalletModel personWalletModel;

	/**
	 * 签到值
	 */
	private String persionSignvalue;

	/**
	 * 车位信息
	 */
	private ParkingSpacesModel personParkingSpacesModel;

	/**
	 * 账户下所对应的的车牌信息
	 */
	private List<String> personLicensePlate;

	public String getPersonPhone() {
		return personPhone;
	}

	public void setPersonPhone(String personPhone) {
		this.personPhone = personPhone;
	}

	public String getPersonMoney() {
		return personMoney;
	}

	public void setPersonMoney(String personMoney) {
		this.personMoney = personMoney;
	}

	public MemberModel getPersonMemerMember() {
		return personMemerMember;
	}

	public void setPersonMemerMember(MemberModel personMemerMember) {
		this.personMemerMember = personMemerMember;
	}

	public WalletModel getPersonWalletModel() {
		return personWalletModel;
	}

	public void setPersonWalletModel(WalletModel personWalletModel) {
		this.personWalletModel = personWalletModel;
	}

	public String getPersionSignvalue() {
		return persionSignvalue;
	}

	public void setPersionSignvalue(String persionSignvalue) {
		this.persionSignvalue = persionSignvalue;
	}

	public ParkingSpacesModel getPersonParkingSpacesModel() {
		return personParkingSpacesModel;
	}

	public void setPersonParkingSpacesModel(
			ParkingSpacesModel personParkingSpacesModel) {
		this.personParkingSpacesModel = personParkingSpacesModel;
	}

	public List<String> getPersonLicensePlate() {
		return personLicensePlate;
	}

	public void setPersonLicensePlate(List<String> personLicensePlate) {
		this.personLicensePlate = personLicensePlate;
	}

}
