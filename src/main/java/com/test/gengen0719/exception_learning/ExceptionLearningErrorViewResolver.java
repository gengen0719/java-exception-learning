package com.test.gengen0719.exception_learning;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.autoconfigure.web.WebProperties.Resources;
import org.springframework.boot.autoconfigure.web.servlet.error.DefaultErrorViewResolver;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;

public class ExceptionLearningErrorViewResolver extends DefaultErrorViewResolver {

	public ExceptionLearningErrorViewResolver(ApplicationContext applicationContext, Resources resources) {
		super(applicationContext, resources);
	}
	
    @Override
    public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model) {
        final ModelAndView mav = super.resolveErrorView(request, status, model);
        mav.addObject("message","doeeee");
        return mav;
   }
	
}
