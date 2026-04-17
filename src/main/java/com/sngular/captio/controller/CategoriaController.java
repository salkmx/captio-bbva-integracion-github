package com.sngular.captio.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sngular.captio.services.CategoriaService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/captio")
@AllArgsConstructor
public class CategoriaController {

	private final CategoriaService categoriaService;

	@PostMapping("/categorias")
	public ResponseEntity<String> crearProyecto() {
		
		categoriaService.eliminarCategorias();
		
		return ResponseEntity.ok("Proyecto creado");
	}

}
