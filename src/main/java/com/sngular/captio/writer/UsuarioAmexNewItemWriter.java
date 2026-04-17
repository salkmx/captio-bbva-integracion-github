package com.sngular.captio.writer;

import org.mapstruct.factory.Mappers;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.sngular.captio.dto.UsuarioDTO;
import com.sngular.captio.mapper.UsuarioAmexNewMapper;
import com.sngular.captio.model.UsuarioAmexNew;
import com.sngular.captio.repository.UsuarioAmexNewRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class UsuarioAmexNewItemWriter implements ItemWriter<UsuarioDTO> {

	private final UsuarioAmexNewRepository usuarioAmexNewRepository;

	@Override
	public void write(Chunk<? extends UsuarioDTO> chunk) throws Exception {

		UsuarioAmexNewMapper usuarioMapper = Mappers.getMapper(UsuarioAmexNewMapper.class);

		for (UsuarioDTO usuario : chunk.getItems()) {
			UsuarioAmexNew usuarioAmexNew = usuarioMapper.toEntity(usuario);

			usuarioAmexNewRepository.save(usuarioAmexNew);

		}
	}

}
