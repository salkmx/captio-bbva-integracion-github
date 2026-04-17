package com.sngular.captio.reader;

import java.util.Iterator;
import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import com.sngular.captio.dto.UserPermissionDTO;
import com.sngular.captio.services.ReporteUsuarioPermisosService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ReporteUsuariosPermisosReader implements ItemReader<UserPermissionDTO> {

	private final ReporteUsuarioPermisosService usrPermissionService;
	
	private Iterator<UserPermissionDTO> iterator;

	@Override
	public UserPermissionDTO read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		if (iterator == null) {
			List<UserPermissionDTO> result = usrPermissionService.getUsersPermissions();
			iterator = result.iterator();
		}
		return iterator.hasNext() ? iterator.next() : null;

	}

}
