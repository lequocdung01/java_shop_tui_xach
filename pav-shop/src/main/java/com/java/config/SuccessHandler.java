package com.java.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

//Giao diện AuthenticationSuccessHandler là một giao diện trong Spring Security được sử dụng để xử lý các sự kiện 
//sau khi xác thực thành công. Nó định nghĩa một phương thức onAuthenticationSuccess() mà bạn cần triển khai. 
//Phương thức này sẽ được gọi khi người dùng đăng nhập thành công và sẽ nhận đối tượng Authentication 
//chứa thông tin về người dùng đã đăng nhập.
public class SuccessHandler implements AuthenticationSuccessHandler {
	//RedirectStrategy là một interface được sử dụng để xử lý chuyển hướng (redirect) 
	//trong các kịch bản liên quan đến bảo mật. 
	//Nó cung cấp các phương thức để xử lý chuyển hướng và định dạng URL chuyển hướng.
	//DefaultRedirectStrategy là một lớp cung cấp một implementaion mặc định của RedirectStrategy. 
	//Nó thực hiện các phương thức trong RedirectStrategy để thực hiện chuyển hướng trên các yêu cầu HTTP.
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	//Authentication authentication: Đối tượng Authentication chứa thông tin về người dùng đã đăng nhập thành công.
	public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Authentication authentication) throws IOException, ServletException {
		boolean hasRoleUser = false;
		boolean hasAdmin = false;
		//được sử dụng để lấy danh sách các GrantedAuthority (quyền được cấp) từ đối tượng Authentication.
		//Trong Spring Security, GrantedAuthority đại diện cho một quyền được cấp cho người dùng sau khi xác thực thành công. 
		//Nó là một interface đơn giản được sử dụng để xác định quyền truy cập của người dùng trong hệ thống.
		//Các lớp cụ thể triển khai GrantedAuthority có thể đại diện cho các quyền hoặc vai trò khác nhau trong ứng dụng của bạn, 
		//chẳng hạn như ROLE_USER, ROLE_ADMIN, ROLE_MANAGER, và nhiều quyền khác tùy thuộc vào yêu cầu của hệ thống.
		//Với mỗi người dùng đã xác thực, danh sách các GrantedAuthority sẽ chứa các quyền hoặc vai trò đã được cấp 
		//cho người dùng đó. Điều này cho phép bạn xác định và kiểm tra quyền truy cập của người dùng để thực hiện 
		//các kiểm tra bảo mật, hiển thị phần nội dung tương ứng, và thực hiện các hành động phù hợp trong ứng dụng của bạn.
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		for (GrantedAuthority grantedAuthority : authorities) {
			if (grantedAuthority.getAuthority().equals("ROLE_USER")) {
				hasRoleUser = true;
				break;
			} else if (grantedAuthority.getAuthority().equals("ROLE_ADMIN")) {
				hasAdmin = true;
				break;
			}
		}
		if (hasRoleUser) {
			redirectStrategy.sendRedirect(httpServletRequest, httpServletResponse, "/cartItem");
		} else if (hasAdmin) {
			redirectStrategy.sendRedirect(httpServletRequest, httpServletResponse, "/admin/home");
		} else {
			throw new IllegalStateException();
		}
	}

}
