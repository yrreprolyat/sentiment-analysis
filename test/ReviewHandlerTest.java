package project2;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.jupiter.api.Assertions.*;

/**
 * CS3354.253 Spring 2020 JUNIT Project 2 Solution 
 * @author Steckler
 */

public class ReviewHandlerTest {
    /**
     * Create a new reviewhandler object, set the ID counter in the abstract to 0 so it doesn't persist,
     * setup and clear printstream, and load pos/neg words.
     */
    @BeforeEach
    public void setUp() {
        System.out.println("Setup method test environment.");
        reviewHandlerUnderTest = new ReviewHandler();
        reviewHandlerUnderTest.setReviewIdCounter(0);
        System.setOut(pStream);
        try{
            reviewHandlerUnderTest.loadPosNegWords(posWordsPath, negWordsPath);
        } catch (IOException e) {
            System.err.println(e.toString());
            e.printStackTrace();
        }
        ((ByteArrayOutputStream) oStream).reset();

    }

    /**
     * Set relevant objects to null, delete the database file if it's present, reset streams
     */
    @AfterEach
    public void tearDown() {
        System.out.println("Cleared method test environment");
        reviewHandlerUnderTest = null;
        if (DBFile.exists()){
            DBFile.delete();
        }
        ((ByteArrayOutputStream) oStream).reset();
        System.setOut(reset);

    }

    /**
     * Loads an empty folder with no files in it, and check that it doesn't load anything
     */
    @Test
    void testLoadReviewsEmpty() {

        // Setup
        String emptyFolderPath = "./data/Movie-reviews/emptyfolder";
        // Run test
        reviewHandlerUnderTest.loadReviews(emptyFolderPath, 0);

        // Verify the results
        assertEquals(0, reviewHandlerUnderTest.getDatabase().size(), "Loadreviews test on an empty folder");

    }

    /**
     * Give a bad filepath to LoadReview and throw null exception
     */
    @Test
    void testLoadReviewsNull() {
        // Run and verify
        assertThrows(NullPointerException.class, ()->reviewHandlerUnderTest.loadReviews("badfilepath", 0));

    }

    /**
     * Loads a folder path with 500 valid files, and expects all 500 added to db
     */
    @Test
    void testLoadReviews500() {
        // Setup
        String posFolder = "./Data/Movie-reviews/pos";
        // Run test
        reviewHandlerUnderTest.loadReviews(posFolder, 1);

        assertEquals(500, reviewHandlerUnderTest.getDatabase().size(), "Loadreviews test 500 files");

    }

    /**
     * Loads a path to a single text file and checks that it's added to db
     */
    @Test
    void testLoadReviewsSingle() {
        reviewHandlerUnderTest.loadReviews(singleRevPathUnk, 1);

        assertEquals(1, reviewHandlerUnderTest.getDatabase().size(), "Load Reviews test on a single txt file.");

    }

    /**
     *  Checks the IO on loadReviews for single review
     */
    @Test
    void testLoadReviewsIOSingle() {
        // Setup
        String test = "Review imported." + NEWLINE
                + "ID: 0" + NEWLINE
                + "Text: text test" + NEWLINE
                + "Real Class: Unknown" + NEWLINE
                + "Classification result: Unknown" + NEWLINE
                + "Real class Unknown." + NEWLINE + NEWLINE;
        // Run the test
        reviewHandlerUnderTest.loadReviews(filePath,2);
        // Verify the results
        assertEquals(test, oStream.toString(), "The terminal output is equivalent.");

    }

    /**
     * Verify the terminal IO of loadreviews on the positive folder
     */
    @Test
    void testLoadReviewsIOPos() {
        // Setup
        String expectedOutput = "Loading reviews..." + NEWLINE
                + "Folder imported." + NEWLINE
                + "Number of entries: 500" + NEWLINE
                + "Correctly classified: 388" + NEWLINE
                + "Misclassified: 112" + NEWLINE
                + "Accuracy: 77.60000000000001%" + NEWLINE;
        // Run the test
        reviewHandlerUnderTest.loadReviews(folderPosPath,1);
        // Verify the output
        assertEquals(expectedOutput, oStream.toString(), "The positive folder output is equivalent.");
    }

    /**
     * Verify the terminal IO of loadReviews for the Negative folder
     */
    @Test
    void testLoadReviewsIONeg(){
        // Setup
        String expectedOutput = "Loading reviews..." + NEWLINE
                + "Folder imported." + NEWLINE
                + "Number of entries: 500" + NEWLINE
                + "Correctly classified: 316" + NEWLINE
                + "Misclassified: 184" + NEWLINE
                + "Accuracy: 63.2%" + NEWLINE;
        // Run the test
        reviewHandlerUnderTest.loadReviews(folderNegPath, 0);
        // Verify the output
        assertEquals(expectedOutput, oStream.toString(), "The negative folder output is equivalent");
    }

    /**
     * Validate a non null result is returned
     * @throws Exception
     */
    @Test
    void testReadReview() throws Exception {

        // Run test
        final MovieReview result = reviewHandlerUnderTest.readReview(filePath, 0);

        // Verify the results
        assertNotNull(result, "readReview successfully returns an object.");
    }

    /**
     * readReview is given a bad filepath and throws exception
     */
    @Test
    void testReadReview_ThrowsIOException() {
        // Run test
        assertThrows(IOException.class, () -> reviewHandlerUnderTest.readReview("./fakeFilePath/file/text.txt", 0));

    }

    /**
     * Classify positive review and verify correct classification
     * @throws IOException
     */
    @Test
    void testClassifyReviewPos() throws IOException {
        // Setup test
        final MovieReview review = reviewHandlerUnderTest.readReview(singleRevPathPos,1);
        // Run test
        final ReviewScore result = reviewHandlerUnderTest.classifyReview(review);
        // Verify the results
        assertEquals(ReviewScore.POSITIVE, result, "The positive review was correctly classified");

    }

    /**
     *  Classify the Unknown review and verify it as unknown.
     */
    @Test
    void testClassifyReviewUnknown() throws IOException {
        // Setup Test
        final MovieReview review = reviewHandlerUnderTest.readReview(singleRevPathUnk, 1);
        // Run test
        final ReviewScore result = reviewHandlerUnderTest.classifyReview(review);
        // Verify the results
        assertEquals(ReviewScore.UNKNOWN, result, "The unknown(pos) review was correctly tagged as unknown");

    }

    /**
     * Use a known review to verify classification result
     * @throws IOException
     */
    @Test
    void testClassifyReviewNeg() throws IOException {
        // Setup
        final MovieReview review = reviewHandlerUnderTest.readReview(singleRevPathNeg, 0);
        // Run test
        final ReviewScore result = reviewHandlerUnderTest.classifyReview(review);

        // Verify the results
        assertEquals(ReviewScore.NEGATIVE, result, "Classify review returns correctly classified review score on known review.");

    }

    /**
     * Give classify a null object to throw exception
     */
    @Test
    void testClassifyReviewNull() {
        // Setup
        MovieReview mock = null;
        // Test and Verify
        assertThrows(NullPointerException.class, ()-> reviewHandlerUnderTest.classifyReview(mock));

    }

    /**
     * Verify a specific review is removed
     */
    @Test
    void testDeleteReview() {
        // Setup
        final MovieReview mock = new MovieReview(0, "filePath0", "delete test", ReviewScore.NEGATIVE, ReviewScore.NEGATIVE);
        reviewHandlerUnderTest.getDatabase().put(mock.getId(), mock);
        // Run test
        reviewHandlerUnderTest.deleteReview(0);
        // Verify the results
        assertEquals(0, reviewHandlerUnderTest.getDatabase().size(), "deleteReview successfully removed db object.");

    }

    /**
     *  Create a mock db to save, and verify with load dependency
     * @throws Exception
     */
    @Test
    void testSaveDB() throws Exception {
        // Setup
        final MovieReview mock0 = new MovieReview(0, filePath, "text test", ReviewScore.UNKNOWN, ReviewScore.UNKNOWN );
        reviewHandlerUnderTest.getDatabase().put(mock0.getId(), mock0);
        // Run test
        reviewHandlerUnderTest.saveDB();
        // Verify the results
        ReviewHandler reviewHandlerUnderTest = new ReviewHandler();
        reviewHandlerUnderTest.loadDB();
        assertEquals(1, reviewHandlerUnderTest.getDatabase().size(), "Saved DB was successfully loaded.");

    }

    /**
     * Load a fresh DB, dependent on saveDB
     * @throws Exception
     */
    @Test
    void testLoadDB() throws Exception {
        // Setup
        reviewHandlerUnderTest.loadReviews(singleRevPathNeg, 0);
        try {
            reviewHandlerUnderTest.saveDB();
        }catch (IOException e) {
            System.err.println("Error accessing the database file.");
            return;
        }
        reviewHandlerUnderTest = new ReviewHandler();
        // Run test
        reviewHandlerUnderTest.loadDB();
        // Verify the results
        assertEquals(1, reviewHandlerUnderTest.getDatabase().size(), "LoadDB successfully populates 1 db object");

    }

    /**
     * Test IOException for loadDB
     * @throws IOException
     */
    @Test
    void testLoadDBIOException() throws IOException {
        // Setup the test to throw the exception
        testPW = new PrintWriter("database.txt");
        testPW.println( 0 + " @ " + singleRevPathNeg + " ! " + "Negative" + " & " + "Negative");
        testPW.close();
        // Run and verify results
        assertThrows(IOException.class, ()-> reviewHandlerUnderTest.loadDB());

    }

    /**
     * Test an uncaught exception in loadDB for number format
     * @throws IOException
     */
    @Test
    void testLoadDBNumFormException() throws IOException {
        // Setup the test
        testPW = new PrintWriter("database.txt");
        testPW.println( "zero" + "@" + singleRevPathNeg + "@" + "Negative" + "@" + "Negative");
        testPW.close();
        // Test and verify
        assertThrows(NumberFormatException.class, ()-> reviewHandlerUnderTest.loadDB());

    }

    /**
     * Test a load DB IO for reading the database
     * @throws IOException
     */
    @Test
    void testLoadDBIO() throws IOException {
        // Setup
        String test = "Reading getDatabase()...Done." + NEWLINE;
        testPW = new PrintWriter("database.txt");
        testPW.println( 0 + " @ " + singleRevPathNeg + " @ " + "Negative" + " @ " + "Negative");
        testPW.close();
        // Run the test
        reviewHandlerUnderTest.loadDB();
        // Verify Results
        assertEquals(test, oStream.toString(), "Verify the string is being output and matches");
        System.setOut(reset);
        assertEquals(1, reviewHandlerUnderTest.getDatabase().size(), "The database is the correct size");

    }

    /**
     * Find an existing ID in the DB
     */
    @Test
    void testSearchById() {
        // Setup
        final MovieReview mock0 = new MovieReview(0, "filePath0", "idtext0", ReviewScore.NEGATIVE, ReviewScore.NEGATIVE);
        final MovieReview mock1 = new MovieReview(1, "filePath1", "idtext1", ReviewScore.NEGATIVE, ReviewScore.NEGATIVE);
        final MovieReview mock2 = new MovieReview(2, "filePath2", "idtext2", ReviewScore.NEGATIVE, ReviewScore.NEGATIVE);
        reviewHandlerUnderTest.getDatabase().put(mock0.getId(), mock0);
        reviewHandlerUnderTest.getDatabase().put(mock1.getId(), mock1);
        reviewHandlerUnderTest.getDatabase().put(mock2.getId(), mock2);
        // Run test
        final MovieReview result = reviewHandlerUnderTest.searchById(1);
        // Verify the results
        assertNotNull(result, "searchById returns an object.");
        assertEquals(result, mock1, "The objects are equivalent.");

    }

    /**
     * Search db for id that is not present
     */
    @Test
    void testSearchByIdNull(){
        //Setup
        final MovieReview mock = new MovieReview(0, "filePath0", "null test", ReviewScore.NEGATIVE, ReviewScore.NEGATIVE);
        reviewHandlerUnderTest.getDatabase().put(mock.getId(), mock);
        //Run test
        assertNull(reviewHandlerUnderTest.searchById(-1), "Searching non existent ID to verify null return");

    }

    /**
     * Hamcrest Test to check that search returns multiple reviews containing same substring
     */
    @Test
    void testSearchBySubstring() {
        // Setup
        final MovieReview mock0 = new MovieReview(0, "filePath0", "text0 substring", ReviewScore.NEGATIVE, ReviewScore.NEGATIVE);
        final MovieReview mock1 = new MovieReview(1, "filePath1", "text1 substring", ReviewScore.NEGATIVE, ReviewScore.NEGATIVE);
        final MovieReview mock2 = new MovieReview(2, "filePath2", "text2 substring", ReviewScore.NEGATIVE, ReviewScore.NEGATIVE);
        reviewHandlerUnderTest.getDatabase().put(mock0.getId(), mock0);
        reviewHandlerUnderTest.getDatabase().put(mock1.getId(), mock1);
        reviewHandlerUnderTest.getDatabase().put(mock2.getId(), mock2);
        // Run test
        final List<MovieReview> result = reviewHandlerUnderTest.searchBySubstring("substring");
        // Verify the results
        assertEquals(3, result.size(), "searchBySubstring populates successfully returns three objects.");
        assertThat("The returned objects are the right objects.",result, hasItems(mock0, mock1, mock2));

    }

    /**
     * Search an empty/null database and verify no result
     */
    @Test
    void testSearchBySubstringNull(){
        // Run test on empty database
        final List<MovieReview> result = reviewHandlerUnderTest.searchBySubstring("substring");
        // Verify the results
        assertNull(result, "Nothing was returned from empty database.");

    }

    /**
     * The review ID counter in Abstract is initialized to 0, and should be 0 if we do nothing to it
     */

    @Test
    void testGetReviewIDCounterNoID(){
        //Setup and Run
        int result = AbstractReviewHandler.getReviewIdCounter();
        // Verify Results
        assertEquals(0, result, "ID is initialized to 0");
    }

    /**
     * Test the ID counter on a single review to ensure it's retrieving the next ID.
     */

    @Test
    void testGetReviewIDCounterOneID(){
        // Setup with single Review
        reviewHandlerUnderTest.loadReviews(singleRevPathPos, 1);
        // Run
        int result = AbstractReviewHandler.getReviewIdCounter();
        // Verify ID incremented by 1
        assertEquals(1, result, "ID incremented successfully by one in Abstract");

    }

    /**
     * Set the ID counter, load a review, test the resulted ID count
     */
    @Test
    void testSetReviewIDCounter(){
        // Setup, expected is 5
        AbstractReviewHandler.setReviewIdCounter(4);
        reviewHandlerUnderTest.loadReviews(singleRevPathPos,1);
        // Run
        int result = reviewHandlerUnderTest.getReviewIdCounter();
        // Verify
        assertEquals(5, result, "Successfully set and added ID.");
    }

    /**
     * Test the exception thrown and caught by reading in bad paths for pos and neg word files
     */
    @Test
    void testLoadPosNegWordsThrowsIOException(){
        // Run and Verify
        assertThrows(IOException.class, ()-> reviewHandlerUnderTest.loadPosNegWords("badpath", "worsepath"));

    }

    /**
     * Test the IO output of successfully loading the positive and negative word hashmaps
     * @throws IOException
     */
    @Test
    void testLoadPosNegWordsIO() throws IOException{
        // Setup
        String test = "Hash set is of size 2006" + NEWLINE
                    + "Hash set is of size 4762" + NEWLINE;
        // Run
        reviewHandlerUnderTest.loadPosNegWords(posWordsPath, negWordsPath);
        // Verify
        assertEquals(test, oStream.toString(), "Pos and Neg words Hash sets loaded successfully");

    }

    /**
     * Series of pathing variables in root content folder using several created, empty, and provided folders/files to
     * validate and expand ease of testing methods
     */
    private static ReviewHandler reviewHandlerUnderTest;
    /**
     * local variable to allow easy cleanup between tests for database file
     */
    private final static File DBFile = new File("database.txt");
    /**
     * Used to create temporary DB files for testing
     */
    private static PrintWriter testPW;
    /**
     * The following file path variables are dependent on the data folder being present
     * in the same top level directory eg ./data  ./project2
     * Could localize several of these variables
     */
    private static String filePath = "./data/Movie-reviews/notemptyfolder/test.txt";
    private static String singleRevPathUnk = "./data/Movie-reviews/pos/0_9.txt";
    private static String singleRevPathNeg = "./data/Movie-reviews/neg/0_3.txt";
    private static String singleRevPathPos = "./data/Movie-reviews/pos/9_7.txt";
    private static String posWordsPath = "./data/positive-words.txt";
    private static String negWordsPath = "./data/negative-words.txt";
    private static String folderPosPath = "./data/Movie-reviews/pos";
    private static String folderNegPath = "./data/Movie-reviews/neg";
    /**
     * Used for testing IO
     */
    private static OutputStream oStream = new ByteArrayOutputStream();
    private static PrintStream pStream = new PrintStream(oStream);
    /**
     * Clear the buffer
     */
    private static final PrintStream reset = System.out;
    /**
     * To avoid linux/windows issues for / \ . carriage return etc
     */
    private final static String NEWLINE = System.getProperty("line.separator");

    /**
     * SPECIAL NOTES
     * the emptyfolder might not actually be empty, reports of a gitignore showing up have been reported
     * Pos file 0_9 classifies as unknown and is used for unknown testing for Tesic, and as negative for Metsis
     * Contents of the file test.txt in notemptyfolder are "text test"
     * Classify returns UNKNOWN if word counts of pos and neg are equal (important for IO testing) (Tesic)
     * Classify returns NEGATIVE if word counts of pos and neg are equal (important for IO testing) (Metsis)
     * ReviewIDcounter has to be manually reset for each test
     * PosWords and NegWords are loaded for each test
     * Several uncaught exceptions are forced and expected
     */
}

