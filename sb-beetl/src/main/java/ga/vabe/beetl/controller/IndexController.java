package ga.vabe.beetl.controller;

import lombok.extern.java.Log;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import javax.annotation.Resource;

@Controller
@RequestMapping
@Log
public class IndexController {

    @Resource
    private WebRequest request;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("name", request.getSessionId());
        return "index.html";
    }

}
