package com.client.calorieserver.repository;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;


import java.io.Serializable;

@NoRepositoryBean
public interface CustomRepository<T, ID>
        extends PagingAndSortingRepository<T, ID> {
    void flushAndRefresh(T t);
}
