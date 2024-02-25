package com.example.nagoyameshi.security;

 import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.nagoyameshi.entity.Nagoyameshiuser;
 
 public class UserDetailsImpl implements UserDetails {
     private final Nagoyameshiuser nagoyameshiuser;
     private final Collection<GrantedAuthority> authorities;
     
     public UserDetailsImpl(Nagoyameshiuser nagoyameshiuser, Collection<GrantedAuthority> authorities) {
         this.nagoyameshiuser = nagoyameshiuser;
         this.authorities = authorities;
     }
     
     public Nagoyameshiuser getUser() {
         return nagoyameshiuser;
     }
     
     // ハッシュ化済みのパスワードを返す
     @Override
     public String getPassword() {
         return nagoyameshiuser.getPassword();
     }
     
     // ログイン時に利用するユーザー名（メールアドレス）を返す
     @Override
     public String getUsername() {
         return nagoyameshiuser.getEmail();
     }
     
     // ロールのコレクションを返す
     @Override
     public Collection<? extends GrantedAuthority> getAuthorities() {
         return authorities;
     }
     
     // アカウントが期限切れでなければtrueを返す
     @Override
     public boolean isAccountNonExpired() {
         return true;
     }
     
     // ユーザーがロックされていなければtrueを返す
     @Override
     public boolean isAccountNonLocked() {
         return true;
     }    
     
     // ユーザーのパスワードが期限切れでなければtrueを返す
     @Override
     public boolean isCredentialsNonExpired() {
         return true;
     }
     
     // ユーザーが有効であればtrueを返す
     @Override
     public boolean isEnabled() {
         return nagoyameshiuser.getEnabled();
     }
}


//package com.example.nagoyameshi.security;
//
//import java.util.Collection;
//
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import com.example.nagoyameshi.entity.Nagoyameshiuser;
//
//public class UserDetailsImpl implements UserDetails {
//	private final Nagoyameshiuser nagoyameshiuser;
//	private final Collection<GrantedAuthority> authorities;
//	
//	public UserDetailsImpl(Nagoyameshiuser nagoyameshiuser, Collection<GrantedAuthority> authorities) {
//		this.nagoyameshiuser = nagoyameshiuser;
//		this.authorities = authorities;
//	}
//	
//	public Nagoyameshiuser getNagoyameshiuser() {
//		return nagoyameshiuser;
//	}
//	
//	//　ハッシュ化済のみパスワードを返す
//	@Override
//	public String getPassword() {
//		return nagoyameshiuser.getPassword();
//	}
//	
//	//ログイン時に利用するユーザー名（メールアドレス）を返す
//	@Override
//	public String getUsername() {
//		return nagoyameshiuser.getEmail();
//	}
//	
//	//ロールのコレクションを返す
//	@Override
//	public Collection<? extends GrantedAuthority> getAuthorities() {
//		return authorities;
//	}
//	
//	//アカウントが期限切れでなければtrueを返す
//	@Override
//	public boolean isAccountNonExpired() {
//		return true;
//	}
//	
//	//ユーザーがロックされていなければtrueを返す
//	@Override
//	public boolean isAccountNonLocked() {
//		return true;
//	}
//	
//	//ユーザーのパスワードが期限切れでなければtrueを返す
//	@Override
//	public boolean isCredentialsNonExpired() {
//		return true;
//	}
//	
//	//ユーザーが有効であればtrueを返す
//	@Override
//	public boolean isEnabled() {
//		return nagoyameshiuser.getEnabled() ;
//	}
//}