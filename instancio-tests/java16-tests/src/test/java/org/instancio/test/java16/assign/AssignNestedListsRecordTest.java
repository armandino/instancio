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
package org.instancio.test.java16.assign;

import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.Selector;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@FeatureTag(Feature.ASSIGN)
@ExtendWith(InstancioExtension.class)
class AssignNestedListsRecordTest {

    private static final Selector ROOT_FIELD = field(Root.class, "val");
    private static final Selector MID_FIELD = field(Mid.class, "val");
    private static final Selector INNER_FIELD = field(Inner.class, "val");

    @Test
    void whenRoot_thenMidAndInner() {
        List<Root> results = Instancio.ofList(Root.class)
                .size(100)
                .generate(ROOT_FIELD, gen -> gen.oneOf("A", "B"))
                .assign(Assign.given(ROOT_FIELD).is("A")
                        .set(MID_FIELD, "A-Mid")
                        .set(INNER_FIELD, "A-Inner"))
                .assign(Assign.given(ROOT_FIELD).is("B")
                        .set(MID_FIELD, "B-Mid")
                        .set(INNER_FIELD, "B-Inner"))
                .create();

        assertResults(results);
    }

    @Test
    void whenRoot_thenMid_whenMid_thenInner() {
        List<Root> results = Instancio.ofList(Root.class)
                .size(100)
                .generate(ROOT_FIELD, gen -> gen.oneOf("A", "B"))
                .assign(Assign.given(ROOT_FIELD).is("A").set(MID_FIELD, "A-Mid"))
                .assign(Assign.given(ROOT_FIELD).is("B").set(MID_FIELD, "B-Mid"))
                .assign(Assign.given(MID_FIELD).is("A-Mid").set(INNER_FIELD, "A-Inner"))
                .assign(Assign.given(MID_FIELD).is("B-Mid").set(INNER_FIELD, "B-Inner"))
                .create();

        assertResults(results);
    }

    private static void assertResults(final List<Root> results) {
        assertThat(results).extracting(r -> r.val).contains("A", "B");
        assertThat(results).allSatisfy(root -> {
            final String rootVal = root.val;

            assertThat(root.mid)
                    .allSatisfy(mid -> {
                        assertThat(mid.val).isEqualTo(rootVal + "-Mid");
                        assertThat(mid.inner).allSatisfy(inner -> assertThat(inner.val).isEqualTo(rootVal + "-Inner"));
                    });
        });
    }

    private record Root(String val, List<Mid> mid) {}

    private record Mid(String val, List<Inner> inner) {}

    private record Inner(String val) {}
}
