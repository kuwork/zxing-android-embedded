package com.ajb.merchants.model;

/**
 * 缴费记录
 * @author chenming
 *
 */
public class RecordModel {
	
	/**
	 * 车场编号
	 */
	private String ParkCode;
	/**
	 * 企业编号
	 */
	private String ItCode;
	
	/**
	 * 订单编号
	 */
	private String re_Id;
	/**
	 * 停车场名字
	 */
	private  String re_carName;
	/**
	 * 
	 * 进场时间
	 */
	private  String re_inTime;
	
	/**
	 * 离场时间
	 */
	private  String re_outTime;
	
	/**
	 * 停车费用
	 */
	private  String re_money;
	/**
	 * 支付时间
	 */
    private String re_payforTime;

    /**
     * 车牌号
     */
    private String re_carNum;
    /**
     * 是否选中
     */
    private Integer select;
    
    /**
     * 用户手机号
     */
    private  String re_phone;

    
	public String getParkCode() {
		return ParkCode;
	}

	public void setParkCode(String parkCode) {
		ParkCode = parkCode;
	}

	public String getItCode() {
		return ItCode;
	}

	public void setItCode(String itCode) {
		ItCode = itCode;
	}

	public String getRe_phone() {
		return re_phone;
	}

	public void setRe_phone(String re_phone) {
		this.re_phone = re_phone;
	}

	public Integer getSelect() {
		return select;
	}

	public void setSelect(Integer select) {
		this.select = select;
	}

	public String getRe_Id() {
		return re_Id;
	}

	public void setRe_Id(String re_Id) {
		this.re_Id = re_Id;
	}

	public String getRe_carName() {
		return re_carName;
	}

	public void setRe_carName(String re_carName) {
		this.re_carName = re_carName;
	}

	public String getRe_inTime() {
		return re_inTime;
	}

	public void setRe_inTime(String re_inTime) {
		this.re_inTime = re_inTime;
	}

	public String getRe_outTime() {
		return re_outTime;
	}

	public void setRe_outTime(String re_outTime) {
		this.re_outTime = re_outTime;
	}

	public String getRe_money() {
		return re_money;
	}

	public void setRe_money(String re_money) {
		this.re_money = re_money;
	}

	public String getRe_payforTime() {
		return re_payforTime;
	}

	public void setRe_payforTime(String re_payforTime) {
		this.re_payforTime = re_payforTime;
	}

	public String getRe_carNum() {
		return re_carNum;
	}

	public void setRe_carNum(String re_carNum) {
		this.re_carNum = re_carNum;
	}

	@Override
	public String toString() {
		return "RecordModel [re_Id=" + re_Id + ", re_carName=" + re_carName
				+ ", re_inTime=" + re_inTime + ", re_outTime=" + re_outTime
				+ ", re_money=" + re_money + ", re_payforTime=" + re_payforTime
				+ ", re_carNum=" + re_carNum + "]";
	}

	public RecordModel() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RecordModel(String re_Id, String re_carName, String re_inTime,
			String re_outTime, String re_money, String re_payforTime,
			String re_carNum) {
		super();
		this.re_Id = re_Id;
		this.re_carName = re_carName;
		this.re_inTime = re_inTime;
		this.re_outTime = re_outTime;
		this.re_money = re_money;
		this.re_payforTime = re_payforTime;
		this.re_carNum = re_carNum;
	}
    
    
    

}
