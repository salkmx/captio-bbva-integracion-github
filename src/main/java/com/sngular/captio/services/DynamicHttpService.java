package com.sngular.captio.services;

import com.sngular.captio.dto.DynamicRecordRequestDTO;
import com.sngular.captio.dto.DynamicRecordResponseDTO;

public interface DynamicHttpService {

	DynamicRecordResponseDTO execute(DynamicRecordRequestDTO req);

}
