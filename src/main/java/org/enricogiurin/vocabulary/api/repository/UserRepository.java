package org.enricogiurin.vocabulary.api.repository;

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

import static org.enricogiurin.vocabulary.jooq.Tables.USER;

import com.yourrents.services.common.util.exception.DataNotFoundException;
import com.yourrents.services.common.util.jooq.JooqUtils;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.enricogiurin.vocabulary.api.component.AuthenticatedUserProvider;
import org.enricogiurin.vocabulary.api.exception.DataExecutionException;
import org.enricogiurin.vocabulary.api.model.User;
import org.enricogiurin.vocabulary.jooq.tables.records.UserRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record3;
import org.jooq.SelectJoinStep;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserRepository {

  public static final String UUID_ALIAS = "uuid";
  public static final String USERNAME_ALIAS = "username";
  public static final String EMAIL_ALIAS = "email";
  private final DSLContext dsl;
  private final JooqUtils jooqUtils;
  private final AuthenticatedUserProvider authenticatedUserProvider;


  public Optional<User> findById(Integer id) {
    return getSelect()
        .where(USER.ID.eq(id))
        .fetchOptional()
        .map(this::map);
  }


  public Integer findUserIdByAuthenticatedEmail() {
    String authenticatedUserEmail = authenticatedUserProvider.getAuthenticatedUserEmail();
    return dsl.select(USER.ID)
        .from(USER)
        .where(USER.EMAIL.eq(authenticatedUserEmail))
        .fetchOptional(USER.ID).orElseThrow(
            () -> new DataNotFoundException("User not found: "
                + authenticatedUserEmail));
  }


  /**
   * Create a new User.
   *
   * @return the new created User
   * @throws DataExecutionException if something unexpected happens
   */
  @Transactional(readOnly = false)
  public User create(User user) {
    UserRecord userRecord = dsl.newRecord(USER);
    userRecord.setUsername(user.username());
    userRecord.setEmail(user.email());
    userRecord.setCreatedAt(LocalDateTime.now());
    userRecord.insert();
    return findById(userRecord.getId()).orElseThrow(
        () -> new DataExecutionException("failed to create user[username]: " + user.username()));
  }

  private SelectJoinStep<Record3<UUID, String, String>> getSelect() {
    return dsl.select(
            USER.EXTERNAL_ID.as(UUID_ALIAS),
            USER.USERNAME.as(USERNAME_ALIAS),
            USER.EMAIL.as(EMAIL_ALIAS))
        .from(USER);
  }

  private User map(Record record) {
    return new User(
        record.get(UUID_ALIAS, UUID.class),
        record.get(USERNAME_ALIAS, String.class),
        record.get(USERNAME_ALIAS, String.class)
    );
  }


}
