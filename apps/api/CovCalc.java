import java.nio.file.*;
import java.util.*;

public class CovCalc {
    public static void main(String[] args) throws Exception {
        List<String> lines = Files.readAllLines(Paths.get("c:/Users/hansa/Downloads/asd-main/asd-main/apps/api/target/site/jacoco/jacoco.csv"));
        int totMissed = 0, totCovered = 0;
        for (int i = 1; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(",");
            int missed = Integer.parseInt(parts[7]);
            int covered = Integer.parseInt(parts[8]);
            String cls = parts[2];
            totMissed += missed;
            totCovered += covered;
            if (missed > 10) {
                System.out.println(cls + " missed=" + missed + " covered=" + covered);
            }
        }
        int tot = totMissed + totCovered;
        System.out.println("Total: " + tot + ", Covered: " + totCovered + ", Ratio: " + (double)totCovered/tot);
    }
}
