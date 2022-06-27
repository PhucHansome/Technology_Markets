package service;

import java.util.List;

public interface IGeneralService<T> {

    List<T> findAll();

    boolean create(T t);

    boolean update(T t);

    boolean remove(long id);
}