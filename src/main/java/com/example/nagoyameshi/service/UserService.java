package com.example.nagoyameshi.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Nagoyameshiuser;
import com.example.nagoyameshi.entity.Role;
import com.example.nagoyameshi.form.SignupForm;
import com.example.nagoyameshi.form.UserEditForm;
import com.example.nagoyameshi.repository.NagoyameshiuserRepository;
import com.example.nagoyameshi.repository.RoleRepository;

@Service
public class UserService {
    private final NagoyameshiuserRepository nagoyameshiuserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserService(NagoyameshiuserRepository nagoyameshiuserRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.nagoyameshiuserRepository = nagoyameshiuserRepository;
        this.roleRepository = roleRepository;        
        this.passwordEncoder = passwordEncoder;                
    }    
    
    
    // メールアドレスからユーザーIDを取得するメソッドに変更
    public Integer findUserIdByEmail(String email) {
        Nagoyameshiuser user = nagoyameshiuserRepository.findByEmail(email);
        if (user != null) {
            return user.getId(); // ユーザーが見つかった場合、そのIDを返す
        } else {
            return null; // ユーザーが見つからない場合、nullを返す
        }
    }

    
    @Transactional
    public Nagoyameshiuser create(SignupForm signupForm) {
    	Nagoyameshiuser user = new Nagoyameshiuser();
    	Role role = roleRepository.findByName("ROLE_FREE_MEMBER");
        
        user.setName(signupForm.getName());
        user.setKana(signupForm.getKana());
        user.setPostalCode(signupForm.getPostalCode());
        user.setAddress(signupForm.getAddress());
        user.setPhoneNumber(signupForm.getPhoneNumber());
        user.setEmail(signupForm.getEmail());
        user.setBirthday(signupForm.getBirthday());
        user.setOccupation(signupForm.getOccupation());
        user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
        user.setRole(role);
        user.setEnabled(false);        
        
        
        return nagoyameshiuserRepository.save(user);
    }
    
    @Transactional
    public void update(UserEditForm userEditForm) {
        Nagoyameshiuser nagoyameshiuser = nagoyameshiuserRepository.getReferenceById(userEditForm.getId());
        
        nagoyameshiuser.setName(userEditForm.getName());
        nagoyameshiuser.setKana(userEditForm.getKana());
        nagoyameshiuser.setPostalCode(userEditForm.getPostalCode());
        nagoyameshiuser.setAddress(userEditForm.getAddress());
        nagoyameshiuser.setPhoneNumber(userEditForm.getPhoneNumber());
        nagoyameshiuser.setEmail(userEditForm.getEmail());
        nagoyameshiuser.setBirthday(userEditForm.getBirthday());
        nagoyameshiuser.setOccupation(userEditForm.getOccupation());
        
        nagoyameshiuserRepository.save(nagoyameshiuser);
    }    
    
    @Transactional
    public void createStripeCustomerId(Nagoyameshiuser nagoyameshiuser, String stripeId) {
        nagoyameshiuser.setStripeCustomerId(stripeId);        
        nagoyameshiuserRepository.save(nagoyameshiuser);
    }
    
    @Transactional
    public void updateRole(Nagoyameshiuser nagoyameshiuser, String roleName) {
        Role role = roleRepository.findByName(roleName);
        nagoyameshiuser.setRole(role);
        nagoyameshiuserRepository.save(nagoyameshiuser);
    } 
    
    // 認証情報のロールを更新する
    public void refreshAuthenticationByRole(String newRole) {
        // 現在の認証情報を取得する
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 新しい認証情報を作成する
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(newRole));
        Authentication newAuth = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), authorities);

        // 認証情報を更新する
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
    
 // メールアドレスが登録済みかどうかをチェックする
    public boolean isEmailRegistered(String email) {
        Nagoyameshiuser user = nagoyameshiuserRepository.findByEmail(email);  
        return user != null;
    }    
    
    // パスワードとパスワード（確認用）の入力値が一致するかどうかをチェックする
    public boolean isSamePassword(String password, String passwordConfirmation) {
        return password.equals(passwordConfirmation);
    }     
    
    // ユーザーを有効にする
    @Transactional
    public void enableUser(Nagoyameshiuser nagoyameshiuser) {
        nagoyameshiuser.setEnabled(true); 
        nagoyameshiuserRepository.save(nagoyameshiuser);
    }    
    
    // メールアドレスが変更されたかどうかをチェックする
    public boolean isEmailChanged(UserEditForm userEditForm) {
        Nagoyameshiuser currentUser = nagoyameshiuserRepository.getReferenceById(userEditForm.getId());
        return !userEditForm.getEmail().equals(currentUser.getEmail());      
    }  
}
