package org.enricogiurin.vocabulary.api.rest;

/*-
 * #%L
 * Vocabulary API
 * %%
 * Copyright (C) 2024 Vocabulary Team
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import com.yourrents.services.common.searchable.Searchable;
import com.yourrents.services.common.util.exception.DataNotFoundException;
import java.security.Principal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.enricogiurin.vocabulary.api.model.Language;
import org.enricogiurin.vocabulary.api.repository.LanguageRepository;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${application.api.base-path}/language")
@RequiredArgsConstructor
@Slf4j
public class LanguageController {

  private final LanguageRepository languageRepository;

  @GetMapping
  ResponseEntity<Page<Language>> getLanguages(
      @ParameterObject Searchable filter,
      @ParameterObject @SortDefault(sort = LanguageRepository.NAME_ALIAS, direction = Direction.ASC) Pageable pagination) {
    Page<Language> page = languageRepository.find(filter, pagination);
    return ResponseEntity.ok(page);
  }

  @GetMapping("/{uuid}")
  ResponseEntity<Language> findByUuid(@PathVariable UUID uuid, Principal principal) {
    log.info("authenticated user: {}", principal.getName());
    Language property = languageRepository.findByExternalId(uuid)
        .orElseThrow(
            () -> new DataNotFoundException("can't find Language having uuid: " + uuid));
    return ResponseEntity.ok(property);
  }

}
