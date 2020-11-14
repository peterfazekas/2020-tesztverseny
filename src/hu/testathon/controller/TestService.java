package hu.testathon.controller;

import hu.testathon.model.domain.FinalResult;
import hu.testathon.model.domain.TestResult;
import hu.testathon.model.domain.TestValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TestService {

    private final TestValidator testValidator;
    private final List<TestResult> testResults;

    public TestService(TestValidator testValidator,
                       List<TestResult> testResults) {
        this.testValidator = testValidator;
        this.testResults = testResults;
    }

    /**
     * 2. feladat: Jelenítse meg a képernyőn a mintának megfelelően,
     * hogy hány versenyző vett részt a tesztversenyen!
     */
    public int getCompetitorsCount() {
        return testResults.size();
    }

    /**
     * 3. feladat: Kérje be egy versenyző azonosítóját, és jelenítse meg a
     * mintának megfelelően a hozzá eltárolt válaszokat! Feltételezheti,
     * hogy a fájlban létező azonosítót adnak meg.
     */
    public String getAnswersById(String id) {
        return getTestResultById(id).getAnswers();
    }

    private TestResult getTestResultById(String id) {
        return testResults.stream()
                .filter(i -> i.getId().equals(id))
                .findFirst()
                .get();
    }

    /**
     * 4. feladat: Írassa ki a képernyőre a helyes megoldást!
     * A helyes megoldás alatti sorba „+” jelet tegyen, ha az adott
     * feladatot az előző feladatban kiválasztott versenyző eltalálta,
     * egyébként egy szóközt!
     */
    public String getCheckedResult(String id) {
        return String.format("%s\t(a helyes megoldás)\n%s\t(a versenyző helyes válaszai)",
                testValidator.getAnswers(),
                testValidator.checkResults(getAnswersById(id)));
    }

    /**
     * 5. feladat: Kérje be egy feladat sorszámát, majd határozza meg,
     * hogy hány versenyző adott a feladatra helyes megoldást, és ez
     * a versenyzők hány százaléka!
     */
    public String getCorrectAnswerStatistic(int taskNumber) {
        long countCorrectAnswers = countCorrectAnswers(taskNumber - 1);
        double percent = countCorrectAnswers * 100.0 / getCompetitorsCount();
        return String.format("A feladatra %d fő, a versenyzők %5.2f%%-a adott helyes választ.",
                countCorrectAnswers, percent);
    }

    private long countCorrectAnswers(int taskNumber) {
        return testResults.stream()
                .map(TestResult::getAnswers)
                .filter(answers -> testValidator.isCorrect(answers, taskNumber))
                .count();
    }

    /**
     * 6. feladat: Határozza meg az egyes versenyzők pontszámát.
     */
    public List<String> getScores() {
        return createFinalResults().stream()
                .map(i -> i.getId() + " " + i.getScore())
                .collect(Collectors.toList());
    }

    private List<FinalResult> createFinalResults() {
        return testResults.stream()
                .map(this::createFinalResult)
                .collect(Collectors.toList());
    }

    private FinalResult createFinalResult(TestResult testResult) {
        return new FinalResult(testResult.getId(), testValidator.calculateScore(testResult.getAnswers()));
    }

    /**
     * 7. feladat: A versenyen a három legmagasabb pontszámot elérő
     * összes versenyzőt díjazzák. Például 5 indulónál előfordulhat,
     * hogy 3 első és 2 második díjat adnak ki. Így megtörténhet az is,
     * * hogy nem kerül sor mindegyik díj kiadására. Írassa ki a képernyőre
     * a díjazottak kódját és pontszámát pontszám szerint csökkenő sorrendben!
     */
    public String getOrderedResult() {
        return createOrderedFinalResults().stream()
                .filter(i -> i.getOrder() <= 3)
                .map(FinalResult::toString)
                .collect(Collectors.joining("\r\n"));
    }

    private List<FinalResult> createOrderedFinalResults() {
        List<FinalResult> sortedFinalResults = createSortedFinalResults();
        FinalResult prevResult = new FinalResult("", 0);
        List<FinalResult> orderedFinalResults = new ArrayList<>();
        for (var actualResult : sortedFinalResults) {
            int order = actualResult.getScore() == prevResult.getScore()
                    ? prevResult.getOrder()
                    : prevResult.getOrder() + 1;
            FinalResult finalResult = new FinalResult(order, actualResult.getId(), actualResult.getScore());
            orderedFinalResults.add(finalResult);
            prevResult = finalResult;
        }
        return orderedFinalResults;
    }

    private List<FinalResult> createSortedFinalResults() {
        return createFinalResults().stream()
                .sorted((i, j) -> j.getScore().compareTo(i.getScore()))
                .collect(Collectors.toList());
    }
}
