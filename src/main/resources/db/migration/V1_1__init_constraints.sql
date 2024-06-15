---
-- #%L
-- Vocabulary API
-- %%
-- Copyright (C) 2024 Vocabulary Team
-- %%
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
-- 
--      http://www.apache.org/licenses/LICENSE-2.0
-- 
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
-- #L%
---


ALTER TABLE ONLY vocabulary.word
    ADD CONSTRAINT vocabulary_pkey PRIMARY KEY (id);

ALTER TABLE ONLY vocabulary.language
    ADD CONSTRAINT language_pkey PRIMARY KEY (id);

ALTER TABLE ONLY vocabulary.translation
    ADD CONSTRAINT translation_pkey PRIMARY KEY (id);

-- fk constraints

ALTER TABLE ONLY vocabulary.word
    ADD CONSTRAINT fk_word_language FOREIGN KEY (language_id) REFERENCES vocabulary.language(id);

ALTER TABLE ONLY vocabulary.translation
    ADD CONSTRAINT fk_translation_language FOREIGN KEY (language_id) REFERENCES vocabulary.language(id);

ALTER TABLE ONLY vocabulary.translation
    ADD CONSTRAINT fk_translation_word FOREIGN KEY (word_id) REFERENCES vocabulary.word(id) ON DELETE CASCADE;
