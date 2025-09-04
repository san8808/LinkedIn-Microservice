package com.codecomet.linkedInProject.postService.client;

import com.codecomet.linkedInProject.postService.dto.Person;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "connection-service", path="/connections", url= "${CONNECTION_SERVICE_URI:}")
@Component
public interface ConnectionServiceClient {

    @GetMapping("/core/{userId}/first-degree")
    List<Person> getFirstDegreeConnections(@PathVariable Long userId);
}
