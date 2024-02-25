package com.example.nagoyameshi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.nagoyameshi.service.StripeService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/primes")
public class PrimeController {
	
	  private final StripeService stripeService; 
	  
	  public PrimeController (StripeService stripeService) { 	      
	         this.stripeService = stripeService;
	    }    
    @GetMapping
    public String index(Model model, HttpServletRequest httpServletRequest) {
        // ここで必要なデータをModelに追加する
        // model.addAttribute("data", data);
    	
    	 String sessionId = stripeService.createStripeSession(httpServletRequest);
    	 
    	  model.addAttribute("sessionId", sessionId);

        return "primes/index"; // prime.htmlテンプレートを表示する
    }
    
    @GetMapping("/delete")
    public String deleteindex(Model model) {
        // ここで必要なデータをModelに追加する
        // model.addAttribute("data", data);

        return "primes/delete"; // prime.htmlテンプレートを表示する
    }
    
//    @GetMapping("/success")
//    public String success(Model model) {
//        // ここで必要なデータをModelに追加する
//        // model.addAttribute("data", data);
//
//        return "primes/success"; // prime.htmlテンプレートを表示する
//    }

}