package org.r2d2c3p0.pivotal.cloudcache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
public class MainController {

    private final EnvironmentHelper environmentHelper;

    public MainController(EnvironmentHelper environmentHelper) {
        this.environmentHelper = environmentHelper;
    }

    @RequestMapping("/")
    public String team(Model model, HttpServletRequest request) throws Exception {

        Map<String, Object> modelMap = new HashMap<>();

        String name = (String) request.getSession().getAttribute("name");
        String instanceAddr = System.getenv("CF_INSTANCE_ADDR");
        if (instanceAddr == null) {
            instanceAddr = "localhost";
        }

        String containerAddr;
        if (System.getenv("PORT") == null) {
            containerAddr= "localhost";
        } else {
            containerAddr=request.getLocalAddr() + ":" + request.getLocalPort();
        }

        model.addAttribute("instanceAddr", instanceAddr);
        model.addAttribute("containerAddr", containerAddr);

        Map<String, ?> services = getVcapServicesMap();
        services = parseServices(services);
        modelMap.put("applicationServices", services);

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

    private Map<String, Object> parseServices(Map<String, ?> services) {
        Map<String, Object> servicesMap = Collections.synchronizedMap(new LinkedHashMap<String, Object>());
        for (Map.Entry<String, ?> entry : services.entrySet()) {
            List list = (List) entry.getValue();

            for (Object object : list) {
                Map map = (Map) object;
                //weird delimiter and UUID is to deal with multiple services of the same type
                servicesMap.put(entry.getKey() + "~~~" + UUID.randomUUID().toString(), map.get("name"));
            }
        }
        return servicesMap;

    }

    @RequestMapping("/kill")
    public void kill() {
        System.exit(1);
    }


    private Map<String, ?> getVcapServicesMap() throws Exception {
        return getEnvMap("VCAP_SERVICES");
    }

    private Map<String, ?> getEnvMap(String vcap) throws Exception {
        String vcapEnv = System.getenv(vcap);
        ObjectMapper mapper = new ObjectMapper();

        if (vcapEnv != null) {
            @SuppressWarnings("unchecked")
            Map<String, ?> vcapMap = mapper.readValue(vcapEnv, Map.class);

            return vcapMap;
        }

        return new HashMap<String, String>();
    }
}