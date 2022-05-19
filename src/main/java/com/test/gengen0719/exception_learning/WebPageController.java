package com.test.gengen0719.exception_learning;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebPageController {
	
    @Autowired
    JdbcTemplate jdbc;

	@GetMapping("/exception-learning")
	public String index(Model model) {
		MurderPlayLogLoader loader = new MurderPlayLogLoader();
		loader.load(model);
		return "index";
	}
	
    @ExceptionHandler(Exception.class)
    public String commonExceptionHandler(Exception e, Model model) {
    	model.addAttribute("error", e.getClass().getName());
    	model.addAttribute("message", e.getMessage());
        return "error";
    }
	
}
