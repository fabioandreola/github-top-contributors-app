package com.fabioandreola.github.topcontributors.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ResourceLoader
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfiguration {

    @Bean
    fun reactiveOauthRedisTemplate(reactiveRedisConnectionFactory: ReactiveRedisConnectionFactory,
                                   resourceLoader: ResourceLoader): ReactiveRedisTemplate<String, Any> {
        val keySerializer: RedisSerializer<String> = StringRedisSerializer()
        val defaultSerializer = JdkSerializationRedisSerializer(resourceLoader.classLoader)
        val serializationContext = RedisSerializationContext
                .newSerializationContext<String, Any>(defaultSerializer).key(keySerializer).hashKey(keySerializer)
                .build()
        return ReactiveRedisTemplate(reactiveRedisConnectionFactory, serializationContext)
    }
}
