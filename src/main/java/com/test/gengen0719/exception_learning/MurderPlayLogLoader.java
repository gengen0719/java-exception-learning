package com.test.gengen0719.exception_learning;

import java.util.List;

import org.springframework.ui.Model;

public class MurderPlayLogLoader {

	public void load(Model model){
		String userName = "太郎"; //TODO User認証
		MurderPlayLogDao dao = new MurderPlayLogDao();
		List<MurderPlayLog> murderPlayLogList= dao.load(userName);
		model.addAttribute("playLogList", murderPlayLogList);
	}
	
}
