/*
 * Copyright 2022-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.instancio.test.features.ofmap;

import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.OF_MAP)
@ExtendWith(InstancioExtension.class)
class OfMapTypeTokenTest {

    @Test
    void typeToken() {
        final Map<Item<String>, Item<Integer>> results = Instancio.ofMap(
                        new TypeToken<Item<String>>() {},
                        new TypeToken<Item<Integer>>() {})
                .create();

        assertThat(results)
                .hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE)
                .allSatisfy((k, v) -> {
                    assertThat(k).hasNoNullFieldsOrProperties();
                    assertThat(v).hasNoNullFieldsOrProperties();
                });
    }
}
