package net.robinfriedli.stringlist;

import com.google.common.collect.Lists;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.BreakIterator;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;

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
    public boolean contains(Object o, boolean ignoreCase) {
        if (ignoreCase && o instanceof String) {
            String s = (String) o;
            return values.stream().anyMatch(v -> v.equalsIgnoreCase(s));
        } else {
            return contains(o);
        }
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
    public boolean containsAll(Collection<?> c) {
        return values.containsAll(c);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean containsAll(Collection c, boolean ignoreCase) {
        return c.stream().allMatch(o -> contains(o, ignoreCase));
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
    public int indexOf(Object o, boolean ignoreCase) {
        if (ignoreCase && o instanceof String) {
            for (int i = 0; i < size(); i++) {
                if (values.get(i).equalsIgnoreCase((String) o)) {
                    return i;
                }
            }

            return -1;
        } else {
            return indexOf(o);
        }
    }

    @Override
    public int lastIndexOf(Object o) {
        return values.lastIndexOf(o);
    }

    @Override
    public int lastIndexOf(Object o, boolean ignoreCase) {
        if (ignoreCase && o instanceof String) {
            if (!isEmpty()) {
                for (int i = size() - 1; i >= 0; i--) {
                    if (values.get(i).equalsIgnoreCase((String) o)) {
                        return i;
                    }
                }
            }

            return -1;
        } else {
            return lastIndexOf(o);
        }
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
        if (predicate.negate().test(this)) {
            throw new AssertionError(errorMessage);
        }
    }

    @Override
    public void assertThat(Predicate<StringList> predicate) throws AssertionError {
        if (predicate.negate().test(this)) {
            throw new AssertionError();
        }
    }

    @Override
    public <E extends Throwable> void assertThat(Predicate<StringList> predicate, String errorMessage, Class<E> throwable) throws E {
        if (predicate.negate().test(this)) {
            throw instantiate(throwable, new Class[]{String.class}, new Object[]{errorMessage});
        }
    }

    @Override
    public <E extends Throwable> void assertThat(Predicate<StringList> predicate, Class<E> throwable) throws E {
        if (predicate.negate().test(this)) {
            throw instantiate(throwable);
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
        // cannot use the assertUnique(Class) method because AssertionError uses an Object instead of a String as message
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
        // cannot use the assertUnique(Class) method because AssertionError uses an Object instead of a String as message
        List<String> checkedValues = Lists.newArrayList();

        for (String value : values) {
            if (!checkedValues.contains(value)) {
                checkedValues.add(value);
            } else {
                throw new AssertionError(errorMessage);
            }
        }
    }

    @Override
    public <E extends Throwable> void assertUnique(Class<E> type) throws E {
        List<String> checkedValues = Lists.newArrayList();

        for (String value : values) {
            if (!checkedValues.contains(value)) {
                checkedValues.add(value);
            } else {
                String message = String.format("Value '%s' is not unique", value);
                throw instantiate(type, new Class[]{String.class}, new Object[]{message});
            }
        }
    }

    @Override
    public <E extends Throwable> void assertUnique(String errorMessage, Class<E> type) throws E {
        List<String> checkedValues = Lists.newArrayList();

        for (String value : values) {
            if (!checkedValues.contains(value)) {
                checkedValues.add(value);
            } else {
                throw instantiate(type, new Class[]{String.class}, new Object[]{errorMessage});
            }
        }
    }

    @Override
    public boolean allMatch(Predicate<String> predicate) {
        return stream().allMatch(predicate);
    }

    @Override
    public boolean anyMatch(Predicate<String> predicate) {
        return stream().anyMatch(predicate);
    }

    @Override
    public boolean noneMatch(Predicate<String> predicate) {
        return stream().noneMatch(predicate);
    }

    public static StringList createWithRegex(String string, String regex) {
        String[] stringList = string.split(regex);
        return of(stringList);
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

    public static StringList splitSentences(String input) {
        return create(BreakIterator.getSentenceInstance(), input);
    }

    public static StringList splitWords(String input) {
        return create(BreakIterator.getWordInstance(), input);
    }

    public static StringList create(BreakIterator breakIterator, String text) {
        StringList stringList = create();

        breakIterator.setText(text);
        int start = breakIterator.first();

        for (int end = breakIterator.next(); end != BreakIterator.DONE; start = end, end = breakIterator.next()) {
            stringList.add(text.substring(start, end));
        }

        return stringList;
    }

    /**
     * Creates a new StringList using the provided List as internal store, meaning changes made to this StringList will affect the provided list
     */
    public static StringList shareList(List<String> stringList) {
        return new StringListImpl(stringList);
    }

    public static StringList create(Iterable<String> strings) {
        return new StringListImpl(Lists.newArrayList(strings));
    }

    public static StringList create(Collection<String> strings) {
        return new StringListImpl(Lists.newArrayList(strings));
    }

    public static StringList of(String... strings) {
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

    public static StringList splitChars(String string) {
        List<String> charsAsString = Lists.newArrayList();
        for (Character character : string.toCharArray()) {
            charsAsString.add(character.toString());
        }

        return create(charsAsString);
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

    private <E> E instantiate(Class<E> type) {
        return instantiate(type, new Class[0], new Object[0]);
    }

    private <E> E instantiate(Class<E> type, Class[] argumentTypes, Object[] arguments) {
        if (argumentTypes.length != arguments.length) {
            throw new IllegalArgumentException("Not the same number if argument types and arguments provided");
        }

        for (int i = 0; i < argumentTypes.length; i++) {
            Class classToCheck = argumentTypes[i];
            Object argument = arguments[i];
            if (!classToCheck.isInstance(argument)) {
                throw new IllegalArgumentException("Type mismatch in provided arguments. "
                        + argument.getClass().getSimpleName() + " is not an instance of " + classToCheck.getSimpleName());
            }
        }

        try {
            Constructor<E> constructor = type.getConstructor(argumentTypes);
            return constructor.newInstance(arguments);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("No matching constructor available. Class " + type.getSimpleName() + " could not be instantiated.", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot access constructor. Class " + type.getSimpleName() + " could not be instantiated.", e);
        } catch (InstantiationException e) {
            throw new RuntimeException("Cannot instantiate class " + type.getSimpleName(), e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Exception while invoking constructor of class " + type.getSimpleName(), e);
        }
    }

    private Character[] stringToCharacterArray(String string) {
        return string.chars().mapToObj(c -> (char) c).toArray(Character[]::new);
    }
}
