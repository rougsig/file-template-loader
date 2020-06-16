package com.github.rougsig.ftl

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Param(val name: String = "", val description: String = "")
