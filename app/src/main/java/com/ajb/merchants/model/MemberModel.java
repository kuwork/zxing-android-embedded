package com.ajb.merchants.model;

/**
 * 会员页面信息
 * 
 * @author chenming
 * 
 */
public class MemberModel {

	/**
	 * 会员账户信息
	 */
	private String m_accout;
	/**
	 * 账户余额
	 */
	private String m_money;
	
	/**
	 * 会员等级
	 */
    private String m_grade;
    
    /**
     * 会员截止期
     */
    private String m_lastdete;
    

	public String getM_accout() {
		return m_accout;
	}

	public void setM_accout(String m_accout) {
		this.m_accout = m_accout;
	}

	public String getM_money() {
		return m_money;
	}

	public void setM_money(String m_money) {
		this.m_money = m_money;
	}



	public String getM_grade() {
		return m_grade;
	}

	public void setM_grade(String m_grade) {
		this.m_grade = m_grade;
	}

	public String getM_lastdete() {
		return m_lastdete;
	}

	public void setM_lastdete(String m_lastdete) {
		this.m_lastdete = m_lastdete;
	}

	

	public MemberModel() {
		super();
		// TODO Auto-generated constructor stub
	}




    
    
    

}
