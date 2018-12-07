package net.robinfriedli.stringlist;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface StringList extends List<String> {

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
}
