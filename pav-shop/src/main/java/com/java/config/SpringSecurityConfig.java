package com.java.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.java.service.PavShopService;

//@Configuration: Đánh dấu rằng lớp được chú thích là một lớp cấu hình trong Spring. 
//Nó cho biết rằng lớp này chứa các cấu hình bean và nó sẽ được quản lý bởi Spring.
//@EnableWebSecurity: Được sử dụng để kích hoạt và cấu hình bảo mật dựa trên web trong ứng dụng của bạn. 
//Khi bạn sử dụng @EnableWebSecurity, nó sẽ kích hoạt Spring Security 
//và cung cấp các tính năng bảo mật trên ứng dụng web của bạn.
@Configuration
@EnableWebSecurity
//Khi bạn mở rộng (extends) lớp WebSecurityConfigurerAdapter trong lớp cấu hình bảo mật của mình, 
//bạn đang tạo một lớp con để cấu hình chi tiết hơn cho bảo mật trên ứng dụng web của bạn.
//Lớp WebSecurityConfigurerAdapter là một lớp trừu tượng trong Spring Security, 
//cung cấp một cơ sở để bạn ghi đè các phương thức và cấu hình các chi tiết bảo mật tùy chỉnh trong ứng dụng của bạn.
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private PavShopService pavShopService;

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		return bCryptPasswordEncoder;
	}

	//AuthenticationManager được sử dụng để xác thực thông tin đăng nhập và cung cấp quyền truy cập cho người dùng
	//AuthenticationManagerBuilder cung cấp các phương thức để cấu hình 
	//Sau khi cấu hình xong, AuthenticationManagerBuilder sẽ sử dụng thông tin bạn đã cung cấp để xây dựng một đối tượng 
	//AuthenticationManager sẵn sàng để xác thực người dùng trong ứng dụng của bạn.
	//auth.userDetailsService(pavShopService), bạn đang cung cấp một đối tượng Service (pavShopService) 
	//để tìm kiếm thông tin người dùng trong cơ sở dữ liệu. Đồng thời, 
	//phương thức .passwordEncoder(passwordEncoder()) được sử dụng để cung cấp một đối tượng PasswordEncoder 
	//để mã hóa và so sánh mật khẩu người dùng.
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

		// Set đặt dịch vụ để tìm kiếm User trong Database.
		// Và sét đặt PasswordEncoder.
		auth.userDetailsService(pavShopService).passwordEncoder(passwordEncoder());

	}

	//Cuối cùng, phương thức configure(HttpSecurity http) được sử dụng để cung cấp các cài đặt bảo mật 
	//và quyền truy cập cho các trang trong ứng dụng web của bạn.
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		//Tắt CSRF (Cross-Site Request Forgery) để đơn giản hóa quá trình kiểm tra CSRF token trong các yêu cầu.
		http.csrf().disable();

		// Các trang không yêu cầu login
		//http.authorizeRequests(): Bắt đầu cấu hình các yêu cầu xác thực (authentication) 
		//và ủy quyền (authorization) trong đối tượng http của lớp HttpSecurity.
		//antMatchers("/", "/login", "/logout"): Xác định các đường dẫn mà bạn muốn áp dụng quyền truy cập.
		//permitAll(): Cho phép tất cả người dùng truy cập các đường dẫn được chỉ định 
		//mà không yêu cầu xác thực (đăng nhập).
		http.authorizeRequests().antMatchers("/", "/login", "/logout").permitAll();

		// Trang /userInfo yêu cầu phải login với vai trò ROLE_USER hoặc ROLE_ADMIN.
		// Nếu chưa login, nó sẽ redirect tới trang /login.
		//access("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')"): Xác định rằng để truy cập vào trang "/checkOut", 
		//người dùng cần phải có một trong các vai trò "ROLE_USER" hoặc "ROLE_ADMIN".
		http.authorizeRequests().antMatchers("/checkOut").access("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')");

		// Trang chỉ dành cho ADMIN
		http.authorizeRequests().antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')");

		// Khi người dùng đã login, với vai trò XX.
		// Nhưng truy cập vào trang yêu cầu vai trò YY,
		// Ngoại lệ AccessDeniedException sẽ ném ra.
		http.authorizeRequests().and().exceptionHandling().accessDeniedPage("/403");

		// Cấu hình cho Login Form.
		http.authorizeRequests().and().formLogin()//
			// Submit URL của trang login
		//.loginProcessingUrl("/doLogin"): Xác định URL để xử lý yêu cầu đăng nhập khi người dùng gửi form đăng nhập.
			.loginProcessingUrl("/doLogin") // Submit URL
			//Đặt URL của trang đăng nhập.
			.loginPage("/login")//
			//Đặt URL mặc định để chuyển hướng sau khi đăng nhập thành công.
			.defaultSuccessUrl("/?login_success")//
			//Cung cấp một đối tượng SuccessHandler để xử lý sau khi đăng nhập thành công. 
			//SuccessHandler là một lớp do người dùng tự triển khai để xử lý logic sau khi đăng nhập thành công.
			//.failureUrl("/login?error=true"): Đặt URL để chuyển hướng sau khi đăng nhập thất bại.
			.successHandler(new SuccessHandler()).failureUrl("/login?error=true")//
			//.usernameParameter("customerId"): Đặt tên tham số dùng để nhận giá trị tên đăng nhập từ form đăng nhập. 
			//Trong trường hợp này, tên đăng nhập được gửi qua form dùng tham số "customerId".
			.usernameParameter("customerId")//
			//.passwordParameter("password"): Đặt tên tham số dùng để nhận giá trị mật khẩu từ form đăng nhập.
			.passwordParameter("password")
			// Cấu hình cho Logout Page.
			//.and().logout().logoutUrl("/logout").logoutSuccessUrl("/"): Cấu hình cho trang đăng xuất. 
			//.logoutUrl("/logout") định nghĩa URL để xử lý yêu cầu đăng xuất, 
			//.logoutSuccessUrl("/") đặt URL để chuyển hướng sau khi đăng xuất thành công về trang gốc ("/").
			.and().logout().logoutUrl("/logout").logoutSuccessUrl("/");
		
		//Bằng cách cấu hình rememberMeParameter("remember"), bạn định nghĩa tên tham số 
		//trong form đăng nhập mà người dùng sẽ sử dụng để bật 
		//hoặc tắt tính năng "Remember Me". Khi tham số này được gửi lên với giá trị "true", 
		//tính năng "Remember Me" sẽ được kích hoạt.
		//Khi tính năng "Remember Me" được bật, nếu người dùng chọn tuỳ chọn "Remember Me" khi đăng nhập, 
		//thông tin xác thực của họ sẽ được lưu trữ (thường là trong cookie) 
		//để họ có thể được đăng nhập tự động trong các lần truy cập sau mà không cần nhập lại thông tin đăng nhập.
		http.rememberMe()
		.rememberMeParameter("remember"); // [remember-me]
		
		//oauth2- đăng nhập từ mxh
//		http.oauth2Login()
//			.loginPage("/login")
//			.defaultSuccessUrl("/", true)
//			.failureUrl("/login")
//			.authorizationEndpoint()
//				.baseUri("/oauth2/authorization");
	}

}
