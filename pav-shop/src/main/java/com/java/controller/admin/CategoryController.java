package com.java.controller.admin;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.java.controller.CommonController;
import com.java.entity.Category;
import com.java.repository.CategoryRepository;

@Controller
public class CategoryController extends CommonController{
	
	@Autowired
	CategoryRepository categoryRepository;
	
	// show list category - table list
		@ModelAttribute("categories")
		public List<Category> showCategory(Model model) {
			List<Category> categories = categoryRepository.findAll();
			model.addAttribute("categories", categories);

			return categories;
		}

		@GetMapping(value = "/admin/categories")
		public String categories(Model model, Principal principal) {
			Category category = new Category();
			model.addAttribute("category", category);
			
//			Customer customer = customersRepository.FindByEmail(principal.getName()).get();
//			model.addAttribute("customer", customer);

			return "admin/categories";
		}

		// add category
		//@Validated được sử dụng để xác thực đảm bảo tính hợp lệ của dữ liệu đầu vào từ giao diện 
				//Các lỗi xác thực (validation errors) sẽ được ghi nhận và được lưu trữ trong BindingResult
				//@ModelAttribute dùng để truyền dữ liệu giữa giao diện người dùng và phương thức xử lý yêu cầu
				//categoryRepository.save(category) nếu đối tượng category chưa tồn tại trong cơ sở dữ liệu, 
				//phương thức save() sẽ thực hiện thao tác lưu trữ và thêm đối tượng category vào cơ sở dữ liệu. 
				//Nếu đối tượng category đã tồn tại trong cơ sở dữ liệu (có cùng id), phương thức save() sẽ thực hiện thao tác cập nhật 
				//và cập nhật thông tin của đối tượng category trong cơ sở dữ liệu
		@PostMapping(value = "/addCategory")
		public String addCategory(@Validated @ModelAttribute("category") Category category, ModelMap model,
				BindingResult bindingResult) {

			if (bindingResult.hasErrors()) {
				model.addAttribute("error", "failure");

				return "admin/categories";
			}

			categoryRepository.save(category);
			model.addAttribute("message", "successful!");

			return "redirect:/admin/categories";
		}
		
		// get Edit category
		//@PathVariable sử dụng để trích xuất giá trị id gán vào id trong Integer
				//ModelMap được sử dụng để truyền dữ liệu với key là category, truyền dữ liệu từ phương
				// thức xử lý yêu cầu đến giao diện người dùng.
				//orElse được sử dụng để trả về category dựa vào id nếu không tìm thấy trả về giá trị null
		@GetMapping(value = "/editCategory/{id}")
		public String editCategory(@PathVariable("id") Integer id, ModelMap model) {
			Category category = categoryRepository.findById(id).orElse(null);
			
			model.addAttribute("category", category);

			return "admin/editCategory";
		}

		// delete category
		@GetMapping("/delete/{id}")
		public String delCategory(@PathVariable("id") Integer id, Model model) {
			categoryRepository.deleteById(id);
			model.addAttribute("message", "Delete successful!");
			
			return "redirect:/admin/categories";
		}
}
