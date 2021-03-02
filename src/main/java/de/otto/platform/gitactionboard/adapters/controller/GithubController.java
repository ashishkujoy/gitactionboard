package de.otto.platform.gitactionboard.adapters.controller;

import de.otto.platform.gitactionboard.adapters.service.cruisecontrol.CruiseControlService;
import de.otto.platform.gitactionboard.adapters.service.cruisecontrol.Project;
import de.otto.platform.gitactionboard.domain.JobDetails;
import de.otto.platform.gitactionboard.domain.service.PipelineService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1")
public class GithubController {
  private final PipelineService pipelineService;
  private final CruiseControlService cruiseControlService;

  @Cacheable(cacheNames = "cctrayXml", sync = true)
  @GetMapping(value = "/cctray.xml", produces = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity<String> getCctrayXml() {
    return createResponseEntityBodyBuilder().body(cruiseControlService.convertToXml(getJobs()));
  }

  private ResponseEntity.BodyBuilder createResponseEntityBodyBuilder() {
    final HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.setAccessControlAllowOrigin("*");

    return ResponseEntity.ok().headers(responseHeaders);
  }

  @Cacheable(cacheNames = "cctray", sync = true)
  @GetMapping(value = "/cctray", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<Project>> getCctray() {
    return createResponseEntityBodyBuilder().body(cruiseControlService.convertToJson(getJobs()));
  }

  private List<JobDetails> getJobs() {
    return pipelineService.fetchJobs();
  }
}
