package com.github.rougsig.filetemplateloader.generator

import java.util.*

typealias Props = LinkedHashMap<String, String>

fun Props.getProperty(key: String): String? = get(key)
fun Props.requireProperty(key: String): String = get(key) ?: throw IllegalStateException("prop required: $key")
fun Props.setProperty(key: String, value: String): String? = put(key, value)
