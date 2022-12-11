/*
 * eiam-support - Employee Identity and Access Management Program
 * Copyright © 2020-2022 TopIAM (support@topiam.cn)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cn.topiam.employee.support.repository.util;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAUpdateClause;

import lombok.NonNull;

/**
 * 封装了QueryDSL系列对象的工具类
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2021/12/29 22:13
 */
public class QuerydslUtils {

    public static <T> JPAQuery<T> newQuery(@NonNull EntityManager entityManager) {
        return new JPAQuery<T>(entityManager);
    }

    public static <T> JPAQuery<T> newQuery(@NonNull EntityManager entityManager,
                                           @NonNull EntityPathBase<?> entityPath) {
        return newQuery(entityManager, entityPath, null);
    }

    public static <T> JPAQuery<T> newQuery(@NonNull EntityManager entityManager,
                                           @NonNull EntityPathBase<?> entityPath,
                                           Predicate predicate) {
        return newQuery(entityManager, entityPath, predicate, new Expression<?>[] {});
    }

    public static <T> JPAQuery<T> newQuery(@NonNull EntityManager entityManager,
                                           @NonNull EntityPathBase<?> entityPath,
                                           Predicate predicate, Expression<?>... selects) {
        return newQuery(entityManager, entityPath, predicate, null, selects);
    }

    public static <T> JPAQuery<T> newQuery(@NonNull EntityManager entityManager,
                                           @NonNull EntityPathBase<?> entityPath,
                                           Predicate predicate, Sort sort,
                                           Expression<?>... selects) {
        JPAQuery<T> query = new JPAQuery<T>(entityManager).from(entityPath);
        if (selects != null && selects.length > 0) {
            query.select(selects);
        }
        if (predicate != null) {
            query.where(predicate);
        }
        if (sort != null) {
            loadSort(query, entityPath, sort);
        }
        return query;
    }

    public static JPADeleteClause newDelete(@NonNull EntityManager entityManager,
                                            @NonNull EntityPathBase<?> entityPath) {
        return new JPADeleteClause(entityManager, entityPath);
    }

    public static JPADeleteClause newDelete(@NonNull EntityManager entityManager,
                                            @NonNull EntityPathBase<?> entityPath,
                                            Predicate predicate) {
        JPADeleteClause delete = new JPADeleteClause(entityManager, entityPath);
        if (predicate != null) {
            delete.where(predicate);
        }
        return delete;
    }

    public static JPAUpdateClause newUpdate(@NonNull EntityManager entityManager,
                                            @NonNull EntityPathBase<?> entityPath) {
        return new JPAUpdateClause(entityManager, entityPath);
    }

    public static <S> JPAUpdateClause newUpdate(@NonNull EntityManager entityManager,
                                                @NonNull EntityPathBase<?> entityPath,
                                                @NonNull Path<S> path, S value) {
        JPAUpdateClause update = newUpdate(entityManager, entityPath);
        if (value == null) {
            update.setNull(path);
        } else {
            update.set(path, value);
        }
        return update;
    }

    public static <S> JPAUpdateClause newUpdate(@NonNull EntityManager entityManager,
                                                @NonNull EntityPathBase<?> entityPath,
                                                @NonNull Path<S> path, S value,
                                                Predicate predicate) {
        JPAUpdateClause update = newUpdate(entityManager, entityPath, path, value);
        if (predicate != null) {
            update.where(predicate);
        }
        return update;
    }

    public static <S> JPAUpdateClause newUpdate(@NonNull EntityManager entityManager,
                                                @NonNull EntityPathBase<?> entityPath,
                                                @NonNull List<? extends Path<?>> paths,
                                                @NonNull List<?> values) {
        return newUpdate(entityManager, entityPath).set(paths, values);
    }

    public static <S> JPAUpdateClause newUpdate(@NonNull EntityManager entityManager,
                                                @NonNull EntityPathBase<?> entityPath,
                                                @NonNull List<? extends Path<?>> paths,
                                                @NonNull List<?> values, Predicate predicate) {
        JPAUpdateClause update = newUpdate(entityManager, entityPath, paths, values);
        if (predicate != null) {
            update.where(predicate);
        }
        return update;
    }

    public static <S> JPAUpdateClause newUpdate(@NonNull EntityManager entityManager,
                                                @NonNull EntityPathBase<?> entityPath,
                                                @NonNull Path<S> path, Expression<S> exp,
                                                Predicate predicate) {
        JPAUpdateClause update = newUpdate(entityManager, entityPath);
        update.set(path, exp);
        if (predicate != null) {
            update.where(predicate);
        }
        return update;
    }

    public static <T> JPAQuery<T> initQuery(@NonNull EntityManager entityManager,
                                            @NonNull EntityPathBase<?> entityPath,
                                            @NonNull Pageable pageable, Predicate predicate) {
        //创建查询对象，并将基础属性赋值，如查询的内容与从哪张表中查询
        JPAQuery<T> query = newQuery(entityManager, entityPath);
        //如果输入参数predicate存在，则将查询的条件设置到查询对象中
        if (predicate != null) {
            query.where(predicate);
        }
        //将分页属性(包括：当前页码和每页读取多少条记录)设置到查询对象中
        query.offset(pageable.getOffset()).limit(pageable.getPageSize());
        PathBuilder<?> pathBuilder = new PathBuilder(entityPath.getType(),
            entityPath.getMetadata());
        for (Sort.Order o : pageable.getSort()) {
            query.orderBy(new OrderSpecifier(o.isAscending() ? Order.ASC : Order.DESC,
                pathBuilder.get(o.getProperty())));
        }
        return query;
    }

    public static <T> Page<T> doQuery(@NonNull EntityManager entityManager,
                                      @NonNull EntityPathBase<?> entityPath,
                                      @NonNull Pageable pageable) {
        return doQuery(entityManager, entityPath, pageable, null);
    }

    public static <T> Page<T> doQuery(@NonNull EntityManager entityManager,
                                      @NonNull EntityPathBase<?> entityPath,
                                      @NonNull Pageable pageable, Predicate predicate) {
        JPAQuery<T> query = initQuery(entityManager, entityPath, pageable, predicate);
        QueryResults<T> qr = query.fetchResults();
        return new PageImpl<>(qr.getResults(), pageable, qr.getTotal());
    }

    public static <T> T doQuery(@NonNull EntityManager entityManager,
                                @NonNull EntityPathBase<?> entityPath, Predicate predicate) {
        JPAQuery<T> query = newQuery(entityManager, entityPath, predicate);
        return query.fetchOne();
    }

    public static <T> List<T> doQuery(@NonNull EntityManager entityManager,
                                      @NonNull EntityPathBase<?> entityPath, Predicate predicate,
                                      Expression<?>... selects) {
        JPAQuery<T> query = newQuery(entityManager, entityPath, predicate, selects);
        return query.fetch();
    }

    public static long doDelete(@NonNull EntityManager entityManager,
                                @NonNull EntityPathBase<?> entityPath) {
        return newDelete(entityManager, entityPath).execute();
    }

    public static long doDelete(@NonNull EntityManager entityManager,
                                @NonNull EntityPathBase<?> entityPath,
                                @NonNull Predicate predicate) {
        return newDelete(entityManager, entityPath, predicate).execute();
    }

    public static <S> long doUpdate(@NonNull EntityManager entityManager,
                                    @NonNull EntityPathBase<?> entityPath, @NonNull Path<S> path,
                                    S value) {
        return newUpdate(entityManager, entityPath, path, value).execute();
    }

    public static <S> long doUpdate(@NonNull EntityManager entityManager,
                                    @NonNull EntityPathBase<?> entityPath, @NonNull Path<S> path,
                                    S value, Predicate predicate) {
        return newUpdate(entityManager, entityPath, path, value, predicate).execute();
    }

    public static <S> long doUpdate(@NonNull EntityManager entityManager,
                                    @NonNull EntityPathBase<?> entityPath,
                                    @NonNull List<? extends Path<?>> paths,
                                    @NonNull List<?> values) {
        return newUpdate(entityManager, entityPath, paths, values).execute();
    }

    public static <S> long doUpdate(@NonNull EntityManager entityManager,
                                    @NonNull EntityPathBase<?> entityPath,
                                    @NonNull List<? extends Path<?>> paths, @NonNull List<?> values,
                                    Predicate predicate) {
        return newUpdate(entityManager, entityPath, paths, values, predicate).execute();
    }

    public static <S> long doUpdate(@NonNull EntityManager entityManager,
                                    @NonNull EntityPathBase<?> entityPath, @NonNull Path<S> path,
                                    Expression<S> exp, Predicate predicate) {
        return newUpdate(entityManager, entityPath, path, exp, predicate).execute();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void loadSort(JPAQuery<?> query, EntityPathBase<?> entityPath, Sort sort) {
        PathBuilder<?> pathBuilder = new PathBuilder(entityPath.getType(),
            entityPath.getMetadata());
        for (Sort.Order o : sort) {
            query.orderBy(new OrderSpecifier(o.isAscending() ? Order.ASC : Order.DESC,
                pathBuilder.get(o.getProperty())));
        }
    }
}
