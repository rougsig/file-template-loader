package com.github.rougsig.ftl

@Repeatable
@Target(AnnotationTarget.FILE)
annotation class Include(val pathName: String)
