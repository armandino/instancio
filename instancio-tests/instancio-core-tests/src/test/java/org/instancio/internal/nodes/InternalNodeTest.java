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
package org.instancio.internal.nodes;

import org.instancio.TypeToken;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.test.support.pojo.collections.lists.ListString;
import org.instancio.test.support.pojo.dynamic.MixedPojo;
import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.test.support.pojo.generics.basic.Pair;
import org.instancio.test.support.pojo.generics.basic.Triplet;
import org.instancio.test.support.pojo.generics.foobarbaz.Baz;
import org.instancio.test.support.pojo.generics.foobarbaz.Foo;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.tags.GenericsTag;
import org.instancio.test.support.tags.NodeTag;
import org.instancio.testsupport.fixtures.Fixtures;
import org.instancio.testsupport.fixtures.Types;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.testsupport.asserts.NodeAssert.assertNode;
import static org.instancio.testsupport.utils.NodeUtils.getChildNode;

@NodeTag
class InternalNodeTest {

    private static final NodeFactory NODE_FACTORY = new NodeFactory(Fixtures.modelContext());

    @Test
    void getNodeKind() {
        assertThat(NODE_FACTORY.createRootNode(Person.class).getNodeKind()).isEqualTo(NodeKind.POJO);
        assertThat(NODE_FACTORY.createRootNode(int.class).getNodeKind()).isEqualTo(NodeKind.JDK);
        assertThat(NODE_FACTORY.createRootNode(Person[].class).getNodeKind()).isEqualTo(NodeKind.ARRAY);
        assertThat(NODE_FACTORY.createRootNode(Types.LIST_STRING.get()).getNodeKind()).isEqualTo(NodeKind.COLLECTION);
        assertThat(NODE_FACTORY.createRootNode(Types.MAP_INTEGER_STRING.get()).getNodeKind()).isEqualTo(NodeKind.MAP);
        assertThat(NODE_FACTORY.createRootNode(new TypeToken<Optional<Integer>>() {}.get()).getNodeKind()).isEqualTo(NodeKind.CONTAINER);
    }

    @Test
    void getOnlyChildValidation() {
        final InternalNode node = NODE_FACTORY.createRootNode(Types.MAP_INTEGER_STRING.get());

        assertThatThrownBy(node::getOnlyChild)
                .isExactlyInstanceOf(IllegalStateException.class)
                .hasMessage("Expected one child, but were 2");
    }

    @Test
    void toBuilder() {
        final InternalNode parent = createNode(Person.class, new TypeToken<Person>() {});
        final List<InternalNode> children = Collections.singletonList(createNode(String.class, new TypeToken<String>() {}));

        final InternalNode node = InternalNode.builder(
                        Types.LIST_STRING.get(),
                        List.class,
                        Fixtures.modelContext().getRootType())
                .nodeKind(NodeKind.COLLECTION)
                .member(ReflectionUtils.getField(ListString.class, "list"))
                .member(ReflectionUtils.getSetterMethod(ListString.class, "setList", List.class))
                .parent(parent)
                .children(children)
                .cyclic()
                .build();

        final InternalNode copy = node.toBuilder().build();

        assertThat(copy.getType()).isEqualTo(node.getType());
        assertThat(copy.getRawType()).isEqualTo(node.getRawType());
        assertThat(copy.getTargetClass()).isEqualTo(node.getTargetClass());
        assertThat(copy.getField()).isEqualTo(node.getField());
        assertThat(copy.getSetter()).isEqualTo(node.getSetter());
        assertThat(copy.getParent()).isEqualTo(node.getParent());
        assertThat(copy.getTypeMap()).isEqualTo(node.getTypeMap());
        assertThat(copy.getOnlyChild()).isEqualTo(node.getOnlyChild());
        assertThat(copy.getChildren()).isEqualTo(node.getChildren());
        assertThat(copy.getNodeKind()).isEqualTo(node.getNodeKind());
        assertThat(copy.isCyclic()).isEqualTo(node.isCyclic());
    }

    @Nested
    class TargetClassTest {

        @Test
        void differentRawTypeAndTargetClass() {
            final Class<?> rawType = List.class;
            final Class<?> targetClass = LinkedList.class;

            final InternalNode node = InternalNode.builder(
                            Types.LIST_STRING.get(),
                            rawType,
                            Fixtures.modelContext().getRootType())
                    .targetClass(targetClass)
                    .build();

            assertNode(node)
                    .hasRawType(rawType)
                    .hasTargetClass(targetClass);
        }

        @Test
        void ifNotSpecified_TargetClassShouldDefaultToRawType() {
            final Class<?> rawType = List.class;

            final InternalNode node = InternalNode.builder(
                            Types.LIST_STRING.get(),
                            rawType,
                            Fixtures.modelContext().getRootType())
                    .build();

            assertNode(node)
                    .hasRawType(rawType)
                    .hasTargetClass(rawType);
        }
    }

    @Nested
    class EqualsTest {

        @Test
        void equalsHashCode() {
            TypeToken<?> typeBazInteger = new TypeToken<Baz<Integer>>() {};
            TypeToken<?> typeBazString = new TypeToken<Baz<String>>() {};

            InternalNode bazInteger = createNode(List.class, typeBazInteger);
            InternalNode bazString = createNode(List.class, typeBazString);
            InternalNode bazIntegerClassNode = InternalNode.builder(
                            typeBazInteger.get(),
                            Baz.class,
                            Fixtures.modelContext().getRootType())
                    .build();

            assertThat(bazString)
                    .isNotEqualTo(bazInteger)
                    .doesNotHaveSameHashCodeAs(bazInteger);

            assertThat(bazInteger)
                    .isNotEqualTo(bazIntegerClassNode)
                    .doesNotHaveSameHashCodeAs(bazIntegerClassNode);
        }

        @Test
        void equalsWithNull() {
            final InternalNode node = NODE_FACTORY.createRootNode(int.class);

            assertThat(node.equals(null)).isFalse();
        }
    }

    @Nested
    @GenericsTag
    class GenericTypeTest {

        @Test
        void listOfString() {
            assertNode(createNode(List.class, Types.LIST_STRING))
                    .hasTargetClass(List.class)
                    .hasType(Types.LIST_STRING.get());
        }

        @Test
        void mapOfIntegerString() {
            assertNode(createNode(Map.class, Types.MAP_INTEGER_STRING))
                    .hasTargetClass(Map.class)
                    .hasType(Types.MAP_INTEGER_STRING.get());
        }

        @Test
        void pairOfIntegerString() {
            assertNode(createNode(Pair.class, Types.PAIR_INTEGER_STRING))
                    .hasTargetClass(Pair.class)
                    .hasType(Types.PAIR_INTEGER_STRING.get());
        }

        @Test
        void tripletOfBooleanStringInteger() {
            assertNode(createNode(Triplet.class, Types.TRIPLET_BOOLEAN_INTEGER_STRING))
                    .hasTargetClass(Triplet.class)
                    .hasType(Types.TRIPLET_BOOLEAN_INTEGER_STRING.get());
        }

        @Test
        void pairOfGenericItemFooList() {
            final TypeToken<?> type = new TypeToken<Pair<Item<Foo<List<Integer>>>, Map<Integer, Foo<String>>>>() {};

            assertNode(createNode(Pair.class, type))
                    .hasTargetClass(Pair.class)
                    .hasType(type.get());
        }
    }

    @Nested
    @GenericsTag
    class NodeTypeMapTest {

        @Test
        void listOfStrings() {
            assertNode(createNode(List.class, Types.LIST_STRING))
                    .hasTypeMappedTo(List.class, "E", String.class)
                    .hasTypeMapWithSize(1);
        }

        @Test
        void mapOfIntegerString() {
            assertNode(createNode(Map.class, Types.MAP_INTEGER_STRING))
                    .hasTypeMappedTo(Map.class, "K", Integer.class)
                    .hasTypeMappedTo(Map.class, "V", String.class)
                    .hasTypeMapWithSize(2);
        }

        @Test
        void tripletOfBooleanStringInteger() {
            assertNode(createNode(Triplet.class, Types.TRIPLET_BOOLEAN_INTEGER_STRING))
                    .hasTypeMappedTo(Triplet.class, "M", Boolean.class)
                    .hasTypeMappedTo(Triplet.class, "N", Integer.class)
                    .hasTypeMappedTo(Triplet.class, "O", String.class)
                    .hasTypeMapWithSize(3);
        }

        @Test
        void pairOfGenericItemFooList() {
            assertNode(createNode(Pair.class, new TypeToken<Pair<Item<String>, Foo<List<Integer>>>>() {}))
                    .hasTypeMappedTo(Pair.class, "L", Types.ITEM_STRING.get())
                    .hasTypeMappedTo(Pair.class, "R", Types.FOO_LIST_INTEGER.get())
                    .hasTypeMapWithSize(2);
        }
    }

    @Nested
    class ToStringTest {

        @Test
        void verifyToString() {
            final InternalNode personNode = NODE_FACTORY.createRootNode(Person.class);

            assertThat(personNode)
                    .hasToString("Node[Person, depth=0, type=Person]");

            assertThat(getChildNode(personNode, "age"))
                    .hasToString("Node[Person.age, Person.setAge(int), depth=1, type=int]");

            assertThat(getChildNode(personNode, "name"))
                    .hasToString("Node[Person.name, Person.setName(String), depth=1, type=String]");

            assertThat(getChildNode(personNode, "address"))
                    .hasToString("Node[Person.address, Person.setAddress(Address), depth=1, type=Address]");

            // without setter
            assertThat(getChildNode(personNode, "finalField"))
                    .hasToString("Node[Person.finalField, depth=1, type=String]");

            assertThat(NODE_FACTORY.createRootNode(String.class))
                    .hasToString("Node[String, depth=0, type=String]");

            assertThat(NODE_FACTORY.createRootNode(new TypeToken<Pair<Item<String>, Foo<List<Integer>>>>() {}.get()))
                    .hasToString("Node[Pair, depth=0, type=Pair<Item<String>, Foo<List<Integer>>>]");
        }

        @Test
        void setterWithoutField() {
            final InternalNode rootNode = NODE_FACTORY.createRootNode(MixedPojo.class);

            assertThat(rootNode)
                    .hasToString("Node[MixedPojo, depth=0, type=MixedPojo]");

            assertThat(getChildNode(rootNode, "setDynamicField", String.class))
                    .hasToString("Node[MixedPojo.setDynamicField(String), depth=1, type=String]");
        }

        @Test
        void ignoredNode() {
            final InternalNode ignoredNode = NODE_FACTORY.createRootNode(Person.class)
                    .toBuilder().nodeKind(NodeKind.IGNORED).build();

            assertThat(ignoredNode).hasToString("Node[Person, depth=0, type=Person, IGNORED]");
        }
    }

    @Nested
    class DisplayStringTest {

        @Test
        void displayString() {
            final InternalNode personNode = NODE_FACTORY.createRootNode(Person.class);

            assertThat(personNode.toDisplayString())
                    .isEqualTo("class Person");

            assertThat(getChildNode(personNode, "age").toDisplayString())
                    .isEqualTo("field Person.age, setter Person.setAge(int)");

            assertThat(getChildNode(personNode, "finalField").toDisplayString())
                    .isEqualTo("field Person.finalField");

            assertThat(NODE_FACTORY.createRootNode(String.class).toDisplayString())
                    .isEqualTo("class String");

            assertThat(NODE_FACTORY.createRootNode(new TypeToken<Pair<Item<String>, Foo<List<Integer>>>>() {}.get()).toDisplayString())
                    .isEqualTo("class Pair<Item<String>, Foo<List<Integer>>>");

            final InternalNode mixedPojo = NODE_FACTORY.createRootNode(MixedPojo.class);
            assertThat(getChildNode(mixedPojo, "setDynamicField", String.class).toDisplayString())
                    .isEqualTo("setter MixedPojo.setDynamicField(String)");
        }

        @Test
        void ignoredNode() {
            final InternalNode ignoredNode = NODE_FACTORY.createRootNode(Person.class)
                    .toBuilder().nodeKind(NodeKind.IGNORED).build();

            assertThat(ignoredNode.toDisplayString()).isEqualTo("ignored");
        }
    }

    private static InternalNode createNode(Class<?> klass, TypeToken<?> type) {
        return InternalNode.builder(
                        type.get(),
                        klass,
                        Fixtures.modelContext().getRootType())
                .targetClass(klass)
                .build();
    }
}
