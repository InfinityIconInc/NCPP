package com.infinityicon.newcellphoneprices;

public class CellPhone_OS_Core extends CellPhone {
	private String cellCore;
	private String cellOS;

	public CellPhone_OS_Core(int cellID, String cellName, String cellDesc,
			String cellImage, String cellBrand, String cellCore, String cellOS) {
		super(cellID, cellName, cellDesc, cellImage, cellBrand);
		this.cellCore = cellCore;
		this.cellOS = cellOS;
	}

	public String getCellCore() {
		return cellCore;
	}

	public void setCellCPU(String cellCore) {
		this.cellCore = cellCore;
	}

	public String getCellOS() {
		return cellOS;
	}

	public void setCellOS(String cellOS) {
		this.cellOS = cellOS;
	}
}
