package vn.LeThanhTuan.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletResponse;
import vn.LeThanhTuan.entity.Order;
import vn.LeThanhTuan.entity.OrderDetail;
import vn.LeThanhTuan.service.OrderService;
import vn.LeThanhTuan.util.AppConstrant;
import vn.LeThanhTuan.util.ExcelExportUtil;

@Controller
@RequestMapping("/vegetfood/admin/order")
public class OrderController {

	@Autowired
	OrderService orderService;

	@GetMapping
	public String getOrder(Model model, @RequestParam(value = "p", defaultValue = "1") int pageIndex,
							@RequestParam(value = "keyword", defaultValue = "") String keyword,
							@RequestParam(value = "s", defaultValue = "5") int pageSize) {
		Page<Order> page = orderService.getAllOrder(pageIndex, keyword, pageSize);
		List<Order> orders = page.getContent();
		int totalPages = page.getTotalPages();

		model.addAttribute("orders", orders);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("currentPage", pageIndex);
		model.addAttribute("keyword", keyword);
		model.addAttribute("pageSize", pageSize);

		return "orderManage";
	}

	@PostMapping
	public String updateOrder(RedirectAttributes attributes, @RequestParam("status") String status,
			@RequestParam(value = "p", defaultValue = "1") int pageIndex, @RequestParam("oid") Integer id,
			@RequestParam(value = "s", defaultValue = "5") int pageSize) {
		Order order = orderService.changeStatusOrder(id, status);
		if (order == null) {
			attributes.addFlashAttribute("error", "Không thể thay đổi trạng thái đơn hàng!");
		} else {
			attributes.addFlashAttribute("success", "Thay đổi trạng thái đơn hàng thành công!");
		}

		return "redirect:/vegetfood/admin/order?p=" + pageIndex + "&s=" + pageSize;
	}

	@GetMapping("/detail")
	public String getOrderDetail(Model model, @RequestParam("oid") Integer id,
								@RequestParam(value = "p", defaultValue = "1") int pageIndex,
								@RequestParam(value = "s", defaultValue = "5") int pageSize) {
		List<OrderDetail> orderDetails = orderService.getOrderDetailByOrderId(id);

		model.addAttribute("orders", orderDetails);
		model.addAttribute("currentPage", pageIndex);
		model.addAttribute("pageSize", pageSize);
		
		return "order-detail";
	}

	@GetMapping("/excel")
	public String exportToExcel(HttpServletResponse response,
			@RequestParam(value = "keyword", defaultValue = "") String keyword) throws IllegalArgumentException, IllegalAccessException, IOException {

		List<Order> orders = orderService.getListOrder(keyword);
		
		ExcelExportUtil<Order> ex = new ExcelExportUtil<>(orders);
		String baseName = AppConstrant.EXPORT_ORDER;

		ex.export(response, baseName);
		
		return "redirect:/vegetfood/admin/order?keyword=" + keyword;
	}
}
