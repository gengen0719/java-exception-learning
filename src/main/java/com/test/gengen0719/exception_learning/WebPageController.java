package com.test.gengen0719.exception_learning;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class WebPageController {
	
    @Autowired
    JdbcTemplate jdbc;

	@GetMapping("/exception-learning")
	public String index(Model model) {
		String userName = "太郎"; //本来はユーザー認証によってuser情報が渡ってくる
		MurderPlayLogLoader loader = new MurderPlayLogLoader();
		return loader.load(userName,model);
	}
	
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String commonExceptionHandler(Exception e, Model model) {
    	model.addAttribute("error", e.getClass().getName());
    	model.addAttribute("message", e.getMessage());
    	e.printStackTrace();
        return "error";
    }
	
}
