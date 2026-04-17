package com.sngular.captio.writer;

import org.mapstruct.factory.Mappers;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.sngular.captio.dto.UsuarioDTO;
import com.sngular.captio.mapper.UsuarioSonarMapper;
import com.sngular.captio.model.UsuarioSonar;
import com.sngular.captio.repository.OpcionesUsuarioRepository;
import com.sngular.captio.repository.UsuarioSonarRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class UsuarioSonarItemWriter implements ItemWriter<UsuarioDTO> {

	private final UsuarioSonarRepository usuarioSonarRepository;

	private final OpcionesUsuarioRepository opcionesUsuarioRepository;

	@Override
	public void write(Chunk<? extends UsuarioDTO> chunk) throws Exception {

		UsuarioSonarMapper usuarioMapper = Mappers.getMapper(UsuarioSonarMapper.class);

		for (UsuarioDTO usuario : chunk.getItems()) {
			UsuarioSonar usuarioEntity = usuarioMapper.toEntity(usuario);

			usuarioSonarRepository.save(usuarioEntity);

			usuarioEntity.getOptions().setUsuario(usuarioEntity);

			opcionesUsuarioRepository.save(usuarioEntity.getOptions());

		}
	}

}
