package com.sngular.captio.services;

import java.util.List;
import java.util.Optional;

/**
 * Generic interface to development services
 *
 * @param <T> Original Entity
 * @param <DTO> DTO Reference Class
 * @param <ID> Entity ID type
 */
public interface IGenericService<T, DTO, ID> {

    /**
     * To save an item on DB
     * @param entity Original Entity
     * @return T Entity
     */
    T save(T entity);

    /**
     * To update an item from DB
     * @param entity Original entity
     * @return T Entity
     */
    T update(T entity);

    /**
     * To get one item from DB
     * @param id
     * @return
     */
    Optional<DTO> findOneById(ID id);

    /**
     * To find all items from DB
     * @param id
     * @return
     */
    List<DTO> findAllById(ID id);

    /**
     * To delete an item from DB
     */
    void deleteAll();
}
