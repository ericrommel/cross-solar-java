package com.crossover.techtrial.dto;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DailyElectricityTest {
    private static final ModelMapper modelMapper = new ModelMapper();
    DailyElectricity de = new DailyElectricity();

    @Before
    public void setUp() throws Exception {
        de.setDate(LocalDate.now());
        de.setMin(10L);
        de.setMax(90L);
        de.setSum(320L);
        de.setAverage(33.25);
    }

    @Test
    public void testDailyElectricity() {

        DailyElectricity daily = modelMapper.map(de, DailyElectricity.class);

        Assert.assertEquals(de.getDate(), daily.getDate());
        Assert.assertEquals(de.getMin(), daily.getMin());
        Assert.assertEquals(de.getMax(), daily.getMax());
        Assert.assertEquals(de.getSum(), daily.getSum());
        Assert.assertEquals(de.getAverage(), daily.getAverage());
    }

    @Test
    public void testToString() {

        Assert.assertEquals(
                de.toString(),
                "DailyElectricity [date=" + de.getDate() + ", sum=" + 320 + ", average="
                        + 33.25 + ", min=" + 10 + ", max=" + 90 + "]"
        );
    }
}