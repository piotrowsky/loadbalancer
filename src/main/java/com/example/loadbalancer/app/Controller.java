package com.example.loadbalancer.app;

import com.example.loadbalancer.domain.LoadBalancer;
import com.example.loadbalancer.domain.RequestHandlingException;
import com.example.loadbalancer.domain.model.Instance;
import com.example.loadbalancer.domain.model.InstanceId;
import com.example.loadbalancer.domain.model.Request;
import com.example.loadbalancer.domain.model.Response;
import com.example.loadbalancer.domain.strategies.ConfigurableInstanceSelectionStrategy;
import com.example.loadbalancer.domain.strategies.StrategyName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
public class Controller {

    private final LoadBalancer loadBalancer;
    private final ConfigurableInstanceSelectionStrategy configurableInstanceSelectionStrategy;

    @Autowired
    public Controller(LoadBalancer loadBalancer, ConfigurableInstanceSelectionStrategy configurableInstanceSelectionStrategy) {
        this.loadBalancer = loadBalancer;
        this.configurableInstanceSelectionStrategy = configurableInstanceSelectionStrategy;
    }

    @RequestMapping(
            value = "/handle",
            method = {GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE})
    @ResponseBody
    public Response handle(@RequestBody String request) throws RequestHandlingException {
        return loadBalancer.handle(new Request(request));
    }

    @PostMapping("/register")
    @ResponseBody
    public void register(@RequestParam("id") String instanceId) throws UnknownHostException {
        final var theInstanceId = new InstanceId(instanceId);
        final var instance = new Instance(
                theInstanceId,
                InetAddress.getLocalHost(),
                r -> new Response(theInstanceId, r.value())
        );
        loadBalancer.register(instance);
    }

    @PostMapping("/unregister")
    @ResponseBody
    public void unregister(@RequestParam("id") String instanceId) {
        loadBalancer.unregister(new InstanceId(instanceId));
    }

    @PostMapping("/strategy")
    @ResponseBody
    public void strategy(@RequestParam("name") String strategyName) {
        configurableInstanceSelectionStrategy.use(StrategyName.valueOf(strategyName));
    }
}
