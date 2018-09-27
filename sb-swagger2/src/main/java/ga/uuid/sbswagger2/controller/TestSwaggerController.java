package ga.uuid.sbswagger2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * <b>Description:</b><br> 
 * @Api 用在请求的类上表示类的说明，访问 http://host/swagger-ui.html 查看效果
 * @author abeholder
 * @version 1.0
 * <b>ProjectName:</b> sb-swagger2
 * <br><b>PackageName:</b> ga.uuid.sbswagger2.controller
 * <br><b>ClassName:</b> TestSwaggerController
 * <br><b>Date:</b> Sep 27, 2018 7:12:20 PM
 */
@Api(value = "/test", tags = "测试接口模块")
@RestController
@RequestMapping("/test")
public class TestSwaggerController {
	
	@RequestMapping
	public String index() {
		return "index default.";
	}
	/**
	 * 
	 * <b>Description:</b><br> 
	 * 很麻烦呀。
	 * @return
	 * @Note
	 * <b>Author:</b> abeholder
	 */
	@ApiOperation(value = "展示首页信息 value", notes = "展示首页信息 notes")
	@GetMapping("/show")
	public Object show(@RequestParam(value = "name", defaultValue = "world") String name) {
		return String.format("hello %s !", name);
	}
	
	@RequestMapping("/type")
	public String type() {
		return "type";
	}
	
	@ApiOperation(value = "id value ya~", notes = "id notes wa!")
	@GetMapping("/{id:\\d+}")
	public String id(@PathVariable Integer id) {
		return "id: " + id;
	}
}
