package r2d2c3p0.pcf.app.services;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.labs.cfenv.CloudFoundryEnvironmentException;
import io.pivotal.labs.cfenv.CloudFoundryService;
import io.pivotal.labs.cfenv.Environment;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CloudFoundryEnv {

    private static final String VCAP_APPLICATION = "VCAP_APPLICATION";
    private static final ObjectMapper OBJECT_MAPPER =
            new ObjectMapper()
                    .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private final Map<String, CloudFoundryService> applications;

    /**
     * Creates a new environment.
     *
     * @param environment the underlying environment from which to obtain the environment variables
     * @throws CloudFoundryEnvironmentException if any of the necessary variables are missing or malformed
     */
    public CloudFoundryEnv(Environment environment) throws CloudFoundryEnvironmentException {
        String vcapApplications = environment.lookup(VCAP_APPLICATION);

        Map<?, ?> rootNode = parse(vcapApplications);

        applications = rootNode.values().stream()
                .map(this::asCollection)
                .flatMap(Collection::stream)
                .map(this::asMap)
                .map(this::createService)
                .collect(Collectors.toMap(CloudFoundryService::getName, Function.identity()));
    }

    private Map<?, ?> parse(String json) throws CloudFoundryEnvironmentException {
        try {
            return OBJECT_MAPPER.readValue(json, Map.class);
        } catch (IOException e) {
            throw new CloudFoundryEnvironmentException("error parsing JSON: " + json, e);
        }
    }

    private CloudFoundryService createService(Map<?, ?> serviceInstanceNode) {
        String name = (String) serviceInstanceNode.get("name");
        String label = (String) serviceInstanceNode.get("label");
        String plan = (String) serviceInstanceNode.get("plan");
        Set<String> tags = asCollection(serviceInstanceNode.get("tags")).stream()
                .map(String.class::cast)
                .collect(Collectors.toSet());

        Map credentials = asMap(serviceInstanceNode.get("credentials"));
        if (credentials == null) {
            credentials = new HashMap<>();
        }

        return new CloudFoundryService(name, label, plan, tags, castKeysToString(credentials));
    }

    private Collection<?> asCollection(Object o) {
        return (Collection<?>) o;
    }

    private Map<?, ?> asMap(Object o) {
        return (Map<?, ?>) o;
    }

    /**
     * Can't use Collectors::toMap because it chokes on null values
     */
    private Map<String, Object> castKeysToString(Map<?, ?> map) {
        Map<String, Object> credentials = new HashMap<>();
        map.forEach((k, v) -> credentials.put((String) k, v));
        return credentials;
    }

    public Set<String> getApplicationNames() {
        return applications.keySet();
    }

    /**
     * Gets information about a particular service by name.
     *
     * @param applicationName the name of the service to get
     * @return information about the service with the given name
     * @throws NoSuchElementException if there is no service with the given name
     */
    public CloudFoundryService getService(String applicationName) throws NoSuchElementException {
        CloudFoundryService service = applications.get(applicationName);
        if (service == null) throw new NoSuchElementException("no such service: " + applicationName);
        return service;
    }

}