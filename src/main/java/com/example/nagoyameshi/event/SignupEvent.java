package com.example.nagoyameshi.event;

 import org.springframework.context.ApplicationEvent;

import com.example.nagoyameshi.entity.Nagoyameshiuser;

import lombok.Getter;
 
 @Getter
 public class SignupEvent extends ApplicationEvent {
     private Nagoyameshiuser nagoyameshiuser;
     private String requestUrl;        
 
     public SignupEvent(Object source, Nagoyameshiuser nagoyameshiuser, String requestUrl) {
         super(source);
         
         this.nagoyameshiuser = nagoyameshiuser;        
         this.requestUrl = requestUrl;
     }
}
