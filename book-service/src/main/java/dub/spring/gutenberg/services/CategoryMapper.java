package dub.spring.gutenberg.services;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import dub.spring.gutenberg.api.book.Category;
import dub.spring.gutenberg.repository.CategoryEntity;

import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

	  @Mappings({
	        //@Mapping(target = "rate", source="entity.rating"),
	        //§@Mapping(target = "serviceAddress", ignore = true)
	    })
	    Category entityToApi(CategoryEntity entity);

	    @Mappings({
	        //@Mapping(target = "rating", source="api.rate"),
	        @Mapping(target = "id", ignore = true),
	        //@Mapping(target = "version", ignore = true)
	    })
	    CategoryEntity apiToEntity(Category api);

	    //List<Recommendation> entityListToApiList(List<RecommendationEntity> entity);
	    //List<RecommendationEntity> apiListToEntityList(List<Recommendation> api);
	
}
