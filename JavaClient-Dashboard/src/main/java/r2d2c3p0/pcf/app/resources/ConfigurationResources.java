package r2d2c3p0.pcf.app.resources;

import io.pivotal.labs.cfenv.CloudFoundryEnvironment;
import io.pivotal.labs.cfenv.CloudFoundryEnvironmentException;
import io.pivotal.labs.cfenv.CloudFoundryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/docker")
public class ConfigurationResources {

    @GetMapping("/hello")
    public String hello() {
        return "Hello docker container";
    }

    @GetMapping("/services")
    public String vcap() {
        CloudFoundryEnvironment environment;
        try {
            environment = new CloudFoundryEnvironment(System::getenv);
            String serviceDetails = null;

            for (String serviceName : environment.getServiceNames()) {
                CloudFoundryService service = environment.getService(serviceName);
                serviceDetails = "[" + service.getName() + "] label = " + service.getLabel();
            }
            return serviceDetails;
        } catch (CloudFoundryEnvironmentException e) {
            return e.toString();
        }
    }

}