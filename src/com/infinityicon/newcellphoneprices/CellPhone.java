package com.infinityicon.newcellphoneprices;

public class CellPhone {
	private int cellID;
	private String cellName;
	private String cellDesc;
	private String cellImage;
	private String cellBrand;
	
	public int getCellID() {
		return cellID;
	}
	public void setCellID(int cellID) {
		this.cellID = cellID;
	}
	public String getCellName() {
		return cellName;
	}
	public void setCellName(String cellName) {
		this.cellName = cellName;
	}
	public String getCellDesc() {
		return cellDesc;
	}
	public void setCellDesc(String cellDesc) {
		this.cellDesc = cellDesc;
	}
	public String getCellImage() {
		return cellImage;
	}
	public void setCellImage(String cellImage) {
		this.cellImage = cellImage;
	}
	public String getCellBrand() {
		return cellBrand;
	}
	public void setCellBrand(String cellBrand) {
		this.cellBrand = cellBrand;
	}
	public String toString ( ) {
		return ( getCellName ( ) );
	}
	
	public CellPhone(int cellID, String cellName, String cellDesc,
			String cellImage, String cellBrand ) {
		this.cellID = cellID;
		this.cellName = cellName;
		this.cellDesc = cellDesc;
		this.cellImage = cellImage;
		this.cellBrand = cellBrand;
	}
}