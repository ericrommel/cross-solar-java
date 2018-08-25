package com.crossover.techtrial.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HourlyElectricityTest {
    HourlyElectricity he1 = new HourlyElectricity();
    HourlyElectricity he2 = new HourlyElectricity();
    Panel p = new Panel();

    @Before
    public void setUp() throws Exception {
        p.setId(1L);
        p.setSerial("1231231231231231");
        he1.setId(1L);
        he1.setPanel(p);
        he1.setReadingAt(LocalDateTime.parse("2018-08-15T09:00:00"));
        he1.setGeneratedElectricity(150L);
        he2.setId(1L);
        he2.setPanel(p);
        he2.setReadingAt(LocalDateTime.parse("2018-08-15T09:00:00"));
        he2.setGeneratedElectricity(150L);
    }

    @Test
    public void testToString() {

        Assert.assertEquals(
                he1.toString(),
                "HourlyElectricity [id=" + he1.getId() + ", panel=" + p.toString() + ", generatedElectricity="
                        + he1.getGeneratedElectricity() + ", readingAt=" + he1.getReadingAt() + "]"
        );
    }

    @Test
    public void testHashCode() {

        Assert.assertTrue(he1.hashCode() == he2.hashCode());
    }

    @Test
    public void testEquals() {

        Assert.assertTrue(he1.equals(he2) && he2.equals(he1));
    }
}