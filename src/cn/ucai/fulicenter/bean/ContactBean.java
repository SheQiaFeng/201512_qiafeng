package cn.ucai.fulicenter.bean;

import java.io.Serializable;

public class ContactBean implements Serializable  {
	
	private String 		result;
	private int 		myuid;
	private int 		cuid;
	
	public ContactBean(){
	}
	
	public ContactBean(String result, int myuid, int cuid) {
		super();
		this.result = result;
		this.myuid = myuid;
		this.cuid = cuid;
	}
	
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public int getMyuid() {
		return myuid;
	}
	public void setMyuid(int myuid) {
		this.myuid = myuid;
	}
	public int getCuid() {
		return cuid;
	}
	public void setCuid(int cuid) {
		this.cuid = cuid;
	}

	@Override
	public String toString() {
		return "ContactBean [result=" + result + ", myuid=" + myuid + ", cuid=" + cuid +"]";
	}


	

}
