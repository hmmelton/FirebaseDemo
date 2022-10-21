package com.hmmelton.firebasedemo.data.repository

/**
 * Generic interface for repositories
 */
interface Repository<T> {
    suspend fun create(id: String, item: T): Boolean

    suspend fun get(id: String): T?

    suspend fun update(id: String, item: T): Boolean

    suspend fun delete(id: String): Boolean
}