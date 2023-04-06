package dub.spring.gutenberg.services;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import dub.spring.gutenberg.api.book.Book;
import dub.spring.gutenberg.repository.BookEntity;

@Mapper(componentModel = "spring")
public interface BookMapper {

	@Mappings({
	})
	Book entityToApi(BookEntity entity);

	@Mappings({
	        @Mapping(target = "id", ignore = true),
	})
	BookEntity apiToEntity(Book api);
   
}
