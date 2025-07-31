package com.edu.auth.domain

interface PatternValidator {
    fun matches(value: String): Boolean
}