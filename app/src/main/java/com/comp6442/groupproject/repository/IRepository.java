package com.comp6442.groupproject.repository;

import java.util.Optional;

public interface IRepository<T>{
  String add(T data);
  Optional<T> getById(String id);
}