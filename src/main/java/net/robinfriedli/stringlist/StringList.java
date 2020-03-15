package net.robinfriedli.stringlist;

import com.google.common.collect.Lists;

import javax.annotation.Nullable;
import java.text.BreakIterator;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;

public interface StringList extends List<String> {

    /**
     * Create a StringList backed by an array list containing the result of splitting the provided string by the given
     * regex.
     *
     * @param string the string to split
     * @param regex  the regex expression
     * @return the create StringList
     */
    static StringList createWithRegex(String string, String regex) {
        String[] stringList = string.split(regex);
        return of(stringList);
    }

    /**
     * Similar to {@link #createWithRegex(String, String)} but also adds the regex string between every element as a
     * separator.
     *
     * @param string the string to split
     * @param regex  the regex to split the string by and add between each element as a separator.
     * @return the creates StringList
     */
    static StringList separateString(String string, String regex) {
        String[] strings = string.split(regex);
        StringList stringList = new StringListImpl(Lists.newArrayList());

        for (int i = 0; i < strings.length; i++) {
            stringList.add(strings[i]);
            if (i < strings.length - 1) stringList.add(regex);
        }

        return stringList;
    }

    /**
     * Create a StringList backed by an array list containing all sentences of the provided string using
     * {@link BreakIterator#getSentenceInstance()} to call {@link #create(BreakIterator, String)} to split the string.
     *
     * @param input the input to split into sentences.
     * @return the created StringList
     */
    static StringList splitSentences(String input) {
        return create(BreakIterator.getSentenceInstance(), input);
    }

    /**
     * Create a StringList backed by an array list containing all words of the provided string using
     * {@link BreakIterator#getWordInstance()} to call {@link #create(BreakIterator, String)} to split the string.
     *
     * @param input the input to split into words.
     * @return the created StringList
     */
    static StringList splitWords(String input) {
        return create(BreakIterator.getWordInstance(), input);
    }

    /**
     * Create a StringList backed by an array list containing the result of using the indices returned by the provided
     * {@link BreakIterator} to substring the input text.
     *
     * @param breakIterator the break iterator supplying the indices to substring the input text with
     * @param text          the input string to split
     * @return the created StringList
     */
    static StringList create(BreakIterator breakIterator, String text) {
        StringList stringList = create();

        breakIterator.setText(text);
        int start = breakIterator.first();

        for (int end = breakIterator.next(); end != BreakIterator.DONE; start = end, end = breakIterator.next()) {
            stringList.add(text.substring(start, end));
        }

        return stringList;
    }

    /**
     * Creates a new StringList using the provided List as backing list, meaning changes made to this StringList will affect the provided list
     */
    static StringList backedBy(List<String> stringList) {
        return new StringListImpl(stringList);
    }

    static StringList create(Iterable<String> strings) {
        return new StringListImpl(Lists.newArrayList(strings));
    }

    static StringList of(String... strings) {
        return new StringListImpl(Lists.newArrayList(strings));
    }

    static StringList create() {
        return new StringListImpl(Lists.newArrayList());
    }

    /**
     * Create a StringList consisting of the results of applying the provided function to each provided element of type
     * {@link E}.
     *
     * @param values          element for which to apply the provided funtion
     * @param getStringValues function producing the values for this list
     * @param <E>             type of supplied values
     * @return the created StringList
     */
    static <E> StringList create(E[] values, Function<E, String> getStringValues) {
        return create(Arrays.asList(values), getStringValues);
    }

    /**
     * Create a StringList consisting of the results of applying the provided function to each provided element of type
     * {@link E}.
     *
     * @param values          element for which to apply the provided funtion
     * @param getStringValues function producing the values for this list
     * @param <E>             type of supplied values
     * @return the created StringList
     */
    static <E> StringList create(Iterable<E> values, Function<E, String> getStringValues) {
        StringList stringList = StringList.create();

        for (E value : values) {
            stringList.add(getStringValues.apply(value));
        }

        return stringList;
    }

    /**
     * Create StringList backed by an array list containing all characters of the provided string.
     *
     * @param string the string to split into chars
     * @return the created StringList
     */
    static StringList splitChars(String string) {
        List<String> charsAsString = Lists.newArrayList();
        for (char character : string.toCharArray()) {
            charsAsString.add(String.valueOf(character));
        }

        return create(charsAsString);
    }

    /**
     * Flatten all provided StringList instances and return all values in a new array list instance.
     *
     * @param stringLists the StringList instances to get the values from
     * @return all values from the StringList instances in the same order as they appear in the StringList
     */
    static List<String> getAllValues(StringList... stringLists) {
        List<String> values = Lists.newArrayList();
        for (StringList stringList : stringLists) {
            values.addAll(stringList.getValues());
        }
        return values;
    }

    /**
     * Create a new StringList consisting of all values of the provided StringList instances in the order in which they
     * appear in the StringList.
     *
     * @param stringLists the StringList instances to get the values from
     * @return the created StringList
     */
    static StringList join(StringList... stringLists) {
        List<String> values = getAllValues(stringLists);
        return StringList.create(values);
    }

    /**
     * Create a new StringList consisting of all values of the provided string lists in the order in which they
     * appear in the list.
     *
     * @param lists the List instances to get the values from
     * @return the created StringList
     */
    @SafeVarargs
    static StringList join(List<String>... lists) {
        StringList stringList = StringList.create();

        for (List<String> list : lists) {
            stringList.addAll(list);
        }

        return stringList;
    }

    static Collector<String, StringList, StringList> collector() {
        return new Collector<String, StringList, StringList>() {
            @Override
            public Supplier<StringList> supplier() {
                return StringList::create;
            }

            @Override
            public BiConsumer<StringList, String> accumulator() {
                return List::add;
            }

            @Override
            public BinaryOperator<StringList> combiner() {
                return (o, o2) -> {
                    o.addAll(o2);
                    return o;
                };
            }

            @Override
            public Function<StringList, StringList> finisher() {
                return o -> (StringList) o;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH));
            }
        };
    }

    /**
     * tries to return the value at the specified index and returns null instead of throwing an exception
     * when out of bounds
     *
     * @param index of value
     * @return value at index or null
     */
    @Nullable
    String tryGet(int index);

    /**
     * Like {@link List#contains(Object)} but optionally ignores the case of the compared string
     */
    boolean contains(Object o, boolean ignoreCase);

    /**
     * Like {@link List#containsAll(Collection)} but optionally ignores the case of the compared string
     */
    boolean containsAll(Collection<?> c, boolean ignoreCase);

    /**
     * @return StringList values as String
     */
    String toString();

    /**
     * @param separator String to separate StringList values
     * @return StringList values as String separated by passed String
     */
    String toSeparatedString(String separator);

    /**
     * @return StringList values as List
     */
    List<String> getValues();

    /**
     * Like {@link List#indexOf(Object)} but optionally ignores the case of the compared string
     */
    int indexOf(Object o, boolean ignoreCase);

    /**
     * Like {@link List#lastIndexOf(Object)} but optionally ignores the case of the compared string
     */
    int lastIndexOf(Object o, boolean ignoreCase);

    /**
     * Retain values that only contain letters
     */
    StringList filterWords();

    /**
     * @return all indices of StringList values that are words, meaning all characters are letters
     */
    List<Integer> getWordPositions();

    /**
     * find all indices of specified String within StringList
     *
     * @param s String to find
     * @return occurrences of said String
     */
    List<Integer> findPositionsOf(String s);

    /**
     * find all indices of specified String within StringList
     *
     * @param s          String to find
     * @param ignoreCase define if you want to find matches regardless of capitalisation
     * @return occurrences of said String
     */
    List<Integer> findPositionsOf(String s, boolean ignoreCase);

    /**
     * checks if value at index is preceded by String s
     *
     * @param index of value
     * @param s     String to check if precedes value at index
     */
    boolean valuePrecededBy(int index, String s);

    /**
     * checks if all values at the specified indices are preceded by String s
     *
     * @param indices of values to check
     * @param s       String to check if precedes values at indices
     */
    boolean valuesPrecededBy(List<Integer> indices, String s);

    /**
     * checks if value at index is succeeded by String s
     *
     * @param index of value to check
     * @param s     String to check if succeeds value at index
     */
    boolean valueSucceededBy(int index, String s);

    /**
     * checks if all values at the specified indices are succeeded by String s
     *
     * @param indices of values to check
     * @param s       String to check if precedes values at indices
     */
    boolean valuesSucceededBy(List<Integer> indices, String s);

    /**
     * check if provided String appears before given index
     */
    boolean valueAppearsBefore(int index, String s);

    /**
     * check if provided String appears after given index
     */
    boolean valueAppearsAfter(int index, String s);

    /**
     * Create a new StringList with values from this StringList within the given range of indices
     */
    StringList subList(int beginIndex, int endIndex);

    /**
     * Create a new StringList with values from this StringList starting at given index
     */
    StringList subList(int beginIndex);

    /**
     * assert that any condition from one of the StringList methods is true
     * <p>
     * e.g stringList.assertThat(p -> p.valuePrecededBy(3, "-"), "value at 3 not preceded by -")
     *
     * @param predicate    any StringList method that returns a boolean
     * @param errorMessage message to throw if assertion fails
     * @throws AssertionError if assertion fails
     */
    void assertThat(Predicate<StringList> predicate, String errorMessage) throws AssertionError;

    /**
     * assert that any condition from one of the StringList methods is true
     * <p>
     * e.g stringList.assertThat(p -> p.valuePrecededBy(3, "-"))
     *
     * @param predicate any StringList method that returns a boolean
     * @throws AssertionError if assertion fails
     */
    void assertThat(Predicate<StringList> predicate) throws AssertionError;

    /**
     * like {@link #assertThat(Predicate)} but allows to throw a custom exception
     */
    <E extends Throwable> void assertThat(Predicate<StringList> predicate, Class<E> throwable) throws E;

    /**
     * like {@link #assertThat(Predicate, String)} but allows to throw a custom exception
     */
    <E extends Throwable> void assertThat(Predicate<StringList> predicate, String errorMessage, Class<E> throwable) throws E;

    /**
     * Applies an action to each String in the list, then returns the list
     *
     * @param action action to apply
     * @return this StringList
     */
    StringList applyForEach(Function<String, String> action);

    /**
     * Applies an action to each String in the list starting at index, then returns the list
     *
     * @param action     action to apply
     * @param beginIndex index to begin at (including)
     * @return this StringList
     */
    StringList applyForEach(Function<String, String> action, int beginIndex);

    /**
     * Applies an action to each String in the list starting at beginIndex, ending at endIndex, then returns the list
     *
     * @param action     action to apply
     * @param beginIndex index to begin at (including)
     * @param endIndex   index to end at (excluding)
     * @return this StringList
     */
    StringList applyForEach(Function<String, String> action, int beginIndex, int endIndex);

    /**
     * Assert that all values in this list are unique
     *
     * @throws AssertionError if a duplicate value is detected
     */
    void assertUnique() throws AssertionError;

    /**
     * Assert that all values in this list are unique
     *
     * @param errorMessage error message to throw
     * @throws AssertionError if a duplicate value is detected
     */
    void assertUnique(String errorMessage) throws AssertionError;

    /**
     * like {@link #assertUnique()} but allows to throw a custom exception
     */
    <E extends Throwable> void assertUnique(Class<E> throwable) throws E;

    /**
     * like {@link #assertUnique(String)} but allows to throw a custom exception
     */
    <E extends Throwable> void assertUnique(String errorMessage, Class<E> throwable) throws E;

    /**
     * @param predicate the predicate to test
     * @return true if all elements in this list match the given predicate
     */
    boolean allMatch(Predicate<String> predicate);

    /**
     * @param predicate the predicate to test
     * @return true if any element in this list matches the given predicate
     */
    boolean anyMatch(Predicate<String> predicate);

    /**
     * @param predicate the predicate to test
     * @return true if no element in this list matches the given predicate
     */
    boolean noneMatch(Predicate<String> predicate);

}
