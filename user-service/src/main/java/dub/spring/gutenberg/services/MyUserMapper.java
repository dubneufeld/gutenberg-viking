package dub.spring.gutenberg.services;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import dub.spring.gutenberg.api.user.MyUser;
import dub.spring.gutenberg.repository.MyUserEntity;


@Mapper(componentModel = "spring")
public interface MyUserMapper {
  
	@Mappings({
	      
	})
	MyUser entityToApi(MyUserEntity entity);

	@Mappings({
			@Mapping(target = "id", ignore = true),       
	})
	MyUserEntity apiToEntity(MyUser api);
}
