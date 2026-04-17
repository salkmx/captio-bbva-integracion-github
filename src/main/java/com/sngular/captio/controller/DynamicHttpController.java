package com.sngular.captio.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sngular.captio.dto.DynamicRecordRequestDTO;
import com.sngular.captio.dto.DynamicRecordResponseDTO;
import com.sngular.captio.services.DynamicHttpService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/dynamic")
public class DynamicHttpController {

	private final DynamicHttpService service;

	@PostMapping("/execute")
	public ResponseEntity<DynamicRecordResponseDTO> execute(@RequestBody DynamicRecordRequestDTO req) {
		DynamicRecordResponseDTO resp = service.execute(req);
		return ResponseEntity.status(resp.status()).body(resp);
	}

}
