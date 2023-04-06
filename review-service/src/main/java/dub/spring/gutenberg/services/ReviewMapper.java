package dub.spring.gutenberg.services;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import dub.spring.gutenberg.api.review.Review;
import dub.spring.gutenberg.repository.ReviewEntity;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

	@Mappings({
	        
	})
	Review entityToApi(ReviewEntity entity);

	@Mappings({
	       
		@Mapping(target = "id", ignore = true),
	        
	})
	ReviewEntity apiToEntity(Review api);
   
}
