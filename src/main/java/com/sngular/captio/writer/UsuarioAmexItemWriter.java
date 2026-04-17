package com.sngular.captio.writer;

import org.mapstruct.factory.Mappers;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.sngular.captio.dto.UsuarioDTO;
import com.sngular.captio.mapper.UsuarioAmexMapper;
import com.sngular.captio.model.UsuarioAmex;
import com.sngular.captio.repository.UsuarioAmexRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class UsuarioAmexItemWriter implements ItemWriter<UsuarioDTO> {

	private final UsuarioAmexRepository usuarioAmexRepository;

	@Override
	public void write(Chunk<? extends UsuarioDTO> chunk) throws Exception {

		UsuarioAmexMapper usuarioMapper = Mappers.getMapper(UsuarioAmexMapper.class);

		for (UsuarioDTO usuario : chunk.getItems()) {
			UsuarioAmex usuarioEntity = usuarioMapper.toEntity(usuario);

			usuarioAmexRepository.save(usuarioEntity);

		}
	}

}
