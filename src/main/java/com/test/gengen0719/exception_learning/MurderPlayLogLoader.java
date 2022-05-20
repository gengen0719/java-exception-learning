package com.test.gengen0719.exception_learning;

import java.util.List;

import org.springframework.ui.Model;

public class MurderPlayLogLoader {

	/**
	 * ユーザーのマーダーミステリーのプレイ記録を読み込んでパラメーターの {@link Model}に詰めます。
	 * @param userName
	 * @param model
	 * @return modelに対応するtemplateを返します
	 */
	public String load(String userName,Model model) {
		MurderPlayLogDao dao = new MurderPlayLogDao();
		List<MurderPlayLog> murderPlayLogList= dao.load(userName);
		model.addAttribute("playLogList", murderPlayLogList);
		model.addAttribute("userName",userName);
		return "playlog";
	}
	
}
