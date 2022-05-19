package com.test.gengen0719.exception_learning;

import java.util.Date;

import lombok.Data;

@Data
public class MurderPlayLog {

	private String userName;
	private String gameId;
	private String gameName;
	private int played;
	private Date playDate;
	
}
