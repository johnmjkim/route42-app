package com.comp6442.route42.data.repository;

import com.comp6442.route42.data.model.Model;

import java.util.List;

public abstract class Repository<T extends Model> {
  protected Class<T> classType;

  abstract void createOne(T item);

  abstract Object getOne(String id);

  // abstract void setOne(T item);

  abstract void createMany(List<T> items);

  // abstract Object getMany(String id);

  abstract void setMany(List<T> items);
}
