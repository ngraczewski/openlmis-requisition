package org.openlmis.referencedata.repository;

import org.openlmis.referencedata.domain.Role;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.UUID;

public interface RoleRightsRepository extends ReferenceDataRepository<Role, UUID> {
  @Override
  @RestResource
  <S extends Role> S save(S entity);

  @Override
  @RestResource
  <S extends Role> Iterable<S> save(Iterable<S> entities);
}