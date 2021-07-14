package br.com.erivelton.pix.shared.handlers

import io.micronaut.aop.Around
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.*

@MustBeDocumented
@Retention(RUNTIME)
@Target(CLASS, TYPE, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
@Around
annotation class ErrorAroundHandler
