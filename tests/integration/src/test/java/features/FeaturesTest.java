package features;

import com.intuit.karate.junit5.Karate;

class FeaturesTest {

    @Karate.Test
    Karate allFeatures() {
        return Karate.run("classpath:features").relativeTo(getClass());
    }
}
