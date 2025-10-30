package beeve.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.core.mapping.MongoMappingContext

@Configuration
@EnableMongoAuditing
class MongoConfig {

    @Bean
    fun mappingMongoConverter(
        databaseFactory: MongoDatabaseFactory,
        mappingContext: MongoMappingContext,
    ): MappingMongoConverter {
        // 1) 기본 converter 직접 만들기
        val dbRefResolver = DefaultDbRefResolver(databaseFactory)
        val converter = MappingMongoConverter(dbRefResolver, mappingContext)

        // 2) _class 필드 없애기
        converter.setTypeMapper(DefaultMongoTypeMapper(null))

        return converter
    }
}