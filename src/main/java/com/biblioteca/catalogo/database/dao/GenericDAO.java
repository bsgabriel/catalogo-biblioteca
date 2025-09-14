package com.biblioteca.catalogo.database.dao;

import com.biblioteca.catalogo.config.DatabaseManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

public abstract class GenericDAO<T, ID> {

    protected final Class<T> entityClass;
    protected final DatabaseManager databaseManager;

    public GenericDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.databaseManager = DatabaseManager.getInstance();
    }

    public T save(T entity) {
        EntityManager em = databaseManager.getEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            T savedEntity = em.merge(entity);
            transaction.commit();
            return savedEntity;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            // TODO: criar exception personalizada
            throw new RuntimeException("Erro ao salvar entidade", e);
        } finally {
            em.close();
        }
    }

    public Optional<T> findById(ID id) {
        EntityManager em = databaseManager.getEntityManager();
        try {
            T entity = em.find(entityClass, id);
            return Optional.ofNullable(entity);
        } finally {
            em.close();
        }
    }

    public List<T> findAll() {
        EntityManager em = databaseManager.getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> query = cb.createQuery(entityClass);
            Root<T> root = query.from(entityClass);
            query.select(root);

            return em.createQuery(query).getResultList();
        } finally {
            em.close();
        }
    }

    public void delete(T entity) {
        EntityManager em = databaseManager.getEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            T managedEntity = em.merge(entity);
            em.remove(managedEntity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Erro ao deletar entidade", e);
        } finally {
            em.close();
        }
    }

    public void deleteById(ID id) {
        findById(id).ifPresent(this::delete);
    }

    protected List<T> executeQuery(String jpql, Object... parameters) {
        EntityManager em = databaseManager.getEntityManager();
        try {
            TypedQuery<T> query = em.createQuery(jpql, entityClass);
            for (int i = 0; i < parameters.length; i++) {
                query.setParameter(i + 1, parameters[i]);
            }
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    protected Optional<T> executeSingleQuery(String jpql, Object... parameters) {
        List<T> results = executeQuery(jpql, parameters);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
}
