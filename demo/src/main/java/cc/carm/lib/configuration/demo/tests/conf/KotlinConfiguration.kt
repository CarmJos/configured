package cc.carm.lib.configuration.demo.tests.conf

import cc.carm.lib.configuration.Configuration
import cc.carm.lib.configuration.annotation.ConfigPath
import cc.carm.lib.configuration.annotation.ConfigVersion
import cc.carm.lib.configuration.kotlin.value.*
import java.util.*

@ConfigPath(root = true)
object KotlinConfiguration : Configuration {
    @ConfigVersion(1)
    val VERSION = valueFrom(Double::class) {
        defaults(1.0)
    }

    val USER_LIST = listFrom(String::class) {
        defaults("Carm Jos")
    }

    val NICKNAME = mapFrom(String::class, ::mutableMapOf) {
        defaultMap(mapOf("Carm Jos" to "Carm"))
        parse { v -> v }
        serialize { v -> v }
    }

    val LINKED_MAP = linkedMapFrom(String::class) {
        parse { value ->
            value
        }
        serialize { v -> v }
        defaults("key", "value")
    }
}