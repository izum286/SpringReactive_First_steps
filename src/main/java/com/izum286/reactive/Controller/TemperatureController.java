package com.izum286.reactive.Controller;

import com.apple.eawt.Application;
import com.izum286.reactive.Model.Temperature;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@RestController
public class TemperatureController {
    private final Set<SseEmitter> clients = new CopyOnWriteArraySet<>();

    @GetMapping("temperature-stream")
    public SseEmitter events (HttpServletRequest request){
        SseEmitter emitter = new SseEmitter();
        clients.add(emitter);
        emitter.onTimeout(()->clients.remove(emitter));
        emitter.onCompletion(() -> clients.remove(emitter));
        return emitter;
    }

    @Async
    @EventListener
    public void handleMessage(Temperature temperature){
        List<SseEmitter> deadEmitters = new ArrayList<>();
        clients.forEach(emitter->{
            try {
                emitter.send(temperature, MediaType.APPLICATION_JSON);
            } catch (Exception e) {
                deadEmitters.add(emitter);
            }
        });
        clients.removeAll(deadEmitters);
    }
}