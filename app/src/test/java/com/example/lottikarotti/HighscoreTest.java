package com.example.lottikarotti;

import com.example.lottikarotti.Highscore.Highscore;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HighscoreTest {
    Highscore highscore;
    Highscore highscore1;

    @BeforeEach
    public void setup(){
        highscore = new Highscore("Isa", 100);
        highscore1 = new Highscore("Bob", 200);
    }

    @Test
    public void testIfHighScoreIsGraterThenOther() {
        Assertions.assertEquals(1, highscore1.compareTo(highscore));
    }

    @Test
    public void testIfHighScoreIsNotGraterThanOther() {
        Assertions.assertEquals(-1, highscore.compareTo(highscore1));
    }

    @Test
    public void testIfHighScoresAreEqual(){
        Assertions.assertEquals(0, highscore.compareTo(new Highscore("ILoveToTest", 100)));
    }
}
