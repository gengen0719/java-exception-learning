package com.test.gengen0719.exception_learning;

import org.springframework.ui.Model;

public class ErrorPageLoader {
	
	public String load(Exception e,Model model) {
    	model.addAttribute("message", e.getMessage());
    	e.printStackTrace();
        return "error";
	}
}
