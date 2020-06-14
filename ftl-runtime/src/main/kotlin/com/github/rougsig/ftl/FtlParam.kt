package com.github.rougsig.ftl

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class FtlParam(val name: String = "", val desc: String = "")
