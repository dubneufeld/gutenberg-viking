package dub.spring.gutenberg.services;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import dub.spring.gutenberg.api.order.Order;
import dub.spring.gutenberg.repository.OrderEntity;

@Mapper(componentModel = "spring")
public interface OrderMapper {
  
	@Mappings({
	})
	Order entityToApi(OrderEntity entity);

	@Mappings({
		@Mapping(target = "id", ignore = true),
	})
	OrderEntity apiToEntity(Order api);

}
