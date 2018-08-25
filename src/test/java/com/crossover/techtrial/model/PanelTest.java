package com.crossover.techtrial.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PanelTest {
    Panel p1 = new Panel();
    Panel p2 = new Panel();

    @Before
    public void setUp() throws Exception {
        p1.setId(1L);
        p1.setSerial("1231231231231231");
        p1.setLatitude(47.498012);
        p1.setLongitude(19.039912);
        p1.setBrand("Tesla");
        p2.setId(1L);
        p2.setSerial("1231231231231231");
        p2.setLatitude(47.498012);
        p2.setLongitude(19.039912);
        p2.setBrand("Tesla");
    }

    @Test
    public void testToString() {

        Assert.assertEquals(
                p1.toString(),
                "Panel [id=" + p1.getId() + ", serial=" + p1.getSerial() + ", longitude=" + p1.getLongitude()
                        + ", latitude=" + p1.getLatitude() + ", brand=" + p1.getBrand() + "]"
        );
    }

    @Test
    public void testHashCode() {

        Assert.assertTrue(p1.hashCode() == p2.hashCode());
    }

    @Test
    public void testEquals() {

        Assert.assertTrue(p1.equals(p2) && p2.equals(p1));
    }
}