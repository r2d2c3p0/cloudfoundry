package org.r2d2c3p0.pivotal.cloudcache;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class MainController {

    @RequestMapping("/")
    public String team(Model model, HttpServletRequest request) {

        String name = (String) request.getSession().getAttribute("name");
        if (name == null) {
            return "login";
        } else {
            model.addAttribute("name", name);
            return "team";
        }
    }

    @RequestMapping(value = "/team", method = RequestMethod.POST)
    public String whichteam(@RequestParam() String name, Model model,
                            HttpServletRequest request) {

        request.getSession().setAttribute("name", name);
        model.addAttribute("name", name);
        return "team";

    }

    @RequestMapping("/kill")
    public void kill() {
        System.exit(1);
    }

}