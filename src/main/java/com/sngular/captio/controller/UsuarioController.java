package com.sngular.captio.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sngular.captio.services.UsuarioService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/captio")
@AllArgsConstructor
public class UsuarioController {

	private final UsuarioService usuarioService;

	@PostMapping("/usuarios")
	public ResponseEntity<String> crearProyecto() {

		usuarioService.eliminarUsuarios();

		return ResponseEntity.ok("Proyecto creado");
	}

}
