package com.java.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.java.entity.Customer;
import com.java.repository.CustomerRepository;

@Service
public class PavShopService implements UserDetailsService {

	@Autowired
	CustomerRepository customerRepository;

	//BCryptPasswordEncoder là một lớp trong Spring Security Framework, 
	//được sử dụng để mã hóa mật khẩu (password) theo thuật toán BCrypt.
	@Autowired
	BCryptPasswordEncoder passwordEncoder;

	//throws UsernameNotFoundException  được sử dụng để khai báo rằng phương thức loadUserByUsername 
	//có khả năng ném ra một ngoại lệ loại UsernameNotFoundException
	//có một tình huống xảy ra mà không tìm thấy tên người dùng tương ứng trong hệ thống
	//thông báo lỗi cho người dùng biết về sự cố này và xử lý nó theo cách thích hợp
	//Optional là một lớp trong Java cung cấp các phương thức để xử lý các giá trị có thể là null một cách an toàn,
	//tránh gây ra lỗi NullPointerException.
	//Phương thức ofNullable là một phương thức tĩnh (static) trong lớp Optional của Java 
	//và có tác dụng tạo một đối tượng Optional từ một giá trị có thể là null.
	
	@Override
	public UserDetails loadUserByUsername(String customerId) throws UsernameNotFoundException {
		Optional<Customer> customer = Optional.ofNullable(customerRepository.findCustomersLogin(customerId));
		//Sử dụng từ khóa final: Bằng cách sử dụng từ khóa final, 
		//biến customerLogin được khai báo là một biến không thể thay đổi sau khi gán giá trị ban đầu. 
		//Điều này có nghĩa là sau khi gán đối tượng Customer cho biến `customerLogin, 
		//không thể gán giá trị khác cho biến này.
		final Customer customerLogin = new Customer();
		//đều sao chép các giá trị tương ứng từ đối tượng Customer được truy xuất từ Optional 
		//và gán cho các thuộc tính tương ứng của đối tượng customerLogin.
		customerLogin.setEnabled(customer.get().getEnabled());
		customerLogin.setCustomerId(customer.get().getCustomerId());
		customerLogin.setEmail(customer.get().getEmail());
		customerLogin.setPassword(customer.get().getPassword());
		customerLogin.setFullname(customer.get().getFullname());
		customerLogin.setRoleId(customer.get().getRoleId());
		return customerLogin;
	}

//    public void loginFormOAuth2(OAuth2AuthenticationToken oauth2) {
//		String email = oauth2.getPrincipal().getAttribute("email");
//		String password = Long.toHexString(System.currentTimeMillis());
//		
//		UserDetails userDetails = User.withUsername(email)
//				.password(passwordEncoder.encode(password)).roles("ROLE_USER").build();
//		
//		Authentication auth = new UsernamePasswordAuthenticationToken(userDetails,null, userDetails.getAuthorities());
//		SecurityContextHolder.getContext().setAuthentication(auth);
//	}
	
}
