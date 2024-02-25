package com.example.nagoyameshi.event;

 import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.nagoyameshi.entity.Nagoyameshiuser;
 
 @Component
public class SignupEventPublisher {
     private final ApplicationEventPublisher applicationEventPublisher;
     
     public SignupEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
         this.applicationEventPublisher = applicationEventPublisher;                
     }
     
     public void publishSignupEvent(Nagoyameshiuser nagoyameshiuser, String requestUrl) {
         applicationEventPublisher.publishEvent(new SignupEvent(this, nagoyameshiuser, requestUrl));
     }
}
