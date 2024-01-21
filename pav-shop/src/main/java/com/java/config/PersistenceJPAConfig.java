package com.java.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
//@EnableTransactionManagement thông báo cho Spring rằng bạn muốn sử dụng quản lý giao dịch. 
//Điều này cho phép Spring tạo ra các proxy giao dịch xung quanh các phương thức được đánh dấu với @Transactional.
// Khi một phương thức được đánh dấu với @Transactional được gọi, 
//Spring sẽ quản lý việc bắt đầu, kết thúc và rollback giao dịch theo cơ chế được cấu hình. 
//Nếu một ngoại lệ xảy ra trong phạm vi giao dịch, Spring sẽ thực hiện rollback để đảm bảo tính toàn vẹn của dữ liệu.
//@EnableTransactionManagement hỗ trợ cho nhiều loại giao dịch, bao gồm giao dịch JDBC, JPA, Hibernate, JMS 
//và các giao dịch tùy chỉnh khác. Bạn có thể tùy chỉnh cấu hình giao dịch cho ứng dụng của bạn 
//bằng cách sử dụng các annotation và các tùy chọn cấu hình khác.

@EnableTransactionManagement
public class PersistenceJPAConfig {

	//@Bean là một annotation được sử dụng để đánh dấu một phương thức trong lớp cấu hình (configuration class) 
	//như là một bean được quản lý bởi Spring Container. Khi một phương thức được chú thích bằng @Bean, 
	//Spring sẽ quản lý và cung cấp bean được trả về từ phương thức đó khi được yêu cầu.
	@Bean
	//LocalContainerEntityManagerFactoryBean là một lớp trong Spring Framework được sử dụng để cấu hình 
	//và tạo đối tượng EntityManagerFactory trong môi trường container (local) cho việc quản lý JPA (Java Persistence API) 
	//trong ứng dụng của bạn.
	//EntityManagerFactory là một interface trong JPA, đại diện cho một trình quản lý thực thể (entity manager factory). 
	//Nó là một cơ sở để tạo và quản lý các EntityManager, được sử dụng để tương tác với cơ sở dữ liệu trong ứng dụng JPA.
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		//em.setDataSource(dataSource());: Thiết lập đối tượng DataSource cho EntityManagerFactoryBean. 
		//DataSource là một thành phần cấu hình bên ngoài được sử dụng để thiết lập kết nối đến cơ sở dữ liệu.
		em.setDataSource(dataSource());
		//Xác định các package cần được quét để tìm kiếm các entity trong ứng dụng. 
		//Trong trường hợp này, các entity được tìm kiếm trong package "com.java.entity".
		em.setPackagesToScan(new String[] { "com.java.entity" });
		//JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();: 
		//Tạo một đối tượng HibernateJpaVendorAdapter, là một implementaion của JpaVendorAdapter, 
		//để cấu hình các thuộc tính cụ thể liên quan đến JPA và Hibernate.
		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		//em.setJpaVendorAdapter(vendorAdapter);: Thiết lập JpaVendorAdapter cho EntityManagerFactoryBean.
		//Với JpaVendorAdapter, bạn có thể thiết lập các thuộc tính như dialect (ngôn ngữ đặc thù cơ sở dữ liệu), 
		//show-sql (hiển thị SQL được tạo ra), tùy chọn kết nối cơ sở dữ liệu, và các thuộc tính cấu hình khác.
		//Một trong những implementaion phổ biến của JpaVendorAdapter là HibernateJpaVendorAdapter, 
		//được sử dụng để cấu hình và tương thích với Hibernate như nhà cung cấp JPA. 
		//HibernateJpaVendorAdapter cung cấp các thuộc tính và cấu hình phù hợp với Hibernate để làm việc với EntityManagerFactory.
		em.setJpaVendorAdapter(vendorAdapter);
		//em.setJpaProperties(additionalProperties());: Thiết lập các thuộc tính JPA khác như các thuộc tính 
		//Hibernate, JDBC, hoặc các thuộc tính cấu hình khác thông qua phương thức additionalProperties().
		em.setJpaProperties(additionalProperties());

		return em;
	}

	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost:3306/pav_shop");
		dataSource.setUsername("root");
		dataSource.setPassword("123456");
		return dataSource;
	}

	@Bean
	//PlatformTransactionManager là một giao diện trong Spring Framework, được sử dụng để quản lý giao dịch trong ứng dụng. 
	//Nó đóng vai trò làm cầu nối giữa ứng dụng và cơ sở dữ liệu, cho phép bạn thực hiện các giao dịch đồng nhất 
	//và bảo đảm tính toàn vẹn của dữ liệu.
	// Nếu xảy ra một ngoại lệ trong quá trình thực hiện giao dịch, PlatformTransactionManager 
	//sẽ tự động thực hiện rollback để đảm bảo tính toàn vẹn của dữ liệu. 
	//Điều này đảm bảo rằng dữ liệu sẽ không bị sai lệch trong trường hợp xảy ra sự cố trong quá trình thực hiện giao dịch.
	public PlatformTransactionManager transactionManager() {
		//Tạo một đối tượng JpaTransactionManager, một implementaion của PlatformTransactionManager,
		//để quản lý giao dịch JPA trong ứng dụng.
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		// Bằng cách gọi entityManagerFactory().getObject(), chúng ta lấy đối tượng EntityManagerFactory 
		//đã được cấu hình và sẵn sàng để sử dụng trong quản lý giao dịch.
		transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());

		return transactionManager;
	}

	@Bean
	// khi bạn sử dụng JPA để tương tác với cơ sở dữ liệu trong một ứng dụng web, các hoạt động như truy vấn dữ liệu, 
	//lưu trữ, cập nhật và xóa dữ liệu có thể gặp phải các ngoại lệ cơ sở dữ liệu như lỗi kết nối, lỗi cú pháp SQL, 
	//hoặc lỗi hợp nhất dữ liệu.
	//nó sẽ tự động áp dụng các quy tắc xử lý ngoại lệ cho các hoạt động JPA.
	// có thể chuyển đổi ngoại lệ này thành một DataAccessException, 
	//giúp bạn có thể xử lý nó một cách chính xác và cung cấp thông báo lỗi phù hợp cho người dùng hoặc ghi log.
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	Properties additionalProperties() {
		Properties properties = new Properties();
		properties.setProperty("hibernate.hbm2ddl.auto", "update");
		properties.setProperty("hibernate.show_sql", "true");
		properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
		return properties;
	}
}
