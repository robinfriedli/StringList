package net.robinfriedli.stringlist;

import com.google.common.collect.Lists;

import javax.annotation.Nonnull;
import java.text.BreakIterator;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class StringListImpl implements StringList {

    private List<String> values;

    public StringListImpl(List<String> stringList) {
        this.values = stringList;
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public String get(int i) {
        return values.get(i);
    }

    @Override
    public String tryGet(int i) {
        if (i < size() && i >= 0) {
            return get(i);
        }
        return null;
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return values.contains(o);
    }

    @Override
    @Nonnull
    public Iterator<String> iterator() {
        return values.iterator();
    }

    @Override
    public String[] toArray() {
        String[] array = new String[size()];

        for (int i = 0; i < size(); i++) {
            array[i] = get(i);
        }

        return array;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return values.toArray(a);
    }

    @Override
    public boolean add(String s) {
        return values.add(s);
    }

    @Override
    public boolean remove(Object o) {
        return values.remove(o);
    }

    @Override
    public boolean addAll(Collection<? extends String> c) {
        return values.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends String> c) {
        return values.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return values.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return values.retainAll(c);
    }

    @Override
    public void clear() {
        values.clear();
    }

    @Override
    public boolean containsAll(Collection c) {
        return values.containsAll(c);
    }

    @Override
    public List<String> getValues() {
        return values;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (String value : values) {
            builder.append(value);
        }

        return builder.toString();
    }

    @Override
    public String toSeparatedString(String separator) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < values.size(); i++) {
            builder.append(values.get(i));

            if (i < values.size() - 1) {
                builder.append(separator);
            }
        }

        return builder.toString();
    }

    @Override
    public String set(int index, String value) {
        return values.set(index, value);
    }

    @Override
    public void add(int index, String element) {
        values.add(index, element);
    }

    @Override
    public String remove(int index) {
        return values.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return values.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return values.lastIndexOf(o);
    }

    @Override
    public ListIterator<String> listIterator() {
        return values.listIterator();
    }

    @Override
    public ListIterator<String> listIterator(int index) {
        return values.listIterator(index);
    }

    @Override
    public StringList filterWords() {
        StringList stringList = create();
        for (String value : values) {
            Character[] chars = stringToCharacterArray(value);
            if (Arrays.stream(chars).allMatch(Character::isLetter)) {
                stringList.add(value);
            }
        }

        return stringList;
    }

    @Override
    public List<Integer> getWordPositions() {
        List<Integer> positions = Lists.newArrayList();

        for (int i = 0; i < this.size(); i++) {
            String value = get(i);
            Character[] chars = stringToCharacterArray(value);

            if (Arrays.stream(chars).allMatch(Character::isLetter)) {
                positions.add(i);
            }
        }

        return positions;
    }

    @Override
    public List<Integer> findPositionsOf(String s) {
        return findPositionsOf(s, false);
    }

    @Override
    public List<Integer> findPositionsOf(String s, boolean ignoreCase) {
        List<Integer> positions = Lists.newArrayList();

        for (int i = 0; i < size(); i++) {
            String value = get(i);

            if (ignoreCase ? s.equalsIgnoreCase(value) : s.equals(value)) {
                positions.add(i);
            }
        }

        return positions;
    }

    @Override
    public boolean valuePrecededBy(int index, String s) {
        if (index == 0) return false;
        return get(index - 1).equals(s);
    }

    @Override
    public boolean valuesPrecededBy(List<Integer> indices, String s) {
        return indices.stream().allMatch(index -> valuePrecededBy(index, s));
    }

    @Override
    public boolean valueSucceededBy(int index, String s) {
        if (index >= size() - 1) return false;
        return get(index + 1).equals(s);
    }

    @Override
    public boolean valuesSucceededBy(List<Integer> indices, String s) {
        return indices.stream().allMatch(index -> valueSucceededBy(index, s));
    }

    @Override
    public boolean valueAppearsBefore(int index, String s) {
        if (index <= 0) return false;
        return subList(0, index).contains(s);
    }

    @Override
    public boolean valueAppearsAfter(int index, String s) {
        if (index >= size() - 1) return false;
        return subList(index + 1).contains(s);
    }

    @Override
    public StringList subList(int beginIndex, int endIndex) {
        return create(values.subList(beginIndex, endIndex));
    }

    @Override
    public StringList subList(int beginIndex) {
        return create(values.subList(beginIndex, size()));
    }

    @Override
    public void assertThat(Predicate<StringList> predicate, String errorMessage) throws AssertionError {
        if (!predicate.test(this)) {
            throw new AssertionError(errorMessage);
        }
    }

    @Override
    public void assertThat(Predicate<StringList> predicate) throws AssertionError {
        if (!predicate.test(this)) {
            throw new AssertionError();
        }
    }

    @Override
    public StringList applyForEach(Function<String, String> action) {
        return applyForEach(action, 0, size());
    }

    @Override
    public StringList applyForEach(Function<String, String> action, int beginIndex) {
        return applyForEach(action, beginIndex, size());
    }

    @Override
    public StringList applyForEach(Function<String, String> action, int beginIndex, int endIndex) {
        for (int i = beginIndex; i < endIndex; i++) {
            String value = get(i);
            set(i, action.apply(value));
        }
        return this;
    }

    @Override
    public void assertUnique() throws AssertionError {
        List<String> checkedValues = Lists.newArrayList();

        for (String value : values) {
            if (!checkedValues.contains(value)) {
                checkedValues.add(value);
            } else {
                throw new AssertionError("Value \"" + value + "\" is not unique");
            }
        }
    }

    @Override
    public void assertUnique(String errorMessage) throws AssertionError {
        List<String> checkedValues = Lists.newArrayList();

        for (String value : values) {
            if (!checkedValues.contains(value)) {
                checkedValues.add(value);
            } else {
                throw new AssertionError(errorMessage);
            }
        }
    }

    public static StringList create(String string, String regex) {
        String[] stringList = string.split(regex);
        return create(stringList);
    }

    public static StringList separateString(String string, String regex) {
        String[] strings = string.split(regex);
        StringList stringList = new StringListImpl(Lists.newArrayList());

        for (int i = 0; i < strings.length; i++) {
            stringList.add(strings[i]);
            if (i < strings.length - 1) stringList.add(regex);
        }

        return stringList;
    }

    public static StringList createSentences(String input) {
        List<String> sentences = Lists.newArrayList();

        BreakIterator iterator = BreakIterator.getSentenceInstance();
        iterator.setText(input);
        int start = iterator.first();

        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            sentences.add(input.substring(start, end));
        }

        return create(sentences);
    }

    public static StringList createWords(String input) {
        List<String> words = Lists.newArrayList();

        BreakIterator iterator = BreakIterator.getWordInstance();
        iterator.setText(input);
        int start = iterator.first();

        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            words.add(input.substring(start, end));
        }

        return create(words);
    }

    public static StringList create(List<String> stringList) {
        return new StringListImpl(stringList);
    }

    public static StringList create(Iterable<String> strings) {
        return new StringListImpl(Lists.newArrayList(strings));
    }

    public static StringList create(Collection<String> strings) {
        return new StringListImpl(Lists.newArrayList(strings));
    }

    public static StringList create(String... strings) {
        return new StringListImpl(Lists.newArrayList(strings));
    }

    public static StringList create() {
        return new StringListImpl(Lists.newArrayList());
    }

    public static <E> StringList create(E[] values, Function<E, String> getStringValues) {
        return create(Arrays.asList(values), getStringValues);
    }

    public static <E> StringList create(Iterable<E> values, Function<E, String> getStringValues) {
        StringList stringList = StringListImpl.create();

        for (E value : values) {
            stringList.add(getStringValues.apply(value));
        }

        return stringList;
    }

    public static StringList charsToList(String string) {
        List<String> charsAsString = Lists.newArrayList();
        for (Character character : string.toCharArray()) {
            charsAsString.add(character.toString());
        }

        return create(charsAsString);
    }

    public Stream<String> stream() {
        return values.stream();
    }

    public static List<String> getAllValues(StringList... stringLists) {
        List<String> values = Lists.newArrayList();
        for (StringList stringList : stringLists) {
            values.addAll(stringList.getValues());
        }
        return values;
    }

    public static StringList join(StringList... stringLists) {
        List<String> values = getAllValues(stringLists);
        return StringListImpl.create(values);
    }

    @SafeVarargs
    public static StringList join(List<String>... lists) {
        StringList stringList = StringListImpl.create();

        for (List<String> list : lists) {
            stringList.addAll(list);
        }

        return stringList;
    }

    public static Collector<String, ?, StringList> collector() {
        return new Collector<String, Object, StringList>() {
            @Override
            public Supplier<Object> supplier() {
                return StringListImpl::create;
            }

            @Override
            public BiConsumer<Object, String> accumulator() {
                return (o, s) -> ((StringList) o).add(s);
            }

            @Override
            public BinaryOperator<Object> combiner() {
                return (o, o2) -> {
                    ((StringList) o).addAll((StringList) o2);
                    return o;
                };
            }

            @Override
            public Function<Object, StringList> finisher() {
                return o -> (StringList) o;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH));
            }
        };
    }

    private Character[] stringToCharacterArray(String string) {
        return string.chars().mapToObj(c -> (char) c).toArray(Character[]::new);
    }
}
